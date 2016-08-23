package au.edu.aekos.shared.data.infomodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
/**
 * This class makes it easy to retrieve information from a submission
 * using metatags
 * 
 * Create an instance with the MetaInfoExtractorFactory
 * 
 * @author Ben
 *
 */
public class MetaInfoExtractor {

	private static final Logger logger = LoggerFactory.getLogger(MetaInfoExtractor.class);
	
	private Submission submission;
	private QuestionnaireConfig questionnaireConfig;
	private Map<String,String> metatagToQuestionIdMap = new HashMap<String,String>();
	private Map<String,String> questionIdToMetatagMap = new HashMap<String,String>();
	private Map<String, SubmissionAnswer> questionIdToSubmissionAnswerMap = new HashMap<String, SubmissionAnswer>();
	private Map<String, Question> questionIdToConfigQuestionMap = new HashMap<String, Question>();
	Map<String, List<SubmissionAnswer>> questionSetAnswerMap = new HashMap<String, List<SubmissionAnswer>>();
	private MetatagEvolutionConfig evolutionConfig = new MetatagEvolutionConfig();
	private List<MultipleQuestionGroup> multipleQuestionGroupList = new ArrayList<MultipleQuestionGroup>();
	
	//Injected by the MetaInfoExtractorFactory, used if display values are 
	//chosen to be used for controlled vocab responses - i.e. useVocabDisplayValues = true;
	private ControlledVocabularyService controlledVocabService;
	private boolean useVocabDisplayValues = false;
	public MetaInfoExtractor() {
		super();
	}
	
	public MetaInfoExtractor(Submission submission,
			QuestionnaireConfig questionnaireConfig,
			Map<String, String> metatagToQuestionIdMap,
			Map<String, SubmissionAnswer> questionIdToSubmissionAnswerMap,
			Map<String, List<SubmissionAnswer>> questionSetAnswerMap,
			MetatagEvolutionConfig evolutionConfig) {
		super();
		this.submission = submission;
		this.questionnaireConfig = questionnaireConfig;
		this.metatagToQuestionIdMap = metatagToQuestionIdMap;
		this.questionIdToSubmissionAnswerMap = questionIdToSubmissionAnswerMap;
		this.questionSetAnswerMap = questionSetAnswerMap;
		if(evolutionConfig != null){
		    this.evolutionConfig = evolutionConfig;
		}
		constructQuestionIdToMetatagMap();
		//Initialise questionIdToCoonfigQuestionMap - used to get trait names for display values for controlled vocab answers
		if(questionnaireConfig != null){
			List<Question> allQuestions = this.questionnaireConfig.getAllQuestions(true);
			questionIdToConfigQuestionMap.clear();
			for(Question q : allQuestions){
				questionIdToConfigQuestionMap.put(q.getId(), q);
			}
			multipleQuestionGroupList = questionnaireConfig.getAllMultipleQuestionGroups();
		}
	}
	
	public MetaInfoExtractor(Submission submission,
			QuestionnaireConfig questionnaireConfig,
			Map<String, String> metatagToQuestionIdMap,
			Map<String, SubmissionAnswer> questionIdToSubmissionAnswerMap,
			Map<String, List<SubmissionAnswer>> questionSetAnswerMap,
			MetatagEvolutionConfig evolutionConfig,
			ControlledVocabularyService controlledVocabService,
			boolean useVocabDisplayValues) {
		super();
		this.submission = submission;
		this.questionnaireConfig = questionnaireConfig;
		this.metatagToQuestionIdMap = metatagToQuestionIdMap;
		this.questionIdToSubmissionAnswerMap = questionIdToSubmissionAnswerMap;
		this.questionSetAnswerMap = questionSetAnswerMap;
		if(evolutionConfig != null){
		    this.evolutionConfig = evolutionConfig;
		}
		this.controlledVocabService = controlledVocabService;
		this.useVocabDisplayValues = useVocabDisplayValues;
		if(useVocabDisplayValues){
			logger.debug("MetaInfoExtractor initialised to use vocab display values");
		}else{
			logger.debug("MetaInfoExtractor initialised to use vocab values");
		}
		constructQuestionIdToMetatagMap();
		//Initialise questionIdToCoonfigQuestionMap - used to get trait names for display values for controlled vocab answers
		if(questionnaireConfig != null){
			List<Question> allQuestions = this.questionnaireConfig.getAllQuestions(true);
			questionIdToConfigQuestionMap.clear();
			for(Question q : allQuestions){
				questionIdToConfigQuestionMap.put(q.getId(), q);
			}
			multipleQuestionGroupList = questionnaireConfig.getAllMultipleQuestionGroups();
		}
	}
	
