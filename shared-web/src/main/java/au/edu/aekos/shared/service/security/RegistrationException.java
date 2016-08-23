package au.edu.aekos.shared.service.security;

public class RegistrationException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistrationException() {
		super();
	}

	public RegistrationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RegistrationException(String arg0) {
		super(arg0);
	}

	public RegistrationException(Throwable arg0) {
		super(arg0);
	}
}