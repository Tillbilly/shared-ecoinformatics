package au.edu.aekos.shared.service.submission;


public class InvalidAuthorNameQuestionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an exception explaining why the current questionnaire config for the author name question is invalid
	 */
	public InvalidAuthorNameQuestionException() {
		super("The system can only handle author name questions that are of MultiQuestionGroup type!");
	}

}
