package au.edu.aekos.shared.data.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.SubmissionLink;

@Repository
public class SubmissionLinkDaoImpl extends AbstractHibernateDao<SubmissionLink, Long> implements SubmissionLinkDao {

	@Override
	public Class<SubmissionLink> getEntityClass() {
		return SubmissionLink.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Long> getLinkedSubmissionIds(Long submissionId) {
		Set<Long> linkedSubmissionIds = new HashSet<Long>();
		Criteria c = createCriteria();
		c.add(Restrictions.eq("sourceSubmission.id", submissionId));
		List<SubmissionLink> slList = c.list();
		if(slList != null){
			for(SubmissionLink sl : slList){
				linkedSubmissionIds.add(sl.getLinkedSubmission().getId());
			}
		}
		return linkedSubmissionIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubmissionLink> getLinksForSubmission(Long submissionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("sourceSubmission.id", submissionId )) ;
		c.setFetchMode("linkedSubmission", FetchMode.JOIN);
		return c.list();
	}

	@Override
	public List<SubmissionLink> getSubmissionLinksBetweenSubmissions(
			Long submissionId1, Long submissionId2) {
		Criteria c = createCriteria();
		Disjunction dj = Restrictions.disjunction();
        dj.add(Restrictions.conjunction().add(Restrictions.eq("sourceSubmission.id", submissionId1)).add( Restrictions.eq("linkedSubmission.id", submissionId2)));	
        dj.add(Restrictions.conjunction().add(Restrictions.eq("sourceSubmission.id", submissionId2)).add( Restrictions.eq("linkedSubmission.id", submissionId1)));
		c.add(dj);
		return c.list();
	}

}