	private void constructQuestionIdToMetatagMap(){
		questionIdToMetatagMap.clear();
		if(metatagToQuestionIdMap != null && metatagToQuestionIdMap.size() > 0){
			for(Map.Entry<String, String> entry : metatagToQuestionIdMap.entrySet()){
				questionIdToMetatagMap.put(entry.getValue(), entry.getKey());
			}
		}
	}
	
	public SubmissionAnswer getSubmissionAnswerForMetatag(String metatag ) throws MetaInfoExtractorException {
		if(! metatagToQuestionIdMap.containsKey(metatag) && ! evolutionConfig.containsHandler(metatag) ){
		    throw new MetaInfoExtractorException("Questionnaire does not contain question for metatag:" + metatag);
		}
		String questionId = getQuestionIdForMetatag(metatag);
		if(! StringUtils.hasLength(questionId)){
			return null;
		}
		return questionIdToSubmissionAnswerMap.get(questionId);
	}
	
	public SubmissionAnswer getSubmissionAnswerForQuestionId(String questionId){
		return questionIdToSubmissionAnswerMap.get(questionId);
	}
	
	private String getQuestionIdForMetatag(String metatag){
		String questionId = metatagToQuestionIdMap.get(metatag);
		if(! StringUtils.hasLength(questionId) ){
			return getQuestionIdForAlternativeMetatag(metatag);
		}
		return questionId;
	}
	
	//The information model may have evolved since the submission was made.
	//Check mapping back via the alternate metatag map in the metatagHandler
	//via the evolution config
	private String getQuestionIdForAlternativeMetatag(String metatag){
		if(! evolutionConfig.containsHandler(metatag)){ //Should have already thrown an exception
			return null;
		}
		ChangedMetatagHandler changeHandler = evolutionConfig.getChangeHandler(metatag);
		if(changeHandler.getIgnore()){
			return null;
		}
		if(changeHandler.getUseDefaultValue()){
			return null;
		}
		if(changeHandler.getAlternateMetatagList() != null){
		    for(String altMeta : changeHandler.getAlternateMetatagList()){
		    	if(metatagToQuestionIdMap.containsKey(altMeta)){
		    		return metatagToQuestionIdMap.get(altMeta);
		    	}
		    }
		}
		return null;
	}
	
	public String getSingleResponseForMetatag(String metatag ) throws MetaInfoExtractorException{
		SubmissionAnswer sa = getSubmissionAnswerForMetatag(metatag );
		if(sa != null && sa.hasResponse()){
			return getSingleResponseFromSubmissionAnswer( sa);
		}else if(sa == null && evolutionConfig.containsHandler(metatag) ){
			ChangedMetatagHandler handler = evolutionConfig.getChangeHandler(metatag);
			if(handler.getUseDefaultValue()){
				return handler.getDefaultValue();
			}else if(handler.getIgnore()){
				return null;
			}
		}
		return null;
	}
	
	public String getSingleResponseForMetatagIgnoreDisplayValue(String metatag ) throws MetaInfoExtractorException{
		SubmissionAnswer sa = getSubmissionAnswerForMetatag(metatag );
		if(sa != null && sa.hasResponse()){
			return getSingleResponseFromSubmissionAnswerIgnoreDisplayValue( sa);
		}else if(sa == null && evolutionConfig.containsHandler(metatag) ){
			ChangedMetatagHandler handler = evolutionConfig.getChangeHandler(metatag);
			if(handler.getUseDefaultValue()){
				return handler.getDefaultValue();
			}else if(handler.getIgnore()){
				return null;
			}
		}
		return null;
	}

