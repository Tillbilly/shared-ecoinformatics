package au.edu.aekos.shared.web.controllers.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.security.RegistrationException;
import au.edu.aekos.shared.service.security.RegistrationService;
import au.edu.aekos.shared.valid.RegistrationModelValidator;
import au.edu.aekos.shared.valid.ResetPasswordValidator;
import au.edu.aekos.shared.web.model.ChangePasswordModel;
import au.edu.aekos.shared.web.model.RegistrationModel;
import au.edu.aekos.shared.web.model.ResetPasswordModel;


@Controller
public class RegistrationController {
	
	@Autowired
	private RegistrationModelValidator registrationModelValidator;
	
	@Autowired
	private ResetPasswordValidator resetPasswordValidator;
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabService;
	
	@RequestMapping(value="/reg/detailedRegistration", method = RequestMethod.GET )
	public String launchRegistrationScreenDetailed(Model model){
		model.addAttribute("registration", new RegistrationModel());
		model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
		return "registration/detailedRegistration";
	}
	
	@RequestMapping(value = "/reg/processRegistration", method = RequestMethod.POST)
	public String processRegistration(@ModelAttribute("registration") RegistrationModel registrationModel,
			                          BindingResult result, Model model, 
			                          HttpServletRequest request) throws Exception {
		registrationModelValidator.validate(registrationModel, result);
		if (result.hasErrors()) {
			model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
			return "registration/detailedRegistration";
		}
		registrationService.processNewRegistrationRequest(registrationModel,request);
		model.addAttribute("emailAddress", registrationModel.getEmailAddress());
		return "registration/registrationThankyou";
	}
	
	@RequestMapping(value="/reg/activateRegistration", method = RequestMethod.GET )
	public String activateRegistration(@RequestParam(value="token", required=true) String token, @RequestParam(value="username", required=true) String username, 
			      Model model, HttpServletRequest request) throws Exception {
		try{
			registrationService.activateRegistration(token, username, request );
		}catch(RegistrationException e){
			model.addAttribute("regException",e);
			return "registration/activationException";
		}
		return "registration/activationSuccessful";
	}
	
	@RequestMapping(value="/reg/changePassword", method = RequestMethod.GET )
	public String changePassword(@RequestParam(value="token", required=true) String token, @RequestParam(value="username", required=true) String username, 
			      Model model ) throws Exception {
		if(!registrationService.validateChangePasswordToken(username, token) ){
			return "home";
		}
		
		model.addAttribute("username",username);
		model.addAttribute("token", token);
		ResetPasswordModel rpmodel = new ResetPasswordModel();
		rpmodel.setUsername(username);
		rpmodel.setToken(token);
		model.addAttribute("resetPasswordModel", rpmodel);
		return "registration/resetPassword";
	}
	
	@RequestMapping(value = "/reg/resetPassword/{username}/{token}", method = RequestMethod.POST)
	public String processChangePassword(@PathVariable("username") String username, 
			                            @PathVariable("token") String token, 
			                            @ModelAttribute("resetPasswordModel") ResetPasswordModel rpmodel,
			                            BindingResult result,
			                            Model model){
		resetPasswordValidator.validateModel(rpmodel, result);
		if(result.hasErrors()){
			return "registration/resetPassword";
		}
		ChangePasswordModel cpm = new ChangePasswordModel();
		cpm.setUsername(username);
		cpm.setNewPassword(rpmodel.getPassword());
		registrationService.processChangePassword(cpm);
		return "registration/resetPasswordValidation";
	}
	
	
	
	
	
	

}
