package au.edu.aekos.shared.service.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.web.model.ChangePasswordModel;
import au.edu.aekos.shared.web.model.RegistrationModel;
import freemarker.template.TemplateException;

public interface RegistrationService {
	
	void processNewRegistrationRequest( RegistrationModel registration, HttpServletRequest request )  throws RegistrationException, IOException, TemplateException;
	
	void processAafRegistration( RegistrationModel registration ) ;
	
	SharedUser registerNewUser(RegistrationModel registration ) throws RegistrationException ;
	
	void activateRegistration(String token, String username,  HttpServletRequest request) throws RegistrationException,Exception ;

	SharedUser findUserByUsername(String username);
	
	RegistrationModel getRegistrationDetails(String username ) throws RegistrationException ;
	
	/**
	 * @param model
	 * @return true if email address has changed
	 * @throws RegistrationException
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	boolean updateRegistrationDetails(RegistrationModel model,  HttpServletRequest request ) throws RegistrationException, IOException, TemplateException ;
	
	void processChangePassword( ChangePasswordModel changePasswordModel );
	
	boolean validateChangePasswordToken( String userName, String token);

	void sendUserChangePasswordEmail(String username, HttpServletRequest request) throws IOException, TemplateException;
	
}