	public List<String> getResponsesFromMultiselectTag(String metatag) throws MetaInfoExtractorException{
		SubmissionAnswer sa = getSubmissionAnswerForMetatag(metatag );
		if(sa != null && !sa.hasResponse()){
			return new ArrayList<String>();
		}else if(sa == null && evolutionConfig.containsHandler(metatag) 
				&& Boolean.TRUE.equals( evolutionConfig.getChangeHandler(metatag).getUseDefaultValue()) ){
			List<String> defaultRespList = new ArrayList<String>();
			String defaultValue = evolutionConfig.getChangeHandler(metatag).getDefaultValue();
			if(StringUtils.hasLength(defaultValue)){
				defaultRespList.add(defaultValue);
			}
		    return defaultRespList;
		}else if(sa == null){
			return new ArrayList<String>();
		}
		if(! ResponseType.getIsMultiselect(sa.getResponseType())){
			throw new MetaInfoExtractorException("Multiselect response expected for metatag:" + metatag);
		}
		return getResponsesFromMultiselectAnswer(sa);
	}
	
	public List<Map<String,List<String>>> getMetatagQuestionSetResponses(String multipleQuestionGroupId) throws MetaInfoExtractorException{
		List<Map<String,List<String>>> groupQuestionSetResponses = new ArrayList<Map<String,List<String>>>();
		if(! questionIdToSubmissionAnswerMap.containsKey(multipleQuestionGroupId) ){
			logger.debug("Current submission " + submission.getId() + " does not contain an answer for question id " + multipleQuestionGroupId );
			return groupQuestionSetResponses;
		}
		SubmissionAnswer sa = questionIdToSubmissionAnswerMap.get(multipleQuestionGroupId);
		if(! ResponseType.MULTIPLE_QUESTION_GROUP.equals( sa.getResponseType() ) ){
			logger.warn("Current submission " + submission.getId() + " answer " + multipleQuestionGroupId + " is not MULTIPLE_QUESTION_GROUP" );
			throw new MetaInfoExtractorException("Current submission " + submission.getId() + " answer " + multipleQuestionGroupId + " is not MULTIPLE_QUESTION_GROUP");
		}
		
		
		for(QuestionSetEntity qse : sa.getQuestionSetList() ){
			Map<String,List<String>> questionSetResponses = new HashMap<String,List<String>>();
			for(Map.Entry<String,SubmissionAnswer> answerEntry : qse.getAnswerMap().entrySet() ){
				SubmissionAnswer childAnswer = answerEntry.getValue();
				if(childAnswer.hasResponse()){
					List<String> childAnswerResponseList = new ArrayList<String>();
					if(ResponseType.getIsMultiselect(childAnswer.getResponseType())){
						childAnswerResponseList = getResponsesFromMultiselectAnswer(childAnswer);
					}else{
						String response = getSingleResponseFromSubmissionAnswer(childAnswer);
						if(StringUtils.hasLength(response)){
							childAnswerResponseList.add(response);
						}
					}
					if(childAnswerResponseList != null && childAnswerResponseList.size() > 0){
						String metatag = questionIdToMetatagMap.get(childAnswer.getQuestionId());
						questionSetResponses.put(metatag, childAnswerResponseList);
					}
				}
			}
			groupQuestionSetResponses.add(questionSetResponses);
		}
		return groupQuestionSetResponses;
	}
	
	public List<Map<String,SubmissionAnswer>> getMetatagQuestionSetAnswers(String multipleQuestionGroupId) throws MetaInfoExtractorException{
		List<Map<String,SubmissionAnswer>> groupQuestionSetAnswers = new ArrayList<Map<String,SubmissionAnswer>>();
		if(! questionIdToSubmissionAnswerMap.containsKey(multipleQuestionGroupId) ){
			logger.debug("Current submission " + submission.getId() + " does not contain an answer for question id " + multipleQuestionGroupId );
			return groupQuestionSetAnswers; //Null object response
		}
		SubmissionAnswer sa = questionIdToSubmissionAnswerMap.get(multipleQuestionGroupId);
		if(! ResponseType.MULTIPLE_QUESTION_GROUP.equals( sa.getResponseType() ) ){
			logger.warn("Current submission " + submission.getId() + " answer " + multipleQuestionGroupId + " is not MULTIPLE_QUESTION_GROUP" );
			throw new MetaInfoExtractorException("Current submission " + submission.getId() + " answer " + multipleQuestionGroupId + " is not MULTIPLE_QUESTION_GROUP");
		}
		
		for(QuestionSetEntity qse : sa.getQuestionSetList() ){
			Map<String,SubmissionAnswer> questionSetAnswers = new HashMap<String,SubmissionAnswer>();
			for(Map.Entry<String,SubmissionAnswer> answerEntry : qse.getAnswerMap().entrySet() ){
				SubmissionAnswer childAnswer = answerEntry.getValue();
				if(childAnswer.hasResponse()){
					//Need the metatag for the answer
					String childAnswerMeta = questionIdToMetatagMap.get(answerEntry.getKey());
					if(childAnswerMeta != null){
						questionSetAnswers.put(childAnswerMeta, childAnswer);
					}
				}
			}
			if(questionSetAnswers.size() > 0){
			    groupQuestionSetAnswers.add(questionSetAnswers);
			}
		}
		return groupQuestionSetAnswers;
	}
	
