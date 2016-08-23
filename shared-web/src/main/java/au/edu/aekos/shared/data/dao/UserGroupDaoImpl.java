package au.edu.aekos.shared.data.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;

@Repository
public class UserGroupDaoImpl extends AbstractHibernateDao<UserGroup, Long> implements UserGroupDao {

	@Override
	public Class<UserGroup> getEntityClass() {
		return UserGroup.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserGroup> retrieveGroupsForAdminUser(String username) {
		Criteria c = createCriteria();
		Criteria groupAdminCriteria = c.createCriteria("groupAdministratorList");
		groupAdminCriteria.add(Restrictions.eq("administrator.username", username));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserGroup> retrieveGroupsWithMember(String username) {
		Criteria c = createCriteria();
		Criteria memberCriteria = c.createCriteria("memberList");
		memberCriteria.add(Restrictions.eq("username", username));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

}
