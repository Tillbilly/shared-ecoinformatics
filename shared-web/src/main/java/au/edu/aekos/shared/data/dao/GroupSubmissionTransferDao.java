package au.edu.aekos.shared.data.dao;

import au.edu.aekos.shared.data.entity.groupadmin.GroupSubmissionTransfer;

public interface GroupSubmissionTransferDao extends HibernateDao<GroupSubmissionTransfer, Long> {

	/**
	 * Successively saving a transferred submission will require the transfer 
	 * records to be updated to reflect the current version of the saved submission
	 * 
	 * @param originalId
	 * @param newId
	 */
	void migrateTansferRecordsToSubmissionId(Long originalId, Long newId);
	
	void deleteTransferRecordsForSubmission(Long submissionId);
	
}
