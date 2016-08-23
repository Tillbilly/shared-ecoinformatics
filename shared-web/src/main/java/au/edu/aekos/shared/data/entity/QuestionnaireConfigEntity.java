package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="QUESTIONNAIRE_CONFIG")
public class QuestionnaireConfigEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String configFileName;
	
	@Column
	private String version;
	
	@Column
	private Date importDate;
	
	@Column(columnDefinition="TEXT")
	private String xml;
	
	@Column
	private boolean active = false;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="config")
	private List<QuestionEntity> questionList = new ArrayList<QuestionEntity>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public List<QuestionEntity> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<QuestionEntity> questionList) {
		this.questionList = questionList;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
