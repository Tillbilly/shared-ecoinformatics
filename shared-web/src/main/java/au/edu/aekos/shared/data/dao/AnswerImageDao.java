package au.edu.aekos.shared.data.dao;

import au.edu.aekos.shared.data.entity.AnswerImage;

public interface AnswerImageDao extends HibernateDao<AnswerImage, Long> {
	
	AnswerImage getAnswerImage(Long submissionId, String questionId);
	
	
	
	
}
