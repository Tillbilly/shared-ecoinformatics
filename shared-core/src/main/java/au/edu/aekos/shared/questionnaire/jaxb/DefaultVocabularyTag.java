package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared", propOrder={})
public class DefaultVocabularyTag  implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlElement(required=true, namespace="http://shared.aekos.org.au/shared")
	private String value;
    
    @XmlElement(name="desc", namespace="http://shared.aekos.org.au/shared")
    private String description;
    
    @XmlElement(name="display", namespace="http://shared.aekos.org.au/shared")
    private String display;

	public DefaultVocabularyTag() {
		super();
	}
	
	public DefaultVocabularyTag(String value, String description) {
		super();
		this.value = value;
		this.description = description;
	}

	public DefaultVocabularyTag(String value) {
		super();
		this.value = value;
	}
    
    @XmlTransient
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlTransient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@XmlTransient
	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
}
