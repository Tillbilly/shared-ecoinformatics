package au.edu.aekos.shared.service.submission;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;

@Service
public class SubmissionCloneServiceImpl implements SubmissionCloneService {

	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Transactional @Override
	public Long cloneSubmission(Long submissionIdToClone,
			String cloneSubmitter, String cloneTitle, Boolean migrate) throws Exception {
		
		Submission original = submissionDao.findSubmissionByIdEagerAnswer(submissionIdToClone);
		String newTitle = checkAndUpdateCloneTitle(cloneTitle, cloneSubmitter);
		SharedUser cloneSubmitterUser = sharedUserDao.findUserByUsername(cloneSubmitter);
		Submission cloneSubmission = new Submission();
		cloneSubmission.setSubmitter(cloneSubmitterUser);
		cloneSubmission.setSubmissionDate(new Date());
		cloneSubmission.setStatus(SubmissionStatus.SAVED);
		cloneSubmission.setTitle(newTitle + "_SAVED");
		if(migrate == null || ! migrate){
			cloneSubmissionAnswers(original, cloneSubmission, newTitle);
		}else{
			metatagMigrateSubmissionAnswers(original, cloneSubmission, newTitle);
		}
		submissionDao.save(cloneSubmission);
		return cloneSubmission.getId();
	}
	
	private String checkAndUpdateCloneTitle(String cloneTitle, String username){
		Submission sub = submissionDao.retrieveSubmissionByTitleAndUsername(cloneTitle, username);
		if(sub == null){
			return cloneTitle;
		}
        String newTitle = "";		
		int version = 0;
		while(sub != null){
			version++;
			newTitle = cloneTitle + " " + version;
			sub = submissionDao.retrieveSubmissionByTitleAndUsername(newTitle, username);
		}
		
		return newTitle;
	}
	
	private void cloneSubmissionAnswers(Submission original, Submission cloneSubmission, String cloneTitle) throws Exception{
		cloneSubmission.setQuestionnaireConfig(original.getQuestionnaireConfig());
		QuestionnaireConfig config = questionnaireConfigService.getQuestionnaireConfig(original);
		String titleQuestionId = config.getSubmissionTitleQuestionId();
		
		Set<SubmissionAnswer> answers = new LinkedHashSet<SubmissionAnswer>();
		answers.addAll(original.getAnswers());
		for(SubmissionAnswer sa : answers ){
			SubmissionAnswer cloneAnswer = cloneSubmissionAnswer(sa);
			if(cloneAnswer != null){
				if(cloneAnswer.getQuestionId().equals(titleQuestionId)){
					cloneAnswer.setResponse(cloneTitle);
				}
				cloneSubmission.getAnswers().add(cloneAnswer);
			}
		}
	}

	//We are not cloning images, spatial , or file artefacts at this point in time.
	private SubmissionAnswer cloneSubmissionAnswer(SubmissionAnswer answer){
		if(! cloneAnswerResponseTypeCheck(answer.getResponseType())){
			return null;
		}
		SubmissionAnswer cloneAnswer = new SubmissionAnswer();
		mapAnswerValues(answer, cloneAnswer);
		return cloneAnswer;
	}
	
	private boolean cloneAnswerResponseTypeCheck(ResponseType responseType){
		if(responseType.equals(ResponseType.BBOX) ||
				responseType.equals(ResponseType.COORDINATE) ||
				responseType.equals(ResponseType.SITE_FILE) ||
				responseType.equals(ResponseType.SPECIES_LIST) ||
				responseType.equals(ResponseType.IMAGE) ||
				responseType.equals(ResponseType.GEO_FEATURE_SET) ||
				responseType.equals(ResponseType.LICENSE_CONDITIONS) ||
				responseType.equals(ResponseType.DOCUMENT) ||
				responseType.equals(ResponseType.MULTIPLE_DOCUMENT) ||
				responseType.equals(ResponseType.MULTISELECT_IMAGE) ){
			return false;
		}
		return true;
	}
	
