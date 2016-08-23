package au.edu.aekos.shared.web.controllers.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.cache.reviewlock.ReviewLockService;
import au.edu.aekos.shared.service.notification.FreemarkerEmailTemplateService;
import au.edu.aekos.shared.service.publication.AsyncPublishService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.service.submission.SubmissionReviewService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.valid.SubmissionReviewValidator;

@Controller
public class PeerReviewController {
	private static final String PEER_REVIEW_SUBMISSIONS_URI = "peerReviewSubmissions";

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
	private SecurityService securityService;

	@Autowired
	private ReviewLockService reviewLockService;
	
	@RequestMapping(value="/groupAdmin/" + PEER_REVIEW_SUBMISSIONS_URI, method=RequestMethod.GET )
	public String listSubmissionsForReview(@RequestParam(required=false) Boolean clearLock, @RequestParam(required=false) Long submissionId, Model model){
		if(Boolean.TRUE.equals(clearLock) && submissionId != null){
			reviewLockService.removeLock(submissionId, securityService.getLoggedInUsername()); 
		}
		model.addAttribute("submissionList", submissionReviewService.getSubmissionsAvailableForPeerReview());
		model.addAttribute("peerReview",Boolean.TRUE);
		model.addAttribute("currMappedUri", PEER_REVIEW_SUBMISSIONS_URI);
		return "submissionsToReviewList";
	}
}
