package au.edu.aekos.shared.web.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.questionnaire.PageAnswersModel;
import au.edu.aekos.shared.service.security.SecurityService;


@Controller
public class ModifySubmissionController {
	
	@Autowired
	private NewSubmissionController newSubmissionController;
	
	@Autowired
	private SecurityService securityService;
	
	@RequestMapping(value = {"/modifySubmission","/questionnaire/modifySubmission"}, method = RequestMethod.GET )
	public String modifyRejectedSubmissionEdit(@RequestParam(value="submissionId", required=true) Long submissionId, 
			      Model model, @ModelAttribute("answers") PageAnswersModel answers , HttpSession session) throws Exception {
		if( ! securityService.canEditSubmission(submissionId) ){
			model.addAttribute("submissionId", submissionId);
			model.addAttribute("username", securityService.getLoggedInUsername() );
			return "editAccessDenied";
		}
		return newSubmissionController.launchPartiallyCompleteSubmissionEdit(submissionId, model, answers, session);
	}
}
