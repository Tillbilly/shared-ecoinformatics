package au.edu.aekos.shared.web.controllers.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionLink;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;
import au.edu.aekos.shared.service.groupadmin.GroupAdminServiceException;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.SubmissionCloneService;
import au.edu.aekos.shared.service.submission.SubmissionLinkService;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.service.submission.SubmissionSearchService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.web.controllers.ManageSubmissionController;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;
import au.edu.aekos.shared.web.model.LinkSubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionModel;

@Controller
public class GroupAdminController {
	private final Logger logger = LoggerFactory.getLogger(GroupAdminController.class);
	
	@Autowired
	private SubmissionSearchService submissionSearchService;
	@Autowired
	private SubmissionModelService submissionModelService;
	@Autowired
	private SecurityService authService;
	@Autowired
	private GroupAdminService groupAdminService;
	@Autowired
	private ManageSubmissionController manageSubmissionController;
	@Autowired
	private NewSubmissionController newSubmissionController;
	@Autowired
	private SubmissionService submissionService;
	@Autowired
	private SubmissionLinkService submissionLinkService;
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	@Autowired
	private SubmissionCloneService submissionCloneService;
	//Late addition functionality - viewing all submissions in a group.  The user will have to have ROLE_GROUP_ADMIN to get here,
		//paths under /groupAdmin are secured to the ROLE_GROUP_ADMIN, see security-context.xml
	@RequestMapping(value="/groupAdmin/manageGroupSubmissions", method=RequestMethod.GET)
	public String viewGroupSubmissions(Model model){
		String currentUser = authService.getLoggedInUsername();
		Map<UserGroup, List<Submission>> groupSubmissionMap = submissionSearchService.getSubmissionsForGroupAdministrator(currentUser);
		model.addAttribute("groupAdminUsername", currentUser);
		model.addAttribute("groupSubmissionMap", groupSubmissionMap);
		return "group/manageGroupSubmissions";
	}
	
	@RequestMapping(value="/groupAdmin/viewSubmission", method=RequestMethod.GET)
	public String viewGroupSubmission(@RequestParam Long submissionId, Model model) throws Exception{
		//Need to check whether the groupAdministrator has the power to edit, delete etc on submissions
		model.addAttribute("groupAdminView", Boolean.TRUE);
		return manageSubmissionController.viewSubmission(submissionId, Boolean.FALSE, model);
	}
	
