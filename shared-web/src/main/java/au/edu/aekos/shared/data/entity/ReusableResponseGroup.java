package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="RU_RESPONSE_GROUP")
public class ReusableResponseGroup {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private String groupId;
	
	@Column
	private String name;
	
	@ManyToOne
	@ForeignKey(name="quest_config_fk")
	private QuestionnaireConfigEntity questionnaireConfig;
	
	@ManyToOne
	@ForeignKey(name="shared_user_fk")
	private SharedUser sharedUser;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="RU_RESPONSE_GROUP_ID", nullable=true)
	@ForeignKey(name="ru_response_group_fk")
	private List<ReusableAnswer> answers = new ArrayList<ReusableAnswer>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public QuestionnaireConfigEntity getQuestionnaireConfig() {
		return questionnaireConfig;
	}

	public void setQuestionnaireConfig(QuestionnaireConfigEntity questionnaireConfig) {
		this.questionnaireConfig = questionnaireConfig;
	}

	public SharedUser getSharedUser() {
		return sharedUser;
	}

	public void setSharedUser(SharedUser sharedUser) {
		this.sharedUser = sharedUser;
	}

	public List<ReusableAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<ReusableAnswer> answers) {
		this.answers = answers;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, ReusableAnswer> getAnswersMappedByQuestionId(){
		Map<String,ReusableAnswer> answerMap = new HashMap<String, ReusableAnswer>();
		for(ReusableAnswer answer : answers ){
			answerMap.put(answer.getQuestionId(), answer);
		}
		return answerMap;
	}
	
	
}
