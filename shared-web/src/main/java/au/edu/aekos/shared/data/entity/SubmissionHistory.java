package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="HIST_SUBMISSION")
public class SubmissionHistory {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private Date historyCreateDate;
	
	@Column
	private Date submissionDate;
	
	@Column
	private Date lastReviewDate;
	
	@Column
	private String commentsForReviewer;
	
	@Column
	private String doi;
	
	@ManyToOne
	@JoinColumn(name="submission_id", nullable=false )
	@ForeignKey(name="submission_fk")
	private Submission submission;
	
	@ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
	@ForeignKey(name="submitter_user_fk")
	private SharedUser submitter;
	
	@OneToOne(optional=true)
	@JoinColumn(name="REVIEW_ID")
	@ForeignKey(name="submission_review_fk")
	private SubmissionReview submissionReview;
	
	@Enumerated(EnumType.STRING)
	private SubmissionStatus status;
	
	@ManyToOne
	@JoinColumn(name="CONFIG_ID")
	@ForeignKey(name="questionnaire_config_fk")
	private QuestionnaireConfigEntity questionnaireConfig;
	
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY )
	@ForeignKey(name="hist_submission_fk", inverseName="answer_fk")
	private List<SubmissionAnswer> answers = new ArrayList<SubmissionAnswer>();

	@ManyToMany(cascade=CascadeType.PERSIST)
	@ForeignKey(name="hist_submission_fk",inverseName="submission_data_fk")
	private List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();

	public SubmissionHistory(){}
	
	public SubmissionHistory(Submission submission) {
		this.historyCreateDate = new Date();
		if(submission.getSubmissionDate() != null){
		    this.submissionDate = new Date(submission.getSubmissionDate().getTime() );
		}
		if(submission.getLastReviewDate() != null){
			this.lastReviewDate = new Date(submission.getLastReviewDate().getTime() );
		}
		this.submission = submission;
		this.commentsForReviewer = submission.getCommentsForReviewer();
		this.status = submission.getStatus();
		this.answers.addAll(submission.getAnswers());
		this.submissionDataList.addAll(submission.getSubmissionDataList());
		this.submitter = submission.getSubmitter();
		this.doi = submission.getDoi();
	}
	
	public SubmissionHistory(Submission submission, SubmissionReview submissionReview){
		this.historyCreateDate = new Date();
		if(submission.getSubmissionDate() != null){
		    this.submissionDate = new Date(submission.getSubmissionDate().getTime() );
		}
		if(submission.getLastReviewDate() != null){
			this.lastReviewDate = new Date(submission.getLastReviewDate().getTime() );
		}
		this.submission = submission;
		this.commentsForReviewer = submission.getCommentsForReviewer();
		this.status = submission.getStatus();
		this.answers.addAll(submission.getAnswers());
		this.submissionDataList.addAll(submission.getSubmissionDataList());
		this.submissionReview = submissionReview;
		this.submitter = submission.getSubmitter();
		this.doi = submission.getDoi();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public SubmissionStatus getStatus() {
		return status;
	}

	public void setStatus(SubmissionStatus status) {
		this.status = status;
	}

	public List<SubmissionAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<SubmissionAnswer> answers) {
		this.answers = answers;
	}

	public List<SubmissionData> getSubmissionDataList() {
		return submissionDataList;
	}

	public void setSubmissionDataList(List<SubmissionData> submissionDataList) {
		this.submissionDataList = submissionDataList;
	}

	public Date getLastReviewDate() {
		return lastReviewDate;
	}

	public void setLastReviewDate(Date lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}

	public SubmissionReview getSubmissionReview() {
		return submissionReview;
	}

	public void setSubmissionReview(SubmissionReview submissionReview) {
		this.submissionReview = submissionReview;
	}

	public Date getHistoryCreateDate() {
		return historyCreateDate;
	}

	public void setHistoryCreateDate(Date historyCreateDate) {
		this.historyCreateDate = historyCreateDate;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public QuestionnaireConfigEntity getQuestionnaireConfig() {
		return questionnaireConfig;
	}

	public void setQuestionnaireConfig(QuestionnaireConfigEntity questionnaireConfig) {
		this.questionnaireConfig = questionnaireConfig;
	}

	public String getCommentsForReviewer() {
		return commentsForReviewer;
	}

	public void setCommentsForReviewer(String commentsForReviewer) {
		this.commentsForReviewer = commentsForReviewer;
	}

	public SharedUser getSubmitter() {
		return submitter;
	}

	public void setSubmitter(SharedUser submitter) {
		this.submitter = submitter;
	}
	
	public Map<String,SubmissionAnswer> getAnswersMappedByQuestionId(){
		Map<String,SubmissionAnswer> answerMap = new HashMap<String,SubmissionAnswer>();
		for(SubmissionAnswer subAnswer : answers ){
			answerMap.put(subAnswer.getQuestionId(), subAnswer);
		}
		return answerMap;
	}
	public Map<Long,SubmissionData> getSubmissionDataMappedById(){
		Map<Long,SubmissionData> dataMap = new HashMap<Long,SubmissionData>();
		for(SubmissionData sd : submissionDataList  ){
			dataMap.put(sd.getId(), sd);
		}
		return dataMap;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}
}
