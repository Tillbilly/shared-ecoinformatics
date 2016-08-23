package au.edu.aekos.shared.data.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.AnswerReview;
import au.edu.aekos.shared.data.entity.ReviewOutcome;

@Repository
public class AnswerReviewDaoImpl extends AbstractHibernateDao<AnswerReview, Long> implements AnswerReviewDao {

	@Override
	public Class<AnswerReview> getEntityClass() {
		return AnswerReview.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnswerReview> retrieveAnswerReviewsBySubmissionIdAndQuestionId(
			Long submissionId, String questionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("questionId", questionId));
		Criteria reviewCriteria = c.createCriteria("review");
		reviewCriteria.add(Restrictions.eq("submission.id", submissionId));
		reviewCriteria.add(Restrictions.ne("reviewOutcome", ReviewOutcome.REVIEW_SAVED));
		reviewCriteria.addOrder(Order.desc("reviewDate"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<AnswerReview> answerReviewList = c.list();
		return answerReviewList;
	}
}
