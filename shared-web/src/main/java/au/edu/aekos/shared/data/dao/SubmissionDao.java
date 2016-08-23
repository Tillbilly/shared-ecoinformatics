package au.edu.aekos.shared.data.dao;

import java.util.Date;
import java.util.List;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionStatus;


public interface SubmissionDao extends HibernateDao<Submission, Long> {
	
	List<Submission> findSubmissionsByUsername(String username);
	
	List<Submission> retrieveSubmissionsForReview(String usernameToExclude);
	
	Submission findSubmissionByIdEagerAnswer(Long submissionId);
	
	SubmissionData retrieveSubmissionDataForSubmission(Long submissionId, Long submissionDataId);
	
	void updateSubmissionStatus(Long submissionId, SubmissionStatus submissionStatus);
	
	void updateSubmissionStatusAndLastReviewDate(Long submissionId, SubmissionStatus status, Date reviewDate);
	
	
	/**
	 * Used for validating a unique submission title - per user.
	 * @param submissionTitle
	 * @param username
	 * @return
	 */
	Submission retrieveSubmissionByTitleAndUsername(String submissionTitle, String username);

	List<Submission> getListOfPublicationFailedSubmissions();
	
	List<Submission> getSubmissionsWithVocabResponse(String response);
	
	/**
	 * Written to serve the link submission function, but can be re-used by other functions
	 * @param username The submitter username
	 * @param statusToInclude Submission Status's of submissions to include.  Defaults to include everything.
	 * @param statusToExclude Exclude's submissions of the given statii ( as opposed to statuses )
	 * @param submissionIdsToExclude Excludes submissions with Ids in the given list. 
	 * @return
	 */
	List<Submission> getListOfSubmissionsForUserName(String username, List<SubmissionStatus> statusToInclude, List<SubmissionStatus> statusToExclude, List<Long> submissionIdsToExclude);

	List<Submission> getListOfSubmissionsForUsers(List<String> users, List<SubmissionStatus> statusToInclude, List<SubmissionStatus> statusToExclude, List<Long> submissionIdsToExclude);
	
	List<Long> findExistingSavedSubmissionIdForSubmission(Long submissionId);
	
	List<Long> getListOfSubmissionIds(SubmissionStatus status);
	
	/**
	 * All submissions that aren't deleted or removed
	 * @return
	 */
	List<Submission> getAllUndeletedSubmissions();

	
	/**
	 * This is used by the system scheduled task that deletes orphaned files ( files from deleted submissions ) 
	 * @return
	 */
	List<Submission> findDeletedSubmissionsWithFileSystemStorageLocations();

}
