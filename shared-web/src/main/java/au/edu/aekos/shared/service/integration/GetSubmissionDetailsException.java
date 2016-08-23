package au.edu.aekos.shared.service.integration;

public class GetSubmissionDetailsException extends Exception {

	private static final long serialVersionUID = 1L;

	public GetSubmissionDetailsException(String msg, Exception e) {
		super(msg, e);
	}

}
