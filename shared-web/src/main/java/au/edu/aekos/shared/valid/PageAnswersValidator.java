package au.edu.aekos.shared.valid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.ChangedMetatagHandler;
import au.edu.aekos.shared.data.infomodel.MetatagEvolutionConfig;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.PageAnswersModel;
import au.edu.aekos.shared.questionnaire.QuestionVisibilityUtils;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.index.StudyAreaBoundingBoxWriter;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.spatial.BBOX;
import au.edu.aekos.shared.spatial.CRSBounds;

@Component
public class PageAnswersValidator implements Validator {

	private static final int MAX_TITLE_LENGTH_CHARS = 200;
	
	@Value("${submission.embargo.question.metatag}")
	private String embargoMetatag = "SHD.embargo";
	
	@Value("${submission.embargo.maxTimeMonths}")
	private Integer maxEmbargoTimeInMonths = 12;
	
	@Value("${submission.temporal.fromDate.metatag}")
	private String fromDateMetatag = "SHD.firstStudyAreaVisitDate";
	
	@Value("${submission.temporal.toDate.metatag}")
	private String toDateMetatag = "SHD.lastStudyAreaVisitDate";
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired  //Need to validate the min and max ordering of the bounding box
	private StudyAreaBoundingBoxWriter boundingBoxTagConfig;
	
	@Autowired
	private MetatagEvolutionConfig evolutionConfig;
	
	//Date string validation - will need reviewing in 2099!
	private static final Pattern DATE_STRING_PATTERN = Pattern.compile("([0-3][0-9])/([0-1][0-9])/([1-2][7|8|9|0][0-9][0-9])");
	
	public boolean supports(Class<?> clazz) {
		return PageAnswersModel.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		// Do nothing, we don't use this as part of the framework
	}
	
