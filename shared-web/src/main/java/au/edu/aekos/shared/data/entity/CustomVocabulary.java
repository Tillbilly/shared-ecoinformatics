package au.edu.aekos.shared.data.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Index;


@Entity(name="CustomVocabulary")
@Table(name="CUSTOM_VOCABULARIES")
public class CustomVocabulary {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="NAME")
	@Index(name="VOCABULARY_NAME_IX",columnNames={"NAME","ACTIVE"})
	private String vocabularyName;
	
	@Column
	private String value;
	
	@Column(name="PARENT")
	private String parentValue;
	
	@Column(name="DISPLAY", columnDefinition = "TEXT")
	private String displayValue;
	
	@Column(name="DESCRIPTION", columnDefinition = "TEXT")
	private String displayValueDescription;
	
	@Column
	private Date loadDate;
	
	@Column
	private String loadedBy;
	
    @Column(name="ACTIVE")
	private Boolean active = Boolean.TRUE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVocabularyName() {
		return vocabularyName;
	}

	public void setVocabularyName(String vocabularyName) {
		this.vocabularyName = vocabularyName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public Date getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(Date loadDate) {
		this.loadDate = loadDate;
	}

	public String getLoadedBy() {
		return loadedBy;
	}

	public void setLoadedBy(String string) {
		this.loadedBy = string;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDisplayValueDescription() {
		return displayValueDescription;
	}

	public void setDisplayValueDescription(String displayValueDescription) {
		this.displayValueDescription = displayValueDescription;
	}

	public String getParentValue() {
		return parentValue;
	}

	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}

	@Override
	public String toString() {
		return "CustomVocabulary [vocabularyName=" + vocabularyName + ", value=" + value + ", parentValue="
				+ parentValue + ", displayValue=" + displayValue + ", displayValueDescription="
				+ displayValueDescription + ", loadDate=" + loadDate + ", loadedBy=" + loadedBy + ", active=" + active
				+ "]";
	}
	
	
}
