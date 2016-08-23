package au.edu.aekos.shared.web.controllers.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.security.RegistrationService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.web.model.RegistrationModel;

@Controller
public class AAFRapidConnectEndpoint {
	
	@Autowired
	private SecurityService securityService;

	@Autowired
	private ControlledVocabularyService controlledVocabService;

	@Autowired
	private RegistrationService registrationService;
	
	//Test Secret 
	//jwLbkBM.,B%f~~1YXOo2lAFs9TNyVu_L
	//https://rapid.test.aaf.edu.au/jwt/authnrequest/research/LBV6-lwcx_fy1uGR
	private Logger logger = LoggerFactory.getLogger(AAFRapidConnectEndpoint.class);
	
	//TODO remove this test endpoint ! ! ! shortly, shortly
	@RequestMapping(value="/integration/AAFRapidConnectEndpoint",method=RequestMethod.POST)
	public String aafRapidConnectEnpointHandler_orig(HttpServletRequest request, Model model){
		SharedUser su = securityService.getCurrentUser();
		if(su.getAafUser() && ! su.getAafRegistered()){
			//Prepare model, then go to aaf registration page
			RegistrationModel registrationModel = new RegistrationModel();
			registrationModel.setUsername(su.getUsername());
			registrationModel.setEmailAddress(su.getEmailAddress());
			registrationModel.setFullName(su.getFullName());
			model.addAttribute("registration", registrationModel);
			model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
			return "registration/aafRegistration";
		}
		return "home";
	}
	
	@RequestMapping(value="/secure/AAFRapidConnectEndpoint",method=RequestMethod.POST)
	public String aafRapidConnectEnpointHandler(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException{
		SharedUser su = securityService.getCurrentUser();
		if(su == null){ //Anita got the system to throw a null pointer here, dual log in types from same browser
			handleDualSessionTypeLogIn(request);
			return "login";
		}
		if(su.getAafUser() && ! su.getAafRegistered()){
			//Prepare model, then go to aaf registration page
			RegistrationModel registrationModel = new RegistrationModel();
			registrationModel.setUsername(su.getUsername());
			registrationModel.setEmailAddress(su.getEmailAddress());
			registrationModel.setFullName(su.getFullName());
			model.addAttribute("registration", registrationModel);
			model.addAttribute("orgList", controlledVocabService.getTraitValueList(ControlledVocabularyService.ORGANISATION_TRAIT, false, true) );
			return "registration/aafRegistration";
		}
		response.sendRedirect(".."); 
		return null;
	}
	
	@RequestMapping(value = "/secure/processAafRegistration", method = RequestMethod.POST)
	public String processAafRegistration(@ModelAttribute("registration") RegistrationModel registrationModel,
			                          BindingResult result, Model model, 
			                          HttpServletRequest request) throws Exception {
		registrationService.processAafRegistration(registrationModel);
		return "home";
	}
	
	/**
	 * Anita got the system to throw a null pointer here,
	 *  she had logged in both with AAF and standard registration
	 *
	 * [http-nio-443-exec-9] 05:19:36,518 DEBUG org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter.doFilter(AbstractPreAuthenticatedProcessingFilter.java:87) 
	 *  Checking secure context token: org.springframework.security.authentication.UsernamePasswordAuthenticationToken@8e39d9cd: 
	 *  Principal: org.springframework.security.core.userdetails.User@ca24ac65: 
	 *  Username: shared; Password: [PROTECTED]; 
	 *  Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ROLE_USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@7798: RemoteIpAddress: 129.127.13.168; SessionId: 09AA8A19154446FEEF250A1145B9A08B; Granted Authorities: ROLE_USER
	 */
	private void handleDualSessionTypeLogIn(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session != null){
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
	}
	

}
