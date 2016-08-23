package au.edu.aekos.shared.web.controllers.admin;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.SharedAuthority;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;
import au.edu.aekos.shared.service.groupadmin.GroupAdminServiceException;
import au.edu.aekos.shared.service.security.RegistrationService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.web.model.ActiveSessionModel;
import au.edu.aekos.shared.web.model.EditGroupUsersModel;
import freemarker.template.TemplateException;

//Adminstrator functions for user management.  User management of their details handled in UserAdminController
@Controller 
public class UserManagementController {

	Logger logger = LoggerFactory.getLogger(UserManagementController.class);
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private GroupAdminService groupAdminService;
	
	@RequestMapping("admin/userManagement")
	public String manageUsersList(Model model){
		model.addAttribute("userList", sharedUserDao.getAllSharedUsers() );
		return "admin/userManagement";
	}
	
	@RequestMapping("admin/editRoles")
	public String editUserRoles(@RequestParam String username, Model model){
		SharedUser user = sharedUserDao.findUserByUsername(username);
		model.addAttribute("user", user);
		//Create the initial role lists to pick from
		Set<SharedAuthority> roles =  user.getRoles();
		List<SharedRole> userRoles = new ArrayList<SharedRole>();
		List<SharedRole> availableRoles = new ArrayList<SharedRole>();
		for(SharedAuthority sa : roles){
			userRoles.add(sa.getSharedRole());
		}
		for(SharedRole role : SharedRole.values() ){
			if(!userRoles.contains(role)){
				availableRoles.add(role);
			}
		}
		model.addAttribute("userRoleList", userRoles);
		model.addAttribute("availableRoleList", availableRoles);
		return "admin/editUserRoles";
	}
	
	@RequestMapping("admin/processEditRoles") 
	public String processEditRoles(@RequestParam(required=true) String username, 
			                       @RequestParam(required=false) String roles, 
			                       @RequestParam(required=false) Boolean removeAllRoles, 
			                       Model model){
		if(removeAllRoles != null && removeAllRoles){
			securityService.updateUserRoles(username, new ArrayList<SharedRole>());
		}else{
			List<SharedRole> sharedRoles = new ArrayList<SharedRole>();
			if(! roles.contains(",")){
				SharedRole sr = SharedRole.valueOf(roles.trim());
				sharedRoles.add(sr);
			}else{
			    String [] roleArray = roles.split(",");
			    for(String roleStr : roleArray ){
			    	SharedRole sr = SharedRole.valueOf(roleStr);
			    	sharedRoles.add(sr);
			    }
			}
			securityService.updateUserRoles(username, sharedRoles);
		}
		model.addAttribute("message","Roles for " + username +" changed.");
		return manageUsersList(model);
	}
			
	@RequestMapping("admin/disableUser")
    public String disableUser(@RequestParam String username, Model model){
		sharedUserDao.setSharedUserActive(username, false);
		model.addAttribute("message","User " + username +" disabled.");
		return manageUsersList(model);
	}
	
	@RequestMapping("admin/activateUser")
	public String activateUser(@RequestParam String username, Model model){
		sharedUserDao.setSharedUserActive(username, true);
		model.addAttribute("message","User " + username +" activated.");
		return manageUsersList(model);
	}
	