	/**
	 * Need to create the cloned submission, using existing tools, but make the owner the superuser.
	 * 
	 * Should the questionnaire version NOT be the same as the current version, 
	 * give the super user the opportunity to migrate to the current questionnaire version,
	 * with warnings.
	 * 
	 * @param submissionId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/groupAdmin/cloneSubmission", method=RequestMethod.GET)
	public String cloneGroupSubmission(@RequestParam Long submissionId, Model model) throws Exception{ 
		if(!groupAdminService.checkGroupAdminSubmissionPermission(submissionId)){
			model.addAttribute("groupAdminErrorMessage", "You do not have permission to clone submission " + submissionId);
			return viewGroupSubmissions(model);
		}
		//Get the submission in question, check the config version and see if 
		//its the same as the current config version.
		SubmissionModel submissionModel = submissionModelService.getSubmission(submissionId);
		Boolean configLatestVersion = questionnaireConfigService.isQuestionnaireConfigLatestVersion(submissionModel.getQuestionnaireConfigId());
		model.addAttribute("submission", submissionModel);
		model.addAttribute("configLatestVersion", configLatestVersion);
		model.addAttribute("clonedToUser", authService.getLoggedInUsername());
		
		return "/group/groupCloneSubmission";
	}
	
	@RequestMapping(value="/groupAdmin/executeClone", method=RequestMethod.GET)
	public String executeCloneGroupSubmission(@RequestParam Long submissionId, 
			                                  @RequestParam String cloneTitle, 
			                                  @RequestParam(required=false) Boolean migrate,  Model model) throws Exception{ 
		if(!groupAdminService.checkGroupAdminSubmissionPermission(submissionId)){
			model.addAttribute("groupAdminErrorMessage", "You do not have permission to clone submission " + submissionId);
			return viewGroupSubmissions(model);
		}
		Long clonedId = submissionCloneService.cloneSubmission(submissionId, authService.getLoggedInUsername(), cloneTitle, migrate);
		model.addAttribute("groupAdminMessage", "Clone Submission " + clonedId + " has been created.");
		return viewGroupSubmissions(model);
	}
	
	@RequestMapping(value="/groupAdmin/linkSubmission", method=RequestMethod.GET)
	public String linkSubmission(@RequestParam(value="submissionId", required=true) Long submissionId, @RequestParam(value="groupId", required=true) Long groupId, Model model) throws Exception, GroupAdminServiceException{
		SubmissionModel sub = submissionModelService.getSubmission(submissionId);
		model.addAttribute("submission", sub);
		//Won't worry about a model object here, we only want the submission metadata, not the answers
		List<Submission> linkableSubmissionList = submissionSearchService.getListOfLinkableSubmissionsForGroup(groupId, submissionId);
		model.addAttribute("linkableSubmissionList", linkableSubmissionList);
		List<SubmissionLink> submissionLinkList = submissionSearchService.getListOfLinkedSubmissions(submissionId);
		model.addAttribute("submissionLinkList", submissionLinkList);
		model.addAttribute("groupId", groupId);
		return "/group/groupLinkSubmissionConsole";
	}
	
	@RequestMapping(value="/groupAdmin/linkSubmission", method=RequestMethod.POST)
	public String processLinkSubmission(@ModelAttribute LinkSubmissionModel linkModel, Model model) throws Exception, GroupAdminServiceException{
		logger.info("Linking submissions");
		submissionLinkService.linkSubmissions(linkModel.getLinkFromSubmissionId(), 
				                              linkModel.getLinkToSubmissionId(), 
				                              linkModel.getLinkType(), 
				                              linkModel.getDescription() );
		return linkSubmission(linkModel.getLinkFromSubmissionId(), linkModel.getGroupId(), model);
	}
	
	@RequestMapping(value="/groupAdmin/unlinkSubmissions/{groupId}/{sourceSubmissionId}/{targetSubmissionId}", method=RequestMethod.GET)
	public String unlinkSubmissions(@PathVariable Long groupId, @PathVariable Long sourceSubmissionId, @PathVariable Long targetSubmissionId, Model model) throws Exception, GroupAdminServiceException{
		logger.info("unlinking submissions " + sourceSubmissionId.toString() + "  " + targetSubmissionId.toString() );
		submissionLinkService.unlinkSubmissions(sourceSubmissionId, targetSubmissionId);
		return linkSubmission(sourceSubmissionId, groupId, model);
	}

	
	
	@RequestMapping(value="/groupAdmin/editSubmission", method=RequestMethod.GET)
	public String editGroupSubmissions(@RequestParam Long submissionId, 
			                           Model model, 
			                           HttpSession session,
			                           HttpServletResponse response) throws Exception{
		//hmmm need to do this twice . . . 
		if(!groupAdminService.checkGroupAdminSubmissionPermission(submissionId)){
			model.addAttribute("groupAdminErrorMessage", "You do not have permission to edit submission " + submissionId);
			return viewGroupSubmissions(model);
		}
		//Check the submission is in an editable state
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub.getStatus().isApproved()){
			model.addAttribute("groupAdminErrorMessage", "Submission " + submissionId + " is in a PUBLISHED state. Can not be edited.");
			return viewGroupSubmissions(model);
		}
		return newSubmissionController.launchGroupAdminSubmissionEdit(submissionId, model, session);
	}
	
	@RequestMapping(value="/groupAdmin/deleteSubmission", method=RequestMethod.GET)
	public String deleteGroupSubmissions(@RequestParam Long submissionId, Model model) throws Exception{
		if(!groupAdminService.checkGroupAdminSubmissionPermission(submissionId)){
			model.addAttribute("groupAdminErrorMessage", "You do not have permission to delete submission " + submissionId);
		}else{
			Submission sub = submissionService.retrieveSubmissionById(submissionId);
			if(sub.getStatus().isApproved()){
				model.addAttribute("groupAdminErrorMessage", "Submission " + submissionId + " is in a PUBLISHED state. Can not be deleted.");
			}else{
				submissionService.updateSubmissionStatus(submissionId, SubmissionStatus.DELETED);
				model.addAttribute("groupAdminMessage", "Submission " + submissionId + " has been deleted.");
			}
		}
		return viewGroupSubmissions(model);
	}
	
	@RequestMapping(value="/groupAdmin/transferSubmission", method=RequestMethod.GET)
	public String transferGroupSubmissions(@RequestParam Long submissionId, @RequestParam Long groupId, Model model) throws Exception, GroupAdminServiceException{
		if(!groupAdminService.checkGroupAdminSubmissionPermission(submissionId)){
			model.addAttribute("groupAdminErrorMessage", "You do not have permission to transfer submission " + submissionId);
			return viewGroupSubmissions(model);
		}
		UserGroup userGroup = groupAdminService.retrieveUserGroup(groupId);
		SubmissionModel submission = submissionModelService.getSubmission(submissionId);
		List<String> usernamesList = prepareTransferUsernamesList(submission.getSubmittedByUsername(), userGroup);
		model.addAttribute("userGroup", userGroup);
		model.addAttribute("submission", submission);
		model.addAttribute("usernamesList", usernamesList);
		return "/group/transferSubmission";
	}
	
	private List<String> prepareTransferUsernamesList(String currentSubmissionOwner, UserGroup userGroup){
		List<String> usernamesList = new ArrayList<String>();
		for(String username : userGroup.getListOfGroupUsernames()){
			if(! username.equals(currentSubmissionOwner)){
				usernamesList.add(username);
			}
		}
		return usernamesList;
	}
	
	
	@RequestMapping(value="/groupAdmin/executeTransfer", method=RequestMethod.GET)
	public String executeTransfer(@RequestParam Long submissionId, @RequestParam Long groupId, @RequestParam String transferTo, @RequestParam String message, Model model) throws Exception{
		groupAdminService.transferGroupSubmissionOwnership(submissionId, transferTo, message);
		return viewGroupSubmissions(model);
	}
	
	
	
}
