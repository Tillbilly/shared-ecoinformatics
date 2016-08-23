package au.edu.aekos.shared.doiclient.service;

public class DoiClientServiceException extends Throwable {

	private static final long serialVersionUID = -696691253680400120L;

	private String xmlServerResponse;
	
	public DoiClientServiceException() {
		super();
	}

	public DoiClientServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public DoiClientServiceException(String message) {
		super(message);
	}

	public DoiClientServiceException(String message, String xmlResponse) {
		super(message);
		this.xmlServerResponse = xmlResponse;
	}
	
	public DoiClientServiceException(Throwable cause) {
		super(cause);
	}

	public String getXmlServerResponse() {
		return xmlServerResponse;
	}

	public void setXmlServerResponse(String xmlServerResponse) {
		this.xmlServerResponse = xmlServerResponse;
	}
}
