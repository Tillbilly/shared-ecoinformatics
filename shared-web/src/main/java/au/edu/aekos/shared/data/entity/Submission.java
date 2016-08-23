package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Entity(name="submission")
@Table(name="SUBMISSION")
public class Submission {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(columnDefinition = "TEXT")
	private String title;
	
	@Column
	private Date submissionDate;
	
	@Column
	private Date lastReviewDate;
	
	//Post review resubmit comments for a reviewer
	@Column(columnDefinition = "TEXT")
	private String commentsForReviewer;
	
	@Enumerated(EnumType.STRING)
	private SubmissionStatus status;
	
	@Enumerated(EnumType.STRING)
	private SubmissionPublicationStatus publicationStatus;
	
	@Column
	private String doi;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CONFIG_ID")
	@ForeignKey(name="questionnaire_config_fk")
	private QuestionnaireConfigEntity questionnaireConfig;
	
	@ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
	@ForeignKey(name="submitter_user_fk")
	private SharedUser submitter;
	
	@Column
	private Boolean contactIsSubmitter = Boolean.FALSE;
	
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE} , fetch=FetchType.LAZY )
	@JoinColumn(name="SUBMISSION_ID", nullable=true)
	@ForeignKey(name="submission_fk")
	private List<SubmissionAnswer> answers = new ArrayList<SubmissionAnswer>();

	@OneToMany(cascade={ CascadeType.PERSIST, CascadeType.MERGE } )
	@JoinColumn(name="SUBMISSION_ID", nullable=true)
	@ForeignKey(name="submission_fk")
	private List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();
	
	@Column(name="DRAFT_FOR_SUB_ID") //After a submission has been saved, can only keep one saved extra version of it.  
	private Long draftForSubmissionId = null;
	
    public SharedUser getSubmitter() {
    	return submitter;
	}
	
	public void setSubmitter(SharedUser submitter) {
		this.submitter = submitter;
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

	public String getTitle() {
		return title;
	}

	@Transient
	public String getListDisplayTitle(){
		if(draftForSubmissionId != null){
			return getTitle().replace("_SAVED", "");
		}
		return getTitle();
	}
	
	public void setTitle(String title) {
		this.title = title;
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
	
	public Map<String,SubmissionAnswer> getAnswersMappedByQuestionId(){
		Map<String,SubmissionAnswer> answerMap = new HashMap<String,SubmissionAnswer>();
		for(SubmissionAnswer subAnswer : answers ){
			answerMap.put(subAnswer.getQuestionId(), subAnswer);
		}
		return answerMap;
	}
	
	//To maintain index order, assumes no response placeholder answers are included.
	public Map<String,List<SubmissionAnswer>> getQuestionIdToQuestionSetAnswerMap(){
		Map<String,List<SubmissionAnswer>> questIdToQuestSetAnswerListMap = new HashMap<String,List<SubmissionAnswer>>();
		//Some weird issues with object proxying -- check the db?? ( answers could appear multiple times?? )
		//Actually, this is a manifest of the Hibernate N+1 problem. Being on an implicit relationship,
		//we can't control the querying of the answers collection, we have already specified lazy fetch mode.
		//Although for use in the MetaInfoExtractor we do eager fetching ..  hmmm.
		//Anyways, here is a work around. Write the SubmissionAnswers to a set.
		Set<SubmissionAnswer> answerSet = new LinkedHashSet<SubmissionAnswer>();
		answerSet.addAll(answers);
		for(SubmissionAnswer sa : answerSet){
			if(ResponseType.MULTIPLE_QUESTION_GROUP.equals( sa.getResponseType()) ){
				for(QuestionSetEntity qse : sa.getQuestionSetList() ){
					Map<String, SubmissionAnswer> qsAnswerMap = qse.getAnswerMap();
					for(Map.Entry<String, SubmissionAnswer> entry : qsAnswerMap.entrySet() ){
						if(! questIdToQuestSetAnswerListMap.containsKey( entry.getKey() )){
							questIdToQuestSetAnswerListMap.put(entry.getKey(), new ArrayList<SubmissionAnswer >() );
						}
						questIdToQuestSetAnswerListMap.get(entry.getKey()).add( entry.getValue() );
					}
				}
			}
		}
		return questIdToQuestSetAnswerListMap;
	}
	
	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public Boolean getContactIsSubmitter() {
		return contactIsSubmitter;
	}

	public void setContactIsSubmitter(Boolean contactIsSubmitter) {
		this.contactIsSubmitter = contactIsSubmitter;
	}

	public SubmissionPublicationStatus getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(SubmissionPublicationStatus publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public String getSubmittingUsername() {
		return submitter.getUsername();
	}

	public Long getDraftForSubmissionId() {
		return draftForSubmissionId;
	}

	public void setDraftForSubmissionId(Long draftForSubmissionId) {
		this.draftForSubmissionId = draftForSubmissionId;
	}

	/**
	 * @return	<code>true</code> if this submission has data files attached, <code>false</code> otherwise
	 */
	public boolean hasDataFiles() {
		return submissionDataList != null && submissionDataList.size() > 0;
	}

	/**
	 * Checks if the submission is ready for public consumption.
	 * 
	 * This check passes if the submission is either PUBLISHED, where it
	 * can be completed viewed, etc or if it is APPROVED, where we can
	 * recognise that it exists but not yet show it. This is because
	 * the DOI minting system needs to see a working URL before it'll mint
	 * a DOI so we have to let the AEKOS portal know that it can acknowledge
	 * that this submission exists but not to the extent of published where
	 * it can show the whole thing.
	 * 
	 * @return	<code>true</code> if this submission is publicly available, <code>false</code> otherwise
	 */
	public boolean isPubliclyAvailable() {
		return status.isApproved();
	}

	/**
	 * Is this submission currently being published?
	 * 
	 * This means that we aren't published yet but we have been
	 * approved for publishing so all we're waiting on it the
	 * automated workflow to complete.
	 * 
	 * @return	<code>true</code> if this submission is currently being published, <code>false</code> otherwise
	 */
	public boolean isBeingPublished() {
		return SubmissionStatus.APPROVED.equals(status);
	}
	
	/**
	 * Is this submission published?
	 * 
	 * @return	<code>true</code> if this submission is already published, <code>false</code> otherwise
	 */
	public boolean isPublished() {
		return SubmissionStatus.PUBLISHED.equals(status);
	}
}
