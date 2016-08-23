package au.edu.aekos.shared.service.doi;

/**
 * TODO other failure info to preserved in the exception
 * @author btill
 */
public class DoiMintingException extends Exception {

	public DoiMintingException() {
		super();
	}

	public DoiMintingException(String message, Throwable cause) {
		super(message, cause);
	}

	public DoiMintingException(String message) {
		super(message);
	}

	public DoiMintingException(Throwable cause) {
		super(cause);
	}

}
