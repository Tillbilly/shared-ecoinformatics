package au.edu.aekos.shared.service.submission;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;
import au.edu.aekos.shared.service.file.FileService;
import au.edu.aekos.shared.service.file.FileSystemDataIntegrityException;
import au.edu.aekos.shared.service.s3.ObjectStoreService;
import au.edu.aekos.shared.service.s3.ObjectStoreServiceException;
import au.edu.aekos.shared.web.util.SharedFileUtils;

@Component
public class SubmissionDataServiceImpl implements SubmissionDataService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubmissionDataServiceImpl.class);

	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Autowired 
	private ObjectStoreService objectStoreService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	@Qualifier(value="taskExecutor")
	private TaskExecutor taskExecutor;
	
	@Value("${submission.upload.tempdir}" )
	private String uploadDir;
	
	@Value("${shared.system.deleteOrphanFilesAgeHours}")
	private Integer orphanAgeExpiryHours = 120;
	
	@Transactional
	public SubmissionData getHydratedSubmissionData(Long submissionDataId) {
		return submissionDataDao.findByIdEagerLocations(submissionDataId);
	}
	
	//TODO better exception handling, and ensuring an object store exists could be looked at.
	@Override
	public void writeSubmissionDataToObjectStore(Submission submission) throws ObjectStoreServiceException {
		
		for(SubmissionData submissionData : submission.getSubmissionDataList() ){
			if(submissionData != null){
				SubmissionData hydratedSD = getHydratedSubmissionData(submissionData.getId());
				if( ! hydratedSD.hasObjectStoreLocation() ){
					FileSystemStorageLocation fss = hydratedSD.getFileSystemStorageLocation();
					if(fss != null){
						File f = new File(fss.getFspath() + fss.getFileName());
						try {
							objectStoreService.asyncUploadFileToObjectStore(f, objectStoreService.getPrimaryObjectStore(), submissionData.getId() );
						} catch (ObjectStoreServiceException e) {
							logger.error("Writing to primary object store " + objectStoreService.getPrimaryObjectStore() + "failed. SubmissionDataId '" + submissionData.getId() + "'", e);
							objectStoreService.asyncUploadFileToObjectStore(f, objectStoreService.getBackupObjectStore(), submissionData.getId() );
						}
					}
				}
			}
		}
	}

	@Override
	@Transactional
	public void deleteSubmissionData(List<SubmissionData> submissionDataList) {
		if(submissionDataList == null ){
			return;
		}
		//Set<ObjectStoreLocation> objectsToDelete = new HashSet<ObjectStoreLocation>();
		logger.info("HARD DELETE submission physical files is SWITCHED OFF!!! This is a workaround . . .");
		//for(SubmissionData submissionData : submissionDataList){
		//	SubmissionData hydratedSD = getHydratedSubmissionData(submissionData.getId());
		//	objectsToDelete.addAll( hydratedSD.getObjectStoreLocations() );
		//	for( FileSystemStorageLocation fssl : hydratedSD.getFileSystemStorageLocations() ){
		//		File fileToDelete = new File(fssl.getFspath() + fssl.getFileName());
		//		if(fileToDelete.exists() && fileToDelete.canWrite()){
					
		//			fileToDelete.delete();
		//		}
		//	}
		//}
		//deleteFromObjectStore(objectsToDelete);
	}
	
	@Override
	@Transactional
	public void systemDeleteSubmissionData(SubmissionData sd) {
		SubmissionData hydratedSD = getHydratedSubmissionData(sd.getId());
		for( FileSystemStorageLocation fssl : hydratedSD.getFileSystemStorageLocations() ){
			File fileToDelete = new File(fssl.getFspath() + fssl.getFileName());
			if(fileToDelete.exists() && fileToDelete.canWrite()){
				logger.info("Deleting " + sd.getId() + " " + fssl.getFspath() + fssl.getFileName());
				fileToDelete.delete();
			}
		}
		Set<ObjectStoreLocation> objectsToDelete = new HashSet<ObjectStoreLocation>();
		objectsToDelete.addAll(hydratedSD.getObjectStoreLocations() );
		deleteFromObjectStore(objectsToDelete);
	}
	
	@Override
	public void deleteFromObjectStore(Set<ObjectStoreLocation> objectsToDelete) {
		for(ObjectStoreLocation osl : objectsToDelete){
			deleteFromObjectStore(osl);
		}
		
	}

	//TODO Strategy to deal with object store exceptions beetter
	public void deleteFromObjectStore(ObjectStoreLocation objectStoreLocation) {
		try {
			logger.info("Deleting object " + objectStoreLocation.getObjectId() + " from " + objectStoreLocation.getObjectStoreIdentifier() + " " + objectStoreLocation.getObjectName());
			objectStoreService.deleteObject(objectStoreLocation.getObjectId(), objectStoreLocation.getObjectStoreIdentifier());
		} catch (ObjectStoreServiceException e) {
			logger.warn("Object store deletion failed : " + objectStoreLocation.getObjectId() + " from " + objectStoreLocation.getObjectStoreIdentifier());
			logger.warn(e.getMessage(),e);
		}
	}

	@Override
	public void asyncDeleteNonPersistedSubmissionData(List<SubmissionData> submissionDataList){
		if(submissionDataList == null || submissionDataList.size() ==0 ){
			return;
		}
		//We should only need to delete filesystem storage locations for non-persisted SubmissionData
		for(SubmissionData sd : submissionDataList ){
			for(FileSystemStorageLocation fssl : sd.getFileSystemStorageLocations() ){
				asyncDeleteFileSystemStorageLocation(fssl);
			}
		}
	}

	private void asyncDeleteFileSystemStorageLocation(FileSystemStorageLocation fssl){
		DeleteFileTask task = new DeleteFileTask(fssl);
		try{
		    taskExecutor.execute(task);
		}catch(TaskRejectedException e){
			logger.error("Async delete task rejected", e);
			task.run();
		}
	}
	
	private class DeleteFileTask implements Runnable {
		File fileToDelete = null;
		public DeleteFileTask(FileSystemStorageLocation fssl){
			fileToDelete = new File(fssl.getFspath() + fssl.getFileName());
		}
		@Override
		public void run() {
			if(fileToDelete != null && fileToDelete.exists() && fileToDelete.canWrite()){
				logger.info("Deleting " + fileToDelete.getPath());
				fileToDelete.delete();
			}
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
	public File retrieveFileToLocal(SubmissionData subData){
	    File file = null;
	    SubmissionData submissionData = submissionDataDao.findByIdEagerLocations(subData.getId());
		FileSystemStorageLocation fssl = submissionData.getFileSystemStorageLocation();
		if(fssl != null ){
			file = new File(fssl.getFspath() + fssl.getFileName());
			if(! file.exists()){
				file = null;
			}
		}else{
			List<ObjectStoreLocation> objectStoreLocationList = submissionData.getObjectStoreLocations();
			if(objectStoreLocationList != null && objectStoreLocationList.size() > 0){
				ObjectStoreLocation osl = objectStoreLocationList.get(0);
				try {
					file = objectStoreService.retrieveObjectToFile(osl.getObjectId(), osl.getObjectStoreIdentifier()) ;
				} catch (ObjectStoreServiceException e) {
					e.printStackTrace();
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return file;
	}
	
	@Transactional
	public void systemCloneSubmissionDataToObjectStore(Long submissionDataId,
			List<String> objectStoreNames) throws ObjectStoreServiceException {
		SubmissionData sd = submissionDataDao.findByIdEagerLocations(submissionDataId);
		if(sd == null ){
			logger.error("Submission Data Entity ID '"  + submissionDataId + "' not found.  Can't perform background system data copy tasks");
			return;
		}
		
		File localFile = null;
		//First need to see if there is a FileSystemStorageLocation
		FileSystemStorageLocation fssl = sd.getFileSystemStorageLocation();
		if(fssl != null){
			localFile = new File(fssl.getFspath() + fssl.getFileName());
			if(localFile.exists() && localFile.canRead() ){
				for(String objectStoreIdentifier : objectStoreNames){
                    objectStoreService.systemAsyncUploadFileToObjectStore(localFile, sd.getFileName(), objectStoreIdentifier, submissionDataId, false);
				}
				return;
			}
		}
		//If we still don't have a local file handle,  need to copy an object store file to local and use that.
		//Have to delete the file as a temp file afterwards
		ObjectStoreLocation osl = getObjectStoreLocationNotNamedInList(sd, objectStoreNames);
		if(osl == null){
			logger.error("No object store location not in list found for '"  + submissionDataId + "'.  Can't perform background system data copy tasks." + objectStoreNames.toString());
		    return;
		}
		try {
			localFile  = objectStoreService.retrieveObjectToFile(osl.getObjectId(), osl.getObjectStoreIdentifier()) ;
		} catch (Exception e) {
			logger.error("Error retrieving " + osl.getObjectId() + " from " +  osl.getObjectStoreIdentifier(), e);
			return;
		}
		
		if(localFile.exists() && localFile.canRead() ){
			for(String objectStoreIdentifier : objectStoreNames){
                objectStoreService.systemAsyncUploadFileToObjectStore(localFile, sd.getFileName(), objectStoreIdentifier, submissionDataId, objectStoreNames.size() == 1);
			}
			return;
		}
	}
	
	private ObjectStoreLocation getObjectStoreLocationNotNamedInList(SubmissionData sd, List<String> objectStoreNames){
		List<ObjectStoreLocation> objStoreLocList = sd.getObjectStoreLocations();
		if(objStoreLocList == null){
			return null;
		}
		for(ObjectStoreLocation ossl : sd.getObjectStoreLocations()){
			if(! objectStoreNames.contains( ossl.getObjectStoreIdentifier()) ){
				return ossl;
			}
		}
		return null;
	}

	@Override @Transactional
	public SubmissionData cloneToNonPersistedFileSystemData(SubmissionData sd) throws FileSystemDataIntegrityException {
		SubmissionData clonedSD = new SubmissionData();
		clonedSD.setEmbargoDate(sd.getEmbargoDate());
		clonedSD.setFileDescription(sd.getFileDescription());
		clonedSD.setFileName(sd.getFileName());
		clonedSD.setFileSizeBytes(sd.getFileSizeBytes());
		clonedSD.setFormat(sd.getFormat());
		clonedSD.setFormatVersion(sd.getFormatVersion());
		clonedSD.setQuestionId(sd.getQuestionId());
		clonedSD.setSiteFileCoordinateSystem(sd.getSiteFileCoordinateSystem());
		clonedSD.setSiteFileCoordSysOther(sd.getSiteFileCoordSysOther());
		clonedSD.setSubmissionDataType(sd.getSubmissionDataType());
		SubmissionData workingSD = sd;
		if(sd.getId() != null){
			SubmissionData hydratedSD = getHydratedSubmissionData(sd.getId());
			if(hydratedSD != null){
				workingSD = hydratedSD;
			}
		}
		
		FileSystemStorageLocation fssl = workingSD.getFileSystemStorageLocation();
		if(fssl != null ){
			try{
			    FileSystemStorageLocation clonedFssl = createNewFileSystemStorageLocation(sd.getFileName(), fssl, sd.getFileName(), sd.getFileSizeBytes() );
			    clonedSD.getStorageLocations().add(clonedFssl);
			    return clonedSD;
			}
			catch(IOException ex){
				logger.error("Error occured cloning " + sd.getFileName() + " " + fssl.getFileName() + " " + fssl.getFspath(), ex);
			}
		}
		
		if(workingSD.getObjectStoreLocations() != null && workingSD.getObjectStoreLocations().size() > 0){
			ObjectStoreLocation osl = workingSD.getObjectStoreLocations().get(0);
			try {
				File cloneFile = objectStoreService.retrieveObjectToFile(osl.getObjectId(), osl.getObjectStoreIdentifier());
				if(cloneFile != null){
					FileSystemStorageLocation clonedFssl = new FileSystemStorageLocation();
					clonedFssl.setObjectName(cloneFile.getName());
					clonedFssl.setFspath(uploadDir);
					clonedSD.getStorageLocations().add(clonedFssl);
					return clonedSD;
				}
				
			}  catch (Exception e) {
				logger.error("Error occured cloning " + sd.getFileName() + " in retrieving from object store", e);
			}
			return null;
		}
		return null;
	}
	
	//Need some validation in here
	private FileSystemStorageLocation createNewFileSystemStorageLocation(String fileName, FileSystemStorageLocation fssl, String originalFilename, Long fileSizeBytes) throws IOException, FileSystemDataIntegrityException{
		File orig = new File(fssl.getFspath() + fssl.getFileName());
		if(! orig.exists() || ! orig.canRead() || orig.length() == 0){
			logger.error("File denoted by " + orig.getPath() + " does not exist, uploaded data integrity error.");
			//Try and recover an earlier version of the file to clone
			orig = attemptToRecoverEarlierFileVersionTolClone(fssl, originalFilename, fileSizeBytes);
			if(orig == null){
				logger.error("File system storage location can not be cloned, location does not exist - FSSL ID " + fssl.getId() +"  " + fssl.getFspath() + fssl.getFileName());
				throw new FileSystemDataIntegrityException("File system storage location can not be cloned, location does not exist - FSSL ID " + fssl.getId() +"  " + fssl.getFspath() + fssl.getFileName());
			}
		}
		
		File clonedFile = fileService.cloneToNewFile(orig, fileName);
		FileSystemStorageLocation fsslNew = new FileSystemStorageLocation();
		fsslNew.setObjectName(clonedFile.getName());
		fsslNew.setFspath(uploadDir);
		return fsslNew;
	}
	
	private File attemptToRecoverEarlierFileVersionTolClone(FileSystemStorageLocation fssl, String originalFilename, Long fileSizeBytes){
		logger.info("Attempting recovery of file " + originalFilename + " at path " + fssl.getFspath());
		File originalFile = new File(fssl.getFspath() + originalFilename);
		if(! originalFile.exists() || ! originalFile.canRead() || originalFile.length() == 0){
			logger.error("File denoted by " + originalFile.getPath() + " does not exist, file recovery failed.");
			return null;
		}
		if(originalFile.length() != fileSizeBytes.longValue()){
			logger.error("Recovered file not of the same size in bytes - original : " + Long.toString( originalFile.length()) + "  expected:" + fileSizeBytes.toString());
		    return null;
		}
		logger.info("File " + originalFile.getPath() + " recovered.");
		
		return originalFile;
	}

	@Override @Transactional
	public void systemDeleteLogicalStorageLocations(SubmissionData sd) {
		SubmissionData submissionData = submissionDataDao.findById(sd.getId());
		submissionDataDao.deleteStorageLocations(submissionData);
		
	}

	@Override @Transactional
	public void systemDeleteOrphanSubmissionData(SubmissionData sd) {
		SubmissionData submissionData = submissionDataDao.findById(sd.getId());
		if(submissionData.getSubmission() != null){
			logger.warn("Submission Data " + sd.getId() + " is not an orphan, delete operation cancelled");
			return;
		}
		//Will proceed to delete the submission data physical locations, and logical records
		/*
		 *  If a File System location exists ( logically and physically ),  and the file is older than orphanAgeExpiryHours, 
		 *  OR no file system location exists
		 *  then we will logically delete the submission data.
		 *  
		 *  Forage each storage location though,  the location is checked for uniqueness in that another
		 *  non-orphaned submission is not using the same file name. If the location is unique, 
		 *  the physical location is deleted.  
		 */
		if( checkOrphanSubmissionDataDeletionCriteria(submissionData) ){
			for(StorageLocation sl : submissionData.getStorageLocations()){
				if(checkStorageLocationOKToDelete(sl, sd.getId())){
					deletePhysicalStorageLocation(sl);
				}
			}
			submissionDataDao.delete(submissionData);
		}else{
			logger.info("submissionData" + submissionData.getId() + " for file " + sd.getFileName() + " is not old enough to remove");
		}
	}
	
	private boolean checkOrphanSubmissionDataDeletionCriteria(SubmissionData submissionData){
		FileSystemStorageLocation fssl = submissionData.getFileSystemStorageLocation();
		if(fssl == null ){
			return true;
		}
		File fileSystemFileToCheck = new File(fssl.getFspath() + fssl.getFileName());
		if(fileSystemFileToCheck.exists() && fileSystemFileToCheck.canRead() ){
			return SharedFileUtils.isFileOlderThan(fileSystemFileToCheck, orphanAgeExpiryHours);
		}
		//The file doesn't exist or we can't read it anyway - process SubmissionData for deletion
		return true;
	}
	
	private boolean checkStorageLocationOKToDelete(StorageLocation storageLocation, Long submissionDataId){
		List<SubmissionData> duplicateList = null;
		if(storageLocation instanceof FileSystemStorageLocation){
			duplicateList = submissionDataDao.getSubmissionDataWithSamePhysicalFileSystemStorageLocation((FileSystemStorageLocation) storageLocation, submissionDataId);
		}else if(storageLocation instanceof ObjectStoreLocation){
			duplicateList = submissionDataDao.getSubmissionDataWithSamePhysicalObjectStoreLocation((ObjectStoreLocation) storageLocation, submissionDataId);
		}
		if(duplicateList == null || duplicateList.size() == 0){
			return true;
		}
		//If we are here, duplicates exist.  If the duplicate is also an orphan then we consider
		//the storage location unique, i.e. OK to delete
		for(SubmissionData duplicate : duplicateList){
			if(duplicate.getId() != submissionDataId && duplicate.getSubmission() != null ){ //Can't be too careful
				return false;
			}
		}
		return true;
	}
	
	private void deletePhysicalStorageLocation(StorageLocation sl){
		if(sl instanceof FileSystemStorageLocation){
			FileSystemStorageLocation fssl = (FileSystemStorageLocation) sl;
			logger.debug("Attempting to delete " + fssl.getFspath() + fssl.getObjectName());
			File fileToDelete = new File(fssl.getFspath() + fssl.getFileName());
			if(fileToDelete != null && fileToDelete.exists() && fileToDelete.canWrite()){
				logger.info("Deleting " + fileToDelete.getPath());
				fileToDelete.delete();
			}
		}else if(sl instanceof ObjectStoreLocation){
			ObjectStoreLocation osl = (ObjectStoreLocation) sl;
			deleteFromObjectStore(osl);
		}
	}
}
