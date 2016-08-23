package au.org.aekos.shared.questionnaire;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class QuestionnaireConfigValidationException extends Exception {

	private static final long serialVersionUID = -5191150255021996600L;
	private Errors errors;

	public QuestionnaireConfigValidationException(Errors errors){
		this.errors = errors;
	}
	
	public QuestionnaireConfigValidationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionnaireConfigValidationException(String message,
			Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public QuestionnaireConfigValidationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public QuestionnaireConfigValidationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
	public String getMessage(){
		if(errors == null || errors.getAllErrors() == null || errors.getAllErrors().size() == 0){
			return super.getMessage();
		}
		StringBuilder sb = new StringBuilder();
		for(ObjectError error : errors.getAllErrors()){
			sb.append(error.getDefaultMessage()).append("\n");
		}
		
		return sb.toString();
	}
	

}