	/**
	 * Was going to set the password to the users name part of their email,  but Java Hmac encryption gives
	 * a different result when the username or password contain non word characters.  could'nt work out why.
	 * 
	 * So,  just send the user a change password email . . .
	 * 
	 * @param username
	 * @param model
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	@RequestMapping("admin/resetPassword") 
	public String resetPassword(@RequestParam String username, HttpServletRequest request, Model model) throws InvalidKeyException, NoSuchAlgorithmException, IOException, TemplateException{
		registrationService.sendUserChangePasswordEmail(username, request);
		model.addAttribute("message","Reset Password email sent to user " + username );
		return manageUsersList(model); 
	}
	
	@RequestMapping("admin/manageGroups")
	public String manageGroups(@RequestParam String username, Model model){
		//model.addAttribute("userList", sharedUserDao.getActiveSharedUsersForGroupAdmin(username) );
		model.addAttribute("groupAdminUsername", username );
		model.addAttribute("groupList", groupAdminService.retrieveGroupsForAdminUser(username));
		model.addAttribute("newGroup", new UserGroup());
		
		return "admin/manageGroups";
	}
	
	@RequestMapping(value="admin/addGroup/{groupAdmin:.+}",method=RequestMethod.POST)
	public String addGroup(@PathVariable String groupAdmin, @ModelAttribute("newGroup") UserGroup fboGroup, Model model){
		if(!StringUtils.hasLength(fboGroup.getName())){
			model.addAttribute("message", "Need to specify group name");
			return manageGroups(groupAdmin, model);
		}
		try {
			groupAdminService.createNewGroupForUser(groupAdmin, fboGroup.getName(), fboGroup.getOrganisation());
		} catch (GroupAdminServiceException e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("message", e.getMessage());
			
		}
		return manageGroups(groupAdmin, model);
	}
	
	@RequestMapping(value="admin/deleteGroup")
	public String deleteGroup(@RequestParam Long groupId, @RequestParam String adminUsername, Model model){
		try {
			groupAdminService.deleteUserGroup(groupId);
			model.addAttribute("message", "Group Deleted");
		} catch (GroupAdminServiceException e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("message", e.getMessage());
		}
		return manageGroups(adminUsername, model);
	}
	
	@RequestMapping(value="admin/editGroupMembers")
	public String editGroupMembers(@RequestParam Long groupId, @RequestParam String adminUsername, Model model){
		model.addAttribute("message", "Edit group members");
		UserGroup group = null;
		try {
			group = groupAdminService.retrieveUserGroup(groupId);
		} catch (GroupAdminServiceException e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("message", e.getMessage());
			return manageGroups(adminUsername, model);
		}
		Set<String> groupUsernames = new HashSet<String>();
		for(SharedUser su : group.getMemberList() ){
			groupUsernames.add(su.getUsername());
		}
		
		List<SharedUser> pickableUsers = groupAdminService.getPickableSharedUsersForGroup( groupUsernames);
		EditGroupUsersModel editGroupModel = new EditGroupUsersModel(group.getId(), adminUsername, group.getMemberList() , pickableUsers);
		
		model.addAttribute("group", group);
		model.addAttribute("adminUsername", adminUsername);
		model.addAttribute("editGroupModel", editGroupModel);
		
		return "admin/editGroupMembers";
	}
	
	@RequestMapping(value="admin/updateGroupMembers",method={RequestMethod.POST})
	public String updateGroupMembers(@ModelAttribute("editGroupModel") EditGroupUsersModel editGroupModel, Model model){
		Set<String> usersInGroup = editGroupModel.getInGroupMap().keySet();
		try {
			groupAdminService.updateGroupUsers(editGroupModel.getGroupId(), usersInGroup);
		} catch (GroupAdminServiceException e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("message", e.getMessage());
		}
		return manageGroups(editGroupModel.getGroupAdminUser(), model);
	}
	
	@RequestMapping(value="admin/togglePeerReview",method={RequestMethod.GET})
	public String togglePeerReview(@RequestParam Long groupId, @RequestParam String adminUsername, @RequestParam Boolean peerReview, Model model){
		groupAdminService.toggleEnablePeerReview(groupId, peerReview);
		return manageGroups(adminUsername, model	);
	}
	
	@RequestMapping(value="admin/changeGroupSuperuser",method={RequestMethod.GET})
	public String changeGroupSuperuser(@RequestParam Long groupId, @RequestParam String adminUsername, Model model) throws GroupAdminServiceException{
		UserGroup group = groupAdminService.retrieveUserGroup(groupId);
		model.addAttribute("group", group);
		model.addAttribute("adminUsername", adminUsername);
		return "admin/changeGroupSuperuser";
	}
	
	@RequestMapping(value="admin/processChangeSuperuser",method={RequestMethod.GET})
	public String processChangeGroupSuperuser(@RequestParam String username, @RequestParam Long groupId, Model model) throws GroupAdminServiceException{
		groupAdminService.changeGroupSuperuser(username, groupId);
	    return manageUsersList(model);
	}
	
	@RequestMapping(value="admin/activeSessions",method={RequestMethod.GET}) 
	public String viewActiveSessions(Model model, HttpServletRequest request){
		List<ActiveSessionModel> activeSessionList = securityService.getListOfUsersWithAnActiveSession(request.getSession().getMaxInactiveInterval());
		model.addAttribute("activeSessionList", activeSessionList);
		model.addAttribute("currentDate", new Date());
		return "admin/activeSessions";
	}
}
