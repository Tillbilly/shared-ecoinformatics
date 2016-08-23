package au.edu.aekos.shared.data.dao;

import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.groupadmin.UserGroupAdmin;

@Repository
public class UserGroupAdminDaoImpl extends AbstractHibernateDao<UserGroupAdmin, Long> implements UserGroupAdminDao {

	@Override
	public Class<UserGroupAdmin> getEntityClass() {
		return UserGroupAdmin.class;
	}

	

}