	public String getParentQuestionIdForQuestionSetMetatag(String metatag){
		for(MultipleQuestionGroup mqg : multipleQuestionGroupList){
			if(mqg.groupContainsMetatag(metatag)){
				return mqg.getId();
			}
		}
		//We may need to invoke the evolution handler if we can't find the metatag
		if(! evolutionConfig.containsHandler(metatag)){ 
			return null;
		}
		ChangedMetatagHandler handler  = evolutionConfig.getChangeHandler(metatag);
		if(handler.getAlternateMetatagList() != null){
		    for(String altMeta : handler.getAlternateMetatagList()){
		    	String parentQID = getParentQuestionIdForQuestionSetMetatag(altMeta);
		    	if(StringUtils.hasLength(parentQID)){
		    		logger.info("Alternative metatag " + altMeta +" used in place of " + metatag);
		    		return parentQID;
		    	}
		    }
		}
		logger.warn("No parent MQG for metatag " + metatag);
		return null;
	}
	
	//This is OK for a single response from a set,  but won't work when processing multiple results i.e. author names
	//That may have null first names.  Or for group processing
	public List<String> getResponseListFromQuestionSet(String metatag) throws MetaInfoExtractorException{
		List<String> responseList = new ArrayList<String>();
		String qId = getQuestionIdForMetatag(metatag);
		if(qId != null && questionSetAnswerMap.containsKey(qId)){
			for(SubmissionAnswer sa : questionSetAnswerMap.get(qId)){
				if(sa.hasResponse()){
					String response = sa.getResponse();
					if("OTHER".equals(sa.getResponse())){
						response = sa.getSuggestedResponse();
					}else if(useVocabDisplayValues && controlledVocabService != null 
							&&  ( ResponseType.CONTROLLED_VOCAB.equals(sa.getResponseType()) || 
							      ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(sa.getResponseType()))){
						//Need to get the trait name from the QuestionnaireConfig
						String traitName = getTraitNameForQuestionId(qId);
						response = getDisplayStringResponseFromControlledVocab(response,traitName);
					}
					if(StringUtils.hasLength(response)){
						responseList.add(response);
					}
				}
			}
		}
		return responseList;
	}
	
	public List<String> getResponsesFromMultiselectAnswer(SubmissionAnswer sa){
		List<String> responseList = new ArrayList<String>();
		if(sa.hasResponse()){
			String traitName = null;
			if(useVocabDisplayValues && controlledVocabService != null &&
					( ResponseType.MULTISELECT_CONTROLLED_VOCAB.equals(sa.getResponseType() )  ||   
				ResponseType.TREE_SELECT.equals( sa.getResponseType() )	) && sa.getQuestionId() != null){
				logger.debug("Use vocab display values");
				String qId = sa.getQuestionId();
				traitName = getTraitNameForQuestionId(qId);
				logger.debug("Question ID " + qId + " trait " + traitName);
			}
			for(SubmissionAnswer msa : sa.getMultiselectAnswerList()){
				if(msa.hasResponse()){
					String response = msa.getResponse();
					logger.debug("response " + response);
					if("OTHER".equals(response)){
						response = msa.getSuggestedResponse();
					}else if(useVocabDisplayValues && StringUtils.hasLength(traitName)){
						response = getDisplayStringResponseFromControlledVocab(response,traitName);
					}
					logger.debug("response to add " + response);
					responseList.add(response);
				}
			}
		}
		return responseList;
	}
	
