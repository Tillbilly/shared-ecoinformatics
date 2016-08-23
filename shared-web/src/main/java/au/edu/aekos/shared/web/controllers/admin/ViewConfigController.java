package au.edu.aekos.shared.web.controllers.admin;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import au.edu.aekos.shared.admin.ReadablePropertyPlaceholderConfigurer;

@Controller
public class ViewConfigController {

	@Autowired
	private ReadablePropertyPlaceholderConfigurer readablePropertyPlaceholderConfigurer;
	
	@RequestMapping("/admin/viewConfig")
	public String viewConfig(Model model){
		Properties props = readablePropertyPlaceholderConfigurer.getSharedProperties();
		model.addAttribute("props", props );
		model.addAttribute("names", props.stringPropertyNames() );
		return "admin/viewConfig";
	}
}
