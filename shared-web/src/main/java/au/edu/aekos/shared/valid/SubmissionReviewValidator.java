package au.edu.aekos.shared.valid;

import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.web.model.AnswerReviewModel;
import au.edu.aekos.shared.web.model.QuestionModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;
import au.edu.aekos.shared.web.model.SubmittedFileReviewModel;

@Component
public class SubmissionReviewValidator implements Validator {

	@Autowired
	private SubmissionModelService submissionManager;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return SubmissionReviewModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SubmissionReviewModel model = ( SubmissionReviewModel) target;
		SubmissionModel submission = null;
		try {
			submission = submissionManager.getSubmission( model.getSubmissionId() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(submission == null){
			errors.reject("","No submission exists with ID " + model.getSubmissionId());
			return;
		}
		validateSubmissionReview(model, submission, errors);
	}
	
	public void validateSubmissionReview(SubmissionReviewModel reviewModel, SubmissionModel submissionModel, Errors errors){
		if(reviewModel.getOutrightReject()){
			if(!StringUtils.hasLength(reviewModel.getNotes())){
				errors.reject("submission.review.recordrejectionmessage");
				errors.rejectValue("notes", "submission.review.recordrejectionmessage");
			}
			return;
		}
		//For each question with a response, an answerReviewOutcome must be recorded.
		//If the outcome is REJECTED - a comment must be entered
		
		for(QuestionModel question : submissionModel.getAllQuestionModels() ){
			if( question.getHasResponse() ){
				AnswerReviewModel answerReview = reviewModel.getAnswerReviews().get(question.getQuestionId());
				if(answerReview == null){
					errors.reject("","No answerReview exists for question " + question.getQuestionId());
					continue;
				}
				if(answerReview.getOutcome() == null){
					errors.rejectValue("answerReviews["+ question.getQuestionId() +"].outcome", "submission.review.nooutcomerecorded","Pass or Reject must be specified");
				}else if(AnswerReviewOutcome.REJECT.equals( answerReview.getOutcome() ) && ! StringUtils.hasLength(answerReview.getComment() ) ){
					errors.rejectValue("answerReviews["+ question.getQuestionId() +"].comment", "submission.review.rejectmessagerequired", "A rejection reason must be specified");
				}
			}
		}
		
		//Validate file reviews  - similar business rules. 
		if(submissionModel.getFileList() != null && submissionModel.getFileList().size() > 0){
		    for(Entry<Long, SubmittedFileReviewModel> entry :    reviewModel.getFileReviews().entrySet() ){
		    	SubmittedFileReviewModel fileReview = entry.getValue();
		    	if(fileReview.getReviewOutcome() == null){
		    		errors.rejectValue("fileReviews["+ entry.getKey() +"].reviewOutcome", "submission.review.nooutcomerecorded","Pass or Reject must be specified");
		    	}else if(SubmissionFileReviewOutcome.REJECT.equals( fileReview.getReviewOutcome() ) &&
		    			! StringUtils.hasLength(fileReview.getComments()) ){
		    		errors.rejectValue("fileReviews["+ entry.getKey() +"].comments", "submission.review.rejectmessagerequired", "A rejection reason must be specified");
		    	}
		    }
		}
	}
}
