package au.edu.aekos.shared.web.model;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.AnswerReview;
import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;


//Keep this simple for the prototype!! ( i.e. no 'constructive editing' of answers )
//If the answer is rejected, a comment is mandatory.
//A comment may be made if an answer passes.
//These answer reviews will be bound to a Map of Question ID - to answer review model.
public class AnswerReviewModel {

	private Long answerReviewId;
	
	private AnswerReviewOutcome outcome;
	
	private String comment;
	
	public AnswerReviewModel(AnswerReview answerReview){
		this.answerReviewId = answerReview.getId();
		this.outcome = answerReview.getAnswerReviewOutcome();
		this.comment = answerReview.getComment();
	}

	public AnswerReviewModel(){}
	
	public AnswerReviewModel(Long answerReviewId, AnswerReviewOutcome outcome,
			String comment) {
		this.answerReviewId = answerReviewId;
		this.outcome = outcome;
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public AnswerReviewOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(AnswerReviewOutcome outcome) {
		this.outcome = outcome;
	}

	public Long getAnswerReviewId() {
		return answerReviewId;
	}

	public void setAnswerReviewId(Long answerReviewId) {
		this.answerReviewId = answerReviewId;
	}
	
	public void appendComment(String newComment, String newLineCharacter){
		if(StringUtils.hasLength(comment)){
			comment += newLineCharacter + newComment;
		}else{
			comment = newComment;
		}
		
	}
	
}
