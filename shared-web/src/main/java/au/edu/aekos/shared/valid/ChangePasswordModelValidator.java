package au.edu.aekos.shared.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.service.security.RegistrationService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.web.model.ChangePasswordModel;

@Component
public class ChangePasswordModelValidator implements Validator {

	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private SecurityService securityService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ChangePasswordModel.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		ChangePasswordModel cpm = (ChangePasswordModel) target;
		//Not sure how this could occur, but anyway, throw a general error
		if( ! cpm.getUsername().equals( securityService.getLoggedInUsername() ) ){
			errors.reject("", "Username provided in form does not match logged in user");
			return;
		}
		//Retrieve the Shared user
		SharedUser su = registrationService.findUserByUsername(cpm.getUsername());
		//check for null user password, 
		if(!StringUtils.hasLength( cpm.getCurrentPassword() ) ){
			errors.rejectValue("currentPassword", "questionnaire.admin.currentpasswordempty");
		}
		if(!StringUtils.hasLength( cpm.getNewPassword() ) ){
			errors.rejectValue("newPassword", "questionnaire.admin.newpasswordempty");
		}
		if(!StringUtils.hasLength( cpm.getNewPassword2() ) ){
			errors.rejectValue("newPassword2", "questionnaire.admin.newpasswordempty");
		}
		if(errors.hasErrors()){
			return;
		}
		
		if(! su.getPassword().equals(cpm.getCurrentPassword()) ){
			errors.rejectValue("currentPassword", "questionnaire.admin.currentpasswordincorrect");
			return;
		}
		if(! cpm.getNewPassword().equals(cpm.getNewPassword2())){
		    errors.rejectValue("newPassword2", "questionnaire.admin.newpasswords.dontmatch");
		}
	}
}
