package au.edu.aekos.shared.data.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;

@Repository
public class SharedUserDaoImpl extends AbstractHibernateDao<SharedUser, String> implements SharedUserDao {

	public Class<SharedUser> getEntityClass() {
		return SharedUser.class;
	}

	public SharedUser findById(String id){
		return findUserByUsername(id);
	}
	@Transactional(propagation=Propagation.REQUIRED)
	public SharedUser findUserByUsername(String username) {
        Criteria c = createCriteria();
		c.add(Restrictions.eq("username", username));
		return (SharedUser) c.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override @Transactional(propagation=Propagation.REQUIRED)
	public List<SharedUser> getAllSharedUsers() {
		Criteria c = createCriteria();
		c.addOrder(Order.asc("username"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override @Transactional
	public List<SharedUser> getPickableSharedUsersForGroup(Set<String> usernamesToExclude) {
		Criteria c = createCriteria()
		             .add(Restrictions.not(Restrictions.in("username", usernamesToExclude)))
		             .add(Restrictions.eq("enabled", true))
		             .addOrder(Order.asc("username"))
		             .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@Override @Transactional(propagation=Propagation.REQUIRED)
	public void setSharedUserActive(String username, boolean active) {
		SharedUser user = findUserByUsername(username);
		user.setEnabled(active);
		saveOrUpdate(user);
	}

	@SuppressWarnings("unchecked")
	@Override @Transactional(propagation=Propagation.REQUIRED)
	public List<SharedUser> getSharedUsersByUsernames(List<String> usernames) {
		Criteria c = createCriteria();
		c.add(Restrictions.in("username", usernames))
		 .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SharedUser> findActiveSharedUsersByRole(SharedRole role) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("enabled", true));
		Criteria authCriteria = c.createCriteria("roles");
		authCriteria.add(Restrictions.eq("sharedRole", role));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

}
