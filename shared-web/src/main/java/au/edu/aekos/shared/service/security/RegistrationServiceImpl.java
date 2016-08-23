package au.edu.aekos.shared.service.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.SharedAuthority;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.service.notification.FreemarkerEmailTemplateService;
import au.edu.aekos.shared.service.notification.NotificationEmailService;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.web.model.ChangePasswordModel;
import au.edu.aekos.shared.web.model.RegistrationModel;
import freemarker.template.TemplateException;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private FreemarkerEmailTemplateService emailTemplateService;
	
	@Autowired
	private NotificationEmailService notificationEmailService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabService;
	
	@Override
	@Transactional
	public void processNewRegistrationRequest(RegistrationModel registration,
			HttpServletRequest request) throws RegistrationException, IOException, TemplateException {
		SharedUser newUser = registerNewUser(registration);
		String activationText = emailTemplateService.getUserActivationEmailText(registration.getUsername(), newUser.getRegistrationToken(), request);
		
		//Changed this to use the email String rather that query the shared user 
		//- for a new shared user, may not be committed hence could possibly be null in the session ( could, not will, it think it has happened although doesn't usually ).
		notificationEmailService.asyncProcessNotification("SHaRED User Account Activation", activationText, registration.getUsername(), registration.getEmailAddress(), NotificationType.ACTIVATION);
	}
	
	@Transactional
	public SharedUser registerNewUser(RegistrationModel registration) throws RegistrationException {
		if( sharedUserDao.findUserByUsername(registration.getUsername()) != null ){
			logger.error("User with username '" +registration.getUsername() + "' already exists. Register new user Failed" );
			throw new RegistrationException("User with username '" +registration.getUsername() + "' already exists. Register new user Failed" );
		}
		
		SharedUser newUser = new SharedUser();
		//User is enabled after responding to activation email
		newUser.setEnabled(Boolean.FALSE);
		newUser.setEmailAddress(registration.getEmailAddress());
		newUser.setUsername(registration.getUsername());
		newUser.setPassword(registration.getPassword());
		newUser.setFullName(registration.getFullName());
		if(!StringUtils.hasLength(registration.getOrganisation())){
			newUser.setOrganisation(registration.getOrganisationOther());
		}else{
			newUser.setOrganisation(registration.getOrganisation());
		}
		newUser.setPostalAddress(registration.getPostalAddress());
		newUser.setPhoneNumber(registration.getPhoneNumber());
		newUser.setWebsite(registration.getWebsite());
		newUser.setRegistrationToken(UUID.randomUUID().toString());
		
		SharedAuthority sa = new SharedAuthority();
		sa.setSharedRole(SharedRole.ROLE_USER);
        sa.setSharedUser(newUser);
        newUser.getRoles().add(sa);
        sharedUserDao.save(newUser);
        return newUser;
	}

	@Override
	@Transactional
	public void activateRegistration(String token, String username, HttpServletRequest request)
			throws RegistrationException, Exception {
		
		SharedUser user = sharedUserDao.findUserByUsername(username);
		if(user == null){
			String message = "No user exists with username'"+username+"'";
			logger.error("Registration Activation Error : " +message);
			throw new RegistrationException(message);
		}
		
		if(user.getRegistrationToken() == null || ! user.getRegistrationToken().equals(token)){
			String message = "Activation failed for '" + username + "'. Invalid token " + token ;
			logger.error("Registration Activation Error : " +message);
			throw new RegistrationException(message);
		}
		
		if(user.getEnabled()){
			String message = "'" + username + "' already activated";
			logger.info("Registration Activation: " +message);
			return;
		}
		
		user.setEnabled(Boolean.TRUE);
		sharedUserDao.saveOrUpdate(user);
		
		//Prepare welcome to SHaRED AEKOS email
		String welcomeText = emailTemplateService.getWelcomeEmailText(user, request);
		notificationEmailService.asyncProcessNotification("Welcome to SHaRED", welcomeText, username,  NotificationType.WELCOME);
	}

	@Override
	@Transactional
	public SharedUser findUserByUsername(String username) {
		return sharedUserDao.findUserByUsername(username);
	}

	@Override
	@Transactional
	public RegistrationModel getRegistrationDetails(String username)
			throws RegistrationException {
		SharedUser user = findUserByUsername(username);
		RegistrationModel model = new RegistrationModel(user);
		if(StringUtils.hasLength( model.getOrganisation()) 
			&& ! controlledVocabService.traitListContainsValue(ControlledVocabularyService.ORGANISATION_TRAIT, false, model.getOrganisation()) ){
			model.setOrganisationOther(model.getOrganisation() );
			model.setOrganisation(null);
		}
		return model;
	}

	@Override
	@Transactional
	public void processAafRegistration( RegistrationModel model ) {
		SharedUser user = findUserByUsername(model.getUsername());
		user.setFullName(model.getFullName());
		if(RegistrationModel.OTHER_ORG_KEY.equals(model.getOrganisation())){
			user.setOrganisation(model.getOrganisationOther());
		}else{
			user.setOrganisation(model.getOrganisation());
		}
		user.setPostalAddress(model.getPostalAddress());
		user.setPhoneNumber(model.getPhoneNumber());
		user.setWebsite(model.getWebsite());
		user.setAafRegistered(Boolean.TRUE);
		sharedUserDao.saveOrUpdate(user);
	}
	
	
	@Override
	@Transactional
	public boolean updateRegistrationDetails(RegistrationModel model, HttpServletRequest request)
			throws RegistrationException, IOException, TemplateException {
		SharedUser user = findUserByUsername(model.getUsername());
		user.setFullName(model.getFullName());
		if(RegistrationModel.OTHER_ORG_KEY.equals(model.getOrganisation())){
			user.setOrganisation(model.getOrganisationOther());
		}else{
			user.setOrganisation(model.getOrganisation());
		}
		user.setPostalAddress(model.getPostalAddress());
		user.setPhoneNumber(model.getPhoneNumber());
		user.setWebsite(model.getWebsite());
		//If email address has changed, user will need to respond to the email to verify their new email address
		boolean emailChanged = ! user.getEmailAddress().equals(model.getEmailAddress());
		if(emailChanged){
			user.setEmailAddress(model.getEmailAddress());
		    user.setRegistrationToken(UUID.randomUUID().toString());
		    user.setEnabled(Boolean.FALSE);
		    //Need to send out an email to the new email address the user will
		    //need to verify before they can log back in??
		    String activationText = emailTemplateService.getUserActivationEmailText(user.getUsername(), user.getRegistrationToken(), request);
			notificationEmailService.asyncProcessNotification("SHaRED User Account Activation", activationText, user.getUsername(), model.getEmailAddress(), NotificationType.ACTIVATION);
		}
		sharedUserDao.saveOrUpdate(user);
		return emailChanged;
	}

	@Override @Transactional
	public void processChangePassword(ChangePasswordModel changePasswordModel) {
		SharedUser user = findUserByUsername(changePasswordModel.getUsername());
		user.setPassword(changePasswordModel.getNewPassword());
		user.setChangePasswordToken(null);
		sharedUserDao.saveOrUpdate(user);
	}
	
	@Override
	@Transactional
	public void sendUserChangePasswordEmail(String username, HttpServletRequest request) throws IOException, TemplateException {
		String changePasswordToken = UUID.randomUUID().toString();
		SharedUser user = sharedUserDao.findUserByUsername(username);
		user.setChangePasswordToken(changePasswordToken);
		sharedUserDao.saveOrUpdate(user);
		//Need to generate change password token, store it,
		//Then send the user a change password email.
		//When they change their password, delete the token.
		String changePasswordEmail = emailTemplateService.getChangePasswordEmailText(user.getUsername(), user.getChangePasswordToken(), request);
		notificationEmailService.asyncProcessNotification("SHaRED Change Password", changePasswordEmail, user.getUsername(),  NotificationType.CHANGE_PASSWORD);
		
	}

	@Override @Transactional
	public boolean validateChangePasswordToken(String userName, String token) {
		SharedUser su = sharedUserDao.findUserByUsername(userName);
		if(su != null ){
			if(token != null && token.equals(su.getChangePasswordToken())){
				return true;
			}
		}
		return false;
	}

}
