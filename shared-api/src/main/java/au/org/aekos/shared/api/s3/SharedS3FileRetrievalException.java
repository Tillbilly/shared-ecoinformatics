package au.org.aekos.shared.api.s3;

public class SharedS3FileRetrievalException extends Exception{

	private static final long serialVersionUID = 7371659238360155318L;

	public SharedS3FileRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

	public SharedS3FileRetrievalException(String message) {
		super(message);
	}

}