	public List<Map<String,List<String>>> getQuestionSetResponsesForMetatags(Set<String> requiredGroupMetatags) throws MetaInfoExtractorException{
		List<Map<String,List<String>>> setResponseMapList = new ArrayList<Map<String,List<String>>>();
		String parentGroupId = validateAndGetParentQuestionSetIdForGroupMetatags(requiredGroupMetatags);
		List<Map<String,List<String>>> mqgResponseMapList = getMetatagQuestionSetResponses(parentGroupId);
		for(Map<String,List<String>> groupResponseMap : mqgResponseMapList){
			Map<String,List<String>> setResponseMap = new HashMap<String,List<String>>();
			for(String metatag : requiredGroupMetatags){
				List<String> metatagResponses = getQuestionSetResponseForMetatag(metatag, groupResponseMap);
			    if(metatagResponses != null && metatagResponses.size() > 0){
			    	setResponseMap.put(metatag, metatagResponses);
			    }
			}
			if(setResponseMap.size() > 0){
				setResponseMapList.add(setResponseMap);
			}
		}
		return setResponseMapList;
	}
	
	public List<Map<String,SubmissionAnswer>> getQuestionSetAnswersForMetatags(Set<String> requiredGroupMetatags) throws MetaInfoExtractorException{
		List<Map<String,SubmissionAnswer>> setResponseMapList = new ArrayList<Map<String,SubmissionAnswer>>();
		String parentGroupId = validateAndGetParentQuestionSetIdForGroupMetatags(requiredGroupMetatags);
		List<Map<String,SubmissionAnswer>> mqgResponseMapList = getMetatagQuestionSetAnswers(parentGroupId);
		for(Map<String,SubmissionAnswer> groupResponseMap : mqgResponseMapList){
			Map<String,SubmissionAnswer> setResponseMap = new HashMap<String,SubmissionAnswer>();
			for(String metatag : requiredGroupMetatags){
				SubmissionAnswer sa = groupResponseMap.get(metatag);
			    if(sa != null){
			    	setResponseMap.put(metatag, sa);
			    }
			}
			if(setResponseMap.size() > 0){
				setResponseMapList.add(setResponseMap);
			}
		}
		return setResponseMapList;
	}
	
	/**
	 * Will find the parent group ID for each metatag in the set, check that they are all non null,
	 * and check they are all equal.
	 * @param requiredGroupMetatags
	 * @return
	 * @throws 
	 */
	private String validateAndGetParentQuestionSetIdForGroupMetatags(Set<String> requiredGroupMetatags) throws MetaInfoExtractorException{
		Map<String,String> validatorMap = new HashMap<String, String>();
		String testParentID = null;
		for(String metatag : requiredGroupMetatags){
			String parentQID = getParentQuestionIdForQuestionSetMetatag(metatag);
			if(!StringUtils.hasLength(parentQID)){
				logger.warn("Metatag " + metatag + " can not be found for submission " + getSubmissionId().toString());
				logger.warn("Checking evolution handler for default or ignore flags");
				if(! evolutionConfig.containsHandler(metatag)){ 
					logger.error("Metatag " + metatag + " does not contain an evolution handler.");
					throw new MetaInfoExtractorException("Metatag " + metatag + " does not contain an evolution handler.");
				}
				ChangedMetatagHandler handler  = evolutionConfig.getChangeHandler(metatag);
				if(! handler.getIgnore() && ! handler.getUseDefaultValue()){
					logger.error("Metatag " + metatag + " not handled by evolution handler. "  + getSubmissionId().toString());
					throw new MetaInfoExtractorException("Metatag " + metatag + " not handled by evolution handler. " + getSubmissionId().toString());
				}
				//If there is a handler in place, we can ignore it.
			}else{
				validatorMap.put(metatag, parentQID);
				if(testParentID == null){
					testParentID = parentQID;
				}
			}
		}
		if(testParentID == null){
			String metatagList = "";
			for(String mt : requiredGroupMetatags){
				metatagList += " " + mt;
			}
			logger.error("Can't resolve multiple question group for metatags " + metatagList + " sID:" + getSubmissionId().toString());
			throw new MetaInfoExtractorException("Can't resolve multiple question group for metatags " + metatagList + " sID:" + getSubmissionId().toString());
		}
		//Now validate every parentQID is the same for entries in the validate map.
		logger.debug("Validating question set for " + testParentID + " sID:" + getSubmissionId().toString());
		for(Map.Entry<String,String> entry : validatorMap.entrySet() ){
			if(!testParentID.equalsIgnoreCase(entry.getValue())){
				String message = "Different MultipleQuestionGroup IDs for group metatags requested : test ID - " + testParentID;
				for(Map.Entry<String,String> ent : validatorMap.entrySet()){
					message += ent.getKey() + " " + ent.getValue() + " ";
				}
				logger.error(message);
				throw new MetaInfoExtractorException(message);
			}
		}
		return testParentID;
	}
	
