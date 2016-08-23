package au.edu.aekos.shared.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="FILE_REVIEW")
public class FileReview {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name="SUBMISSION_DATA_ID")
	@ForeignKey(name="submission_data_fk")
	private SubmissionData submissionData;
	
	@Enumerated(EnumType.STRING)
	private SubmissionFileReviewOutcome reviewOutcome;
	
	@Column(columnDefinition="TEXT")
	private String comments;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REVIEW_ID")
	@ForeignKey(name="submission_review_fk")
	private SubmissionReview review;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SubmissionData getSubmissionData() {
		return submissionData;
	}

	public void setSubmissionData(SubmissionData submissionData) {
		this.submissionData = submissionData;
	}

	public SubmissionFileReviewOutcome getReviewOutcome() {
		return reviewOutcome;
	}

	public void setReviewOutcome(SubmissionFileReviewOutcome reviewOutcome) {
		this.reviewOutcome = reviewOutcome;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public SubmissionReview getReview() {
		return review;
	}

	public void setReview(SubmissionReview review) {
		this.review = review;
	}
	
}
