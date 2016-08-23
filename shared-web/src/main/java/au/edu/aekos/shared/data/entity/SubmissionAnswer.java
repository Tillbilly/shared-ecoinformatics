package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Entity
@Table(name="ANSWER",
       uniqueConstraints = {@UniqueConstraint(columnNames = { "questionid", "submission_id" })} )
public class SubmissionAnswer {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	@Index(name="ANSWER_QUEST_ID_IX")
	private String questionId;
	
	@Column(columnDefinition = "TEXT") 
	private String response;
	
	//Used for controlled vocab display answers SHD-103
	@Column(columnDefinition = "TEXT")
	private String displayResponse;
	
	@Column
	private String suggestedResponse;
	
	@Enumerated(EnumType.STRING)
	private ResponseType responseType;
	
	@Column
	private String metaTag;
	
	@OneToOne(optional=true, cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@JoinColumn(name="IMAGE_ID")
	@ForeignKey(name="submission_answer_fk")
	private AnswerImage answerImage;
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="multianswer_id")
	@OrderColumn(name="multiorder")
	@ForeignKey(name="multianswer_fk")
	private List<SubmissionAnswer> multiselectAnswerList = new ArrayList<SubmissionAnswer>();
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="PARENT_ANSWER_ID")
	@OrderColumn(name="set_order")
	@ForeignKey(name="parent_answer_fk")
	private List<QuestionSetEntity> questionSetList = new ArrayList<QuestionSetEntity>();
	
	//Inverse relation to facilitate querying in specific question IDs 
	//TODO perhaps unnecessary  -- actually its used for the image search
	@ManyToOne(fetch=FetchType.LAZY)
	private Submission submission;
	
	public AnswerImage getAnswerImage() {
		return answerImage;
	}

	public void setAnswerImage(AnswerImage answerImage) {
		this.answerImage = answerImage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getSuggestedResponse() {
		return suggestedResponse;
	}

	public void setSuggestedResponse(String suggestedResponse) {
		this.suggestedResponse = suggestedResponse;
	}

	public List<SubmissionAnswer> getMultiselectAnswerList() {
		return multiselectAnswerList;
	}

	public void setMultiselectAnswerList(List<SubmissionAnswer> multiselectAnswerList) {
		this.multiselectAnswerList = multiselectAnswerList;
	}

	public boolean hasResponse(){
		if(ResponseType.getIsMultiselect(responseType)){
			if(multiselectAnswerList == null || multiselectAnswerList.size() == 0 ||
				! StringUtils.hasLength( multiselectAnswerList.get(0).getResponse() ) ){
				return false;
			}
			return true;
			
		}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(responseType)){
			if(questionSetList == null || questionSetList.size() == 0){
				return false;
			}
			for( QuestionSetEntity qse : questionSetList ){
				for(SubmissionAnswer sa : qse.getAnswerMap().values() ){
					if(sa.hasResponse()){
						return true;
					}
				}
			}
			return false;
		}
		else{
			return StringUtils.hasLength(response);
		}
	}
	
	@Override
	public String toString() {
		switch (responseType) {
		case TEXT:
			return questionId + "=" + response;
		default:
			return questionId + "[" + responseType + "]";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((answerImage == null) ? 0 : answerImage.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((multiselectAnswerList == null) ? 0 : multiselectAnswerList
						.hashCode());
		result = prime * result
				+ ((questionId == null) ? 0 : questionId.hashCode());
		result = prime * result
				+ ((response == null) ? 0 : response.hashCode());
		result = prime * result
				+ ((responseType == null) ? 0 : responseType.hashCode());
		result = prime
				* result
				+ ((suggestedResponse == null) ? 0 : suggestedResponse
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubmissionAnswer other = (SubmissionAnswer) obj;
		if (answerImage == null) {
			if (other.answerImage != null)
				return false;
		} else if (!answerImage.equals(other.answerImage))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (multiselectAnswerList == null) {
			if (other.multiselectAnswerList != null)
				return false;
		} else if (!multiselectAnswerList.equals(other.multiselectAnswerList))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		if (responseType != other.responseType)
			return false;
		if (suggestedResponse == null) {
			if (other.suggestedResponse != null)
				return false;
		} else if (!suggestedResponse.equals(other.suggestedResponse))
			return false;
		return true;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public String getDisplayResponse() {
		return displayResponse;
	}

	public void setDisplayResponse(String displayResponse) {
		this.displayResponse = displayResponse;
	}

	public List<QuestionSetEntity> getQuestionSetList() {
		return questionSetList;
	}

	public void setQuestionSetList(List<QuestionSetEntity> questionSetList) {
		this.questionSetList = questionSetList;
	}

	public String getMetaTag() {
		return metaTag;
	}

	public void setMetaTag(String metaTag) {
		this.metaTag = metaTag;
	}

}
