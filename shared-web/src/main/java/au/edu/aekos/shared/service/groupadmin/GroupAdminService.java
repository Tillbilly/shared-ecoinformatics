package au.edu.aekos.shared.service.groupadmin;

import java.util.List;
import java.util.Set;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;

public interface GroupAdminService {
	
	UserGroup createNewGroupForUser(String username, String groupName, String organisation) throws GroupAdminServiceException;
	
	UserGroup retrieveUserGroup(Long groupId) throws GroupAdminServiceException;
	
	List<UserGroup> retrieveGroupsForAdminUser(String username);
	
	/**
	 * TODO In the future, only allow users to be in 1 group at a time
	 * @param username
	 * @return
	 */
	List<UserGroup> retrieveGroupsWithUserMember(String username);
	
	List<SharedUser> retrieveSharedUsersInGroupsWithAdministrator(String username, boolean excludeAdministrator);
	
	List<SharedUser> getAllUsersInGroupsWithPeerReviewActive(boolean excludeAdministrator);
	
	void updateGroupUsers(Long groupId, Set<String> usersInGroup) throws GroupAdminServiceException;
	
	void addUsersToGroup(Long groupId, List<String> users) throws GroupAdminServiceException;
	
	void deleteUserGroup(Long groupId) throws GroupAdminServiceException;

	void removeUsersFromGroup(Long groupId, List<String> users) throws GroupAdminServiceException;
	
	/**
	 * Checks whether the current logged in user has group admin access over the submissionId
	 * @param submissionId
	 * @return
	 */
	boolean checkGroupAdminSubmissionPermission(Long submissionId);
	
	void transferGroupSubmissionOwnership(Long submissionId, String toUser, String message);
	
	void toggleEnablePeerReview(Long groupId, Boolean enablePeerReview);
	
	List<SharedUser> getGroupAdministratorsForSubmission(Long submissionId);
	
	void changeGroupSuperuser(String username, Long groupId);
	
	UserGroup retrieveGroupWithAdminAndMember(String adminUserName, String groupMember);

	Set<SharedUser> getUsersInAnyGroup();

	List<SharedUser> getPickableSharedUsersForGroup(Set<String> usersInGroup);
	
}
