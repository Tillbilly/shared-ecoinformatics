package au.edu.aekos.shared.data.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.ReusableResponseGroup;

@Repository
public class ReusableResponseGroupDaoImpl extends AbstractHibernateDao<ReusableResponseGroup,Long> implements ReusableResponseGroupDao{

	@Override
	public Class<ReusableResponseGroup> getEntityClass() {
		return ReusableResponseGroup.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReusableResponseGroup> retrieveResponseGroups( Long questionnaireId, String userId, String groupId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("questionnaireConfig.id", questionnaireId) )
		 .add(Restrictions.eq("sharedUser.id", userId ) )
		 .add(Restrictions.eq("groupId", groupId ))
		 .addOrder(Order.asc("name"));
		return c.list();
	}

	@Override
	public ReusableResponseGroup retrieveResponseGroupByName(String groupName,
			Long questionnaireId, String userId, String groupId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("questionnaireConfig.id", questionnaireId) )
		 .add(Restrictions.eq("sharedUser.id", userId ) )
		 .add(Restrictions.eq("groupId", groupId ))
		 .add(Restrictions.eq("name", groupName )) ;
		
		return (ReusableResponseGroup) c.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReusableResponseGroup> retrieveResponseGroups(
			Long questionnaireId, String userId, Set<String> groupIds) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("questionnaireConfig.id", questionnaireId) )
		 .add(Restrictions.eq("sharedUser.id", userId ) )
		 .add(Restrictions.in("groupId", groupIds ))
		 .addOrder(Order.asc("groupId") )
		 .addOrder(Order.asc("name"));
		return c.list();
	}

}