	private void mapAnswerValues(SubmissionAnswer from, SubmissionAnswer to){
		to.setResponse(from.getResponse());
		to.setSuggestedResponse(from.getSuggestedResponse());
		to.setDisplayResponse(from.getDisplayResponse());
		to.setMetaTag(from.getMetaTag());
		to.setResponseType(from.getResponseType());
		to.setQuestionId(from.getQuestionId());
		if(from.getQuestionSetList() != null && from.getQuestionSetList().size() > 0){
			mapQuestionSetListToCloneAnswer(from, to);
		}
		if(from.getMultiselectAnswerList() != null && from.getMultiselectAnswerList().size() > 0){
			for(SubmissionAnswer msA : from.getMultiselectAnswerList()){
				SubmissionAnswer cloneMsA = cloneSubmissionAnswer(msA);
				if(cloneMsA != null){
					to.getMultiselectAnswerList().add(cloneMsA);
				}
			}
		}
	}
	
	private void mapQuestionSetListToCloneAnswer(SubmissionAnswer from, SubmissionAnswer to){
		for(QuestionSetEntity qse : from.getQuestionSetList()){
			QuestionSetEntity cloneQse = new QuestionSetEntity();
			cloneQse.setMultipleQuestionGroupId(qse.getMultipleQuestionGroupId());
			for(Map.Entry<String, SubmissionAnswer> entry : qse.getAnswerMap().entrySet()){
				String qid = entry.getKey();
				SubmissionAnswer setAnswer = entry.getValue();
				SubmissionAnswer cloneSetAnswer = cloneSubmissionAnswer(setAnswer);
				if(cloneSetAnswer != null){
					cloneQse.getAnswerMap().put(qid, cloneSetAnswer);
				}
			}
			to.getQuestionSetList().add(cloneQse);
		}
	}
	
	/**
	 * 
	 * @param original
	 * @param cloneSubmission
	 * @param cloneTitle
	 * @throws Exception 
	 */
    private void metatagMigrateSubmissionAnswers(Submission original, Submission cloneSubmission, String cloneTitle) throws Exception{
    	QuestionnaireConfig latestConfig = questionnaireConfigService.getQuestionnaireConfig();
    	String titleQuestionId = latestConfig.getSubmissionTitleQuestionId();
    	QuestionnaireConfigEntity qce = questionnaireConfigService.getLatestQuestionnaireConfigEntity();
    	cloneSubmission.setQuestionnaireConfig(qce);
    	MetaInfoExtractor mie = metaInfoExtractorFactory.getInstance(original);
    	
    	//This is a bit hairy, but iterate over the config building clone submission answers from information retrieved
    	//from the MetaInfoExtractor
    	for(Object item : latestConfig.getItems().getEntryList() ){
    		if(item instanceof Question){
    			processQuestionMigrate((Question) item, cloneSubmission, mie, titleQuestionId, cloneTitle);
    		}else if(item instanceof QuestionGroup){
    			processQuestionGroupMigrate((QuestionGroup) item, cloneSubmission, mie, titleQuestionId, cloneTitle); 
    		}else if(item instanceof MultipleQuestionGroup){
    			processQuestionSetMigrate((MultipleQuestionGroup) item, cloneSubmission, mie, titleQuestionId, cloneTitle); 
    		}
    	}
	}
	
