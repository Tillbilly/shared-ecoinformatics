package au.edu.aekos.shared.web.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.cache.reviewlock.ReviewLockService;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionLink;
import au.edu.aekos.shared.data.entity.SubmissionLinkType;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.service.index.SolrIndexService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.EmbargoService;
import au.edu.aekos.shared.service.submission.SubmissionLinkService;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.service.submission.SubmissionReviewService;
import au.edu.aekos.shared.service.submission.SubmissionSearchService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.web.model.EditEmbargoModel;
import au.edu.aekos.shared.web.model.LinkSubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;

@Controller
public class ManageSubmissionController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManageSubmissionController.class);
	
	@Autowired
	private SubmissionSearchService submissionSearchService;
	@Autowired
	private SubmissionService submissionService;
	@Autowired
	private SubmissionModelService submissionManager;
	@Autowired
	private SecurityService authService;
	@Autowired
	private SubmissionReviewService submissionReviewService;
	@Autowired
	private SubmissionLinkService submissionLinkService;
	@Autowired
	private ReviewLockService reviewLockService;
	@Autowired
	private EmbargoService embargoService;
	@Autowired
	private SolrIndexService solrIndexService;
	
	@RequestMapping(value="/manageSubmissions", method=RequestMethod.GET )
	public String manageSubmissions(Model model){
		String username = authService.getLoggedInUsername();
		model.addAttribute("username", username);
		model.addAttribute("userSubmissionList", submissionSearchService.getActiveSubmissionsForUser(username));
		return "manage_submissions";
	}
	
	@RequestMapping(value="/viewSubmission", method=RequestMethod.GET)
	public String viewSubmission(@RequestParam(value="submissionId", required=true) Long submissionId, 
			@RequestParam(value="showResponseType", required=false) Boolean showResponseType, Model model) throws Exception{
		SubmissionModel sub = submissionManager.getSubmission(submissionId);
		model.addAttribute("submission", sub);
		model.addAttribute("showResponseType", showResponseType);
		String username = authService.getLoggedInUsername();
		model.addAttribute("username", username);
		if (sub.getStatus().equals(SubmissionStatus.REJECTED.name()) ) {
			SubmissionReviewModel reviewModel = submissionReviewService.getLastReviewForSubmission(sub.getSubmissionId());
			model.addAttribute("review", reviewModel);
		}else if(sub.getStatus().equals(SubmissionStatus.REJECTED_SAVED.name())){
			SubmissionReviewModel reviewModel = submissionReviewService.getLastReviewForSubmission(sub.getDraftForSubmissionId());
			model.addAttribute("review", reviewModel);
		}
		List<SubmissionLink> submissionLinks = submissionSearchService.getListOfLinkedSubmissions(submissionId);
		model.addAttribute("submissionLinks", submissionLinks);
		model.addAttribute("lockedForReview", Boolean.valueOf(reviewLockService.submissionLockedForReview(submissionId)));
		return "viewSubmission";
	}
	
	@RequestMapping(value="/viewSubmissionGroupAdmin", method=RequestMethod.GET)
	public String viewSubmissionGroupAdmin(@RequestParam(value="submissionId", required=true) Long submissionId, Model model) throws Exception{
		model.addAttribute("groupAdminView", Boolean.TRUE);
		return viewSubmission(submissionId, Boolean.FALSE, model);
	}
	
	@RequestMapping(value="/linkSubmission", method=RequestMethod.GET)
	public String linkSubmission(@RequestParam(value="submissionId", required=true) Long submissionId, Model model) throws Exception{
		SubmissionModel sub = submissionManager.getSubmission(submissionId);
		model.addAttribute("submission", sub);
		//Won't worry about a model object here, we only want the submission metadata, not the answers
		List<Submission> linkableSubmissionList = submissionSearchService.getListOfLinkableSubmissionsForUser(authService.getLoggedInUsername(), submissionId);
		model.addAttribute("linkableSubmissionList", linkableSubmissionList);
		List<SubmissionLink> submissionLinkList = submissionSearchService.getListOfLinkedSubmissions(submissionId);
		model.addAttribute("submissionLinkList", submissionLinkList);
		model.addAttribute("linkTypes", SubmissionLinkType.getCreatableLinkTypes());
		return "linkSubmissionConsole";
	}
	
	@RequestMapping(value="/linkSubmission", method=RequestMethod.POST)
	public String processLinkSubmission(@ModelAttribute LinkSubmissionModel linkModel, Model model) throws Exception{
		logger.info("Linking submissions");
		submissionLinkService.linkSubmissions(linkModel.getLinkFromSubmissionId(), 
				                              linkModel.getLinkToSubmissionId(), 
				                              linkModel.getLinkType(), 
				                              linkModel.getDescription());
		return linkSubmission(linkModel.getLinkFromSubmissionId(), model);
	}
	
	@RequestMapping(value="/unlinkSubmissions/{sourceSubmissionId}/{targetSubmissionId}", method=RequestMethod.GET)
	public String unlinkSubmissions(@PathVariable Long sourceSubmissionId, @PathVariable Long targetSubmissionId, Model model) throws Exception{
		logger.info("unlinking submissions " + sourceSubmissionId.toString() + "  " + targetSubmissionId.toString() );
		submissionLinkService.unlinkSubmissions(sourceSubmissionId, targetSubmissionId);
		return linkSubmission(sourceSubmissionId, model);
	}
	
	@RequestMapping(value="/deleteSubmission", method=RequestMethod.GET)
	public String deleteSubmission(@RequestParam(value="submissionId", required=true) Long submissionId, Model model) throws Exception{
		submissionService.deleteSubmissionIfCurrentUserAuthorised(submissionId);
		return manageSubmissions(model);
	}
	
	
	@RequestMapping(value="/admin/editEmbargo", method=RequestMethod.GET)
	public String editEmbargo(@RequestParam(value="submissionId", required=true) Long submissionId, Model model) throws Exception{
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub == null){
			return "home";
		}
		model.addAttribute("embargoModel" , new EditEmbargoModel(sub.getSubmittingUsername(), submissionId, sub.getTitle(),  embargoService.getCurrentEmbargoDate(submissionId)));
		return "admin/editEmbargo";
	}
	
	@RequestMapping(value="/admin/processEditEmbargo", method=RequestMethod.POST)
	public String processEditEmbargo(@ModelAttribute EditEmbargoModel editEmbargoModel, Model model ) throws Exception{
		Submission sub = submissionService.retrieveSubmissionById(editEmbargoModel.getSubmissionId());
		if(sub == null){
			return "home";
		}
		embargoService.updateEmbargoDate(editEmbargoModel.getSubmissionId(), editEmbargoModel.getEmbargoDate());
		if(SubmissionStatus.PUBLISHED.equals(sub.getStatus())){ //Only need to re-index published submissions
		    solrIndexService.addSubmissionToSolr(sub); //The index service retrieves a fresh submission from the db for indexing purposes
		}
		return viewSubmission(editEmbargoModel.getSubmissionId(), false, model);
	}
	
	//Late addition functionality - viewing all submissions in a group.  The user will have to have ROLE_GROUP_ADMIN to get here,
	//paths under /groupAdmin are secured to the ROLE_GROUP_ADMIN, see security-context.xml
	@RequestMapping(value="/groupAdmin/viewGroupSubmissions", method=RequestMethod.GET)
	public String viewGroupSubmissions(Model model){
		String currentUser = authService.getLoggedInUsername();
		Map<UserGroup, List<Submission>> groupSubmissionMap = submissionSearchService.getSubmissionsForGroupAdministrator(currentUser);
		model.addAttribute("groupAdminUsername", currentUser);
		model.addAttribute("groupSubmissionMap", groupSubmissionMap);
		return "group/viewGroupSubmissions";
	}
}