	public Long getSubmissionId(){
		if(submission != null){
			return submission.getId();
		}
		return null;
	}
	
	public List<String> getCitationFormatAuthorNameList(String givenNameTag, String surnameTag) throws MetaInfoExtractorException{
		String mqgId1 = getParentQuestionIdForQuestionSetMetatag(givenNameTag);
		String mqgId2 = getParentQuestionIdForQuestionSetMetatag(surnameTag);
		if(!StringUtils.hasLength(mqgId1) || ! mqgId1.equals(mqgId2) ){
			logger.error("Author metatags can not be found in current submission id " + submission.getId() + " " + givenNameTag + " " + surnameTag);
			throw new MetaInfoExtractorException("Author metatags can not be found in current submission id " + submission.getId() + " " + givenNameTag + " " + surnameTag);
		}
		List<Map<String,List<String>>> questionSetResponses = getMetatagQuestionSetResponses(mqgId1);
		List<String> authorList = new ArrayList<String>();
		for(Map<String,List<String>> setResponseMap : questionSetResponses){
			List<String> surnameResp = getQuestionSetResponseForMetatag(surnameTag, setResponseMap);
			if(surnameResp == null || surnameResp.size() == 0){
				logger.warn("empty author group entry " + submission.getId());
				continue;
			}
			String surname = surnameResp.get(0);
			String initials = null;
			List<String> initialsResp = getQuestionSetResponseForMetatag(givenNameTag, setResponseMap);
			if(initialsResp != null && initialsResp.size() > 0){
				initials = initialsResp.get(0);
			}
			if(StringUtils.hasLength(initials)){
				authorList.add(surname + ", " + initials);
			}else{
				authorList.add(surname);
			}
		}
		return authorList;
	}
	
	private List<String> getQuestionSetResponseForMetatag(String metatag, Map<String,List<String>> questionSetResponseMap){
		if(questionSetResponseMap.containsKey(metatag)){
			return questionSetResponseMap.get(metatag);
		}
		//If we are here we either have no response for this set for the metatag,
		// or we may need to invoke the evolution handler
		if(! metatagToQuestionIdMap.containsKey(metatag) && evolutionConfig.containsHandler(metatag)){
			ChangedMetatagHandler changeHandler = evolutionConfig.getChangeHandler(metatag);
			if(changeHandler.getIgnore()){
				return  new ArrayList<String>();
			}
			if(changeHandler.getUseDefaultValue()){
				List<String> response = new ArrayList<String>();
				response.add(changeHandler.getDefaultTextValue());
				return response;
			}
			if(changeHandler.getAlternateMetatagList() != null){
			    for(String altMeta : changeHandler.getAlternateMetatagList()){
			    	if(questionSetResponseMap.containsKey(altMeta)){
			    		logger.info("Using alternative metatag " + altMeta + " for " + metatag);
			    		return questionSetResponseMap.get(altMeta);
			    	}
			    }
			}
			logger.info("no alternative metatag found for " + metatag);
		}
		logger.debug("Returning empty response for metatag " + metatag);
		return new ArrayList<String>();
	}
	
