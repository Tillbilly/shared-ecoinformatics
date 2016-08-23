package au.edu.aekos.shared.service.submission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionAnswerDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;



/**
 * Checks whether a submission is under embargo, and performs future admin related embargo operations.
 * @author btill
 */
@Service
public class EmbargoServiceImpl implements EmbargoService {

	private Logger logger = LoggerFactory.getLogger(EmbargoServiceImpl.class);
	
	@Value("${submission.embargo.question.metatag}")
	private String embargoMetatag = "SHD.embargo";
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionAnswerDao submissionAnswerDao;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Transactional
	public boolean isSubmissionUnderEmbargo(Long submissionId)  {
		Submission sub = submissionDao.findById(submissionId);
		if(sub == null){
			return false;
		}
		QuestionnaireConfig qc = null;
		try{
		    qc = questionnaireConfigService.getQuestionnaireConfig(sub);
		}catch(Exception e){
			logger.error("Can't create questionnaire config for submission " + submissionId.toString());
			return false;
		}
		String questionId = qc.getMetatagToQuestionIdMap().get(embargoMetatag);
		SubmissionAnswer embargoAnswer = sub.getAnswersMappedByQuestionId().get(questionId);
		return checkSubmissionAnswerEmbargoActive(embargoAnswer);
	}
	
	@Override @Transactional
	public boolean isSubmissionUnderEmbargo(MetaInfoExtractor metaInfoExtractor) {
		try {
			SubmissionAnswer sa = metaInfoExtractor.getSubmissionAnswerForMetatag(embargoMetatag);
			return checkSubmissionAnswerEmbargoActive(sa);
		} catch (MetaInfoExtractorException e) {
			logger.error("Can't extract SubmissionAnswer for metatag '"+ embargoMetatag +"' from submission " + metaInfoExtractor.getSubmissionId().toString(), e );
		}
		return false;
	}
	
	private boolean checkSubmissionAnswerEmbargoActive(SubmissionAnswer sa){
		if(sa == null || ! sa.hasResponse() || ! ResponseType.DATE.equals(sa.getResponseType())){
			return false;
		}
		String dateResponse = sa.getResponse(); 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date embargoDate = sdf.parse(dateResponse);
			if(embargoDate.compareTo(new Date()) >= 0){
				return true;
			}
		} catch (ParseException e) {
			logger.error("Parse exception checking embargo date SubmissionAnswer id : " + sa.getId().toString() + " for response '" + sa.getResponse() + "'", e);
			return false;
		}
		return false;
	}

	@Override
	public Date getCurrentEmbargoDate(Long submissionId) {
		try{
			MetaInfoExtractor mie = metaInfoExtractorFactory.getInstance(submissionId, false);
			SubmissionAnswer sa = mie.getSubmissionAnswerForMetatag(embargoMetatag);
			if(sa == null || !sa.hasResponse()){
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.parse(sa.getResponse());
		}catch(Exception e){
			logger.error("Can't get current embargo date for submissionId " + submissionId.toString(), e);
		}
		return null;
	}

	@Override @Transactional
	public void updateEmbargoDate(Long submissionId, Date embargoDate) throws Exception {
		MetaInfoExtractor mie = metaInfoExtractorFactory.getInstance(submissionId, false);
		SubmissionAnswer sa = mie.getSubmissionAnswerForMetatag(embargoMetatag);
		SubmissionAnswer freshSA = submissionAnswerDao.findById(sa.getId());
		if(embargoDate == null){
			logger.info("Changing embargo data for submissionId" + submissionId.toString() + " to null");
			freshSA.setResponse(null);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			freshSA.setResponse(sdf.format(embargoDate));
			logger.info("Changing embargo data for submissionId" + submissionId.toString() + " to " + freshSA.getResponse());
		}
		submissionAnswerDao.saveOrUpdate(freshSA);
		submissionAnswerDao.flush();
		metaInfoExtractorFactory.removeSubmissionFromCache(submissionId);
	}

}
