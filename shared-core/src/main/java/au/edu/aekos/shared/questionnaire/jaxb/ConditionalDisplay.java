package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains the conditional logic on whether to display the questionnaire item
 * 
 * the list so far - 
 * 
 * display if question id x has also been displayed.
 * display if response to question id x is not null
 * display if response to question id x is null ( i.e. no site file )
 * display if response to question id x is not null and has value y - string comparison
 * 
 * Always check if questionId has been displayed.
 * 
 * This is just a value object tho.
 * 
 * 
 * @author a1042238
 *
 */
@XmlType(namespace="http://shared.aekos.org.au/shared", propOrder={})
public class ConditionalDisplay implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlAttribute(required=true)
	private String questionId = null;
    @XmlAttribute
    private Boolean responseNotNull = Boolean.FALSE;
    @XmlAttribute
    private Boolean responseNull = Boolean.FALSE;
    @XmlAttribute
    private String responseValue = null;
	@XmlTransient
    public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	@XmlTransient
	public Boolean getResponseNotNull() {
		return responseNotNull;
	}
	public void setResponseNotNull(Boolean responseNotNull) {
		this.responseNotNull = responseNotNull;
	}
	@XmlTransient
	public String getResponseValue() {
		return responseValue;
	}
	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}
	@XmlTransient
	public Boolean getResponseNull() {
		return responseNull;
	}
	public void setResponseNull(Boolean responseNull) {
		this.responseNull = responseNull;
	}
	
}
