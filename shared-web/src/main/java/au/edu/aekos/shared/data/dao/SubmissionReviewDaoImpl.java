package au.edu.aekos.shared.data.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.ReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionReview;

@Repository
public class SubmissionReviewDaoImpl extends AbstractHibernateDao<SubmissionReview, Long> implements SubmissionReviewDao  {

	@Override
	public Class<SubmissionReview> getEntityClass() {
		return SubmissionReview.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SubmissionReview getLastReviewForSubmissionId(Long submissionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("submission.id", submissionId))
		  .add(Restrictions.ne("reviewOutcome", ReviewOutcome.REVIEW_SAVED))
		  .addOrder(Order.desc("reviewDate"));
		List results = c.list();
		if(results != null && results.size() > 0){
			SubmissionReview sr = (SubmissionReview) results.get(0);
			//Rehydrate the review collections.
			sr.getAnswerReviews().size();
			sr.getFileReviews().size();
			return sr;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SubmissionReview getSavedReviewForSubmissionId(Long submissionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("submission.id", submissionId)).addOrder(Order.desc("reviewDate"));
		List results = c.list();
		if(results != null && results.size() > 0){
			SubmissionReview sr = (SubmissionReview) results.get(0);
			if(ReviewOutcome.REVIEW_SAVED.equals(sr.getReviewOutcome())){
				sr.getAnswerReviews().size();
				sr.getFileReviews().size();
				return sr;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubmissionReview> getSavedReviewsForSubmissionId(
			Long submissionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("submission.id", submissionId))
		  .add(Restrictions.eq("reviewOutcome", ReviewOutcome.REVIEW_SAVED));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	

}
