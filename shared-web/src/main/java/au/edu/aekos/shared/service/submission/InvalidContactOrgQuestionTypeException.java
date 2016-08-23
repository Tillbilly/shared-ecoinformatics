package au.edu.aekos.shared.service.submission;


public class InvalidContactOrgQuestionTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an exception explaining why the current questionnaire config for the author name question is invalid
	 */
	public InvalidContactOrgQuestionTypeException() {
		super("The system can only handle legal contact organisation name questions that are of MultiQuestionGroup type!");
	}

}
