package au.edu.aekos.shared.web.model;

import java.util.Date;

import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionStatus;


public class QuestionHistoryModel {

	private String questionId;
	private String response;
	private AnswerReviewOutcome answerReviewOutcome;
	private Long answerId;
	private String reviewComments;
	private SubmissionStatus submissionStatus;
	private Date submissionDate;
	private Date reviewDate;
	private String submittedBy;
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public AnswerReviewOutcome getAnswerReviewOutcome() {
		return answerReviewOutcome;
	}
	public void setAnswerReviewOutcome(AnswerReviewOutcome answerReviewOutcome) {
		this.answerReviewOutcome = answerReviewOutcome;
	}
	public String getReviewComments() {
		return reviewComments;
	}
	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}
	public SubmissionStatus getSubmissionStatus() {
		return submissionStatus;
	}
	public void setSubmissionStatus(SubmissionStatus submissionStatus) {
		this.submissionStatus = submissionStatus;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public String getSubmittedBy() {
		return submittedBy;
	}
	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}
	public Long getAnswerId() {
		return answerId;
	}
	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}
	public Date getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	
	
}
