package au.edu.aekos.shared.web.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SharedHomeController {
	
	@Value("${shared.version}")
	private String sharedVersionString;
	
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String showHomePage( Model model ){
		model.addAttribute("sharedVersion", sharedVersionString);
		return "home";
	}

	@RequestMapping(value="/sessionExpired", method = RequestMethod.GET)
	public String sessionExpired( Model model ){
		model.addAttribute("sessionExpiredMessage", "Your session expired. Any unsaved submission information has been lost.<br />Note: you have not been logged out.");
		return showHomePage( model );
	}
	
}
