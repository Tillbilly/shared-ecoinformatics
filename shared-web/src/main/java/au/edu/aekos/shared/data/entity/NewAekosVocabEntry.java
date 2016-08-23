package au.edu.aekos.shared.data.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Index;


@Entity(name="NewAekosVocabulary")
@Table(name="NEW_AEKOS_VOCABULARY")
public class NewAekosVocabEntry {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="NAME")
	@Index(name="NEW_AEKOS_VOCAB_NAME_IX",columnNames={"NAME","ACTIVE"})
	private String vocabularyName;
	
	@Column
	private String value;
	
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

}
