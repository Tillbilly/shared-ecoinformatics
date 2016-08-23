package au.edu.aekos.shared.service.submission;

import java.io.File;
import java.util.List;
import java.util.Set;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.service.file.FileSystemDataIntegrityException;
import au.edu.aekos.shared.service.s3.ObjectStoreServiceException;

public interface SubmissionDataService {

	SubmissionData getHydratedSubmissionData(Long submissionDataId);
	
	/**
	 * If a submissionData does not have an objectStoreLocation entry, one will be created.
	 * @param submission
	 */
	void writeSubmissionDataToObjectStore( Submission submission ) throws ObjectStoreServiceException;
	
	/**
	 * Currently switched OFF!! will need to be reviewed.
	 * 
	 * what this means is if a submission is saved many times there will be 
	 * many copies of the data on the file system that will need to be cleaned up.
	 * 
	 * Deletes all objects at Storage Locations in the SubmissionDataList
	 * We may want to keep SubmissionData so be careful.
	 * @param submissionDataList
	 */
	void deleteSubmissionData(List<SubmissionData> submissionDataList);
	
	/**
	 * performed by the TaskExecutor
	 */
	void asyncDeleteNonPersistedSubmissionData(List<SubmissionData> submissionDataList);
	/**
	 * All objects in the set will be deleted using the provided object store info.
	 * @param objectsToDelete
	 */
	void deleteFromObjectStore(Set<ObjectStoreLocation> objectsToDelete);
	
	void deleteFromObjectStore(ObjectStoreLocation objectStoreLocation);
	
	File retrieveFileToLocal(SubmissionData submissionData);
	
	void systemCloneSubmissionDataToObjectStore(Long submissionDataId, List<String> objectStoreNames) throws ObjectStoreServiceException;
	
	SubmissionData cloneToNonPersistedFileSystemData(SubmissionData sd) throws FileSystemDataIntegrityException;
	
	/**
	 * Will delete the underlying data from the storage locations,  and delete the storage location records.
	 * @param sd
	 */
	void systemDeleteSubmissionData(SubmissionData sd) ;
	
	void systemDeleteLogicalStorageLocations(SubmissionData sd) ;
	
	void systemDeleteOrphanSubmissionData(SubmissionData sd);
	
	
	
}
