package au.edu.aekos.shared.service.submission;


public interface SubmissionCloneService {

	/**
	 * Creates a clone submission, in the SAVED state
	 * 
	 * Cloning does not include answers for Data, species or spatial questions.
	 * 
	 * Optionally migrate to the latest questionnaire via metatag mapping, if migrate = true.
	 * 
	 * @param submissionIdToClone
	 * @param cloneSubmitter
	 * @param cloneTitle
	 * @param migrate
	 * @return
	 * @throws Exception 
	 */
	Long cloneSubmission(Long submissionIdToClone, String cloneSubmitter, String cloneTitle, Boolean migrate) throws Exception;
	
}
