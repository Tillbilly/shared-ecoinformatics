package au.edu.aekos.shared.web.model;

public class ResubmitAnswerIndicator {

	private String questionId;
	private Boolean hasAnswerChanged = Boolean.FALSE;
	private Boolean wasRejected = Boolean.FALSE;
	
	public ResubmitAnswerIndicator(String questionId, Boolean hasAnswerChanged,
			Boolean wasRejected) {
		super();
		this.questionId = questionId;
		this.hasAnswerChanged = hasAnswerChanged;
		this.wasRejected = wasRejected;
	}
	public ResubmitAnswerIndicator() {
		super();
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public Boolean getHasAnswerChanged() {
		return hasAnswerChanged;
	}
	public void setHasAnswerChanged(Boolean hasAnswerChanged) {
		this.hasAnswerChanged = hasAnswerChanged;
	}
	public Boolean getWasRejected() {
		return wasRejected;
	}
	public void setWasRejected(Boolean wasRejected) {
		this.wasRejected = wasRejected;
	}
}
