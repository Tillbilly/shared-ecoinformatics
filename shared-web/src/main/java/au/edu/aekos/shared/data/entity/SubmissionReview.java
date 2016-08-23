package au.edu.aekos.shared.data.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="SUBMISSION_REVIEW")
public class SubmissionReview {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private Date reviewDate;
	
	@Column(columnDefinition = "TEXT")
	private String rejectionMessage;
	
	@Column(columnDefinition = "TEXT")
	private String notes;
	
	@Enumerated(EnumType.STRING)
	private ReviewOutcome reviewOutcome;
	
	@ManyToOne(targetEntity=SharedUser.class)
    @JoinColumn(name="REVIEWER_ID", nullable=false)
	@ForeignKey(name="shared_user_fk")
	private SharedUser reviewer;
	
	@ManyToOne
	@JoinColumn(name="SUBMISSION_ID")
	@ForeignKey(name="submission_fk")
	private Submission submission;
	
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="review" )
	private Set<AnswerReview> answerReviews = new HashSet<AnswerReview>();
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="review" )
	private Set<FileReview> fileReviews = new HashSet<FileReview>();
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRejectionMessage() {
		return rejectionMessage;
	}

	public void setRejectionMessage(String rejectionMessage) {
		this.rejectionMessage = rejectionMessage;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public SharedUser getReviewer() {
		return reviewer;
	}

	public void setReviewer(SharedUser reviewer) {
		this.reviewer = reviewer;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public Set<AnswerReview> getAnswerReviews() {
		return answerReviews;
	}

	public void setAnswerReviews(Set<AnswerReview> answerReviews) {
		this.answerReviews = answerReviews;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public ReviewOutcome getReviewOutcome() {
		return reviewOutcome;
	}

	public void setReviewOutcome(ReviewOutcome reviewOutcome) {
		this.reviewOutcome = reviewOutcome;
	}

	public Set<FileReview> getFileReviews() {
		return fileReviews;
	}

	public void setFileReviews(Set<FileReview> fileReviews) {
		this.fileReviews = fileReviews;
	}
}
