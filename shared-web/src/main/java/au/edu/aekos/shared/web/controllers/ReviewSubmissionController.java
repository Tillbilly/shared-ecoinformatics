package au.edu.aekos.shared.web.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.cache.reviewlock.ReviewLockException;
import au.edu.aekos.shared.cache.reviewlock.ReviewLockService;
import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.ReviewOutcome;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.notification.FreemarkerEmailTemplateService;
import au.edu.aekos.shared.service.notification.ReviewerNotificationService;
import au.edu.aekos.shared.service.publication.AsyncPublishService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.ReviewAnswerHistoryIndicator;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.service.submission.SubmissionReviewService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.valid.SubmissionReviewValidator;
import au.edu.aekos.shared.web.controllers.group.PeerReviewController;
import au.edu.aekos.shared.web.model.AnswerReviewHistoryModel;
import au.edu.aekos.shared.web.model.AnswerReviewModel;
import au.edu.aekos.shared.web.model.QuestionModel;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionRejectionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;
import au.edu.aekos.shared.web.model.SubmittedFileReviewModel;
import freemarker.template.TemplateException;

@Controller
public class ReviewSubmissionController {

	private static final Logger logger = LoggerFactory.getLogger(ReviewSubmissionController.class);
	private static final String LIST_SUBMISSIONS_FOR_REVIEW_URI = "listSubmissionsForReview";
	public static final String CURRENT_REVIEW = "currentReview";
	
	@Autowired
	private SubmissionModelService submissionManager;
	
	@Autowired
	private SubmissionReviewService submissionReviewService;
	
	@Autowired
	private SubmissionReviewValidator submissionReviewValidator;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private AsyncPublishService publicationService;
	
	@Autowired
	private FreemarkerEmailTemplateService emailTemplateService;
	
	@Autowired
	private ReviewLockService reviewLockService;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private PeerReviewController peerReviewController;
	
	@Autowired
	private ReviewerNotificationService reviewerNotificationService;
	
	@RequestMapping(value=LIST_SUBMISSIONS_FOR_REVIEW_URI, method=RequestMethod.GET )
	public String listSubmissionsForReview(@RequestParam(required=false) Boolean clearLock, @RequestParam(required=false) Long submissionId, Model model){
		if(Boolean.TRUE.equals(clearLock) && submissionId != null){
			reviewLockService.removeLock(submissionId, securityService.getLoggedInUsername()); 
		}
		model.addAttribute("submissionList", submissionReviewService.getSubmissionsAvailableForReview());
		model.addAttribute("currMappedUri", LIST_SUBMISSIONS_FOR_REVIEW_URI);
		return "submissionsToReviewList";
	}
	
	@RequestMapping(value="/reviewSubmission", method = RequestMethod.GET)
	public String launchSubmissionReview(@RequestParam(required=true) Long submissionId, @RequestParam Boolean peerReview, Model model, HttpSession session ) throws Exception{
		if(! checkReviewLock(submissionId)){
			String message = "Submission " + submissionId + " is being reviewed by another reviewer";
			model.addAttribute("message", message);
			if(peerReview){
				return peerReviewController.listSubmissionsForReview(null, null, model);
			}
			return listSubmissionsForReview(null, null, model);
		}
		SubmissionModel submissionModel = submissionManager.getSubmission(submissionId);
		model.addAttribute("submission", submissionModel);
		SubmissionReviewModel reviewModel = prepareNewSubmissionReviewModel(submissionModel);
		if(SubmissionStatus.RESUBMITTED.name().equals( submissionModel.getStatus() ) ){
			logger.info("History Retrieved");
			Map<String, ReviewAnswerHistoryIndicator> reviewAnsHistIndicatorMap = submissionReviewService.buildAnswerHistoryMap(submissionId);
            model.addAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
            session.setAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
		}
		if(peerReview != null){
			reviewModel.setPeerReview(peerReview);
		}
		model.addAttribute("review" , reviewModel);
		model.addAttribute("peerReview", peerReview);
		return "submissionReview";
	}
	
