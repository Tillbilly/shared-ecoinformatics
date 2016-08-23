package au.edu.aekos.shared.doiclient.jaxb.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.util.StringUtils;

@XmlRootElement(name="response")
public class Response {

	public static final String SUCCESS_RESPONSE_TYPE = "success";
	public static final String FAILURE_RESPONSE_TYPE = "failure";
	
	@XmlAttribute
	private String type;
	
	@XmlElement
	private String responsecode;
	
	@XmlElement
	private String message;
	
	@XmlElement
	private String doi;
	
	@XmlElement
	private String url;
	
	@XmlElement
	private String app_id;
	
	@XmlElement
	private String verbosemessage;

	@XmlTransient
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@XmlTransient
	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}
	@XmlTransient
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@XmlTransient
	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}
	@XmlTransient
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	@XmlTransient
	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	@XmlTransient
	public String getVerbosemessage() {
		return verbosemessage;
	}

	public void setVerbosemessage(String verbosemessage) {
		this.verbosemessage = verbosemessage;
	}
	
	@XmlTransient
	public boolean isSuccess(){
		return (SUCCESS_RESPONSE_TYPE.equals(type) && StringUtils.hasLength(doi));
	}
	
}
