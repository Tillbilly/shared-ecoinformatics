package au.edu.aekos.shared.data.dao;

import java.util.List;
import java.util.Set;

import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;

public interface SharedUserDao extends HibernateDao<SharedUser, String> {
	
	SharedUser findUserByUsername(String username);
	
	List<SharedUser> getSharedUsersByUsernames(List<String> usernames);
	
	List<SharedUser> getAllSharedUsers();
	
	void setSharedUserActive(String username, boolean active);
	
	List<SharedUser> getPickableSharedUsersForGroup(Set<String> usersInGroup);
	
	List<SharedUser> findActiveSharedUsersByRole(SharedRole role);

}