	@RequestMapping(value="/modifyReview", method = RequestMethod.GET)  
	public String modifyReview( @RequestParam(required=true) Long submissionId , Model model, HttpSession session  ) throws Exception{
		if(! checkReviewLock(submissionId)){
			return "sessionExpired";
		}
		
		SubmissionModel submissionModel = submissionManager.getSubmission(submissionId);
		model.addAttribute("submission", submissionModel);
		SubmissionReviewModel reviewModel = (SubmissionReviewModel) session.getAttribute(CURRENT_REVIEW);
		if(reviewModel == null || ! reviewModel.getSubmissionId().equals(submissionId) ){
			//session expired redirect - blow the session and return to the management list screen.
			return "sessionExpired";
		}
		if(SubmissionStatus.RESUBMITTED.name().equals( reviewModel.getSubmissionStatus() ) ){
			Map<String, ReviewAnswerHistoryIndicator> reviewAnsHistIndicatorMap = ( Map<String, ReviewAnswerHistoryIndicator> ) session.getAttribute("answerHistoryMap");
			if(reviewAnsHistIndicatorMap == null){
				reviewAnsHistIndicatorMap = submissionReviewService.buildAnswerHistoryMap(submissionId);
				session.setAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
			}
	        model.addAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
		}
		model.addAttribute("review" , reviewModel);
		model.addAttribute("peerReview", reviewModel.getPeerReview());
		return "submissionReview";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/submitReview", method = RequestMethod.POST)
	public String submitReview( @ModelAttribute("review") SubmissionReviewModel submissionReview, 
			                   BindingResult result, 
			                   Model model, HttpSession session ) throws Exception{
		if(! checkReviewLock(submissionReview.getSubmissionId())){
			return "sessionExpired";
		}
		logger.info("review submitted");
		SubmissionModel submission = submissionManager.getSubmission( submissionReview.getSubmissionId() );
		populateFileQuestionAndFileReviews(submissionReview, submission);
		submissionReviewValidator.validateSubmissionReview(submissionReview, submission,result);
		SubmissionModel submissionModel = submissionManager.getSubmission(submissionReview.getSubmissionId());
		model.addAttribute("submission", submissionModel );
		model.addAttribute("peerReview", submissionReview.getPeerReview());
		if(result.hasErrors()){
			submissionReview.setOutrightReject(false);
			if(SubmissionStatus.RESUBMITTED.name().equals( submissionReview.getSubmissionStatus() ) ){
				Map<String, ReviewAnswerHistoryIndicator> reviewAnsHistIndicatorMap = ( Map<String, ReviewAnswerHistoryIndicator> ) session.getAttribute("answerHistoryMap");
				if(reviewAnsHistIndicatorMap == null){
					reviewAnsHistIndicatorMap = submissionReviewService.buildAnswerHistoryMap(submissionReview.getSubmissionId());
					session.setAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
				}
	            model.addAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
			}
			model.addAttribute("validationFailed", Boolean.TRUE);
            return "submissionReview";
		}
		setReviewOutcome(submissionReview);
		session.setAttribute(CURRENT_REVIEW, submissionReview);
		if(submissionReview.getContainsRejection()){
			SubmissionRejectionModel rejectionModel = prepareSubmissionRejectionModel(submissionReview, submissionModel );
			model.addAttribute("rejection", rejectionModel);
			return "submissionReviewRejection";
		}
		return "submissionReviewConfirmation";
	}
	
	
	@RequestMapping(value="/saveIncompleteReview", method = RequestMethod.POST)
	public String saveIncompleteReview( @ModelAttribute("review") SubmissionReviewModel submissionReview, Model model, HttpSession session ) throws Exception{
		logger.info("Saving incomplete review");
		submissionReviewService.saveIncompleteReview(submissionReview);
		SubmissionModel submissionModel = submissionManager.getSubmission(submissionReview.getSubmissionId());
		model.addAttribute("submission", submissionModel );
		if(SubmissionStatus.RESUBMITTED.name().equals( submissionModel.getStatus() ) ){
			logger.info("History Retrieved");
			Map<String, ReviewAnswerHistoryIndicator> reviewAnsHistIndicatorMap = submissionReviewService.buildAnswerHistoryMap(submissionReview.getSubmissionId());
            model.addAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
            session.setAttribute("answerHistoryMap", reviewAnsHistIndicatorMap);
		}
		model.addAttribute("peerReview", submissionReview.getPeerReview());
		model.addAttribute("review" , submissionReview);
		return "submissionReview";
	}
	
	private boolean checkReviewLock(Long submissionId){
		try{
			reviewLockService.setReviewLock(submissionId, securityService.getLoggedInUsername());
		}catch(ReviewLockException ex){
			logger.info("Review lock unnavailable " + submissionId + "  " + securityService.getLoggedInUsername(), ex);
			return false;
		}
		return true;
	}
	
	private void setReviewOutcome(SubmissionReviewModel submissionReview){
		if( submissionReview.getOutrightReject() ){
			submissionReview.setReviewOutcome(ReviewOutcome.REJECTED);
		}else if(submissionReview.getContainsRejection()){
			submissionReview.setReviewOutcome(ReviewOutcome.MOD_REQUESTED);
		}else{
			submissionReview.setReviewOutcome(ReviewOutcome.PUBLISH);
		}
	}
	
	@RequestMapping(value="/finaliseReview", method = RequestMethod.GET)
	public String finaliseReview(@RequestParam(required=true) Long submissionId, HttpSession session , Model model){
		SubmissionReviewModel reviewModel = (SubmissionReviewModel) session.getAttribute(CURRENT_REVIEW);
		if(reviewModel == null || ! reviewModel.getSubmissionId().equals(submissionId) || ! checkReviewLock(submissionId)){
			return "sessionExpired";
		}
		submissionReviewService.saveSubmissionReview(reviewModel);
		if(ReviewOutcome.PUBLISH.equals( reviewModel.getReviewOutcome() ) ){
			//Launch the publish service outside of the Transaction wrapping saveSubmissionReview.
			//Need the review information to be persisted.
			Submission sub = submissionService.retrieveSubmissionById(submissionId);
			if(SubmissionStatus.APPROVED.equals( sub.getStatus() ) ){
				publicationService.publishSubmission(submissionId);
			}else if(SubmissionStatus.PEER_REVIEWED.equals(sub.getStatus())){
				reviewerNotificationService.notifyReviewersAboutSubmissionForReview(submissionId);
			}
		}
		model.addAttribute("reviewModel", reviewModel);
		model.addAttribute("peerReview", reviewModel.getPeerReview());
		reviewLockService.removeLock(submissionId);
		return "submissionReviewThankyou";
	}
	
	@RequestMapping(value="/processRejection", method = RequestMethod.POST)
	public String finaliseReviewRejectedSubmission(@ModelAttribute("rejection") SubmissionRejectionModel rejectionModel, Model model, HttpSession session, HttpServletRequest request ) throws Exception{
		SubmissionReviewModel reviewModel = (SubmissionReviewModel) session.getAttribute(CURRENT_REVIEW);
		if(reviewModel == null || ! reviewModel.getSubmissionId().equals(rejectionModel.getSubmissionId()) || ! checkReviewLock(reviewModel.getSubmissionId()) ){
			//session expired redirect - blow the session and return to the management list screen.
			return "sessionExpired";
		}
		SubmissionModel submission = submissionManager.getSubmission( reviewModel.getSubmissionId() );
		submissionReviewService.processRejection(submission, reviewModel, rejectionModel, request);
		model.addAttribute("reviewModel", reviewModel);
		reviewLockService.removeLock(submission.getSubmissionId());
		return "submissionReviewThankyou";
	}
	
	@RequestMapping(value="ajax/getReviewAnswerHistory", method = RequestMethod.GET)
	public String getReviewAnswerHistory(@RequestParam(required=true) Long submissionId, 
			                             @RequestParam(required=true) String questionId,
			                             HttpSession session , Model model){
		AnswerReviewHistoryModel reviewHistoryModel = submissionReviewService.getReviewAnswerHistory(submissionId, questionId);
		model.addAttribute("answerHistoryModel", reviewHistoryModel);
		return "ajax/reviewAnswerHistory";
	}
	
	private SubmissionRejectionModel prepareSubmissionRejectionModel(
			SubmissionReviewModel submissionReview,
			SubmissionModel submissionModel) throws IOException, TemplateException {
		String emailTxt = "";
		if(submissionReview.getOutrightReject()){
			emailTxt = emailTemplateService.getDiscardedSubmissionRejectionEmailText( submissionModel, submissionReview);
		}else{
			if(submissionReview.getPeerReview() == null || Boolean.FALSE.equals(submissionReview.getPeerReview())){
			    emailTxt = emailTemplateService.getRejectionEmailText( submissionModel, submissionReview);
			}else{
				emailTxt = emailTemplateService.getPeerReviewRejectionEmailText(submissionModel, submissionReview, securityService.getCurrentUser() );
			}
		}
		SubmissionRejectionModel rejectionModel = new SubmissionRejectionModel();
		rejectionModel.setRejectionEmail(emailTxt);
		rejectionModel.setSubmissionId(submissionModel.getSubmissionId());
		return rejectionModel;
	}

	private SubmissionReviewModel prepareNewSubmissionReviewModel(SubmissionModel submissionModel){
		SubmissionReviewModel reviewModel = new SubmissionReviewModel();
		reviewModel.setSubmissionId(submissionModel.getSubmissionId());
		reviewModel.setSubmissionStatus(submissionModel.getStatus());
		reviewModel.setSubmissionTitle(submissionModel.getSubmissionTitle() );
		for(QuestionModel questionModel : submissionModel.getAllQuestionModels() ){
			reviewModel.getAnswerReviews().put(questionModel.getQuestionId(), new AnswerReviewModel());
			if(questionModel.isTitleQuestion()){
				reviewModel.getAnswerReviews().get(questionModel.getQuestionId()).setOutcome(AnswerReviewOutcome.PASS);
			}
		}
		for(SubmissionDataFileModel dataFileModel : submissionModel.getFileList()     ){
			reviewModel.getFileReviews().put(dataFileModel.getId(), new SubmittedFileReviewModel());
		}
		submissionReviewService.repopulateSavedReview(reviewModel);
		
		return reviewModel;
	}	
	
	/*****************************************************************************************************
	 *                                                                                                   *
	 *****************************************************************************************************/
	private void populateFileQuestionAndFileReviews(SubmissionReviewModel submissionReview, SubmissionModel submission ){
		populateSingleFileQuestionAndFileReviews(submissionReview, submission);
		//Populate document question and reviews
		Map<Long, SubmittedFileReviewModel> fileReviewMap = submissionReview.getFileReviews();
		if(fileReviewMap.size() == 0){
			return;
		}
		for(Map.Entry<Long, SubmittedFileReviewModel> entry : fileReviewMap.entrySet() ){
			SubmittedFileReviewModel fileReview = entry.getValue();
			//Only copy over IF it is rejected and comments are present.
			if( fileReview.getReviewOutcome() != null 
					&& SubmissionFileReviewOutcome.REJECT.equals( fileReview.getReviewOutcome() )
					&& StringUtils.hasLength(fileReview.getComments()) ){
				//Need to retrieve the data file type, and work out whether to update the
				//fileQuestionReview
				Long subDataId = entry.getKey();
				SubmissionDataFileModel sdfm = null;
				for(SubmissionDataFileModel sd : submission.getFileList() ){
					if(sd.getId().equals(subDataId) && SubmissionDataType.DOCUMENT.name().equals(sd.getFileType()) ){
						sdfm = sd;
						break;
					}
				}
				if(sdfm != null && StringUtils.hasLength(sdfm.getQuestionId())){
					AnswerReviewModel answerReviewModel = submissionReview.getAnswerReviews().get(sdfm.getQuestionId());
					if( answerReviewModel != null && ! AnswerReviewOutcome.REJECT.equals(answerReviewModel.getOutcome()) ){
						answerReviewModel.setOutcome(AnswerReviewOutcome.REJECT);
						answerReviewModel.setComment(fileReview.getComments());
					}
				}
			}
		}
	}
	
	private void populateSingleFileQuestionAndFileReviews(SubmissionReviewModel submissionReview, SubmissionModel submission ){
		//First, identify single file questions - SITE_FILE, LICENSE_CONDITIONS, SPECIES_LIST
		List<QuestionModel> questionList = submission.getAllQuestionModels();
		Map<ResponseType, QuestionModel> fileQuestionMap = new HashMap<ResponseType, QuestionModel>();
		for(QuestionModel qm : questionList){
			if( (ResponseType.SITE_FILE.equals( qm.getResponseType() ) ||
					ResponseType.LICENSE_CONDITIONS.equals( qm.getResponseType() ) || 
					ResponseType.SPECIES_LIST.equals( qm.getResponseType() ) ) && qm.getHasResponse() ){
				fileQuestionMap.put(qm.getResponseType(), qm);
				if(fileQuestionMap.size() == 3){
					break;
				}
			}
		}
		if(fileQuestionMap.size() == 0){
			return;
		}
		// if a review is present for one of these file types, need to copy the review details to the question IF
		//the question does`nt already contain a reject message.
		Map<Long, SubmittedFileReviewModel> fileReviewMap = submissionReview.getFileReviews();
		if(fileReviewMap.size() == 0){
			return;
		}
		for( Map.Entry<Long, SubmittedFileReviewModel> entry : fileReviewMap.entrySet() ){
			SubmittedFileReviewModel fileReview = entry.getValue();
			//Only copy over IF it is rejected and comments are present.
			if( fileReview.getReviewOutcome() != null 
					&& SubmissionFileReviewOutcome.REJECT.equals( fileReview.getReviewOutcome() )
					&& StringUtils.hasLength(fileReview.getComments()) ){
				//Need to retrieve the data file type, and work out whether to update the
				//fileQuestionReview
				Long subDataId = entry.getKey();
				SubmissionDataType sdt = null;
				for(SubmissionDataFileModel sd : submission.getFileList() ){
					if(sd.getId().equals(subDataId) ){
						String fileType = sd.getFileType();
						sdt = SubmissionDataType.valueOf(fileType);
						break;
					}
				}
				if(sdt == null){
					continue;
				}
				ResponseType respType = null;
				if(SubmissionDataType.LICENSE_COND.equals( sdt) ){
					respType = ResponseType.LICENSE_CONDITIONS;
				}else if(SubmissionDataType.SPECIES_LIST.equals(sdt)){
					respType = ResponseType.SPECIES_LIST;
				}else if(SubmissionDataType.SITE_FILE.equals(sdt)){
					respType = ResponseType.SITE_FILE;
				}
				if(respType != null && fileQuestionMap.containsKey(respType) ){
					String questionId = fileQuestionMap.get(respType).getQuestionId();
					AnswerReviewModel answerReview = submissionReview.getAnswerReviews().get(questionId);
					if(answerReview != null && ! ( AnswerReviewOutcome.REJECT.equals(answerReview.getOutcome()) && StringUtils.hasLength( answerReview.getComment() ))){
						answerReview.setOutcome(AnswerReviewOutcome.REJECT);
						answerReview.setComment(fileReview.getComments());
					}
				}
			}
		}
	}
	

	
	
}
