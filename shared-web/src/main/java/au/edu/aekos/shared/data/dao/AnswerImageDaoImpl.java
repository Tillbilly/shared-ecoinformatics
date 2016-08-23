package au.edu.aekos.shared.data.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.AnswerImage;

@Repository
public class AnswerImageDaoImpl extends AbstractHibernateDao<AnswerImage, Long> implements AnswerImageDao {

	@Override
	public Class<AnswerImage> getEntityClass() {
		return AnswerImage.class;
	}

	@Override
	@Transactional
	public AnswerImage getAnswerImage(Long submissionId, String questionId) {
		Criteria c = createCriteria();
		Criteria answerCriteria = c.createCriteria("answer");
		answerCriteria.add(Restrictions.eq("questionId", questionId))
		              .add(Restrictions.eq("submission.id", submissionId) );
		return (AnswerImage) c.uniqueResult();
	}
}
