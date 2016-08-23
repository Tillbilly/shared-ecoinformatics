package au.edu.aekos.shared.service.submission;

import java.util.List;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
/**
 * Web tier service class handling delegation to business tier services, 
 * and mapping to presentation model objects.
 * 
 * @author Ben Till
 *
 */
public interface SubmissionModelService {

	SubmissionModel getSubmission(Long submissionId) throws Exception;

	/**
	 * Extracts submission data to a format that's friendly for displaying.
	 * 
	 * @param dq	The {@link DisplayQuestionnaire} to extract the submission data from
	 * @return		A List containing one element for each submission data file found in the provided DisplayQuestionnaire
	 */
	List<SubmissionDataFileModel> mapSubmittedDataInfoToSubmissionModel(DisplayQuestionnaire dq);
}
