package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.SubmissionReview;

public interface SubmissionReviewDao extends HibernateDao<SubmissionReview, Long> {
	
	SubmissionReview getLastReviewForSubmissionId(Long submissionId);
	
	SubmissionReview getSavedReviewForSubmissionId(Long submissionId);
	
	//This is used for deleting saved reviews after the real review gets finalised.
	List<SubmissionReview> getSavedReviewsForSubmissionId(Long submissionId);
	
}
