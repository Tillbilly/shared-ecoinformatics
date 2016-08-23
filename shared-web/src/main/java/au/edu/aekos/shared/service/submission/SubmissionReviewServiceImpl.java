package au.edu.aekos.shared.service.submission;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.cache.config.QuestionnaireConfigCache;
import au.edu.aekos.shared.cache.config.QuestionnaireConfigElement;
import au.edu.aekos.shared.cache.reviewlock.ReviewLockService;
import au.edu.aekos.shared.data.dao.AnswerReviewDao;
import au.edu.aekos.shared.data.dao.SubmissionAnswerDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.dao.SubmissionHistoryDao;
import au.edu.aekos.shared.data.dao.SubmissionReviewDao;
import au.edu.aekos.shared.data.entity.AnswerReview;
import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.FileReview;
import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.ReviewOutcome;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionHistory;
import au.edu.aekos.shared.data.entity.SubmissionReview;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;
import au.edu.aekos.shared.service.notification.FreemarkerEmailTemplateService;
import au.edu.aekos.shared.service.notification.NotificationEmailService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.web.model.AnswerReviewHistoryModel;
import au.edu.aekos.shared.web.model.AnswerReviewHistoryRow;
import au.edu.aekos.shared.web.model.AnswerReviewModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionRejectionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;
import au.edu.aekos.shared.web.model.SubmittedFileReviewModel;
import freemarker.template.TemplateException;

