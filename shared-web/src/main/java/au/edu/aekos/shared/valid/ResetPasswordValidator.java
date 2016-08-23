package au.edu.aekos.shared.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.web.model.ResetPasswordModel;

@Component
public class ResetPasswordValidator implements Validator{

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ResetPasswordModel.class.equals(clazz);
	}

	public void validateModel(ResetPasswordModel model, Errors errors) {
		SharedUser su = sharedUserDao.findUserByUsername(model.getUsername());
		if(su == null){
			errors.reject("", "No shared user exists with username " + model.getUsername());
			return;
		}
		if(! su.getEmailAddress().equals(model.getEmailAddress()) ){
			errors.rejectValue("emailAddress", "", "The email address entered does not match the stored email address for user " + model.getUsername());
			return;
		}
		if(su.getChangePasswordToken() == null || ! su.getChangePasswordToken().equals(model.getToken()) ){
			errors.reject("", "The stored token is not equal to the token supplied with this change password request.  Please generate a new token");
		}
		
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		// Do nothing, we don't use this as part of the framework
	}
}
