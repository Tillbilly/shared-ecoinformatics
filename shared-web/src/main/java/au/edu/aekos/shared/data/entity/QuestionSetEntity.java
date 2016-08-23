package au.edu.aekos.shared.data.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="QUESTION_SET")
public class QuestionSetEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="MULTI_GROUP_ID")
	private String multipleQuestionGroupId;
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="QUESTION_SET_ID")
	@MapKey(name="questionId")
	@ForeignKey(name="question_set_fk")
	private Map<String, SubmissionAnswer> answerMap = new HashMap<String,SubmissionAnswer>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMultipleQuestionGroupId() {
		return multipleQuestionGroupId;
	}

	public void setMultipleQuestionGroupId(String multipleQuestionGroupId) {
		this.multipleQuestionGroupId = multipleQuestionGroupId;
	}

	public Map<String, SubmissionAnswer> getAnswerMap() {
		return answerMap;
	}

	public void setAnswerMap(Map<String, SubmissionAnswer> answerMap) {
		this.answerMap = answerMap;
	}
	
	
	
}