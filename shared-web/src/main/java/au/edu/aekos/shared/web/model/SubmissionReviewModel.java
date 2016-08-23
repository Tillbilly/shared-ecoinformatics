package au.edu.aekos.shared.web.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.ReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;

public class SubmissionReviewModel {
	
	private String submissionTitle;
	
	private Long submissionReviewId;
	
	private Long submissionId;

	private String notes;
	
	private String rejectionMessage;
	
	private ReviewOutcome reviewOutcome;
	
	private Boolean outrightReject = Boolean.FALSE;
	
	private Map<String, AnswerReviewModel> answerReviews = new HashMap<String,AnswerReviewModel>();
	
	private Map<Long, SubmittedFileReviewModel> fileReviews = new HashMap<Long, SubmittedFileReviewModel>();
	
	private Boolean hasRejectedAnswers = Boolean.FALSE;
	
	private Boolean hasRejectedFiles = Boolean.FALSE;
	
	private Date reviewDate ;
	
	private String reviewer;
	
	private String submissionStatus;
	
	private Boolean peerReview = Boolean.FALSE;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getRejectionMessage() {
		return rejectionMessage;
	}

	public void setRejectionMessage(String rejectionMessage) {
		this.rejectionMessage = rejectionMessage;
	}

	public ReviewOutcome getReviewOutcome() {
		return reviewOutcome;
	}

	public void setReviewOutcome(ReviewOutcome reviewOutcome) {
		this.reviewOutcome = reviewOutcome;
	}

	public Map<String, AnswerReviewModel> getAnswerReviews() {
		return answerReviews;
	}

	public void setAnswerReviews(Map<String, AnswerReviewModel> answerReviews) {
		this.answerReviews = answerReviews;
	}

	public Map<Long, SubmittedFileReviewModel> getFileReviews() {
		return fileReviews;
	}

	public void setFileReviews(Map<Long, SubmittedFileReviewModel> fileReviews) {
		this.fileReviews = fileReviews;
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	
	
	public boolean getContainsRejection(){
		if(outrightReject){
			return true;
		}
		
		for(AnswerReviewModel reviewModel : answerReviews.values()){
			if(AnswerReviewOutcome.REJECT.equals(reviewModel.getOutcome())){
				return true;
			}
		}
		for(SubmittedFileReviewModel fileReview : fileReviews.values()){
			if(SubmissionFileReviewOutcome.REJECT.equals(fileReview.getReviewOutcome())){
				return true;
			}
		}
		
		return false;
	}

	public Boolean getOutrightReject() {
		return outrightReject;
	}

	public void setOutrightReject(Boolean outrightReject) {
		this.outrightReject = outrightReject;
	}
	
	public Long getSubmissionReviewId() {
		return submissionReviewId;
	}

	public void setSubmissionReviewId(Long submissionReviewId) {
		this.submissionReviewId = submissionReviewId;
	}

	public Boolean getHasRejectedAnswers() {
		return hasRejectedAnswers;
	}

	public void setHasRejectedAnswers(Boolean hasRejectedAnswers) {
		this.hasRejectedAnswers = hasRejectedAnswers;
	}

	public Boolean getHasRejectedFiles() {
		return hasRejectedFiles;
	}

	public void setHasRejectedFiles(Boolean hasRejectedFiles) {
		this.hasRejectedFiles = hasRejectedFiles;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public String getSubmissionStatus() {
		return submissionStatus;
	}

	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
	}

	public String getSubmissionTitle() {
		return submissionTitle;
	}

	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}

	public Boolean getPeerReview() {
		return peerReview;
	}

	public void setPeerReview(Boolean peerReview) {
		this.peerReview = peerReview;
	}
}
