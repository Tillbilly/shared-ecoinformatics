package au.edu.aekos.shared.service.groupadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.GroupSubmissionTransferDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.UserGroupAdminDao;
import au.edu.aekos.shared.data.dao.UserGroupDao;
import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.SharedAuthority;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.groupadmin.GroupAdminPrivilageEnum;
import au.edu.aekos.shared.data.entity.groupadmin.GroupSubmissionTransfer;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroupAdmin;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroupAdminPrivilage;
import au.edu.aekos.shared.service.notification.NotificationEmailService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.SubmissionService;

@Service
public class GroupAdminServiceImpl implements GroupAdminService {

	private Logger logger = LoggerFactory.getLogger(GroupAdminServiceImpl.class);
	@Autowired
	private SharedUserDao sharedUserDao;
	@Autowired
	private UserGroupDao userGroupDao;
	@Autowired
	private SecurityService authService;
	@Autowired
	private SubmissionService submissionService;
	@Autowired
	private NotificationEmailService notificationEmailService;
	@Autowired
	private GroupSubmissionTransferDao groupSubmissionTransferDao;
	@Autowired
	private UserGroupAdminDao userGroupAdminDao;
	
	@Override @Transactional
	public UserGroup createNewGroupForUser(String username, String groupName, String organisation) throws GroupAdminServiceException {
		SharedUser groupAdminUser = sharedUserDao.findUserByUsername(username);
		if(groupAdminUser == null){
			logger.error("No user with user name " + username);
			throw new GroupAdminServiceException("No user with user name " + username);
		}
		if(! groupAdminUser.getIsGroupAdministrator() ){
			logger.error("User " + username + " does not have Group Admin Role");
			throw new GroupAdminServiceException("User " + username + " does not have Group Admin Role");
		}
		
		UserGroup ug = new UserGroup();
		ug.setName(groupName);
		ug.setOrganisation(organisation);
		
		UserGroupAdmin uga = new UserGroupAdmin();
		uga.setAdministrator(groupAdminUser);
		uga.getPrivilages().add(new UserGroupAdminPrivilage(GroupAdminPrivilageEnum.GROUP_PEER_REVIEWER));
		uga.getPrivilages().add(new UserGroupAdminPrivilage(GroupAdminPrivilageEnum.GROUP_CONTACT));
		ug.getGroupAdministratorList().add(uga);
		
		//Add the admin user to the group too.
		ug.getMemberList().add(groupAdminUser);
		userGroupDao.saveOrUpdate(ug);
		return ug;
	}

	@Override @Transactional
	public List<UserGroup> retrieveGroupsForAdminUser(String username) {
		List<UserGroup> userGroupList = userGroupDao.retrieveGroupsForAdminUser(username);
		hydrateUserGroupList(userGroupList);
		return userGroupList;
	}
	
	@Override @Transactional
	public List<UserGroup> retrieveGroupsWithUserMember(String username) {
		List<UserGroup> userGroupList = userGroupDao.retrieveGroupsWithMember(username);
		hydrateUserGroupList(userGroupList);
		return userGroupList;
	}
	
	@Override @Transactional
	public List<SharedUser> retrieveSharedUsersInGroupsWithAdministrator(
			String username, boolean excludeAdministrator) {
		List<SharedUser> sharedUserList = new ArrayList<SharedUser>();
		List<UserGroup> adminGroupList = retrieveGroupsForAdminUser(username);
		if(adminGroupList != null && adminGroupList.size() > 0){
			Set<SharedUser> sharedUserSet = new HashSet<SharedUser>();
			for(UserGroup ug : adminGroupList){
				if(ug.getPeerReviewActive() != null && ug.getPeerReviewActive() ){
					for(SharedUser su : ug.getMemberList() ){
						if(! su.getUsername().equals(username) || ! excludeAdministrator ){
							sharedUserSet.add(su);
						}
					}
				}
			}
			sharedUserList.addAll(sharedUserSet);
		}
		return sharedUserList;
	}
	
	private void hydrateUserGroupList(List<UserGroup> userGroupList ){
		if(userGroupList == null){
			return;
		}
		//Lets hydrate everything we need, namely, the user lists, for display on the group admin page
		for(UserGroup ug : userGroupList){
			ug.getGroupAdministratorList().size();
			ug.getMemberList().size();
		}
		
	}

