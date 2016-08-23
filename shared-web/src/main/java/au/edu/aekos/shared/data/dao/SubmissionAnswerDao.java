package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;

public interface SubmissionAnswerDao extends HibernateDao<SubmissionAnswer, Long>{
	
	SubmissionAnswer retrieveSubmissionAnswerBySubmissionIdAndQuestionId(Long submissionId, String questionId);
	
	SubmissionAnswer retrieveQuestionSetSubmissionAnswer(Long submissionId, String groupQuestionId, Integer groupIndex, String questionId);
	
	List<SubmissionAnswer> getSubmissionAnswerWithResponse(String response);

}
