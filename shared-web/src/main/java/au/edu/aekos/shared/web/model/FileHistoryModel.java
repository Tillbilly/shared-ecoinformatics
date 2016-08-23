package au.edu.aekos.shared.web.model;

import java.util.Date;

import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionStatus;

public class FileHistoryModel {
	
	private Long fileId;
	private String fileName;
	private String description;
	private String submitter;
	private SubmissionFileReviewOutcome reviewOutcome;
	private SubmissionStatus submissionStatus;
	private String reviewComments;
	private Date reviewDate ;
	private Date submittedDate;
	private Boolean isDeleted = Boolean.FALSE;
	
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public SubmissionFileReviewOutcome getReviewOutcome() {
		return reviewOutcome;
	}
	public void setReviewOutcome(SubmissionFileReviewOutcome reviewOutcome) {
		this.reviewOutcome = reviewOutcome;
	}
	public String getReviewComments() {
		return reviewComments;
	}
	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}
	public Date getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	public Date getSubmittedDate() {
		return submittedDate;
	}
	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public SubmissionStatus getSubmissionStatus() {
		return submissionStatus;
	}
	public void setSubmissionStatus(SubmissionStatus submissionStatus) {
		this.submissionStatus = submissionStatus;
	}
	

}
