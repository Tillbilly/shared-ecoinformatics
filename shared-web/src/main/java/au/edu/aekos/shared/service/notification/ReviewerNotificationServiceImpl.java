package au.edu.aekos.shared.service.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import freemarker.template.TemplateException;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroupAdmin;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;

@Service
public class ReviewerNotificationServiceImpl implements ReviewerNotificationService{

	private Logger logger = LoggerFactory.getLogger(ReviewerNotificationServiceImpl.class);
	
	@Autowired
	private NotificationEmailService notificationEmailService;
	
	@Autowired
	private FreemarkerEmailTemplateService freemarkerEmailTemplateService;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private GroupAdminService groupAdminService;
	
	@Value(value="${shared.reviewalert.active}")
	private Boolean reviewAlertActive = Boolean.FALSE;
	
	@Override @Transactional
	public void notifyReviewersAboutSubmissionForReview(Long submissionId) {
		if( ! reviewAlertActive){
			logger.info("Review alert diasabled");
			return;
		}
		List<SharedUser> reviewerList = getReviewersForSubmission(submissionId);
		Submission sub = submissionDao.findById(submissionId);
		for(SharedUser su : reviewerList){
		    try {
				String emailText = freemarkerEmailTemplateService.getReviewAlertEmailText(su.getUsername(), sub.getTitle(), submissionId.toString(), sub.getSubmittingUsername());
				notificationEmailService.asyncProcessNotification("Submission Review Alert", emailText, su.getUsername(), NotificationType.REVIEW_ALERT);
		    } catch (IOException e) {
				logger.error("Error processing Review Alert for submission ID " + submissionId.toString(), e);
			} catch (TemplateException e) {
				logger.error("Error processing Review Alert for submission ID " + submissionId.toString(), e);
			}
		}
	}
	
	/**
	 * If the submission is owned by group member, and the group has peer review active,
	 * then notify the super user for the group ( SharedRole.ROLE_GROUP_ADMIN ).
	 * 
	 * Otherwise, notify all reviewers.
	 * 
	 * 
	 * 
	 * @param submissionId
	 * @return
	 */
	private List<SharedUser> getReviewersForSubmission(Long submissionId){
		List<SharedUser> peerReviewList = getPeerReviewerList(submissionId);
		if(peerReviewList.size() > 0){
			return peerReviewList;
		}
		//If no peer review action, return all reviewers
		return sharedUserDao.findActiveSharedUsersByRole(SharedRole.ROLE_REVIEWER);
	}

	private List<SharedUser> getPeerReviewerList(Long submissionId){
		List<SharedUser> peerReviewerList = new ArrayList<SharedUser>();
		Submission sub = submissionDao.findById(submissionId);
		if(SubmissionStatus.PEER_REVIEWED.equals(sub.getStatus())){
			return peerReviewerList;
		}
		SharedUser owner = sub.getSubmitter();
		//If the owner is a groupAdministrator, the submission does not need to be peer reviewed.
		List<UserGroup> adminGroupList = groupAdminService.retrieveGroupsForAdminUser(owner.getUsername());
		if(adminGroupList != null && adminGroupList.size() > 0){
			return peerReviewerList;
		}
		
		List<UserGroup> groupList = groupAdminService.retrieveGroupsWithUserMember(owner.getUsername());
		if(groupList != null && groupList.size() > 0){
			for(UserGroup ug : groupList){
				if(Boolean.TRUE.equals(ug.getPeerReviewActive())){
					for(UserGroupAdmin uga : ug.getGroupAdministratorList()){
						peerReviewerList.add(uga.getAdministrator());
					}
				}
			}
		}
		return peerReviewerList;
	}
}
