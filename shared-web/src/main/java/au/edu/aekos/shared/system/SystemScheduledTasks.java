package au.edu.aekos.shared.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.service.notification.AdminNotificationService;
import au.edu.aekos.shared.service.s3.ObjectStoreDataManifestService;
import au.edu.aekos.shared.service.s3.ObjectStoreServiceException;
import au.edu.aekos.shared.service.submission.SubmissionDataService;
import au.edu.aekos.shared.web.util.SharedFileUtils;
import au.edu.aekos.shared.web.util.SharedUrlUtils;

@Component("systemScheduledTasks")
public class SystemScheduledTasks {
	
	@Value("${shared.system.runscheduledtasks}")
	private Boolean runScheduledTasks = Boolean.FALSE;
	
	@Value("${shared.system.deleteOrphanFilesAgeHours}")
	private Integer orphanAgeExpiryHours = 120;
	
    private Logger logger = LoggerFactory.getLogger(SystemScheduledTasks.class)	;
	
    @Autowired
    private SubmissionDataDao submissionDataDao;
    
    @Autowired
    private SubmissionDataService submissionDataService;
    
    @Autowired
    private SubmissionDao submissionDao;
	
	@Value("${s3.objectstore.identifier}")
	private String primaryObjectStore;
	
	@Value("${s3.objectstore.backup.identifier}")
	private String backupObjectStore;
	
	@Autowired
	private ObjectStoreDataManifestService dataManifestService;
	
	@Autowired
	private AdminNotificationService adminNotificationService;
    
    /**
     * Queries the submission data to find data that does not have locations
     * in both the primary and the back up object stores.
     * When data is found that is not in the specified object store, it is copied
     * over.
     */
	public void writeSubmissionDataToObjectStores(){
		if(! runScheduledTasks){
			return;
		}
		try{
			logger.info("Beginning scheduled task 'writeSubmissionDataToObjectStores'");
			//First check primary object store is up to date - in case write to primary object store failed during the day
			List<Long> submissionDataIds = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation(primaryObjectStore, true);
			if(submissionDataIds != null && submissionDataIds.size() > 0){
				logger.info(submissionDataIds.size() + " Data objects found not present in primary store " + primaryObjectStore);
				List<String> objectStoreNames = new ArrayList<String>();
				objectStoreNames.add(primaryObjectStore);
				for(Long submissionDataId : submissionDataIds){
					try{
						logger.info("Attempting to write submission data id '" + submissionDataId +"' to primary object store '" + primaryObjectStore +"'");
					    submissionDataService.systemCloneSubmissionDataToObjectStore(submissionDataId, objectStoreNames);
					}catch(ObjectStoreServiceException ex){
						logger.error("Error writing submission data id '" + submissionDataId +"' to primary object store '" + primaryObjectStore +"'", ex);
					}
				}
			}
			//Now to copy to the back up Object Store
			submissionDataIds = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation(backupObjectStore, true);
			if(submissionDataIds != null && submissionDataIds.size() > 0){
				logger.info(submissionDataIds.size() + " Data objects found not present in backup store " + backupObjectStore);
				List<String> objectStoreNames = new ArrayList<String>();
				objectStoreNames.add(backupObjectStore);
				for(Long submissionDataId : submissionDataIds){
					try{
						logger.info("Attempting to write submission data id '" + submissionDataId +"' to back up object store '" + backupObjectStore +"'" );
					    submissionDataService.systemCloneSubmissionDataToObjectStore(submissionDataId, objectStoreNames);
					}catch(ObjectStoreServiceException ex){
						logger.error("Error writing submission data id '" + submissionDataId +"' to back up object store '" + backupObjectStore +"'", ex);
					}
				}
			}
		}catch(Exception e){ //Catch All, we don't want an exception interfereing with normal operations. Log exception, notify admin.
			logger.error("Error occured running s3 system task", e);
			adminNotificationService.notifyAdminByEmail("SHaRED system task execution error notification", "Error occured during system task writeSubmissionDataToObjectStores from host " + SharedUrlUtils.getHostname(), e);
		}
	}
	
