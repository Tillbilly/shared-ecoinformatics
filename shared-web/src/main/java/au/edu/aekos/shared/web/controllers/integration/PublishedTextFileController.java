package au.edu.aekos.shared.web.controllers.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.service.file.PublishedTextFileService;
import au.edu.aekos.shared.web.model.PublishedTextFileViewModel;


/**
 * Returns a very simple page displaying the contents of an uploaded text file
 * 
 * Used to show species and site file contents from the aekos portal
 * 
 * @author btill
 */

@Controller
public class PublishedTextFileController {
	
	@Autowired
	private PublishedTextFileService publishedTextFileService;
	
	@RequestMapping("/viewPublishedTextFile")
	public String viewPublishedTextFile(@RequestParam(required=true) Long submissionId, @RequestParam(required=true) Long dataId, Model model){
		PublishedTextFileViewModel textFileViewModel = publishedTextFileService.getViewModel(submissionId, dataId);
		model.addAttribute("fileViewModel", textFileViewModel);
		return "published/viewPublishedTextFile";
	}
	
	

}
