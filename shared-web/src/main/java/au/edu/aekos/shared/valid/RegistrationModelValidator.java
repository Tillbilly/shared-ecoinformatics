package au.edu.aekos.shared.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.service.security.RegistrationService;
import au.edu.aekos.shared.web.model.ChangePasswordModel;
import au.edu.aekos.shared.web.model.RegistrationModel;

@Component
public class RegistrationModelValidator implements Validator {

	@Autowired
	private RegistrationService registrationService;
	
	public boolean supports(Class<?> clazz) {
		return RegistrationModel.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		RegistrationModel model = (RegistrationModel) target;
		//username not null
		if( ! StringUtils.hasLength( model.getUsername() ) ){
			errors.rejectValue("username", "registration.username.notnull", "Username can`t be empty");
		}else if(model.getUsername().contains(" ")){ //Make sure the username does not contain spaces
			errors.rejectValue("username", "registration.username.nospaces", "Username can`t contain spaces");
		}
		//password not null
		if( ! StringUtils.hasLength( model.getPassword()) ){
			errors.rejectValue("password", "registration.password.notnull", "Password can`t be empty");
		}
		//email not null
		if( ! StringUtils.hasLength( model.getEmailAddress() ) ){
			errors.rejectValue("emailAddress", "registration.email.notnull", "Email Address can`t empty");
		}
		
		if(errors.hasErrors()){
			return;
		}
		//username is`nt taken
		if( registrationService.findUserByUsername(model.getUsername()) != null ){
			Object [] values = { model.getUsername() };
			errors.rejectValue("username", "registration.username.exists", values, "Username exists");
		}
		
		//email looks legit ( has an @ sign, not in the first or last position )
		if(! emailAddressContainsAt(model.getEmailAddress()) ){
			errors.rejectValue("emailAddress", "registration.email.invalid", "Email Address is invalid");
		}
	}
	
	public void validateUpdateRegistrationDetails(RegistrationModel model,  Errors errors){
		//email not null
		if( ! StringUtils.hasLength( model.getEmailAddress() ) ){
			errors.rejectValue("emailAddress", "registration.email.notnull", "Email Address can`t empty");
		}
		if(errors.hasErrors()){
			return;
		}
		if(! emailAddressContainsAt(model.getEmailAddress()) ){
			errors.rejectValue("emailAddress", "registration.email.invalid", "Email Address is invalid");
		}
	}

	public boolean emailAddressContainsAt(String emailAddress){
		if(emailAddress.indexOf("@") < 1 || emailAddress.indexOf("@") > emailAddress.length() -2 ){
			return false;
		}
		return true;
	}
	
	public void validateChangePasswordModel(ChangePasswordModel cpm, Errors errors){
		
		
	}
	
	
}
