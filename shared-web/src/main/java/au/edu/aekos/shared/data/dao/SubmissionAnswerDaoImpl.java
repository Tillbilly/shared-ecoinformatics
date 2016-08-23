package au.edu.aekos.shared.data.dao;

import java.util.List;
import org.slf4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class SubmissionAnswerDaoImpl extends AbstractHibernateDao<SubmissionAnswer, Long> implements SubmissionAnswerDao{

	private static final Logger logger = LoggerFactory.getLogger(SubmissionAnswerDaoImpl.class);
	
	@Override
	public Class<SubmissionAnswer> getEntityClass() {
		return SubmissionAnswer.class;
	}

	@Override 
	public SubmissionAnswer retrieveSubmissionAnswerBySubmissionIdAndQuestionId(
			Long submissionId, String questionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("questionId", questionId ))
		 .add(Restrictions.eq("submission.id", submissionId ));
		return (SubmissionAnswer) c.uniqueResult();
	}

	@Override
	public List<SubmissionAnswer> getSubmissionAnswerWithResponse(
			String response) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("response", response)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@Override
	public SubmissionAnswer retrieveQuestionSetSubmissionAnswer(
			Long submissionId, String groupQuestionId, Integer groupIndex,
			String questionId) {
		SubmissionAnswer groupAnswer = retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, groupQuestionId);
		if(groupAnswer == null || ! ResponseType.MULTIPLE_QUESTION_GROUP.equals(groupAnswer)){
			logger.warn("No mqg found for submission ID " + submissionId.toString() + "  groupQuestionId " + groupQuestionId);
			return null;
		}
		if(groupAnswer.getQuestionSetList() == null || groupAnswer.getQuestionSetList().size() <= groupIndex.intValue() ){
			logger.warn("No Question Set found at index "+ groupIndex.toString() +" found for submission ID " + submissionId.toString() + "  groupQuestionId " + groupQuestionId);
			return null;
		}
		QuestionSetEntity qse = groupAnswer.getQuestionSetList().get(groupIndex.intValue());
		if(! qse.getAnswerMap().containsKey(questionId) ){
			logger.warn("No Question id "+ questionId  + " found in question set at index "+ groupIndex.toString() +" submission ID " + submissionId.toString() + " groupQuestionId " + groupQuestionId);
			return null;
		}
		return qse.getAnswerMap().get(questionId);
	}
}