	public List<String> getAuthorNameList(String givenNameTag, String surnameTag) throws MetaInfoExtractorException{
		List<String> authorNameList = new ArrayList<String>();
		List<String> givenNamesList = getResponseListFromQuestionSet(givenNameTag);
		List<String> surnamesList = getResponseListFromQuestionSet(surnameTag);
		int listSize = 0;
		if(givenNamesList.size() > listSize){
			listSize = givenNamesList.size();
		}
		if(surnamesList.size() > listSize){
			listSize = surnamesList.size();
		}
		if(listSize == 0){
			return authorNameList;
		}
		for(int x = 0; x < listSize; x++){
			String givenNames = "";
			if(givenNamesList.size() > x){
				String name = givenNamesList.get(x);
				if(StringUtils.hasLength(name)){
					givenNames = name + " ";
				}
			}
			
			String surname = "";
			if(surnamesList.size() > x){
				String name = surnamesList.get(x);
				if(StringUtils.hasLength(name)){
					surname = name;
				}
			}
			String authorName = givenNames + surname;
			if(StringUtils.hasLength(authorName)){
				authorNameList.add(authorName);
			}
		}
		return authorNameList;
	}
	
	public String getDatasetPublicationYear(){
		//First try last review date
		Date publicationDate = new Date();
		if(submission.getLastReviewDate() != null){
			publicationDate = submission.getLastReviewDate();
		}else if(submission.getSubmissionDate() != null){
			publicationDate = submission.getSubmissionDate();
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(publicationDate);
		Integer year = new Integer(cal.get(Calendar.YEAR) );
		return year.toString();
	}
	
	//Handles possible use vocab display, and vocab suggested values
	private String getSingleResponseFromSubmissionAnswer(SubmissionAnswer sa){
		if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(sa.getResponseType()) &&
				StringUtils.hasLength(sa.getSuggestedResponse() ) ){
			return sa.getSuggestedResponse();
		}else if(ResponseType.CONTROLLED_VOCAB.equals(sa.getResponseType()) || 
				ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(sa.getResponseType()) ){
			if(sa.getQuestionId() != null){
				return getDisplayStringResponseForVocabQuestionId(sa.getResponse(), sa.getQuestionId());
			}
		}
	    return sa.getResponse();
	}
	
	private String getSingleResponseFromSubmissionAnswerIgnoreDisplayValue(SubmissionAnswer sa){
		if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(sa.getResponseType()) &&
				StringUtils.hasLength(sa.getSuggestedResponse() ) ){
			return sa.getSuggestedResponse();
		}else if(ResponseType.CONTROLLED_VOCAB.equals(sa.getResponseType()) || 
				ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(sa.getResponseType()) ){
			if(sa.getQuestionId() != null){
				return sa.getResponse();
			}
		}
	    return sa.getResponse();
	}
	
	private String getDisplayStringResponseForVocabQuestionId(String response, String questionId){
		String traitName = getTraitNameForQuestionId(questionId);
		if(! StringUtils.hasLength(traitName)){
			return response;
		}
		return getDisplayStringResponseFromControlledVocab(response, traitName);
	}
	
	private String getTraitNameForQuestionId(String questionId){
		if(! questionIdToConfigQuestionMap.containsKey(questionId) ){
			return null;
		}
		Question q = questionIdToConfigQuestionMap.get(questionId);
		return q.getTraitName();
	}
	
	private String getDisplayStringResponseFromControlledVocab(String response, String traitName){
		if(!StringUtils.hasLength(response)){
			return response;
		}
		if(controlledVocabService != null && useVocabDisplayValues ){
			logger.debug("Looking up display text for vocab value " + response + " for trait " + traitName);
			String displayText = controlledVocabService.getTraitDisplayText(traitName, response);
			if(StringUtils.hasLength(displayText)){
				logger.debug("Display text for vocab value " + response + " for trait " + traitName + " found :" + displayText);
				return displayText;
			}
		}
	    return response;
	}
	public void setControlledVocabService(
			ControlledVocabularyService controlledVocabService) {
		this.controlledVocabService = controlledVocabService;
	}
	public void setUseVocabDisplayValues(boolean useVocabDisplayValues) {
		this.useVocabDisplayValues = useVocabDisplayValues;
	}
	public Submission getSubmission() {
		return submission;
	}
	public void setSubmission(Submission submission) {
		this.submission = submission;
	}
	public QuestionnaireConfig getQuestionnaireConfig() {
		return questionnaireConfig;
	}
	public void setQuestionnaireConfig(QuestionnaireConfig questionnaireConfig) {
		this.questionnaireConfig = questionnaireConfig;
	}
	public MetatagEvolutionConfig getEvolutionConfig() {
		return evolutionConfig;
	}
}
