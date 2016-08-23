package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;


public interface SubmissionDataDao extends HibernateDao<SubmissionData, Long> {
	
	Long addStorageLocation(Long submissionDataId, StorageLocation location);
	
	Long transactionalAddStorageLocation(Long submissionDataId, StorageLocation location);
	
	void removeStorageLocation(Long submissionDataId, Long storageLocationId);
	
	SubmissionData findByIdEagerLocations(Long id);
	
	boolean submissionDataExists(Long id);
	
	List<Long> findSubmissionDataIDsWithoutObjectStoreLocation(boolean excludeNullSubmissions);
	
	List<Long> findSubmissionDataIDsWithoutObjectStoreLocation(String objectStoreName, boolean excludeNullSubmissions);
	
	/**
	 * Finds submission data not attached to a submission.
	 * 
	 * This is an issue because I ran into some issues when folk were accessing the same
	 * submission from multiple sessions and saving multiple times.  
	 * I stopped deleting any files, but successive saved submissions are removed.
	 * Hence we clean up later so as not to lose any files we might need.
	 * 
	 * Its not elegant, but not losing data is more important.
	 * 
	 * UPDATE!!!  Forgot about historical submissions.  
	 * Historical submissions keep reference to submission data -
	 * these shouldn't be deleted!! ( During this process anyway )
	 * 
	 * 
	 * @return
	 */
	List<SubmissionData> findOrphanSubmissionData();
	
	List<SubmissionData> getSubmissionDataWithSamePhysicalFileSystemStorageLocation(FileSystemStorageLocation fssl, 
			                                                                        Long submissionDataIdToExclude);
	
	List<SubmissionData> getSubmissionDataWithSamePhysicalObjectStoreLocation(ObjectStoreLocation osl, 
        Long submissionDataIdToExclude);
	
	/**
	 * Manually delete the Storage Location entities from the session
	 * @param submissionData
	 */
	void deleteStorageLocations(SubmissionData submissionData);

}