    public void validatePageAnswers( PageAnswersModel pageAnswers, Errors errors, DisplayQuestionnaire config) {
    	int pageNumber = pageAnswers.getPageNumber();
    	QuestionnairePage page = config.getPages().get(pageNumber - 1);
    	String titleQuestionId = config.getConfig().getSubmissionTitleQuestionId();
    	for (Object element : page.getElements()) {
    		if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.Question){
    			Question q = ( Question ) element;
    			if(QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(q, pageAnswers, config )){
    			    validateQuestion( q, pageAnswers, errors, titleQuestionId, config );
    			}
    		}
    		else if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup){
    			QuestionGroup group = (QuestionGroup) element;
    			if(QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(group, pageAnswers , config)){
    			    validateQuestionGroup (group, pageAnswers, errors, config, titleQuestionId );
    			}
    		}
    		else if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup){
    			MultipleQuestionGroup multipleQuestionGroup = (MultipleQuestionGroup) element;
    			if(QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(multipleQuestionGroup, pageAnswers , config)){
    				validateMultipleQuestionGroup (multipleQuestionGroup, pageAnswers, errors, config);
    			}
    		}
		}
    	if(! errors.hasErrors()){
    	    validateTemporalFields(pageAnswers, errors, config);
    	}
    	if(! errors.hasErrors()){
    	    validateBBOXAnswers(pageAnswers, errors, config);
    	}
	}
    
    private void validateQuestion(Question question, PageAnswersModel answers, Errors errors, String titleQuestionId, DisplayQuestionnaire config){
    	if(question.getResponseMandatory() != null && question.getResponseMandatory().booleanValue()){
    		Answer a = answers.getAnswers().get(question.getId());
    		if(! a.getIsMultiSelect()){
	    		if(! StringUtils.hasLength( a.getResponse() ) ){
	    			errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.mandatory");
	    		}else if( question.getId().equals(titleQuestionId) ){
	    			if( config.getSubmissionId() == null && checkExistsSubmissionTitleForUser(a.getResponse(), config) ){
	    				Object [] vars = { a.getResponse() };
	    				errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.nonuniquetitle", vars , "A submission with the same title already exists for this user.");
	    			}
	    			//Validate title length
	    			if(a.getResponse().length() > MAX_TITLE_LENGTH_CHARS ){
	    				Object [] vars = { new Integer(MAX_TITLE_LENGTH_CHARS) };
	    				errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.titletoolong", vars , "Submission Title is too long");
	    			}
	    		}
	    		if( a.getResponseType().equals(ResponseType.CONTROLLED_VOCAB_SUGGEST) 
	    				&& DisplayQuestionnaire.CV_OTHER_VALUE.equals(a.getResponse()) 
	    				&& ! StringUtils.hasLength( a.getSuggestedResponse() ) ){
	    			errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.mandatory");
	    		}
    		}else{
    			validateMultiselectQuestion(question, errors, a);
    		}
    	}
    	if(ResponseType.DATE.equals( question.getResponseType()) ){
    		Answer a = answers.getAnswers().get(question.getId());
    		if(a.hasResponse()){
    			if( ! checkDateFormat(a.getResponse()) ){
    				errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.invalidDateFormat");
    			}else{
    				validateEmbargoDate(question,a,errors);
    			}
    		}
    	}
    	if( ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(question.getResponseType() ) ){ 
    		Answer a = answers.getAnswers().get(question.getId());
			if( DisplayQuestionnaire.CV_OTHER_VALUE.equals(a.getResponse()) 
				&& ! StringUtils.hasLength( a.getSuggestedResponse() ) ){
			    errors.rejectValue("answers["+ question.getId() + "].suggestedResponse", "questionnaire.validation.suggestedResponseNull");
			}
		}
    	//Custom Validation - this could be pluggable, but for now its only for Integer validation on a text field
    	if(ResponseType.TEXT.equals(question.getResponseType()) && "int".equals(question.getCustomValidation()) ){
    		Answer a = answers.getAnswers().get(question.getId());
    		if(StringUtils.hasLength( a.getResponse() ) ){
    			String intTest =  a.getResponse().trim();
    			for(int x = 0; x < intTest.length() ; x++){
    			    if(! Character.isDigit(intTest.charAt(x) ) ){
    			    	errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.integerValue");
    			    	break;
    			    }
    			}
    		}
    	}
    }

	/**
	 * @param question	The question for the answer that's being validated
	 * @param errors	Object to report any validation failures
	 * @param answer	The answer to validate
	 */
	void validateMultiselectQuestion(Question question, Errors errors, Answer answer) {
		Answer multiA  = getFirstResponseMultiselectAnswerOrFirst(answer.getMultiselectAnswerList());
		if(! StringUtils.hasLength( multiA.getResponse() ) ){
			errors.rejectValue("answers["+ question.getId() + "].multiselectAnswerList[0].response", "questionnaire.validation.mandatory");
		}
		if(multiA.getResponseType() != null && multiA.getResponseType().equals(ResponseType.CONTROLLED_VOCAB_SUGGEST) 
				&& DisplayQuestionnaire.CV_OTHER_VALUE.equals(multiA.getResponse()) 
				&& ! StringUtils.hasLength( multiA.getSuggestedResponse() ) ){
			errors.rejectValue("answers["+ question.getId() + "].multiselectAnswerList[0].response", "questionnaire.validation.mandatory");
		}
	}
    
    /**
     * We have already removed null answers when writing to the session questionnaire.
     * Need to preserve that logic for redisplay
     * @param multiselectAnswers
     * @return
     */
    Answer getFirstResponseMultiselectAnswerOrFirst( List<Answer> multiselectAnswers ){
    	for(Answer ans : multiselectAnswers ){
    		if(StringUtils.hasLength(ans.getResponse())){
    			return ans;
    		}
    	}
    	return Answer.NULL_ANSWER;
    }
    
    private void validateMultipleQuestionGroup(MultipleQuestionGroup multipleQuestionGroup, PageAnswersModel answers, Errors errors, DisplayQuestionnaire config ){
    	if(multipleQuestionGroup.getResponseMandatory() && ! multipleQuestionGroupHasNonNullEntry(answers.getAnswers().get(multipleQuestionGroup.getId()))){
    		//verify at least one non null response set is present in the list.
    		Object [] vars = { multipleQuestionGroup.getId() };
    		errors.reject("questionnaire.validation.multipleQuestionGroup.mandatory", vars, "Requires at least one entry");
    		return;
    	}
    	
    	Answer questionSetAnswer = answers.getAnswers().get(multipleQuestionGroup.getId());
    	if(questionSetAnswer.getAnswerSetList() != null && questionSetAnswer.getAnswerSetList().size() > 0){
            for(int x = 0; x < questionSetAnswer.getAnswerSetList().size(); x++ ){
            	Map<String, Answer> answerSet = questionSetAnswer.getAnswerSetList().get(x);
            	//The first set always needs to exist, beyond that, if there is no answer for the whole set 
            	//it won`t be persisted, so no need to validate.
            	boolean validateElements = true;
            	
            	if((x==0 && ! multipleQuestionGroup.getResponseMandatory() ) || x > 0){
            		validateElements = false;
            		//see if there are any non null elements in the answerSet, 
        		    for(Answer ans : answerSet.values()){
	        			if(ans.hasResponse()){
	        				validateElements = true;
	        				break;
	        			}
        		    }
        		}
            	if(validateElements){
	            	for(Object obj : multipleQuestionGroup.getElements()){
	            		Question q = (Question) obj;
	            		if(q.getResponseMandatory() && ! answerSet.get(q.getId()).hasResponse() ){
	            			errors.rejectValue("answers["+ multipleQuestionGroup.getId() + "].answerSetList["+ x +"]["+ q.getId() +"].response", "questionnaire.validation.mandatory");
	            		}else if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(q.getResponseType()) 
	            				&& "OTHER".equals(answerSet.get(q.getId()).getResponse()) 
	            				&& !StringUtils.hasLength(answerSet.get(q.getId()).getSuggestedResponse() ) ){
	            			errors.rejectValue("answers["+ multipleQuestionGroup.getId() + "].answerSetList["+ x +"]["+ q.getId() +"].suggestedResponse", "questionnaire.validation.suggestedResponseNull");
	            		}
	            	}
            	}
            }
    	}
    }
    
    private boolean multipleQuestionGroupHasNonNullEntry(Answer answerSetAnswer){
    	boolean hasNonNullEntry = false;
    	if(answerSetAnswer.getAnswerSetList() != null && answerSetAnswer.getAnswerSetList().size() > 0){
    		for(Map<String,Answer> answerSet : answerSetAnswer.getAnswerSetList()){
    			for(Answer ans : answerSet.values() ){
					if(ans.hasResponse()){
						hasNonNullEntry = true;
						break;
					}
				}
    			if(hasNonNullEntry){
    				break;
    			}
    		}
    	}
    	return hasNonNullEntry;
    }
    
    private void validateQuestionGroup(QuestionGroup questionGroup, PageAnswersModel answers, Errors errors, DisplayQuestionnaire config, String titleQuestionId ){
    	for (Object element : questionGroup.getElements()) {
    		if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.Question){
    			Question q = ( Question ) element;
    			if(QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(q, answers, config )){
    			    validateQuestion( (Question) element, answers, errors,titleQuestionId , config);
    			}
    		}
    		else if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup){
    			QuestionGroup group = (QuestionGroup) element;
    			if(QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(group, answers , config)){
    			    validateQuestionGroup ((QuestionGroup) element, answers, errors , config,titleQuestionId );
    			}
    		}
    		else if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup){
    			MultipleQuestionGroup multipleQuestionGroup = (MultipleQuestionGroup) element;
    			if(QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(multipleQuestionGroup, answers , config)){
    				validateMultipleQuestionGroup (multipleQuestionGroup, answers, errors, config);
    			}
    		}
		}
    }
    
    //Assuming format dd/MM/yyyy
    public static boolean checkDateFormat(String dateString){
    	Matcher m = DATE_STRING_PATTERN.matcher(dateString);
    	return m.matches();
    }
    
    private void validateEmbargoDate(Question question, Answer a, Errors errors){
    	if(StringUtils.hasLength(a.getResponse()) && ResponseType.DATE.equals(question.getResponseType()) && embargoMetatag.equals(question.getMetatag())){
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    		Date d = null;
    		try {
				d = sdf.parse(a.getResponse());
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
    		
    		Calendar c = GregorianCalendar.getInstance();
    		c.add(Calendar.MONTH, maxEmbargoTimeInMonths);
    		if(d.compareTo( c.getTime() ) > 0 ){
	    	    Object [] vals = { maxEmbargoTimeInMonths.toString() };
	    	    errors.rejectValue("answers["+ question.getId() + "].response", "questionnaire.validation.maxEmbargoDate", vals   , "Max embargo date duration breached" );
    		}
    	}
    }
    
    private boolean checkExistsSubmissionTitleForUser(String submissionTitle, DisplayQuestionnaire config){
    	String username = securityService.getLoggedInUsername();
    	Submission sub = submissionService.retrieveSubmissionByTitleAndUsername(submissionTitle, username);
    	if(sub != null && sub.getId() != config.getSubmissionId() ){
    		return true;
    	}
    	//Now check there is'nt a saved submission of the same title
    	Submission sub2 = submissionService.retrieveSubmissionByTitleAndUsername(submissionTitle + SubmissionService.SAVED_SUB_TITLE_SFX, username);
    	if(sub2 != null && ! sub2.getId().equals(config.getSavedSubmissionId())){
    		return true;
    	}
    	return false;
    }
    
    private void validateBBOXAnswers(PageAnswersModel pageAnswers, Errors errors, DisplayQuestionnaire config){
    	Map<String,String> tagToQIDMap = config.getConfig().getMetatagToQuestionIdMap();
    	String minXTag = boundingBoxTagConfig.getMinXTag();
    	String minXQuestionId = tagToQIDMap.get(minXTag);
    	//first check if the question is on the current page we are validating.
    	if(! pageAnswers.getAnswers().containsKey(minXQuestionId)){
    		return;
    	}
    	Question q = config.getAllQuestionsMapFromConfig().get(minXQuestionId);
    	if(q == null){
    		return;
    	}
    	//Now check if its visible - might be the specific question, but if the question has no display condition need to
    	//go back thru the hierarchy to see if the enclosing group is visible.
    	boolean isVisible = true;
    	if(q.getDisplayCondition() != null){
    		isVisible = QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(q, pageAnswers, config );
    	}else{
    		//Need to find if an enclosing QuestionGroup is not visible
    		for(QuestionGroup qg : config.getConfig().getQuestionGroupMap().values() ){
    			if(qg.getChildQuestionById(minXQuestionId) != null && ! QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(qg, pageAnswers, config ) ){
    				isVisible = false;
    				break;
    			}
    		}
    	}
    	//if it is, grab the rest of the BBOX coords and make sure they parse to double
    	//and that the min coords are indeed less than the max coords
    	if(isVisible ){
    		Double minX = null;
    		Double minY = null;
    		Double maxX = null;
    		Double maxY = null;
    		try{
	    		minX = parseAndValidateBBOXOrdinate(tagToQIDMap, boundingBoxTagConfig.getMinXTag(), pageAnswers, errors);
	    		minY = parseAndValidateBBOXOrdinate(tagToQIDMap, boundingBoxTagConfig.getMinYTag(), pageAnswers, errors);
	    		maxX = parseAndValidateBBOXOrdinate(tagToQIDMap, boundingBoxTagConfig.getMaxXTag(), pageAnswers, errors);
	    		maxY = parseAndValidateBBOXOrdinate(tagToQIDMap, boundingBoxTagConfig.getMaxYTag(), pageAnswers, errors);
    		}catch(NullPointerException ex){
    			errors.rejectValue("answers["+ minXQuestionId + "].response", "", "Problem with bounding box configuration");
    			return;
    		}
    		if(minX == null || minY == null || maxX == null || maxY == null){
    			errors.rejectValue("answers["+ minXQuestionId + "].response", "", "Problem with bounding box configuration");
    			return;
    		}
    		if (minX.doubleValue() >= maxX.doubleValue() ){
    			errors.rejectValue("answers["+ minXQuestionId + "].response", "", "Min X must be less than Max X");
    		}
    		if (minY.doubleValue() >= maxY.doubleValue() ){
    			errors.rejectValue("answers["+ tagToQIDMap.get(boundingBoxTagConfig.getMinYTag()) + "].response", "", "Min Y must be less than Max Y");
    		}
    		if(! errors.hasErrors() ){
    			//Validate against the CRS Bounds
    			String crsQID = tagToQIDMap.get(boundingBoxTagConfig.getCrsTag() );
    			String epsgCode = pageAnswers.getAnswers().get(crsQID).getResponse();
    			BBOX bounds = CRSBounds.getBoundsBBOX(epsgCode);
    			if(!CRSBounds.isXInBounds(minX, epsgCode)) {
    				errors.rejectValue("answers["+ tagToQIDMap.get(boundingBoxTagConfig.getMinXTag()) + "].response", "", "Min X must be in range " + bounds.getXmin().toString() +"  to  "  + bounds.getXmax().toString() + " for CRS " + epsgCode );
    			}
    			if(!CRSBounds.isYInBounds(minY, epsgCode)) {
    				errors.rejectValue("answers["+ tagToQIDMap.get(boundingBoxTagConfig.getMinYTag()) + "].response", "", "Min Y must be in range " + bounds.getYmin().toString() +"  to  "  + bounds.getYmax().toString() + " for CRS " + epsgCode);
    			}
    			if(!CRSBounds.isXInBounds(maxX, epsgCode)) {
    				errors.rejectValue("answers["+ tagToQIDMap.get(boundingBoxTagConfig.getMaxXTag()) + "].response", "", "Max X must be in range " + bounds.getXmin().toString() +"  to  "  + bounds.getXmax().toString() + " for CRS " + epsgCode );
    			}
    			if(!CRSBounds.isYInBounds(maxY, epsgCode)) {
    				errors.rejectValue("answers["+ tagToQIDMap.get(boundingBoxTagConfig.getMaxYTag()) + "].response", "", "Max Y must be in range " + bounds.getYmin().toString() +"  to  "  + bounds.getYmax().toString() + " for CRS " + epsgCode);
    			}
    		}
    	}
    }
    
    private Double parseAndValidateBBOXOrdinate(Map<String,String> tagToQIDMap, String tag, PageAnswersModel pageAnswers, Errors errors){
    	String questionId = tagToQIDMap.get(tag);
		String ordinateStr = pageAnswers.getAnswers().get(questionId).getResponse();
		Double parsedOrdinate = null;
		try{
			parsedOrdinate = Double.valueOf(ordinateStr);
 		}catch(NumberFormatException ex){
 			errors.rejectValue("answers["+ questionId + "].response", "questionnaire.validation.bbox.numberFormat");
 		}
    	
    	return parsedOrdinate;
    }
    
    /**
     * To validate the first and last visit date are in the right order, 
     * first check those question metatags are on this page, then ensure both answers are not null,
     * then do the date comparison.  Dates CAN be the same day
     * @throws ParseException 
     */
    private void validateTemporalFields( PageAnswersModel pageAnswers, Errors errors, DisplayQuestionnaire config) {
    	Map<String, Answer> pgAnsMap =  pageAnswers.getAnswers();
    	Map<String, String> metatagToQuestIDMap = config.getConfig().getMetatagToQuestionIdMap();
    	String fromQID = metatagToQuestIDMap.get(fromDateMetatag);
    	String toQID = metatagToQuestIDMap.get(toDateMetatag);
    	if(!StringUtils.hasLength(fromQID) || !StringUtils.hasLength(toQID)){
    		ChangedMetatagHandler cmh = evolutionConfig.getChangeHandler(fromDateMetatag);
    		if(cmh != null){
	    		for(String altMeta : cmh.getAlternateMetatagList() ){
	    			if(metatagToQuestIDMap.containsKey(altMeta)){
	    				fromQID = metatagToQuestIDMap.get(altMeta);
	    				break;
	    			}
	    		}
    		}
    		cmh = evolutionConfig.getChangeHandler(toDateMetatag);
    		if(cmh != null){
	    		for(String altMeta : cmh.getAlternateMetatagList() ){
	    			if(metatagToQuestIDMap.containsKey(altMeta)){
	    				toQID = metatagToQuestIDMap.get(altMeta);
	    				break;
	    			}
	    		}
    		}
    	}
    	if(!StringUtils.hasLength(fromQID) || !StringUtils.hasLength(toQID)){
    		return;
    	}
    	if(!pgAnsMap.containsKey(fromQID) || !pgAnsMap.containsKey(toQID)){
    		return;
    	}
    	Answer fromDateAnswer = pgAnsMap.get(fromQID);
    	Answer toDateAnswer = pgAnsMap.get(toQID);
    	if(! ResponseType.DATE.equals(fromDateAnswer.getResponseType()) || ! fromDateAnswer.hasResponse() ||
    			! ResponseType.DATE.equals(toDateAnswer.getResponseType()) || ! toDateAnswer.hasResponse() ){
    		return;
    	}
    	
    	//If we are still here, we should have the 2 date responses,  we want to validate that the to date response
    	//is >= from date response 
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	try{
		    Date fromDate = sdf.parse(fromDateAnswer.getResponse());
		    Date toDate = sdf.parse(toDateAnswer.getResponse());
		    if(fromDate.compareTo(toDate) > 0){
		    	errors.rejectValue("answers["+ fromQID + "].response", "questionnaire.validation.temporalcoverage.start");
		    	errors.rejectValue("answers["+ toQID + "].response", "questionnaire.validation.temporalcoverage.end");
		    }
    	}catch(ParseException ex){
    		//We should'nt get here as date types are validated before we check for temporal reality
    		
    	}
    }
}
