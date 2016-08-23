package au.edu.aekos.shared.service.file;

public class FileSystemDataIntegrityException extends Exception {
	private static final long serialVersionUID = 1;

	public FileSystemDataIntegrityException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileSystemDataIntegrityException(String message) {
		super(message);
	}

}
