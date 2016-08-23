package au.edu.aekos.shared.data.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.PublicationLog;
import au.edu.aekos.shared.data.entity.Submission;

@Repository
public class PublicationLogDaoImpl extends AbstractHibernateDao<PublicationLog, Long> implements PublicationLogDao{

	@Override
	public Class<PublicationLog> getEntityClass() {
		return PublicationLog.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicationLog> retrieveLogEntriesForSubmission(Submission sub) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("submission", sub))
		  .addOrder(Order.desc("logTime"));
		return c.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, PublicationLog> getLatestPublicationLogEntriesForSubmissions( List<Long> submissionIdList ) {
		Map<Long, PublicationLog> logMap = new HashMap<Long, PublicationLog>();
		if(submissionIdList == null || submissionIdList.size() == 0){
			return logMap;
		}
		Criteria c = createCriteria();
		c.add(Restrictions.in("submission.id",submissionIdList ));
		c.addOrder(Order.desc("submission.id")).addOrder(Order.desc("logTime"));
		List<PublicationLog> completeList = c.list();
		if(completeList != null && completeList.size() > 0){
			for(PublicationLog log : completeList ){
				if(! logMap.containsKey(log.getSubmission().getId()) ){
					logMap.put(log.getSubmission().getId(), log);
				}
			}
		}
		return logMap;
	}
}
