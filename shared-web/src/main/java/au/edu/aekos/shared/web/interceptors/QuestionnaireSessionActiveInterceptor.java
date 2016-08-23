package au.edu.aekos.shared.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;

/**
 * Checks whether a DisplayQuestionnaire is active in the users session.
 * If it is`nt, redirects to home
 * 
 * TODO Put a questionnaireSessionIdentifier on the requests.
 * 
 * @author btill
 */
public class QuestionnaireSessionActiveInterceptor implements
		HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		DisplayQuestionnaire questionnaire = (DisplayQuestionnaire) request.getSession().getAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE);
		
		String URI = request.getRequestURI();
		if(questionnaire==null && request.getParameter("new") == null && 
				!( URI.contains("modifySubmission") || URI.contains("editIncompleteSubmission"))) {
			response.sendRedirect(request.getContextPath() + "/sessionExpired" ); 
			return false;
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
