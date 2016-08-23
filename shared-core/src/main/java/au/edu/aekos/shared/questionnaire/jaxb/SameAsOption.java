package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared", propOrder={})
public class SameAsOption implements Serializable {
	private static final long serialVersionUID = -4789879619993798579L;
	@XmlAttribute(required=true)
	private String questionId;
	@XmlAttribute(required=true)
	private String text;
	@XmlTransient
	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	@XmlTransient
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
