package au.edu.aekos.shared.service.notification;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import freemarker.template.TemplateException;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionRejectionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;

public interface FreemarkerEmailTemplateService {

	/**
	 * 
	 * @param submission
	 * @param reviewModel
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	String getDiscardedSubmissionRejectionEmailText(
			SubmissionModel submission, SubmissionReviewModel reviewModel) throws IOException, TemplateException;

	@SuppressWarnings("rawtypes")
	Map buildDataModelForSubmissionRejectionEmail(SubmissionModel submission, SubmissionReviewModel reviewModel);
	
	String getRejectionEmailText(SubmissionModel submission, SubmissionReviewModel reviewModel) throws IOException, TemplateException;
	
	String getRejectionDetailsEmailText( SubmissionModel submission, SubmissionReviewModel reviewModel, SubmissionRejectionModel rejectionModel, HttpServletRequest request) throws IOException, TemplateException;
	
	String getPeerReviewRejectionEmailText( SubmissionModel submission, SubmissionReviewModel reviewModel, SharedUser peerReviewer) throws IOException, TemplateException;
	
	String getUserActivationEmailText ( String username, String registrationToken, HttpServletRequest request) throws IOException, TemplateException;
	
	String getChangePasswordEmailText(String username, String changePasswordToken, HttpServletRequest request) throws IOException, TemplateException;

	String getWelcomeEmailText(SharedUser user, HttpServletRequest request) throws IOException, TemplateException;
	
	String getSubmissionPublishedEmailText(String username, String submissionTitle, String submissionUrl, String doi ) throws IOException, TemplateException;
	
	String getReviewAlertEmailText(String reviewer, String submissionTitle, String submissionId, String submitter ) throws IOException, TemplateException;
	
}
