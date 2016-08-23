package au.edu.aekos.shared.web.model;

import java.util.Date;

import au.edu.aekos.shared.data.entity.ReviewOutcome;

public class ReviewHistoryModel {

	private Long reviewId;
	private String notes;
	private Date reviewDate;
	private String reviewer;
	private ReviewOutcome reviewOutcome;
	
	public Long getReviewId() {
		return reviewId;
	}
	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
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
	public ReviewOutcome getReviewOutcome() {
		return reviewOutcome;
	}
	public void setReviewOutcome(ReviewOutcome reviewOutcome) {
		this.reviewOutcome = reviewOutcome;
	}
}
