package au.edu.aekos.shared.service.submission;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionReview;
import au.edu.aekos.shared.web.model.AnswerReviewHistoryModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionRejectionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;



public interface SubmissionReviewService {

	List<Submission> getSubmissionsAvailableForReview();
	
	List<Submission> getSubmissionsAvailableForPeerReview();
	
	Long saveSubmissionReview(SubmissionReviewModel reviewModel, SharedUser reviewer);
	
	Long saveSubmissionReview(SubmissionReviewModel reviewModel );
	
	Long saveIncompleteReview(SubmissionReviewModel reviewModel );
	
	void processRejection(SubmissionModel submission, SubmissionReviewModel reviewModel, SubmissionRejectionModel rejectionModel , HttpServletRequest request);
	
	SubmissionReviewModel getLastReviewForSubmission(Long submissionId);
	
	SubmissionReviewModel buildSubmissionReviewModel(SubmissionReview submissionReview);
	
	Map<String, ReviewAnswerHistoryIndicator> buildAnswerHistoryMap(Long submissionId);

	AnswerReviewHistoryModel getReviewAnswerHistory(Long submissionId, String questionId);
	
	void repopulateSavedReview(SubmissionReviewModel reviewModel);
	
}
