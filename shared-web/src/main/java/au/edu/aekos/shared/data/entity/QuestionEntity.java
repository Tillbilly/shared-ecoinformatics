package au.edu.aekos.shared.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;


/**
 * I`m not sure how the lords of data feel about normality, when data is 
 * duplicated in an xml String stored as a column
 * 
 * in any case, to facilitate querying the submission wholly from the db,
 * Will persist a 'question' entity and relate it to the QuestionnaireConfig.
 * 
 * A submission answer will need to reference the question also.
 * 
 * @author a1042238
 *
 */
@Entity
@Table(name="QUESTION")
public class QuestionEntity {
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String questionId;
	@Column(columnDefinition="TEXT")
	private String questionText;
	@Enumerated(EnumType.STRING)
	private ResponseType responseType;
	@Column
	private String traitName;
	
	@ManyToOne
	@JoinColumn(name="CONFIG_ID", nullable=false)
	@ForeignKey(name="config_fk")
	private QuestionnaireConfigEntity config;
	
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
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public ResponseType getResponseType() {
		return responseType;
	}
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	public String getTraitName() {
		return traitName;
	}
	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}
	public QuestionnaireConfigEntity getConfig() {
		return config;
	}
	public void setConfig(QuestionnaireConfigEntity config) {
		this.config = config;
	}

}
