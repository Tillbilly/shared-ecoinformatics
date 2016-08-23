package au.edu.aekos.shared.service.submission;

public interface SubmissionAuthorisationService {
	
	boolean userCanRead(String username, Long submissionId);
	
	boolean userCanWrite(String username, Long submissionId);

}
