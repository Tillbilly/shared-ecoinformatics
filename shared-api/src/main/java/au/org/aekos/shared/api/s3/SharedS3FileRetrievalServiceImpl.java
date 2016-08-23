package au.org.aekos.shared.api.s3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aekos.shared.api.json.fileinfo.StorageLocationInfo;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfo;
import au.org.ecoinformatics.s3client.S3RESTHttpClient;


/**
 * This is the service used by the aekos portal to retrieve shared data files from an s3 object store,
 * to a local file.
 * 
 * The primary and backup object store clients must be configured and set in the service before using.
 * 
 * It is mandatory that the secret key for the object stores IS NOT checked in to a public git repo, 
 * for the prod deploy. 
 * 
 * Here is an example Spring s3 client config from shared-web
 * 
 * <bean id="s3RestClientDev" class="au.org.ecoinformatics.s3client.S3RESTHttpClientImpl">
            <property name="active" value="true" />
            <property name="objectStoreName" value="nectar_shared_dev" />
            <property name="S3ApiUrl" value="https://swift.rc.nectar.org.au:8888" />
            <property name="bucket" value="shared_submissions" />
            <property name="apiKey" value="d9921bab82084cd4ac8beb43ec4da435" />
            <property name="secretKey" value="4b834e1a23d84be2a09a19cc6e32ad37" />
        </bean>
 *
 * You can obtain the appropriate info for your deploy environment from the nectar 'container' page.
 *
 * The object store config must match the install of shared web you are looking at,
 * i.e. shared-qa  or shared-prod
 * 
 * It is the responsibility of the user to construct the destination file location
 * and pass it in to this service.
 *
 * @author btill
 */
public class SharedS3FileRetrievalServiceImpl implements SharedS3FileRetrievalService {

	private static int RESPONSE_CODE_OK = 200;
	
	private Logger logger = LoggerFactory.getLogger(SharedS3FileRetrievalServiceImpl.class);
	
	private S3RESTHttpClient primaryObjectStoreClient;
	
	private S3RESTHttpClient backupObjectStoreClient;
	
	@Override
	public void retrieveObjectToLocal(SubmissionFileInfo fileInfo, File destinationFile) throws SharedS3FileRetrievalException{
		FileRetrieveCommand command = getFileRetrieveCommand(fileInfo);
		try {
			performDownload(command, destinationFile);
		} catch (FileNotFoundException e) {
			logger.error("Download File Failed - FileNotFound " + destinationFile.getPath(), e);
			throw new SharedS3FileRetrievalException("Download File Failed - FileNotFound " + destinationFile.getPath(), e);
		} catch (Exception e) {
			logger.error("Exception thrown attempting to download " + destinationFile.getPath(), e);
			throw new SharedS3FileRetrievalException("Download File Failed " + command.getErrorString() + " Dest File " +  destinationFile.getPath(), e);
		}
	}
	
	private void performDownload(FileRetrieveCommand command, File destinationFile) throws FileNotFoundException, Exception{
		S3RESTHttpClient s3Client = command.getS3Client();
		logger.info("Attempting to download  ObjStore" + s3Client.getObjectStoreName() + " objId " + command.getLocationInfo().getObjectId() );
		FileOutputStream fos = new FileOutputStream(destinationFile);
		int responseCode = s3Client.getFileStreaming(command.getLocationInfo().getObjectId(), fos);
		if(responseCode != RESPONSE_CODE_OK){
			logger.error("Streaming download failed: response code " + Integer.toString(responseCode));
			throw new SharedS3FileRetrievalException("Streaming download failed: response code " + Integer.toString(responseCode) + " ObjStore " + s3Client.getObjectStoreName() + " objId " + command.getLocationInfo().getObjectId() );
		}
		logger.info(" ObjStore " + s3Client.getObjectStoreName() + " objId " + command.getLocationInfo().getObjectId() + " retrieved to " + destinationFile.getName() + " " + destinationFile.getPath());
	}
	
	
	/**
	 * Returns a 'command' object containing info on the s3client to use and object information to retrieve.
	 * 
	 * @param fileInfo
	 * @return
	 */
	private FileRetrieveCommand getFileRetrieveCommand(SubmissionFileInfo fileInfo) throws SharedS3FileRetrievalException {
		FileRetrieveCommand command = getVerifiedFileRetrieveCommand(primaryObjectStoreClient, fileInfo);
		if(command != null){
			return command;
		}
		command = getVerifiedFileRetrieveCommand(backupObjectStoreClient, fileInfo);
		if(command == null){
			throw new SharedS3FileRetrievalException("Can not locate verified object store location for submissionDataId " + fileInfo.getId());
		}
		return command;
	}
	
	private FileRetrieveCommand getVerifiedFileRetrieveCommand(S3RESTHttpClient s3Client, SubmissionFileInfo fileInfo){
		if(s3Client == null){
			logger.error("s3Client not configured");
			return null;
		}
		StorageLocationInfo slInfo  = retrieveStorageLocationForObjectStoreName(s3Client.getObjectStoreName(), fileInfo);
		if(slInfo == null){
			return null;
		}
		try {
			if( s3Client.objectExistInBucket(slInfo.getObjectId()) ){
				return new FileRetrieveCommand(s3Client, slInfo);
			}
			logger.error("Object " + slInfo.getObjectId() + " does not exist in object store " + s3Client.getObjectStoreName() + " but is defined in StorageLocationInfo");
		} catch (Exception e) {
			logger.error("S3Client exception - check key exists in object store " + "Object " + slInfo.getObjectId() +" Bucket " + s3Client.getObjectStoreName(), e );
		}
		return null;
	}
	
	
	
	
	private StorageLocationInfo retrieveStorageLocationForObjectStoreName(String objectStoreName, SubmissionFileInfo fileInfo){
		if(fileInfo.getS3Locations() == null || fileInfo.getS3Locations().size() == 0){
			logger.error("No storage location info provided in SubmissionFileInfo for SHaRED Submission Data Id " + fileInfo.getId().toString());
			return null;
		}
		for(StorageLocationInfo storageLocation : fileInfo.getS3Locations()){
			if(objectStoreName.equals(storageLocation.getObjectStoreIdentifier())){
				return storageLocation;
			}
		}
		logger.info("No storage location found for object store name " + objectStoreName + " in SubmissionFileInfo, SHaRED Submission Data Id " + fileInfo.getId().toString());
		return null;
	}
	
	private class FileRetrieveCommand {
		private S3RESTHttpClient s3Client;
		private StorageLocationInfo locationInfo;
		
		public FileRetrieveCommand(S3RESTHttpClient s3Client,
				StorageLocationInfo locationInfo) {
			super();
			this.s3Client = s3Client;
			this.locationInfo = locationInfo;
		}

		public S3RESTHttpClient getS3Client() {
			return s3Client;
		}

		public StorageLocationInfo getLocationInfo() {
			return locationInfo;
		}
		
		public String getErrorString(){
			return "Bucket " + s3Client.getObjectStoreName() + " ObjId " + locationInfo.getObjectId();
		}
		
	}

	public S3RESTHttpClient getPrimaryObjectStoreClient() {
		return primaryObjectStoreClient;
	}

	public void setPrimaryObjectStoreClient(
			S3RESTHttpClient primaryObjectStoreClient) {
		this.primaryObjectStoreClient = primaryObjectStoreClient;
	}

	public S3RESTHttpClient getBackupObjectStoreClient() {
		return backupObjectStoreClient;
	}

	public void setBackupObjectStoreClient(S3RESTHttpClient backupObjectStoreClient) {
		this.backupObjectStoreClient = backupObjectStoreClient;
	}

}