	private void processQuestionMigrate(Question question, Submission cloneSubmission, MetaInfoExtractor mie, String titleQuestionId, String title) throws MetaInfoExtractorException{
		if(cloneAnswerResponseTypeCheck(question.getResponseType())){
		   //First, check to see whether an answer exists for the metatag
			if(StringUtils.hasLength(question.getMetatag())){
				SubmissionAnswer originalAnswer = mie.getSubmissionAnswerForMetatag(question.getMetatag());
				if(originalAnswer != null){
					SubmissionAnswer cloneAnswer = new SubmissionAnswer();
					mapAnswerValues(originalAnswer, cloneAnswer);
					cloneAnswer.setQuestionId(question.getId());
					cloneAnswer.setResponseType(question.getResponseType());
					if(question.getId().equals(titleQuestionId)){
						cloneAnswer.setResponse(title);
					}
					cloneSubmission.getAnswers().add(cloneAnswer);
				}
			}else{
				//Display controlling yes/no answers - 
				//Try and find Question with the same ID in old questionnaire, 
				//compare text
				//If the same, map over the SubmissionAnswer
				String questionId = question.getId();
				Question oldQuestion = mie.getQuestionnaireConfig().getQuestionById(questionId);
				if(question.getText().equals(oldQuestion.getText()) && question.getResponseType().equals(oldQuestion.getResponseType())){
					SubmissionAnswer originalAnswer = mie.getSubmissionAnswerForQuestionId(questionId);
					if(originalAnswer != null && originalAnswer.hasResponse()){
						SubmissionAnswer clonedAnswer = new SubmissionAnswer();
						mapAnswerValues(originalAnswer, clonedAnswer);
					}
				}
			}
		}
	}
	
    private void processQuestionGroupMigrate(QuestionGroup questionGroup, Submission cloneSubmission, MetaInfoExtractor mie, String titleQuestionId, String title) throws MetaInfoExtractorException{
    	for(Object item : questionGroup.getItems().getEntryList() ){
	    	if(item instanceof Question){
				processQuestionMigrate((Question) item, cloneSubmission, mie, titleQuestionId, title);
			}else if(item instanceof QuestionGroup){
				processQuestionGroupMigrate((QuestionGroup) item, cloneSubmission, mie, titleQuestionId, title); 
			}else if(item instanceof MultipleQuestionGroup){
				processQuestionSetMigrate((MultipleQuestionGroup) item, cloneSubmission, mie, titleQuestionId, title); 
			}
    	}
	}
	
    private void processQuestionSetMigrate(MultipleQuestionGroup multipleQuestionGroup, Submission cloneSubmission, MetaInfoExtractor mie, String titleQuestionId, String title) throws MetaInfoExtractorException{
		//Get a map of the metatag to required questions from the multipleQuestionGroup
    	Map<String, Question> groupQuestionMap = new HashMap<String, Question>();
    	String groupId = multipleQuestionGroup.getId();
    	for(Object obj : multipleQuestionGroup.getItems().getEntryList() ){
    		if(obj instanceof Question){
    			Question q = (Question) obj;
    			groupQuestionMap.put(q.getMetatag(), q);
    		}
    	}
    	if(groupQuestionMap.size() == 0){
    		return;
    	}
    	List<Map<String, SubmissionAnswer>> questionSetResponses = mie.getQuestionSetAnswersForMetatags(groupQuestionMap.keySet());
		if(questionSetResponses.size() == 0){
			return;
		}
		SubmissionAnswer setAnswer = new SubmissionAnswer();
		setAnswer.setQuestionId(groupId);
		setAnswer.setResponseType(ResponseType.MULTIPLE_QUESTION_GROUP);
		for(Map<String, SubmissionAnswer> answerMap : questionSetResponses ){
			QuestionSetEntity qse = new QuestionSetEntity();
			qse.setMultipleQuestionGroupId(groupId);
			for(Map.Entry<String, SubmissionAnswer> entry : answerMap.entrySet()){
				//For each entry, build a new SubmissionAnswer and add it to the qse, based on the Question in the group Question Map
				String ansMeta = entry.getKey();
				Question q = groupQuestionMap.get(ansMeta);
				if(q != null){
				    SubmissionAnswer answer = new SubmissionAnswer();
				    SubmissionAnswer ansToClone = entry.getValue();
				    mapAnswerValues(ansToClone, answer);
				    answer.setQuestionId(q.getId());
				    answer.setResponseType(q.getResponseType());
				    qse.getAnswerMap().put(q.getId(), answer);
				}
			}
			if(qse.getAnswerMap().size() > 0){
			    setAnswer.getQuestionSetList().add(qse);
			}
		}
		if(setAnswer.getQuestionSetList().size()  > 0){
			cloneSubmission.getAnswers().add(setAnswer);
		}
		
	}
}