	public void writeObjectStoreManifests(){
		if(! runScheduledTasks){
			return;
		}
		try {
			dataManifestService.writeManifestToObjectStore(primaryObjectStore);
		} catch( Exception e) {
			logger.error("Error occured running s3 system task", e);
			adminNotificationService.notifyAdminByEmail("SHaRED system task execution error notification", "Error occured during system task writeManifestToObjectStore " + primaryObjectStore + " from host " + SharedUrlUtils.getHostname(), e);
		} 
		
		try {
			dataManifestService.writeManifestToObjectStore(backupObjectStore);
		} catch( Exception e) {
			logger.error("Error occured running s3 system task", e);
			adminNotificationService.notifyAdminByEmail("SHaRED system task execution error notification", "Error occured during system task writeManifestToObjectStore " + backupObjectStore + " from host " + SharedUrlUtils.getHostname(), e);
		} 
	}
	
	
	/**
	 * Orphaned file system files - from deleted / cancelled / superceded saved submissions
	 * Will only delete files older than the configured hour amount.
	 */
	public void cleanUpOrphanedFileSystemFiles(){
		if(! runScheduledTasks){
			return;
		}
		logger.info("Begin orphaned file clean up");
		List<Submission> deletedSubmissionList = submissionDao.findDeletedSubmissionsWithFileSystemStorageLocations();
		if(deletedSubmissionList != null && deletedSubmissionList.size() > 0){
		for(Submission submission : deletedSubmissionList){
			logger.info("About to check orphan files for submission " + submission.getId() + " " +submission.getTitle());
			if(SubmissionStatus.DELETED.equals(submission.getStatus()) && submission.hasDataFiles()){
				try{
					for(SubmissionData sd : submission.getSubmissionDataList()){
						FileSystemStorageLocation fssl = sd.getFileSystemStorageLocation();
						if(fssl == null || fssl.getFspath() == null || fssl.getFileName() == null){
							continue;
						}
						File fileToDelete = new File(fssl.getFspath() + fssl.getFileName());
						boolean deleteLogicalLocations = false;
						if(fileToDelete.exists() && fileToDelete.canWrite()){
							if(SharedFileUtils.isFileOlderThan(fileToDelete, orphanAgeExpiryHours) ){
								logger.info("deleting physical files");
								submissionDataService.systemDeleteSubmissionData(sd);
								deleteLogicalLocations = true;
							}
						}else{
							logger.warn("File system location specified in submission data id " + sd.getId() + " " + fssl.getFspath() + fssl.getFileName() + " can not be read. Logical location will be removed.");
							deleteLogicalLocations = true;
						}
						if(deleteLogicalLocations){
							logger.info("deleting logical locations from submission data record.");
							submissionDataService.systemDeleteLogicalStorageLocations(sd);
						}
					}
				}catch(Exception e){
					logger.error("Error occured orphan files s3 system task - submission  " + submission.getId() + " " + submission.getTitle(), e);
					adminNotificationService.notifyAdminByEmail("SHaRED system task execution error notification", "Error occured during system task cleanUpOrphanedFileSystemFiles for submission " + submission.getId() + " from host " + SharedUrlUtils.getHostname(), e);
				}
			}
		}
		}else{
			logger.info("No orphaned files found linked to submissions.");
		}
		logger.info("Begin orphaned SubmissionData clean up");
		runOrphanSubmissionDataCleanUp();
	}
	
	private void runOrphanSubmissionDataCleanUp(){
		List<SubmissionData> orphanDataList = submissionDataDao.findOrphanSubmissionData();
		if(orphanDataList == null || orphanDataList.size() == 0){
			logger.info("No orphaned submission data found. Task finishing.");
			return;
		}
		logger.info("Found " + orphanDataList.size() + " possible submission data entries for deletion");
		for(SubmissionData sd : orphanDataList){
			logger.debug("Processing orphan submission data " + sd.getId() + " " + sd.getFileName());
			submissionDataService.systemDeleteOrphanSubmissionData(sd);
		}
	}
	
	
}
