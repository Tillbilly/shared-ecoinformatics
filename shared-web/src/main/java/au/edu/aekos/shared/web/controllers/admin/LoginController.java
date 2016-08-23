package au.edu.aekos.shared.web.controllers.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;
import au.edu.aekos.shared.web.json.JsonIsSessionActiveResponse;


@Controller
public class LoginController {
	
	@Value("${shared.version}")
	private String sharedVersionString;
	
	@RequestMapping(value="/login", method=RequestMethod.GET )
	public String loginPage(Model model){
		model.addAttribute("sharedVersion", sharedVersionString);
		return "login";
	}

	@RequestMapping(value = "/isSessionActive", method = RequestMethod.GET)
	public void isSessionActive(HttpSession session, HttpServletResponse resp) throws IOException {
		DisplayQuestionnaire quest = (DisplayQuestionnaire) session.getAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE);
		resp.setContentType(SharedConstants.IE_SAFE_RESPONSE_TYPE);
		boolean questionnaireFoundOnSession = quest != null;
		JsonIsSessionActiveResponse jsonResp = new JsonIsSessionActiveResponse(questionnaireFoundOnSession);
		resp.getOutputStream().print(jsonResp.getJsonString());
		resp.flushBuffer();
	}
}
