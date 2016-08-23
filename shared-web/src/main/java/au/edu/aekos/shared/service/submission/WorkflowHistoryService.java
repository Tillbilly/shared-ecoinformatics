package au.edu.aekos.shared.service.submission;

import java.util.Map;

import au.edu.aekos.shared.web.model.ResubmitAnswerIndicator;
import au.edu.aekos.shared.web.model.ResubmitDataIndicator;
import au.edu.aekos.shared.web.model.WorkflowHistoryModel;

public interface WorkflowHistoryService {

	void getSubmissionWorkflowHistoryForQuestion(Long submissionId, String questionId );
	
	/**
	 * This excludes the current submission / review 
	 * - only processes records from the history table
	 * @param submissionId
	 */
	WorkflowHistoryModel getSubmissionWorkflowHistory(Long submissionId);
	
	void buildIndicatorMaps( Long submissionId,  Map<String, ResubmitAnswerIndicator> answerIndicatorMap, Map<Long, ResubmitDataIndicator> dataIndicatorMap);
	
}
