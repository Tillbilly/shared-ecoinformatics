package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionHistory;
import au.edu.aekos.shared.data.entity.SubmissionReview;


public interface SubmissionHistoryDao extends HibernateDao<SubmissionHistory, Long>{

	SubmissionHistory createSubmissionHistory(Long submissionId);
	
	SubmissionHistory createSubmissionHistory(Submission submission);
	
	SubmissionHistory createSubmissionHistory(Submission submission, SubmissionReview submissionReview);
	
	List<SubmissionHistory> retrieveSubmissionHistoryForSubmission(Long submissionId);
	
	/**
	 * Returns List of submission data from the given list that is NOT part of a SubmissionHistory record.
	 * @param orphanedSubmissionData
	 * @return
	 */
	List<SubmissionData> determineOrphanedSubmissionData(List<SubmissionData> orphanedSubmissionDataList);
}