@Service
public class SubmissionReviewServiceImpl implements SubmissionReviewService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubmissionReviewServiceImpl.class);

	@Autowired
	private SubmissionReviewDao submissionReviewDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionHistoryDao submissionHistoryDao;
	
	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private NotificationEmailService notificationEmailService;
	
	@Autowired
	private FreemarkerEmailTemplateService freemarkerService;
	
	@Autowired
	private SubmissionAnswerDao submissionAnswerDao;
	
	@Autowired
	private AnswerReviewDao answerReviewDao;
	
	@Autowired
	private QuestionnaireConfigCache questionnaireConfigCache;
	
	@Autowired
	private ReviewLockService reviewLockService;
	
	@Autowired
	private GroupAdminService groupAdminService;
	
	@Override
	@Transactional
	public Long saveSubmissionReview(SubmissionReviewModel reviewModel ) {
		return saveSubmissionReview(reviewModel, securityService.getCurrentUser() );
	}
	
	@Override
	@Transactional
	public Long saveSubmissionReview(SubmissionReviewModel reviewModel,
			SharedUser reviewer) {
		SubmissionReview review = mapReviewModelToNewEntity(reviewModel, reviewer);
		submissionReviewDao.save(review);
		//Record submissionHistory before updating the status
		submissionHistoryDao.createSubmissionHistory(reviewModel.getSubmissionId());
		//update the underlying submission`s status based on the reviewOutcome
		SubmissionStatus status = updateSubmissionStatusBasedOnReviewOutcome(reviewModel.getSubmissionId(), reviewModel.getReviewOutcome(), review.getReviewDate(), reviewModel.getPeerReview());
        //Delete any saved reviews for the submission
		deleteIncompleteReviewsForSubmission(reviewModel.getSubmissionId());
		
		return review.getId();
	}
	
	@Transactional
	public Long saveIncompleteReview(SubmissionReviewModel reviewModel ) {
		 SharedUser reviewer = securityService.getCurrentUser();
		 SubmissionReview review = new SubmissionReview();
		 review.setReviewOutcome( ReviewOutcome.REVIEW_SAVED );
		 review.setReviewer(reviewer);
		 review.setReviewDate(new Date());
		 review.setNotes(reviewModel.getNotes());
		 mapSubmissionAndAnswerReviewsToReviewEntity( review,  reviewModel);
		 submissionReviewDao.save(review);
		 return review.getId();
	}
	
	private SubmissionReview mapReviewModelToNewEntity(SubmissionReviewModel submissionReviewModel, SharedUser reviewer){
		SubmissionReview review = new SubmissionReview();
		review.setReviewOutcome( submissionReviewModel.getReviewOutcome() );
		review.setReviewer(reviewer);
		review.setReviewDate(new Date());
		review.setNotes(submissionReviewModel.getNotes());
		review.setRejectionMessage(submissionReviewModel.getRejectionMessage());
		mapSubmissionAndAnswerReviewsToReviewEntity( review,  submissionReviewModel);
		return review;
	}

	private void mapSubmissionAndAnswerReviewsToReviewEntity(SubmissionReview review, SubmissionReviewModel submissionReviewModel){
		Submission submission = submissionDao.findById(submissionReviewModel.getSubmissionId());
		review.setSubmission(  submission );
		Map<String, SubmissionAnswer> submissionAnswerMap = submission.getAnswersMappedByQuestionId();
		mapReviewModelAnswersToReviewEntity( review,  submissionReviewModel, submissionAnswerMap);
	}
	
	private void mapReviewModelAnswersToReviewEntity(SubmissionReview review, SubmissionReviewModel submissionReviewModel, Map<String, SubmissionAnswer> submissionAnswerMap){
		for( Entry<String,AnswerReviewModel> answerReviewModelEntry :  submissionReviewModel.getAnswerReviews().entrySet()) {
			AnswerReview answerReview = new AnswerReview();
			answerReview.setQuestionId(answerReviewModelEntry.getKey());
			AnswerReviewModel answerReviewModel = answerReviewModelEntry.getValue();
			answerReview.setAnswerReviewOutcome(answerReviewModel.getOutcome());
			answerReview.setComment(answerReviewModel.getComment());
			answerReview.setReview(review);
			answerReview.setSubmissionAnswer( submissionAnswerMap.get(answerReviewModelEntry.getKey()) );
			//Not supporting edited entry for the prototype.
			review.getAnswerReviews().add(answerReview);
		}
		//Map data file reviews
		for( Entry<Long, SubmittedFileReviewModel> fileReviewModelEntry : submissionReviewModel.getFileReviews().entrySet()){
			FileReview fileReview = new FileReview();
			Long fileId = fileReviewModelEntry.getKey();
			SubmittedFileReviewModel fileReviewModel = fileReviewModelEntry.getValue();
			fileReview.setSubmissionData(submissionDataDao.findById(fileId));
			fileReview.setReviewOutcome(fileReviewModel.getReviewOutcome());
			fileReview.setComments(fileReviewModel.getComments());
			fileReview.setReview(review);
			review.getFileReviews().add(fileReview);
		}
	}
	
	
	
	
	
    private SubmissionStatus updateSubmissionStatusBasedOnReviewOutcome(Long submissionId, ReviewOutcome reviewOutcome, Date reviewDate, Boolean peerReview){
    	SubmissionStatus status = null;
    	if(ReviewOutcome.REJECTED.equals(reviewOutcome )){
    		status = SubmissionStatus.DISCARDED;
    	}else if(ReviewOutcome.MOD_REQUESTED.equals( reviewOutcome) ) {
    		status = SubmissionStatus.REJECTED;
    	}else if(Boolean.TRUE.equals(peerReview)){
    		status  = SubmissionStatus.PEER_REVIEWED;
    	}else{
    		status  = SubmissionStatus.APPROVED;
    	}
    	submissionDao.updateSubmissionStatusAndLastReviewDate(submissionId, status, reviewDate);
    	return status;
	}
	
	@Override
	@Transactional //Won't return submissions with review locks by other reviewers TODO Remove PEER Review submissions
	public List<Submission> getSubmissionsAvailableForReview() {
		List<Submission> submissionList = retrieveReviewableSubmissions();
		//Now filter out any submissions owned by groupUsers of any status but peer reviewed.
		removeGroupOwnedPeerReviewableSubmissions(submissionList);
		return submissionList;
	}
	
	private void removeGroupOwnedPeerReviewableSubmissions(List<Submission> submissionList){
		//If a user is in a group that has peer review active,  only display the submission if it is Status.PEER_REVIEWED
		List<SharedUser> groupMemberList = groupAdminService.getAllUsersInGroupsWithPeerReviewActive(true);
		if(submissionList != null && groupMemberList != null && groupMemberList.size() > 0){
			Iterator<Submission> subIter = submissionList.iterator();
			while(subIter.hasNext()){
				Submission sub = subIter.next();
				SharedUser submitter = sub.getSubmitter();
				if(groupMemberList.contains(submitter) && ! SubmissionStatus.PEER_REVIEWED.equals(sub.getStatus())){
					subIter.remove();
				}
			}
		}
	}
	
	
	private List<Submission> retrieveReviewableSubmissions(){
		String reviewer = securityService.getLoggedInUsername();
		List<Submission> submissionList = submissionDao.retrieveSubmissionsForReview(reviewer);
		if(submissionList != null && submissionList.size() > 0){
		    Set<Long> lockedSubmissionIds = reviewLockService.getLockedSubmissionIdsExcludingLocksByUser(reviewer);
		    if(lockedSubmissionIds.size() > 0){
		    	Iterator<Submission> iter = submissionList.iterator();
		    	while(iter.hasNext()){
		    		Submission sub = iter.next();
		    		if(lockedSubmissionIds.contains(sub.getId())){
		    			iter.remove();
		    		}
		    	}
		    }
		}
		return submissionList;
	}
	
	@Override @Transactional //A superuser submission does not get peer reviewed.
	public List<Submission> getSubmissionsAvailableForPeerReview() {
		//This works a bit differently, need to find all users in the group, EXCEPT superuser, then get all
		//reviewable submissions for those users.  Might use this method to then exclude submissions from
		//the getSubmissionsAvailableForReview method.  Keep the lock thing in place. 
		
		//So, first find the users in groups for the logged in user
		List<SharedUser> groupUsers = groupAdminService.retrieveSharedUsersInGroupsWithAdministrator(securityService.getLoggedInUsername(), true);
		if(groupUsers != null && groupUsers.size() > 0){
			List<Submission> submissionList = retrieveReviewableSubmissions();
			//Remove submissions not owned by a groupUser, and remove groupUser owned submissions that have status PEER_REVIEWED
			Iterator<Submission> subIter = submissionList.iterator();
			while(subIter.hasNext()){
				Submission sub = subIter.next();
				if(! groupUsers.contains(sub.getSubmitter()) || SubmissionStatus.PEER_REVIEWED.equals(sub.getStatus())){
					subIter.remove();
				}
			}
			return submissionList;
		}
		
		return new ArrayList<Submission>();
	}

	@Override
	@Transactional
	public void processRejection(SubmissionModel submission, SubmissionReviewModel reviewModel, SubmissionRejectionModel rejectionModel, HttpServletRequest request) {
		SubmissionStatus preReviewStatus = submission.getStatusObj();
		saveSubmissionReview(reviewModel);
		ReviewOutcome outcome = reviewModel.getReviewOutcome();
		if(ReviewOutcome.MOD_REQUESTED.equals(outcome)){
			String emailText = rejectionModel.getRejectionEmail();
			try {
				emailText = freemarkerService.getRejectionDetailsEmailText(submission, reviewModel, rejectionModel, request);
			} catch (IOException e) {
				logger.error("Failed to send email", e);
			} catch (TemplateException e) {
				logger.error("Failed to send email", e);
			}
			notificationEmailService.asyncProcessNotification("AEKOS Submission Rejected", emailText, submission.getSubmittedByUsername(), NotificationType.REJECTION);
			if(SubmissionStatus.PEER_REVIEWED.equals(preReviewStatus)){
				sendGroupAdminRejectionNotification(emailText, submission.getSubmissionId());
			}
			
			//It has been asked to send the rejection email to the reviewer so they can keep a record of what they have said
			notificationEmailService.asyncProcessNotification("Reviewer Record - Submission Rejected  submission id:" + submission.getSubmissionId().toString(),
					                                           "Rejected Submission Email Sent\n-------------------------------\n" + emailText, 
					                                           securityService.getLoggedInUsername(), 
					                                           NotificationType.REVIEWER_REJECTION);
		}else{
			//This is an outright submission rejection
			notificationEmailService.asyncProcessNotification("AEKOS Submission Rejected", rejectionModel.getRejectionEmail(), submission.getSubmittedByUsername(), NotificationType.REJECTION);
		}
	}

	private void sendGroupAdminRejectionNotification(String emailText, Long submissionId){
		List<SharedUser> administratorList = groupAdminService.getGroupAdministratorsForSubmission(submissionId);
		String peerReviewerEmail = "A submission you have peer reviewed has been rejected.\nThe notification below  has been sent to the submission owner.\n\n" + emailText;
		if(administratorList != null ){
		    for(SharedUser su : administratorList){
			    notificationEmailService.asyncProcessNotification("Group Submission Rejected", peerReviewerEmail, su.getUsername(), NotificationType.GROUP_REVIEW_REJECTION);
		    }
		}
	}
	
	
	@Transactional
	public SubmissionReviewModel getLastReviewForSubmission(Long submissionId) {
		SubmissionReview sr = submissionReviewDao.getLastReviewForSubmissionId(submissionId);
		if(sr == null){
			return null;
		}
		return mapSubmissionReviewToReviewModel(sr);
	}
	
	private SubmissionReviewModel mapSubmissionReviewToReviewModel(SubmissionReview sr){
		SubmissionReviewModel submissionReviewModel = new SubmissionReviewModel();
		submissionReviewModel.setSubmissionReviewId( sr.getId() );
		submissionReviewModel.setNotes(sr.getNotes());
		submissionReviewModel.setReviewOutcome(sr.getReviewOutcome());
		submissionReviewModel.setSubmissionId( sr.getSubmission().getId() );
		submissionReviewModel.setReviewer( sr.getReviewer().getUsername() );
		boolean hasRejectedAnswers = false;
		for(AnswerReview answerReview : sr.getAnswerReviews()){
			AnswerReviewModel answerReviewModel = new AnswerReviewModel(answerReview);
			if(AnswerReviewOutcome.REJECT.equals(answerReview.getAnswerReviewOutcome() ) ){
				hasRejectedAnswers = true;
			}
			submissionReviewModel.getAnswerReviews().put(answerReview.getQuestionId(), answerReviewModel );
		}
		boolean hasRejectedFiles = false;
		for(FileReview fileReview : sr.getFileReviews() ){
			SubmittedFileReviewModel fileReviewModel = new SubmittedFileReviewModel();
			fileReviewModel.setComments( fileReview.getComments() );
			fileReviewModel.setReviewOutcome(fileReview.getReviewOutcome());
			fileReviewModel.setSubmissionDataId(fileReview.getSubmissionData().getId());
			if(SubmissionFileReviewOutcome.REJECT.equals(fileReview.getReviewOutcome() ) ){
				hasRejectedFiles = true;
			}
			submissionReviewModel.getFileReviews().put(fileReview.getSubmissionData().getId(), fileReviewModel);
		}
		submissionReviewModel.setHasRejectedAnswers(hasRejectedAnswers);
		submissionReviewModel.setHasRejectedFiles(hasRejectedFiles);
		submissionReviewModel.setReviewDate(sr.getReviewDate());
		return submissionReviewModel;
	}

	@Override
	public SubmissionReviewModel buildSubmissionReviewModel(
			SubmissionReview submissionReview) {
		return mapSubmissionReviewToReviewModel(submissionReview);
	}

	@Override
	@Transactional
	public Map<String, ReviewAnswerHistoryIndicator> buildAnswerHistoryMap( Long submissionId) {
		Map<String, ReviewAnswerHistoryIndicator> reviewAnswerHistoryMap = new HashMap<String, ReviewAnswerHistoryIndicator>();
		Submission sub = submissionDao.findById(submissionId);
		Map<String, SubmissionAnswer> currentAnswerMap = sub.getAnswersMappedByQuestionId();
		List<SubmissionHistory> subHistoryList = submissionHistoryDao.retrieveSubmissionHistoryForSubmission(submissionId);
		if(subHistoryList == null || subHistoryList.size() == 0){
			return reviewAnswerHistoryMap;
		}
		SubmissionHistory latestHistory = subHistoryList.get(0);
		Map<String, SubmissionAnswer> historyAnswerMap = latestHistory.getAnswersMappedByQuestionId();

		SubmissionReviewModel sr = getLastReviewForSubmission( submissionId );
		Map<String, AnswerReviewModel> answerReviewMap = sr.getAnswerReviews();
		
		for( Map.Entry<String, SubmissionAnswer> currAnswerEntry : currentAnswerMap.entrySet() ){
			String questId = currAnswerEntry.getKey();
			SubmissionAnswer currAnswer = currAnswerEntry.getValue();
			AnswerReviewModel lastReview = answerReviewMap.get(questId);
			if(lastReview != null && AnswerReviewOutcome.REJECT.equals( lastReview.getOutcome()) ){
				reviewAnswerHistoryMap.put(questId, ReviewAnswerHistoryIndicator.WAS_REJECTED);
				continue;
			}
			SubmissionAnswer histAnswer = historyAnswerMap.get(questId);
			if(histAnswer == null || histAnswer.getId() != currAnswer.getId()){
				reviewAnswerHistoryMap.put(questId, ReviewAnswerHistoryIndicator.ANSWER_CHANGED);
				continue;
			}
			reviewAnswerHistoryMap.put(questId, ReviewAnswerHistoryIndicator.NO_CHANGE);
		}
		return reviewAnswerHistoryMap;
	}

	@Override
	@Transactional //Might need to put in a short ttl cache, or launch a job to populate other answer histories
	public AnswerReviewHistoryModel getReviewAnswerHistory(Long submissionId,
			String questionId) {
		//submissionReviewDao.getLastReviewForSubmissionId(submissionId);
		QuestionnaireConfigElement cachedQuestionnaire = questionnaireConfigCache.getQuestionnaireConfigBySubmissionId(submissionId);
		SubmissionAnswer sa = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, questionId);
		List<AnswerReview> answerReviewList = answerReviewDao.retrieveAnswerReviewsBySubmissionIdAndQuestionId(submissionId, questionId);
		if(ResponseType.MULTIPLE_QUESTION_GROUP.equals( sa.getResponseType() ) ){
			return buildAnswerReviewHistoryModelForQuestionSet(cachedQuestionnaire.getMultipleQuestionGroupMap().get(questionId) , sa, answerReviewList);
		}
		return buildAnswerReviewHistoryModel(cachedQuestionnaire.getQuestionMap().get(questionId), sa, answerReviewList);
	}
	
	private AnswerReviewHistoryModel buildAnswerReviewHistoryModel(Question question, SubmissionAnswer sa, List<AnswerReview> answerReviewList){
		AnswerReviewHistoryModel model = new AnswerReviewHistoryModel();
		model.setQuestionText(question.getText());
		//Now build the report rows
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(sa != null){
			String answerText = "";
			if(ResponseType.getIsMultiselect(sa.getResponseType())){
				answerText = getMultiselectAnswerText(sa);	
			}else{
				answerText = sa.getResponse() ;
			}
			AnswerReviewHistoryRow row = createRowFromSubmissionAnswer( sa, answerText, sdf);
			model.getReviewHistoryRows().add(row);
		}
		if(answerReviewList != null && answerReviewList.size() > 0 ){
			for(AnswerReview ansReview : answerReviewList ){
				String answerText = "";
				if(ResponseType.getIsMultiselect( ansReview.getSubmissionAnswer().getResponseType()) ){
					answerText =  getMultiselectAnswerText(ansReview.getSubmissionAnswer()) ;
				}else{
					answerText = ansReview.getSubmissionAnswer().getResponse();
				}
				AnswerReviewHistoryRow row = createRowFromAnswerReview(ansReview, answerText, sdf);
				model.getReviewHistoryRows().add(row);
			}
		}
		return model;
	}
	
	private AnswerReviewHistoryModel buildAnswerReviewHistoryModelForQuestionSet(MultipleQuestionGroup mqg, SubmissionAnswer sa, List<AnswerReview> answerReviewList){
		AnswerReviewHistoryModel model = new AnswerReviewHistoryModel();
		model.setQuestionText(mqg.getText());
		//Now build the report rows
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(sa != null){
			String answerText = getAnswerTextForQuestionSetAnswer(mqg , sa);
			AnswerReviewHistoryRow row = createRowFromSubmissionAnswer( sa, answerText, sdf);
			model.getReviewHistoryRows().add(row);
		}
		if(answerReviewList != null && answerReviewList.size() > 0 ){
			for(AnswerReview ansReview : answerReviewList ){
				String answerText = getAnswerTextForQuestionSetAnswer(  mqg, ansReview.getSubmissionAnswer() );
				AnswerReviewHistoryRow row = createRowFromAnswerReview(ansReview, answerText, sdf);
				model.getReviewHistoryRows().add(row);
			}
		}
		return model;
	}
	
	private AnswerReviewHistoryRow createRowFromSubmissionAnswer( SubmissionAnswer sa, String answerText,SimpleDateFormat sdf){
		AnswerReviewHistoryRow row = new AnswerReviewHistoryRow();
		row.setAnswerText( answerText  );
		String submittedBy = sa.getSubmission().getSubmitter().getUsername();
		Date submittedDate = sa.getSubmission().getSubmissionDate();
		row.setContextLine1("Last submitted by " + submittedBy);
		row.setContextLine2("on " + sdf.format(submittedDate));
		return row;
	}
	
	private AnswerReviewHistoryRow createRowFromAnswerReview( AnswerReview ansReview, String answerText,SimpleDateFormat sdf){
		AnswerReviewHistoryRow row = new AnswerReviewHistoryRow();
		row.setAnswerText(answerText);
		Date reviewDate = ansReview.getReview().getReviewDate();
		String reviewer = ansReview.getReview().getReviewer().getUsername();
		row.setContextLine1("Reviewed by " + reviewer);
		row.setContextLine2("on " + sdf.format(reviewDate));
		row.setReviewStatus(ansReview.getAnswerReviewOutcome().name());
		row.setComments(ansReview.getComment());
		return row;
	}
	private String getMultiselectAnswerText(SubmissionAnswer sa){
		StringBuilder response = new StringBuilder("");
		if(sa.getMultiselectAnswerList() == null || sa.getMultiselectAnswerList().size() == 0){
			return "";
		}
		//lets return some inline html of a table with the responses
		response.append("<table>");
		for(SubmissionAnswer msSA : sa.getMultiselectAnswerList() ){
			response.append("<tr><td>").append(msSA.getResponse()).append("</td></tr>");
		}
		response.append("</table>");
		return response.toString();
	}
	
	
	private String getAnswerTextForQuestionSetAnswer( MultipleQuestionGroup mqg, SubmissionAnswer sa ){
		if(sa.getQuestionSetList() == null || sa.getQuestionSetList().size() == 0){
			return "";
		}
		StringBuilder sb = new StringBuilder("");
		sb.append("<table>");
		for(QuestionSetEntity questionSet : sa.getQuestionSetList() ){
			for(Object obj : mqg.getElements() ){
				Question q = (Question) obj;
				sb.append("<tr><td>").append(q.getText()).append("</td><td>");
				SubmissionAnswer qsa = questionSet.getAnswerMap().get(q.getId() );
				if(qsa != null && StringUtils.hasLength(qsa.getResponse() )){
					sb.append(qsa.getResponse());
				}
				sb.append("</td></tr>" );
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	private void deleteIncompleteReviewsForSubmission(Long submissionId){
		List<SubmissionReview> savedReviewList = submissionReviewDao.getSavedReviewsForSubmissionId(submissionId);
		if(savedReviewList != null && savedReviewList.size() > 0){
			for(SubmissionReview sr : savedReviewList){
				submissionReviewDao.delete(sr);
			}
		}
	}
	
	
	@Override @Transactional
	public void repopulateSavedReview(SubmissionReviewModel reviewModel) {
		SubmissionReview savedReview = submissionReviewDao.getSavedReviewForSubmissionId(reviewModel.getSubmissionId());
		if(savedReview != null){
			reviewModel.setNotes(reviewModel.getNotes());
			for(AnswerReview ar : savedReview.getAnswerReviews()){
				reviewModel.getAnswerReviews().put(ar.getQuestionId(), new AnswerReviewModel(ar) );
			}
			for(FileReview fr : savedReview.getFileReviews()){
				reviewModel.getFileReviews().put(fr.getSubmissionData().getId(), new SubmittedFileReviewModel(fr));
			}
		}
	}

	
	
}
