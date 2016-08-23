package au.edu.aekos.shared.service.submission;

import au.edu.aekos.shared.data.entity.AnswerImage;

public interface SubmissionAnswerService {

	AnswerImage retrieveAnswerImageForMultiselectImageAnswer(Long submissionId, String questionId, String imageNameId );
	
}
