package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlType(namespace="http://shared.aekos.org.au/shared", propOrder={})
public class DefaultVocabulary implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 1L;
	
	@XmlElements({ @XmlElement(name = "tag", type = DefaultVocabularyTag .class, namespace="http://shared.aekos.org.au/shared") })
	private List<DefaultVocabularyTag> listEntries = new ArrayList<DefaultVocabularyTag>();

	@XmlTransient
	public List<DefaultVocabularyTag> getListEntries() {
		return listEntries;
	}

	public void setListEntries(List<DefaultVocabularyTag> listEntries) {
		this.listEntries = listEntries;
	}
	
}
