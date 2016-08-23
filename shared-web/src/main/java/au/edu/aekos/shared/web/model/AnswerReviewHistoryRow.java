package au.edu.aekos.shared.web.model;

public class AnswerReviewHistoryRow {

	private String contextLine1;
	private String contextLine2;
	private String answerText;
	private String reviewStatus;
	private String comments;
	
	public String getContextLine1() {
		return contextLine1;
	}
	public void setContextLine1(String contextLine1) {
		this.contextLine1 = contextLine1;
	}
	public String getContextLine2() {
		return contextLine2;
	}
	public void setContextLine2(String contextLine2) {
		this.contextLine2 = contextLine2;
	}
	public String getAnswerText() {
		return answerText;
	}
	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	public String getReviewStatus() {
		return reviewStatus;
	}
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
