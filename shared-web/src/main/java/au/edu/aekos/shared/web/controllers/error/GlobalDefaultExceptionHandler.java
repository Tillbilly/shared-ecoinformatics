package au.edu.aekos.shared.web.controllers.error;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import au.edu.aekos.shared.service.notification.AdminNotificationService;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;

/**
 * This global exception handler code is borrowed from a spring.io blog post
 * here:  http://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
 */
class GlobalDefaultExceptionHandler {
	
    public static final String GLOBAL_EXCEPTION_VIEW = NewSubmissionController.GLOBAL_EXCEPTION_VIEW;
    
    @Autowired
    private AdminNotificationService adminNotificationService;

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	sendAdminAlertEmail(e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(GLOBAL_EXCEPTION_VIEW);
        return mav;
    }
    
    private void sendAdminAlertEmail(Exception e){
    	adminNotificationService.notifyAdminByEmail("SHaRED Global Exception Caught", "Global Exception Caught", e);
    }
}
