package au.edu.aekos.shared.service.notification;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;
import au.edu.aekos.shared.web.model.AnswerReviewModel;
import au.edu.aekos.shared.web.model.MultiselectAnswerModel;
import au.edu.aekos.shared.web.model.QuestionModel;
import au.edu.aekos.shared.web.model.QuestionSetModel;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionRejectionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;
import au.edu.aekos.shared.web.model.SubmittedFileReviewModel;
import au.edu.aekos.shared.web.util.SharedUrlUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class FreemarkerEmailTemplateServiceImpl implements
		FreemarkerEmailTemplateService {

	@Value("${freemarker.template.resourcepath}")
	private String templateResourcePath;

	private Configuration defaultConfiguration = null;
	
	private static final String DISCARDED_FTL = "outrightRejection.ftl";
	
	private static final String REJECTED_FTL = "rejected.ftl";
	
	private static final String PEER_REVIEW_REJECTED_FTL = "peerReviewRejected.ftl";
	
	private static final String REJECTED_DETAIL_FTL = "rejectedDetail.ftl";
	
	private static final String ACTIVATION_FTL = "userActivation.ftl";
	
	private static final String CHANGE_PASSWORD_FTL = "changePassword.ftl";
	
	private static final String WELCOME_FTL = "welcome.ftl";
	
	private static final String PUBLISHED_FTL = "submissionPublished.ftl";
	
	private static final String REVIEW_ALERT_FTL = "reviewAlert.ftl";
	
	@Autowired
	private GroupAdminService groupAdminService;
	
	private Configuration getDefaultFreemakerConfiguration(){
		if(defaultConfiguration == null){
			defaultConfiguration = new Configuration();	
			defaultConfiguration.setTemplateLoader(new ClassTemplateLoader(this.getClass() ,  templateResourcePath ) );
			defaultConfiguration.setObjectWrapper(new DefaultObjectWrapper());
		}
		return defaultConfiguration;
	}
	
	@Override
	public String getDiscardedSubmissionRejectionEmailText( SubmissionModel submission, SubmissionReviewModel reviewModel  ) throws IOException, TemplateException{
		return processFreemarker(DISCARDED_FTL, buildDataModelForSubmissionRejectionEmail( submission, reviewModel) );
	}

	@Override
	public String getRejectionEmailText( SubmissionModel submission, SubmissionReviewModel reviewModel)
			throws IOException, TemplateException {
		return processFreemarker(REJECTED_FTL, buildDataModelForSubmissionRejectionEmail( submission, reviewModel) );
	}
	
	@Override
	public String getPeerReviewRejectionEmailText(SubmissionModel submission,
			SubmissionReviewModel reviewModel,
			 SharedUser peerReviewer) throws IOException, TemplateException {
		return  processFreemarker(PEER_REVIEW_REJECTED_FTL, buildDataModelForSubmissionPeerReviewRejectionEmail( submission, reviewModel, peerReviewer) );
	}
	
	public String getRejectionDetailsEmailText( SubmissionModel submission, SubmissionReviewModel reviewModel, SubmissionRejectionModel rejectionModel, HttpServletRequest request)
			throws IOException, TemplateException {
		return processFreemarker(REJECTED_DETAIL_FTL, buildDataModelForRejectionDetailsEmail( submission, reviewModel, rejectionModel, request) );
	}
	
	@Override
	public String getUserActivationEmailText(String username, String registrationToken,
			HttpServletRequest request) throws IOException, TemplateException {
		return processFreemarker(ACTIVATION_FTL, buildDataModelForActivationEmail( username,registrationToken, request) );
	}
	
	@Override
	public String getChangePasswordEmailText(String username,
			String changePasswordToken, HttpServletRequest request)
			throws IOException, TemplateException {
		return processFreemarker(CHANGE_PASSWORD_FTL, buildDataModelForChangePasswordEmail( username,changePasswordToken, request) );
		
	}
	
	@Override
	public String getWelcomeEmailText(SharedUser user,
			HttpServletRequest request) throws IOException, TemplateException {
		return processFreemarker(WELCOME_FTL, buildDataModelForWelcomeEmail( user, request) );
	}
		
	@Override
	public String getSubmissionPublishedEmailText(String username, String submissionTitle, String submissionUrl, String doi ) throws IOException, TemplateException {
		return processFreemarker(PUBLISHED_FTL, buildDataModelForPublishedEmail( username, submissionTitle, submissionUrl, doi) );
	}
	
	@Override
	public String getReviewAlertEmailText(String reviewer,
			String submissionTitle, String submissionId, String submitter)
			throws IOException, TemplateException {
		return processFreemarker(REVIEW_ALERT_FTL, buildDataModelForReviewAlertEmail( reviewer, submissionTitle, submissionId, submitter) );
	}
	

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map buildDataModelForSubmissionRejectionEmail(SubmissionModel submission, SubmissionReviewModel reviewModel){
		Map rejectionEmailDataModel = new HashMap();
		rejectionEmailDataModel.put("username", submission.getSubmittedByUsername() );
		rejectionEmailDataModel.put("submissionTitle", submission.getSubmissionTitle() );
		rejectionEmailDataModel.put("reviewComments", reviewModel.getNotes());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
		rejectionEmailDataModel.put("submissionDate", sdf.format(submission.getSubmissionDate()) );
		return rejectionEmailDataModel;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map buildDataModelForSubmissionPeerReviewRejectionEmail( SubmissionModel submission, SubmissionReviewModel reviewModel, SharedUser peerReviewer){
		Map rejectionEmailDataModel = buildDataModelForSubmissionRejectionEmail(submission, reviewModel);
		rejectionEmailDataModel.put("peerReviewerUsername", peerReviewer.getUsername());
		rejectionEmailDataModel.put("peerReviewerEmail", peerReviewer.getEmailAddress());
		UserGroup userGroup = groupAdminService.retrieveGroupWithAdminAndMember(peerReviewer.getUsername(), submission.getSubmittedByUsername());
		rejectionEmailDataModel.put("groupName", userGroup != null ? userGroup.getName() : "");
		rejectionEmailDataModel.put("groupOrganisation", userGroup != null ? userGroup.getOrganisation() : "");
		return rejectionEmailDataModel;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map buildDataModelForRejectionDetailsEmail(SubmissionModel submission, SubmissionReviewModel reviewModel, SubmissionRejectionModel rejectionModel,HttpServletRequest request){
		Map rejectionDetailsDataModel = new HashMap();
		rejectionDetailsDataModel.put("modifyUrl", buildSubmissionModifyUrl(reviewModel, request) );
		rejectionDetailsDataModel.put("emailText", rejectionModel.getRejectionEmail() );
		rejectionDetailsDataModel.put("responseRejections", buildResponseRejectionSequence( submission, reviewModel  ) );
		rejectionDetailsDataModel.put("dataRejections", buildDataRejectionSequence( submission, reviewModel  ) );
		
		return rejectionDetailsDataModel;
	}
	
	private String buildSubmissionModifyUrl(SubmissionReviewModel reviewModel, HttpServletRequest request){
		String baseUrl = SharedUrlUtils.constructBaseServletUrl(request);
		return baseUrl += "/modifySubmission?submissionId=" + reviewModel.getSubmissionId().toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List buildResponseRejectionSequence(SubmissionModel submission, SubmissionReviewModel reviewModel ){
		List responseRejectionSequence = new ArrayList();
		if(reviewModel.getContainsRejection()){
			Map<String, QuestionModel> questionModelMap = submission.getAllQuestionModelMap();
		    for(Map.Entry<String, AnswerReviewModel > entry :  reviewModel.getAnswerReviews().entrySet() ){
		    	String questionId = entry.getKey();
		    	AnswerReviewModel review = entry.getValue();
		    	if(review.getOutcome().equals(AnswerReviewOutcome.REJECT) ){
		    		QuestionModel question = questionModelMap.get(questionId);
		    		responseRejectionSequence.add(buildResponseRejectionMap(review, question));
		    	}
		    }
		} 
		return responseRejectionSequence;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildResponseRejectionMap(AnswerReviewModel review, QuestionModel question){
		Map  respRejecMap= new HashMap();
		respRejecMap.put("questionId", question.getQuestionId() );
		respRejecMap.put("question", question.getQuestionText());
		
		if(question.getIsMultiSelect()){
			String respStr = "";
			if(question.getMultiselectAnswerList() != null ){
				boolean notFirst = false;
				for(MultiselectAnswerModel msa : question.getMultiselectAnswerList() ){
					if(notFirst){
						respStr += ", ";
					}else{
						notFirst = true;
					}
					respStr += msa.getResponseText();
				}
			}
			respRejecMap.put("response", respStr );
		}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals( question.getResponseType()) ){
			StringBuilder str = new StringBuilder();
			for(int x = 0; x < question.getQuestionSetModelList().size(); x++){
				if(x > 0){
					str.append(", ");
				}
				QuestionSetModel qsm = question.getQuestionSetModelList().get(x);
				str.append("[");
				for(QuestionModel qm : qsm.getQuestionModelList()){
					str.append(qm.getQuestionText()).append(" ").append(   StringUtils.hasLength(qm.getResponseText())? qm.getResponseText() : "" ).append("   ");
				}
				str.append("]");
			}
			respRejecMap.put("response", str.toString() );
		}
		else{
			respRejecMap.put("response", question.getResponseText());
		}
		respRejecMap.put("review", review.getComment());
		return respRejecMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List buildDataRejectionSequence(SubmissionModel submission, SubmissionReviewModel reviewModel ){
		List dataRejectionSequence = new ArrayList();
		for(Map.Entry<Long, SubmittedFileReviewModel> entry : reviewModel.getFileReviews().entrySet() ){
			Long fileId = entry.getKey();
			SubmittedFileReviewModel fileReview = entry.getValue();
			if( SubmissionFileReviewOutcome.REJECT.equals( fileReview.getReviewOutcome()) ){
				//Get the SubmissionDataFileModel by fileId
				SubmissionDataFileModel fileModel = submission.getSubmissionDataFileModelById(fileId);
				if(fileModel != null){
					Map  fileRejectMap= new HashMap();
					fileRejectMap.put("fileName", fileModel.getFileName() );
					fileRejectMap.put("fileDescription", fileModel.getFileDescription() );
					fileRejectMap.put("review", fileReview.getComments() );
					dataRejectionSequence.add(fileRejectMap);
				}
			}
		}
		return dataRejectionSequence;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildDataModelForActivationEmail(String username, String activationToken, HttpServletRequest request){
		Map  dataMap= new HashMap();
		dataMap.put("username", username);
		dataMap.put("activationUrl", buildActivationUrl(username, activationToken, request));
		return dataMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildDataModelForChangePasswordEmail(String username, String changePasswordToken, HttpServletRequest request){
		Map  dataMap= new HashMap();
		dataMap.put("username", username);
		dataMap.put("changePasswordUrl", buildChangePasswordUrl(username, changePasswordToken, request));
		return dataMap;
	}
	
	private String buildActivationUrl(String username, String activationToken,  HttpServletRequest request){
		String baseUrl = SharedUrlUtils.constructBaseServletUrl(request);
		return baseUrl += "/reg/activateRegistration?username=" + username + "&token=" + activationToken;
	}
	
	private String buildChangePasswordUrl(String username, String changePasswordToken,  HttpServletRequest request){
		String baseUrl = SharedUrlUtils.constructBaseServletUrl(request);
		return baseUrl += "/reg/changePassword?username=" + username + "&token=" + changePasswordToken;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildDataModelForWelcomeEmail(SharedUser user, HttpServletRequest request){
		Map dataModel = new HashMap();
		dataModel.put("username", user.getUsername());
		dataModel.put("password", user.getPassword());
		dataModel.put("homeUrl", SharedUrlUtils.constructBaseServletUrl(request) );
		return dataModel;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildDataModelForPublishedEmail( String username, String submissionTitle, String submissionUrl, String doi){
		Map dataModel = new HashMap();
		dataModel.put("username", username);
		dataModel.put("submissionTitle", submissionTitle);
		dataModel.put("submissionUrl", submissionUrl );
		dataModel.put("doi", doi );
		return dataModel;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildDataModelForReviewAlertEmail( String reviewer, String submissionTitle, String submissionId, String submitter){
		Map dataModel = new HashMap();
		dataModel.put("reviewer", reviewer);
		dataModel.put("submissionTitle", submissionTitle);
		dataModel.put("submissionId", submissionId );
		dataModel.put("submitter", submitter );
		return dataModel;
	}
	
	@SuppressWarnings("rawtypes")
	private String processFreemarker(String template, Map dataModel) throws IOException, TemplateException{ 
		Configuration cfg = getDefaultFreemakerConfiguration();
		Template tmp = cfg.getTemplate(template );
		StringWriter sw = new StringWriter();
		tmp.process(dataModel, sw);
		return sw.toString();
	}
	
}
