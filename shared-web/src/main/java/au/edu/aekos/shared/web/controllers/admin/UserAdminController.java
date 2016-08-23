package au.edu.aekos.shared.web.controllers.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.security.RegistrationException;
import au.edu.aekos.shared.service.security.RegistrationService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.valid.ChangePasswordModelValidator;
import au.edu.aekos.shared.valid.RegistrationModelValidator;
import au.edu.aekos.shared.web.model.ChangePasswordModel;
import au.edu.aekos.shared.web.model.RegistrationModel;
import freemarker.template.TemplateException;

@Controller
public class UserAdminController {

	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabService;
	
	@Autowired
	private RegistrationModelValidator registrationModelValidator;
	
	@Autowired
	private ChangePasswordModelValidator changePasswordModelValidator;
	
	@RequestMapping(value="useradmin/updateDetails", method = RequestMethod.GET)
	public String changeUserDetails( Model model) throws RegistrationException{
		String username = securityService.getLoggedInUsername();
		RegistrationModel registrationModel = registrationService.getRegistrationDetails(username);
		registrationModel.setPassword(null);
	    model.addAttribute("registration", registrationModel);
	    model.addAttribute("changePassword", new ChangePasswordModel(username) );
	    model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
		return "admin/updateDetails";
	}
	
	@RequestMapping(value="useradmin/updateDetails", method = RequestMethod.POST)
	public String processChangeUserDetails( @ModelAttribute("registration") RegistrationModel registrationModel, BindingResult result, Model model, HttpServletRequest request) throws RegistrationException, IOException, TemplateException{
		registrationModelValidator.validateUpdateRegistrationDetails(registrationModel, result);
		if(result.hasErrors()){
			model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
			return "admin/updateDetails";
		}
		boolean emailAddrChanged = registrationService.updateRegistrationDetails(registrationModel, request);
		model.addAttribute("orgName", controlledVocabService.getDisplayStringForTraitValue(ControlledVocabularyService.ORGANISATION_TRAIT, false, registrationModel.getOrganisation()));
		model.addAttribute("emailAddressChanged", new Boolean(emailAddrChanged));
		model.addAttribute("registrationModel",registrationModel);
		return "admin/updateDetailsComplete";
	}
	
	@RequestMapping(value="useradmin/changePassword", method = RequestMethod.POST)
	public String processChangePassword( @ModelAttribute("changePassword") ChangePasswordModel changePasswordModel, BindingResult result, Model model) throws RegistrationException{
		changePasswordModelValidator.validate(changePasswordModel, result);
		if(result.hasErrors()){
			String username = securityService.getLoggedInUsername();
			RegistrationModel registrationModel = registrationService.getRegistrationDetails(username);
			registrationModel.setPassword(null);
			model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
		    model.addAttribute("registration", registrationModel);
		    return "admin/updateDetails";
		}
		registrationService.processChangePassword(changePasswordModel);
		return "admin/passwordUpdated";
	}
	

	
}
