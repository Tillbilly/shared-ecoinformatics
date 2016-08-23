package au.edu.aekos.shared.service.quest;

public class QuestionnaireConfigNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public QuestionnaireConfigNotFoundException() {
		super();
	}

	public QuestionnaireConfigNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public QuestionnaireConfigNotFoundException(String message) {
		super(message);
	}

	public QuestionnaireConfigNotFoundException(Throwable cause) {
		super(cause);
	}
}
