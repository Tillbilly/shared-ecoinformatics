package au.edu.aekos.shared.service.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionAnswerDao;
import au.edu.aekos.shared.data.entity.AnswerImage;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Service
public class SubmissionAnswerServiceImpl implements SubmissionAnswerService {

	@Autowired
	private SubmissionAnswerDao submissionAnswerDao;
	
	@Transactional
	public AnswerImage retrieveAnswerImageForMultiselectImageAnswer(
			Long submissionId, String questionId, String imageNameId) {
		SubmissionAnswer sa = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, questionId);
		if( sa != null && ResponseType.MULTISELECT_IMAGE.equals(sa.getResponseType()) && sa.getMultiselectAnswerList().size() > 0){
			for(SubmissionAnswer msa : sa.getMultiselectAnswerList() ){
				if(msa.getAnswerImage() != null && msa.getAnswerImage().containsImageId(imageNameId)){
					return msa.getAnswerImage();
				}
			}
		}
		return null;
	}

}
