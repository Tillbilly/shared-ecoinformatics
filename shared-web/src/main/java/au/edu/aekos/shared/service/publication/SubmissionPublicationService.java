package au.edu.aekos.shared.service.publication;

import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.PublicationLog;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionPublicationStatus;

public interface SubmissionPublicationService {
	
	
	SubmissionPublicationStatus publishSubmission(Long submissionId);
	/**
	 * @return 
	 * 
	 * 
	 */
	SubmissionPublicationStatus publishSubmission(Submission submission);
	
	List<Submission> getListOfPublicationFailedSubmissions();
	
	Map<Long, PublicationLog> getLatestPublicationLogEntriesForSubmissionList(List<Submission> submissionList);
	
	Map<Long, PublicationLog> getLatestPublicationLogEntriesForSubmissions(List<Long> submissionIdList);
	
	void reindexSubmissions();
	
}
