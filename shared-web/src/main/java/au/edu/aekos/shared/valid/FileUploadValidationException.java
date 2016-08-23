package au.edu.aekos.shared.valid;

public class FileUploadValidationException extends Throwable {

	private static final long serialVersionUID = 1L;

	public FileUploadValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadValidationException(String message) {
		super(message);
	}

}
