package au.edu.aekos.shared.service.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.service.notification.AdminNotificationService;
import au.edu.aekos.shared.web.util.SharedFileUtils;
import au.org.ecoinformatics.s3client.S3RESTHttpClient;


@Service
public class ObjectStoreServiceImpl implements ObjectStoreService, InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectStoreServiceImpl.class);
	
	@Value("${s3.objectstore.identifier}")
	private String primaryObjectStore;
	
	@Value("${s3.objectstore.backup.identifier}")
	private String backupObjectStore;
	
	@Value("${submission.upload.tempdir}")
	private String tempFileDir;
	
	@Autowired
	@Qualifier(value="taskExecutor")
	private TaskExecutor taskExecutor;
	
	@Autowired  //TaskExecutor used by background system tasks
	@Qualifier(value="systemTaskExecuter")
	private TaskExecutor systemTaskExecuter;
	
	@Autowired
	private List<S3RESTHttpClient> s3ClientList = new ArrayList<S3RESTHttpClient>();
	
	@Autowired
	private SubmissionDataDao submissionDataDao;

	@Autowired
	private AdminNotificationService adminNotificationService;

	@Value("${objectstore.config}")
	private String s3keyFile;
	
	public String getPrimaryObjectStore(){
		return primaryObjectStore;
	}
	public String getBackupObjectStore() {
		return backupObjectStore;
	}
	
	public void asyncUploadFileToObjectStore(File fileOnLocalDisk, String objectStoreIdentifier, Long submissionDataId) throws ObjectStoreServiceException {
		S3RESTHttpClient s3Client = getClientForIdentifier(objectStoreIdentifier);
		if(s3Client == null){
			throw new ObjectStoreServiceException("No active s3 client named '" + objectStoreIdentifier +"' exists.");
		}
		//Confirm the file exists
		if(! fileOnLocalDisk.exists() ){
			throw new ObjectStoreServiceException("File " + fileOnLocalDisk.getName() + " does not exist. Upload failed.");
		}
		if(! fileOnLocalDisk.canRead() ){
			throw new ObjectStoreServiceException("File " + fileOnLocalDisk.getName() + " can not be read. Upload failed.");
		}
		//Check Submission Data object exists for identifier
		if(! submissionDataDao.submissionDataExists(submissionDataId) ){
			throw new ObjectStoreServiceException("No SubmissionData with id '" + submissionDataId + "' exists.");
		}
		
		UploadFileTask uploadTask = new UploadFileTask( s3Client, fileOnLocalDisk, submissionDataId  );
		try{
		    taskExecutor.execute(uploadTask);
		}catch(TaskRejectedException e){
			logger.error("Upload file asynchronous task rejected, running maunally", e);
			uploadTask.run();
		}
		
		
	}
		
	public S3RESTHttpClient getClientForIdentifier(String identifier){
		for(S3RESTHttpClient s3Client : s3ClientList ){
			if(s3Client.getObjectStoreName().equalsIgnoreCase(identifier) &&
					s3Client.isActive()){
				return s3Client;
			}
		}
		logger.info("No active s3 client named '" + identifier +"' exists." );
		return null;
	}
	
	
	
	private class UploadFileTask implements Runnable {
        
		S3RESTHttpClient s3Client;
		File fileOnLocalDisk;
		Long submissionDataId;
		
		public UploadFileTask(S3RESTHttpClient s3Client, File fileOnLocalDisk,
				Long submissionDataId) {
			super();
			this.s3Client = s3Client;
			this.fileOnLocalDisk = fileOnLocalDisk;
			this.submissionDataId = submissionDataId;
		}

		@Override
		public void run() {
			String key = null;
			try {
				key = UUID.randomUUID().toString();
				while(s3Client.objectExistInBucket(key)){
					key = UUID.randomUUID().toString();
				}
				logger.info("Upload File Task - PUT " + fileOnLocalDisk.getName() + " to " + s3Client.getObjectStoreName() + " with key '" + key + "'");
				s3Client.putFile(fileOnLocalDisk, key);
				logger.info("Upload File Task - PUT succeeded" );
			} catch (Exception e) {
				//Handle s3 write failure - notification? write to redo table
				String message = "Upload file failed " + fileOnLocalDisk.getName() + " to " + s3Client.getObjectStoreName() + " with key '" + key + "'" + " for submissionDataId '" + submissionDataId + "'";
				Date now = new Date();
				adminNotificationService.notifyAdminByEmail("Object store write failed", message + "\n" + now.toString(), e);
				logger.error(message, e);
				return;
			}
			//Now save a new object store location to the SubmissionData record.
			ObjectStoreLocation osl = new ObjectStoreLocation();
			osl.setObjectId(key);
			osl.setObjectName(fileOnLocalDisk.getName());
			osl.setObjectStoreIdentifier(s3Client.getObjectStoreName());
			Long storageLocationId = submissionDataDao.transactionalAddStorageLocation(submissionDataId, osl);
			if(storageLocationId == null){
				logger.error("Object store location for key '" + key +" not created for submissionDataId '" + submissionDataId + "'");
			}else{
				logger.info("Object store location id '" + storageLocationId + "' created for submissionDataId '" + submissionDataId + "'");
			}
		}
	}

	//This system method uses a different TaskExecuter, and requires more info in the Runnable Task.
	public void systemAsyncUploadFileToObjectStore(File fileOnLocalDisk, String originalFileName, String objectStoreIdentifier, Long submissionDataId, boolean deleteFileAfterwards) throws ObjectStoreServiceException {
		S3RESTHttpClient s3Client = getClientForIdentifier(objectStoreIdentifier);
		if(s3Client == null){
			throw new ObjectStoreServiceException("No active s3 client named '" + objectStoreIdentifier +"' exists.");
		}
		//Confirm the file exists
		if(! fileOnLocalDisk.exists() ){
			throw new ObjectStoreServiceException("File " + fileOnLocalDisk.getName() + " does not exist. Upload failed.");
		}
		if(! fileOnLocalDisk.canRead() ){
			throw new ObjectStoreServiceException("File " + fileOnLocalDisk.getName() + " can not be read. Upload failed.");
		}
		//Check Submission Data object exists for identifier
		if(! submissionDataDao.submissionDataExists(submissionDataId) ){
			throw new ObjectStoreServiceException("No SubmissionData with id '" + submissionDataId + "' exists.");
		}
		
		systemTaskExecuter.execute(new SystemUploadFileTask( s3Client, fileOnLocalDisk, submissionDataId, originalFileName, deleteFileAfterwards  ));
		
		
	}
	
    private class SystemUploadFileTask implements Runnable {
        
		S3RESTHttpClient s3Client;
		File fileOnLocalDisk;
		Long submissionDataId;
		String originalFileName = "";
		boolean deleteFileAfterwards = false;
		
		public SystemUploadFileTask(S3RESTHttpClient s3Client, File fileOnLocalDisk,
				Long submissionDataId, String originalFileName, boolean deleteFileAfterwards) {
			super();
			this.s3Client = s3Client;
			this.fileOnLocalDisk = fileOnLocalDisk;
			this.submissionDataId = submissionDataId;
			this.originalFileName = originalFileName;
			this.deleteFileAfterwards = deleteFileAfterwards; 
		}

		@Override
		public void run() {
			String key = null;
			try {
				key = UUID.randomUUID().toString();
				while(s3Client.objectExistInBucket(key)){
					key = UUID.randomUUID().toString();
				}
				logger.info("Upload File Task - PUT " + fileOnLocalDisk.getName() + " to " + s3Client.getObjectStoreName() + " with key '" + key + "'");
				s3Client.putFile(fileOnLocalDisk, key);
				logger.info("Upload File Task - PUT succeeded" );
				if(deleteFileAfterwards){
					logger.info("Deleting " + fileOnLocalDisk.getPath());
					fileOnLocalDisk.delete();
				}
			} catch (Exception e) {
				//Handle s3 write failure - notification? write to redo table
				String message = "Upload file failed " + fileOnLocalDisk.getName() + " to " + s3Client.getObjectStoreName() + " with key '" + key + "'" + " for submissionDataId '" + submissionDataId + "'";
				Date now = new Date();
				adminNotificationService.notifyAdminByEmail("Object store write failed", message + "\n" + now.toString(), e);
				logger.error(message, e);
				if(deleteFileAfterwards){
					logger.info("Deleting " + fileOnLocalDisk.getPath());
					fileOnLocalDisk.delete();
				}
				return;
			}
			//Now save a new object store location to the SubmissionData record.
			ObjectStoreLocation osl = new ObjectStoreLocation();
			osl.setObjectId(key);
			osl.setObjectName(originalFileName);
			osl.setObjectStoreIdentifier(s3Client.getObjectStoreName());
			Long storageLocationId = submissionDataDao.transactionalAddStorageLocation(submissionDataId, osl);
			if(storageLocationId == null){
				logger.error("Object store location for key '" + key +" not created for submissionDataId '" + submissionDataId + "'");
			}else{
				logger.info("Object store location id '" + storageLocationId + "' created for submissionDataId '" + submissionDataId + "'");
			}
		}
	}
	


	@Override
	public int deleteObject(String key, String objectStoreIdentifier) throws ObjectStoreServiceException {
		S3RESTHttpClient s3Client = getClientForIdentifier(objectStoreIdentifier);
		if(s3Client == null){
			throw new ObjectStoreServiceException("No S3RESTHttpClient exists for identifier '" + objectStoreIdentifier + "'" );
		}
		int retValue = 0;
		try {
			retValue = s3Client.deleteObject(key);
		} catch (Exception e) {
			throw new ObjectStoreServiceException("Delete object from '" + objectStoreIdentifier + "' with key '" + key +"' failed", e);
		}
		return retValue;
	}
	
	//Used for site file retrieval.
	@Override 
	public File retrieveObjectToFile(String key, String objectStoreIdentifier)
			throws Exception {
		S3RESTHttpClient s3Client = getClientForIdentifier(objectStoreIdentifier);
		if(s3Client == null){
			throw new ObjectStoreServiceException("No S3RESTHttpClient exists for identifier '" + objectStoreIdentifier + "'" );
		}
		//get new temporary output file handle
		File tempFile = SharedFileUtils.createUniqueFile(key, tempFileDir);
		FileOutputStream fos = new FileOutputStream(tempFile);
		s3Client.getFileStreaming(key, fos);
		return tempFile;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
        //Check that the s3 config file is in the right place, and that all active s3 clients have keys present.
		//If the keys are'nt set, retrieve them from the config file and assign them.
		Properties s3configProps = null;
		for(S3RESTHttpClient s3client : s3ClientList){
			if(s3client.isActive()){
				if(! s3client.areKeysSet()){
					if(s3configProps == null){
					    s3configProps = loadS3Properties();
					}
					
					String apiKey = s3configProps.getProperty(getAccessKeyPropertyKey(s3client.getObjectStoreName()));
					if(!StringUtils.hasLength(apiKey) ){
						logger.error("Missing API Key for object store '" + s3client.getObjectStoreName() + "'");
						//If this object store is the primary object store - throw an exception
						if(primaryObjectStore.equals(s3client.getObjectStoreName())){
							throw new ObjectStoreServiceException("No API Key defined for primary object store '" + primaryObjectStore + "' in config file '" + s3keyFile +"'");
						}
					}
					s3client.setApiKey(apiKey);
					
					String secretKey = s3configProps.getProperty(getSecretKeyPropertyKey(s3client.getObjectStoreName()));
					if(!StringUtils.hasLength(secretKey) ){
						logger.error("Missing Secret Key for object store '" + s3client.getObjectStoreName() + "'");
						//If this object store is the primary object store - throw an exception
						if(primaryObjectStore.equals(s3client.getObjectStoreName())){
							throw new ObjectStoreServiceException("No Secret Key defined for primary object store '" + primaryObjectStore + "' in config file '" + s3keyFile +"'");
						}
					}
					s3client.setSecretKey(secretKey);
				}
			}
		}
	}
	
	private Properties loadS3Properties() throws ObjectStoreServiceException{
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(s3keyFile));
			return props;
		} catch (FileNotFoundException e) {
			logger.error("File not found for s3keyFile '" + s3keyFile + "'" , e);
			throw new ObjectStoreServiceException("File not found for s3keyFile '" + s3keyFile + "'", e);
		} catch (IOException e) {
			logger.error("IO Exception for s3keyFile '" + s3keyFile + "'" , e);
			throw new ObjectStoreServiceException("IO Exception for s3keyFile '" + s3keyFile + "'" , e);
		}
	}
	
	private static final String ACCESS_KEY_PROP_PREFIX = "s3.accesskey.";
	private static final String SECRET_KEY_PROP_PREFIX = "s3.secretkey.";
	public static String getAccessKeyPropertyKey(String objectStoreName){
		return ACCESS_KEY_PROP_PREFIX + objectStoreName;
	}
    public static String getSecretKeyPropertyKey(String objectStoreName){
		return SECRET_KEY_PROP_PREFIX + objectStoreName;
	}
    
	@Override @Transactional
	public void streamObjectStoreFile(OutputStream os, SubmissionData submissionData) throws ObjectStoreServiceException {
		SubmissionData sd = submissionDataDao.findByIdEagerLocations(submissionData.getId());
		ObjectStoreLocation osl = getActiveObjectStoreLocationForSubmissionData(sd);
		S3RESTHttpClient s3Client = getClientForIdentifier(osl.getObjectStoreIdentifier());
		if(s3Client == null){
			logger.error("No s3Client for identifier " + osl.getObjectStoreIdentifier());
			throw new ObjectStoreServiceException("No s3Client for identifier " + osl.getObjectStoreIdentifier());
		}
		Integer responseCode = null;
		try {
			responseCode = s3Client.getFileStreaming(osl.getObjectId(), os);
		} catch (Exception e) {
			logger.error("No s3Client for identifier " + osl.getObjectStoreIdentifier());
			throw new ObjectStoreServiceException("No s3Client for identifier " + osl.getObjectStoreIdentifier());
		}
		logger.info("S3 client GET response code " + responseCode.toString());
		if(200 != responseCode.intValue()){
			logger.error("File GET returned response code " + responseCode.toString() + " for " + osl.getObjectId() + " from object store " + osl.getObjectStoreIdentifier());
			throw new ObjectStoreServiceException("File GET returned response code " + responseCode.toString() + " for " + osl.getObjectId() + " from object store " + osl.getObjectStoreIdentifier());
		}
	}
	
	private ObjectStoreLocation getActiveObjectStoreLocationForSubmissionData(SubmissionData sd) throws ObjectStoreServiceException{
		List<ObjectStoreLocation> objectStoreLocationList = sd.getObjectStoreLocations();
		for(ObjectStoreLocation osl : objectStoreLocationList){
			if(primaryObjectStore.equalsIgnoreCase(osl.getObjectStoreIdentifier())){
				return osl;
			}
		}
		//Still here? need to check the back up object store location
		for(ObjectStoreLocation osl : objectStoreLocationList){
			if(backupObjectStore.equalsIgnoreCase(osl.getObjectStoreIdentifier())){
				return osl;
			}
		}
		logger.error("No active object store configured for submission data id " + sd.getId().toString());
		throw new ObjectStoreServiceException("No active object store configured for submission data id " + sd.getId().toString());
	}
	
	
	
}
