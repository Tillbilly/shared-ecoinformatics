package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.AnswerReview;

public interface AnswerReviewDao extends HibernateDao<AnswerReview, Long>{
	/**
	 * 
	 * @param submissionId
	 * @param questionId
	 * @return List of AnswerReviews from newest to oldest.
	 */
	List<AnswerReview> retrieveAnswerReviewsBySubmissionIdAndQuestionId(Long submissionId, String questionId);

}
