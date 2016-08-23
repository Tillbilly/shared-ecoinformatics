package au.edu.aekos.shared.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.aekos.shared.service.submission.SubmissionSearchService;

@Controller
public class ShowAllSubmissionsController {

	@Autowired
	private SubmissionSearchService submissionSearchService;
	
	@RequestMapping(value="/showAllSubmissions", method=RequestMethod.GET )
	public String showAllSubmissions(Model model){
		model.addAttribute("submissionList", submissionSearchService.getAllUndeletedSubmissions());
		return "showAllSubmissions";
	}
}
