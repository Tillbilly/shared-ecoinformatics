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
import org.hibernate.annotations.Index;

@Entity
@Table(name="ANSWER_REVIEW")
public class AnswerReview {

	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private AnswerReviewOutcome answerReviewOutcome;
	
	@Column  //Not 3NF, but questionIds should never change - denormalised a bit for efficiency and db legibility
	@Index(name = "ANS_REVIEW_QUEST_ID_IX")
	private String questionId;  
	
	@Column(columnDefinition = "TEXT")
	private String comment;
	
	@Column
	private String editedEntry;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REVIEW_ID")
	@ForeignKey(name="submission_review_fk")
	private SubmissionReview review;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ANSWER_ID")
	@ForeignKey(name="submission_answer_fk")
	private SubmissionAnswer submissionAnswer;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getEditedEntry() {
		return editedEntry;
	}

	public void setEditedEntry(String editedEntry) {
		this.editedEntry = editedEntry;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public SubmissionReview getReview() {
		return review;
	}

	public void setReview(SubmissionReview review) {
		this.review = review;
	}

	public AnswerReviewOutcome getAnswerReviewOutcome() {
		return answerReviewOutcome;
	}

	public void setAnswerReviewOutcome(AnswerReviewOutcome answerReviewOutcome) {
		this.answerReviewOutcome = answerReviewOutcome;
	}

	public SubmissionAnswer getSubmissionAnswer() {
		return submissionAnswer;
	}

	public void setSubmissionAnswer(SubmissionAnswer submissionAnswer) {
		this.submissionAnswer = submissionAnswer;
	}
}