	@Override @Transactional
	public void addUsersToGroup(Long groupId, List<String> users) throws GroupAdminServiceException {
		List<SharedUser> newUserList = sharedUserDao.getSharedUsersByUsernames(users);
		if(newUserList == null || newUserList.size() == 0){
			logger.info("no users found to add to group ID " + groupId.toString());
			return;
		}
		
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.error("Can't add users to group, No group with ID:" + groupId.toString());
			throw new GroupAdminServiceException("No group with ID:" + groupId.toString());
		}
		//Don't want to add users twice if they are already group members
		Iterator<SharedUser> iter = newUserList.iterator();
		while(iter.hasNext()){
			SharedUser newUser = iter.next();
			for(SharedUser su : userGroup.getMemberList()){
				if(newUser.getUsername().equals(su.getUsername())){
					iter.remove();
					logger.info("Username " + newUser.getUsername() + " already a member of UserGroup " + userGroup.getName() );
					break;
				}
			}
		}
		if(newUserList.size() > 0){
			userGroup.getMemberList().addAll(newUserList);
			userGroupDao.saveOrUpdate(userGroup);
		}
	}

	@Override @Transactional
	public void removeUsersFromGroup(Long groupId, List<String> users) throws GroupAdminServiceException {
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.error("Can't remove users from group, No group with ID:" + groupId.toString());
			throw new GroupAdminServiceException("No group with ID:" + groupId.toString());
		}
		for(String userToRemove : users){
		    Iterator<SharedUser> userIterator = userGroup.getMemberList().iterator();
		    boolean userNotFound = true;
		    while(userIterator.hasNext()){
		    	SharedUser su = userIterator.next();
		    	if(su.getUsername().equals(userToRemove)){
		    		userNotFound = false;
		    		userIterator.remove();
		    		break;
		    	}
		    }
		    if(userNotFound){
		    	logger.info("User " + userToRemove + " is not a member of group " + userGroup.getName() + " so can not be removed.");
		    }
		}
		userGroupDao.saveOrUpdate(userGroup);
	}
	
	@Override @Transactional
	public void deleteUserGroup(Long groupId) throws GroupAdminServiceException {
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.error("Can't delete group, No group with ID:" + groupId.toString());
			throw new GroupAdminServiceException("No group with ID:" + groupId.toString());
		}
		userGroupDao.delete(userGroup);
	}

	@Override @Transactional
	public UserGroup retrieveUserGroup(Long groupId)
			throws GroupAdminServiceException {
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.error("No group with ID:" + groupId.toString());
			throw new GroupAdminServiceException("No group with ID:" + groupId.toString());
		}
		userGroup.getGroupAdministratorList().size();
		userGroup.getMemberList().size();
		return userGroup;
	}

	@Override @Transactional
	public void updateGroupUsers(Long groupId, Set<String> usersInGroup) throws GroupAdminServiceException {
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.error("Can't update group, No group with ID:" + groupId.toString());
			throw new GroupAdminServiceException("No group with ID:" + groupId.toString());
		}
		//First, remove users that are not in the group anymore
		Iterator<SharedUser> userIter = userGroup.getMemberList().iterator();
		while(userIter.hasNext()){
			SharedUser user = userIter.next();
			if(!usersInGroup.contains(user.getUsername())){
				userIter.remove();
			}
		}
		//Build a set of the users that remain
		Set<String> remainingUsers = new HashSet<String>();
		for(SharedUser su : userGroup.getMemberList()){
			remainingUsers.add(su.getUsername());
		}
		//Iterate over the input users set, adding a user if not already added.
		for(String username : usersInGroup){
			if(! remainingUsers.contains(username)){
                SharedUser newMember = sharedUserDao.findUserByUsername(username);
                userGroup.getMemberList().add(newMember);
			}
		}
		userGroupDao.saveOrUpdate(userGroup);
	}

	@Override @Transactional
	public boolean checkGroupAdminSubmissionPermission(Long submissionId) {
		//First check the logged in user is a group administrator, and retrieve their groups.
		String groupAdminUsername = authService.getLoggedInUsername();
		List<UserGroup> userGroupList = retrieveGroupsForAdminUser(groupAdminUsername);
		if(userGroupList == null || userGroupList.size() == 0){
			return false;
		}
		Submission submission = submissionService.retrieveSubmissionById(submissionId);
		if(submission == null){
			return false;
		}
		SharedUser submissionOwner = submission.getSubmitter();
		for(UserGroup group : userGroupList){  //Cater for possible multiple groups . . . 
			for(SharedUser groupMember : group.getMemberList() ){
				if(submissionOwner.getUsername().equals(groupMember.getUsername())){
					return true;
				}
			}
		}
		return false;
	}

	@Override @Transactional
	public void transferGroupSubmissionOwnership(Long submissionId,
			String toUser, String message) {
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		SharedUser originalOwner = sub.getSubmitter();
		SharedUser newOwner = sharedUserDao.findUserByUsername(toUser);
		SharedUser transferExecutorUser = sharedUserDao.findUserByUsername(authService.getLoggedInUsername());
		submissionService.changeSubmissionOwner(sub, newOwner);
		GroupSubmissionTransfer transferEntity = new GroupSubmissionTransfer();
		transferEntity.setSubmission(sub);
		transferEntity.setMessage(message);
		transferEntity.setTransferDate(new Date());
		transferEntity.setTransferFromUser(originalOwner);
		transferEntity.setTransferToUser(newOwner);
		transferEntity.setTransferredByUser(transferExecutorUser);
		groupSubmissionTransferDao.saveOrUpdate(transferEntity);
		String emailSubject = "SHaRED submission " + submissionId + " has been transferred to you";
		notificationEmailService.asyncProcessNotification(emailSubject, appendSubmissionTransferEmail(message, sub), toUser, NotificationType.TRANSFER);
	}
	
	private String appendSubmissionTransferEmail(String message, Submission submission){
		return message.concat("\n\n").concat("Title:").concat(submission.getTitle()).concat("\nID:").concat(submission.getId().toString());
	}

	@Override @Transactional
	public void toggleEnablePeerReview(Long groupId, Boolean enablePeerReview) {
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.warn("Attempt to set enable peer review " + enablePeerReview.toString() + " for group ID " + groupId + " failed.  Group does not exist.");
			return;
		}
		logger.info("Changing enable peer review to " + userGroupDao + " for group id " + groupId);
		userGroup.setPeerReviewActive(enablePeerReview);
		userGroupDao.saveOrUpdate(userGroup);
	}

	@Override @Transactional
	public List<SharedUser> getAllUsersInGroupsWithPeerReviewActive(
			boolean excludeAdministrators) {
		Set<SharedUser> sharedUserSet = new HashSet<SharedUser>();
		for(UserGroup group : userGroupDao.getAll() ){
			if(group.getPeerReviewActive() != null && group.getPeerReviewActive()){
				sharedUserSet.addAll(getUsersFromUserGroup(group, excludeAdministrators));
			}
		}
		List<SharedUser> suList = new ArrayList<SharedUser>();
		suList.addAll(sharedUserSet);
		return suList;
	}

    private Set<SharedUser> getUsersFromUserGroup(UserGroup group, boolean excludeAdministrators){
    	Set<SharedUser> sharedUsersInGroup = new HashSet<SharedUser>();
    	for(SharedUser su : group.getMemberList()){
    		if(! excludeAdministrators || ! isUserGroupAdministrator(su, group)){
    			sharedUsersInGroup.add(su);
    		}
    	}
    	return sharedUsersInGroup;
    }
    
    private boolean isUserGroupAdministrator(SharedUser su, UserGroup group){
    	for(UserGroupAdmin groupAdmin : group.getGroupAdministratorList() ){
    		if(su.getUsername().equals(groupAdmin.getAdministrator().getUsername())){
    			return true;
    		}
    	}
    	return false;
    }

	@Override @Transactional
	public List<SharedUser> getGroupAdministratorsForSubmission(
			Long submissionId) {
		List<SharedUser> groupAdministrators = new ArrayList<SharedUser>();
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		SharedUser submitter = sub.getSubmitter();
		List<UserGroup> userGroups = retrieveGroupsWithUserMember(submitter.getUsername());
		if(userGroups != null && userGroups.size() > 0){
			Set<SharedUser> groupAdminSet = new HashSet<SharedUser>();
			for(UserGroup ug : userGroups){
				for(UserGroupAdmin admin : ug.getGroupAdministratorList() ){
					groupAdminSet.add( admin.getAdministrator() );
				}
			}
			groupAdministrators.addAll(groupAdminSet);
		}
		return groupAdministrators;
	}

	@Override @Transactional
	public void changeGroupSuperuser(String username, Long groupId) {
		UserGroup userGroup = userGroupDao.findById(groupId);
		if(userGroup == null){
			logger.warn("Request to change group Id " + groupId.toString() + " failed. Group does not exist");
			return;
		}
		//Need to revoke the Role from the current group admin, and remove the current group Admin from the administrators list.
		//Then grant the group admin role to the specified user and make them an administrator
		SharedUser newAdmin = sharedUserDao.findUserByUsername(username);
		//The new admin must be a member of the UserGroup
		if(! userGroup.getListOfGroupUsernames().contains(username)){
			logger.warn("Request to change group Id " + groupId.toString() + " failed. User " + username + " not a member of the group");
			return;
		}
		revokeGroupAdministratorsRole(userGroup);
		addGroupAdministratorRole(newAdmin);
		//Now, remove the existing administrators from the list, and add the new one
		Iterator<UserGroupAdmin> adminIter = userGroup.getGroupAdministratorList().iterator();
		while(adminIter.hasNext()){
			UserGroupAdmin admin = adminIter.next();
			adminIter.remove();
			userGroupAdminDao.delete(admin);
		}
		UserGroupAdmin newUGA = new UserGroupAdmin();
		newUGA.setAdministrator(newAdmin);
		newUGA.getPrivilages().add(new UserGroupAdminPrivilage(GroupAdminPrivilageEnum.GROUP_PEER_REVIEWER));
		newUGA.getPrivilages().add(new UserGroupAdminPrivilage(GroupAdminPrivilageEnum.GROUP_CONTACT));
		userGroup.getGroupAdministratorList().add(newUGA);
		userGroupDao.saveOrUpdate(userGroup);
	}
 
	private void revokeGroupAdministratorsRole(UserGroup group){
		for(UserGroupAdmin groupAdmin : group.getGroupAdministratorList() ){
			SharedUser user = groupAdmin.getAdministrator(); //Currently only one administrator
			List<SharedRole> sharedRoleList = new ArrayList<SharedRole>();
			for(SharedAuthority sa : user.getRoles() ){
				SharedRole role = sa.getSharedRole();
				if(! SharedRole.ROLE_GROUP_ADMIN.equals(role)){
					sharedRoleList.add(role);
				}
			}
			authService.updateUserRoles(user.getUsername(), sharedRoleList);
		}
	}

	private void addGroupAdministratorRole(SharedUser user){
		List<SharedRole> roleList = new ArrayList<SharedRole>();
		for(SharedAuthority sa : user.getRoles() ){
			roleList.add(sa.getSharedRole());
		}
		if(!roleList.contains(SharedRole.ROLE_GROUP_ADMIN)){
			roleList.add(SharedRole.ROLE_GROUP_ADMIN);
		}
		authService.updateUserRoles(user.getUsername(), roleList);
	}

	@Override @Transactional
	public UserGroup retrieveGroupWithAdminAndMember(String adminUserName,
			String groupMember) {
		List<UserGroup> adminGroups = retrieveGroupsForAdminUser(adminUserName);
		List<UserGroup> userGroups = retrieveGroupsWithUserMember(groupMember);
		if(adminGroups != null && userGroups != null){
			for(UserGroup userGroup : adminGroups){
				if(userGroups.contains(userGroup)){
					return userGroup;
				}
			}
		}
		return null;
	}

	@Override @Transactional
	public Set<SharedUser> getUsersInAnyGroup() {
		Set<SharedUser> groupUserSet = new HashSet<SharedUser>();
		for(UserGroup group : userGroupDao.getAll() ){
			groupUserSet.addAll( group.getMemberList());
		}
		return groupUserSet;
	}
	
	@Override @Transactional
	public List<SharedUser> getPickableSharedUsersForGroup( Set<String> usersInGroup){
		for(SharedUser user : getUsersInAnyGroup()){
			usersInGroup.add(user.getUsername());
		}
		return sharedUserDao.getPickableSharedUsersForGroup(usersInGroup);
	}

	

}
