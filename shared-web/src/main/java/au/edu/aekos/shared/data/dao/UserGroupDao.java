package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;

public interface UserGroupDao  extends HibernateDao<UserGroup, Long>{
	
	List<UserGroup> retrieveGroupsForAdminUser(String username);
	
	List<UserGroup> retrieveGroupsWithMember(String username);

}
