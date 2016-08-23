package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Entity
@Table(name="RU_ANSWER")
public class ReusableAnswer {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private String questionId;
	
	@Column(columnDefinition = "TEXT")
	private String response;
	
	@Column(columnDefinition = "TEXT")
	private String displayResponse;
	
	@Column
	private String suggestedResponse;
	
	@Enumerated(EnumType.STRING)
	private ResponseType responseType;
	
	@OneToOne(optional=true, cascade={CascadeType.ALL})
	@JoinColumn(name="IMAGE_ID")
	@ForeignKey(name="reusable_answer_fk")
	private ReusableAnswerImage answerImage;
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="multianswer_id")
	@OrderColumn(name="multiorder")
	@ForeignKey(name="multianswer_fk")
	private List<ReusableAnswer> multiselectAnswerList = new ArrayList<ReusableAnswer>();
	
	public ReusableAnswer() {
		super();
	}
	
	public ReusableAnswer(SubmissionAnswer submissionAnswer) {
		this.questionId = submissionAnswer.getQuestionId();
		this.response = submissionAnswer.getResponse();
		this.responseType = submissionAnswer.getResponseType();
		this.suggestedResponse = submissionAnswer.getSuggestedResponse();
		
		if(submissionAnswer.getAnswerImage() != null ){
			this.answerImage = new ReusableAnswerImage(submissionAnswer.getAnswerImage(), this );
		}
		
		if(submissionAnswer.getMultiselectAnswerList() != null && submissionAnswer.getMultiselectAnswerList().size()  > 0){
			for(SubmissionAnswer msAns : submissionAnswer.getMultiselectAnswerList() ){
				this.multiselectAnswerList.add(new ReusableAnswer( msAns ) );
			}
		}
	}	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getSuggestedResponse() {
		return suggestedResponse;
	}

	public void setSuggestedResponse(String suggestedResponse) {
		this.suggestedResponse = suggestedResponse;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public ReusableAnswerImage getAnswerImage() {
		return answerImage;
	}

	public void setAnswerImage(ReusableAnswerImage answerImage) {
		this.answerImage = answerImage;
	}

	public List<ReusableAnswer> getMultiselectAnswerList() {
		return multiselectAnswerList;
	}

	public void setMultiselectAnswerList(List<ReusableAnswer> multiselectAnswerList) {
		this.multiselectAnswerList = multiselectAnswerList;
	}

	public String getDisplayResponse() {
		return displayResponse;
	}

	public void setDisplayResponse(String displayResponse) {
		this.displayResponse = displayResponse;
	}
	
	

}
