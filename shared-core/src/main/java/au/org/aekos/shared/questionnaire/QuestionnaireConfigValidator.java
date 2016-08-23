package au.org.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.questionnaire.jaxb.SameAsOption;


/**
 * The idea here is that this Validator will ensure the post bound, jaxb object 
 * will actually support the configurable questionnaire page flow and functionality. 
 * 
 * 
 * @author a1042238
 *
 */
public class QuestionnaireConfigValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return QuestionnaireConfig.class.equals(clazz);
	}

	//Need to validate that - 
	//All questions have text ( actually - handled by the jaxb bind, among other things )
	
	//All QuestionGroups have unique IDs
	//No repeated question IDs, ( could allow this in mutually exclusive groups - but not today )
	
	//All pre-requisite questions are present in the survey.
	//All pre-requisite questions are in the correct order i.e. preceding their display condition
	//All pre-requisite questions requiring a specific value, are of controlled vocab or YES_NO type
	//All controlled vocab questions specify a traitName for the list.
	
	//If a question is CONTROLLED_VOCAB and has same as options specified,
	//The same as options must use the same vocabulary.  
	//If a same as option is a CONTROLLED_VOCAB_SUGGEST, the question must also be CONTROLLED_VOCAB_SUGGEST
	
	//Prepopulate questions must be of the same type, and only TEXT, TEXT_BOX, CV and CV_SUGGEST
	
	//No repeated question IDs, 
	
	//Only one SITE_FILE response question present  SHD-311
	
	public void validate(Object target, Errors errors) {
		QuestionnaireConfig config =  ( QuestionnaireConfig ) target;
		Map<String, QuestionGroup> questionGroupMap = new HashMap<String, QuestionGroup>();
		Map<String, Question> questionMap = new HashMap<String, Question>();
		List<String> idOccurrenceList = new ArrayList<String>();
		for(Object obj : config.getItems().getEntryList() ){
			if(obj instanceof QuestionGroup ){
				validateQuestionGroup( (QuestionGroup) obj, errors, questionGroupMap, questionMap, idOccurrenceList );
			}else if(obj instanceof Question){
				validateQuestion( (Question) obj, errors, questionMap, idOccurrenceList );
			}
		}
		//All controlled vocab questions specify a traitName for the list.
		if(! errors.hasErrors()){
			validateTitleQuestionIdExistsAndIsMandatory(config, questionMap, errors) ;
			for(String questionId : questionMap.keySet() ){
				Question q = questionMap.get(questionId);
				if(q.getResponseType().equals(ResponseType.CONTROLLED_VOCAB) 
						|| q.getResponseType().equals(ResponseType.CONTROLLED_VOCAB_SUGGEST)  ){
					if( ! StringUtils.hasLength( q.getTraitName() ) ){
				        errors.reject(null, "Question " + questionId + " requires a traitName to be supplied for the controlled vocabulary");
					}else if(q.getSameAsOptionList() != null 
							&& q.getSameAsOptionList().getOptionList() != null && q.getSameAsOptionList().getOptionList().size() > 0 ){
						validateSameAsOptions( q, questionMap, errors );
					}
				}
				validatePrepopulate(q, questionMap, errors);
			}
			validateReusableGroups(config, questionMap, questionGroupMap, errors );
			validateNoMoreThanOneSiteFile(config, errors);
		}
		if(! errors.hasErrors()){
			validateMetaModel(config, errors);
		}
		
	}
	
	private void validateQuestionGroup( QuestionGroup group, Errors errors,
			                            Map<String, QuestionGroup> questionGroupMap, 
			                            Map<String, Question> questionMap, 
			                            List<String> idOccurrenceList ){
		if( questionGroupMap.containsKey( group.getId() ) ){
			//TODO Can provide more context information in the error message if required.
			errors.reject(null, "There is more than 1 QuestionGroup with ID " + group.getId() + ". Group IDs must be unique." );
		}else{
			questionGroupMap.put(group.getId(), group);
			idOccurrenceList.add("G_" + group.getId());
			ConditionalDisplay cd = group.getDisplayCondition();
			if(cd != null){
				validateDisplayConditions(cd, group.getId(), false, questionMap, errors);
			}
			for(Object obj : group.getElements() ){
				if(obj instanceof QuestionGroup ){
					validateQuestionGroup( (QuestionGroup) obj, errors, questionGroupMap, questionMap, idOccurrenceList );
				}else if(obj instanceof Question){
					validateQuestion( (Question) obj, errors, questionMap, idOccurrenceList );
				}
			}
		}
	}
	
	public void validateQuestion( Question question, 
			Errors errors,
            Map<String, Question> questionMap, 
            List<String> idOccurrenceList ){
		if(question.getResponseType() == null ){
			errors.reject(null, "Question with ID " + question.getId() + " has an invalid response type." );
			return;
		}
		
		//Ensure question id is unique
		if(questionMap.containsKey(question.getId()) ){
			errors.reject(null, "There is more than 1 Question with ID " + question.getId() + ". Question IDs must be unique." );
		}
		else{
			questionMap.put(question.getId(), question);
			idOccurrenceList.add(question.getId());
			ConditionalDisplay cd = question.getDisplayCondition();
			if(cd != null){
				validateDisplayConditions(cd, question.getId(), true, questionMap, errors);
			}
		}
    }
	
	public void validateDisplayConditions(ConditionalDisplay cd, String id, boolean isQuestion, Map<String, Question> questionMap, Errors errors){
		if( questionMap.get( cd.getQuestionId() ) == null ){
			String message = "Display Condition for ";
			if(isQuestion){
				message += "question ";
			}else{
				message += "questionGroup ";
			}
			message += id + " depends on question " + cd.getQuestionId() + " which does not exist, or precede " + id + " in the config file";
			errors.reject(null, message);
			return;
		}
		
		Question q = questionMap.get( cd.getQuestionId() );
		if( StringUtils.hasLength(cd.getResponseValue() ) && 
				! ( ResponseType.CONTROLLED_VOCAB.equals( q.getResponseType() ) 
						|| ResponseType.YES_NO.equals( q.getResponseType() ) 
						|| ResponseType.CONTROLLED_VOCAB_SUGGEST.equals( q.getResponseType() )) ){
			String message = "Display Condition for ";
			if(isQuestion){
				message += "question ";
			}else{
				message += "questionGroup ";
			}
			message += id + " requests a responseValue, but question " + cd.getQuestionId() + " is not of type CONTROLLED_VOCAB or YES_NO.";  
			errors.reject(null, message);
		}		
	}
	
	public void validateTitleQuestionIdExistsAndIsMandatory(QuestionnaireConfig config, Map<String, Question> questionMap, Errors errors){
		if(StringUtils.hasLength( config.getSubmissionTitleQuestionId()) ) {
			if( ! questionMap.containsKey(config.getSubmissionTitleQuestionId()) ){
				errors.reject(null,"A submissionTitleQuestionId element has been specified :" 
			          + config.getSubmissionTitleQuestionId() + " but no question with that id has been defined.");
				return;
			}
			Question q = questionMap.get(config.getSubmissionTitleQuestionId());
			if( q.getResponseMandatory() == null || !  q.getResponseMandatory()){
				errors.reject(null,"A submissionTitleQuestionId element has been specified :" 
				          + config.getSubmissionTitleQuestionId() + " but the question is not defined as mandatory.");
			}
		}
	}
	
	public void validateReusableGroups(QuestionnaireConfig config,
			Map<String, Question> questionMap,
			Map<String, QuestionGroup> questionGroupMap, Errors errors) {
		
		for(QuestionGroup qg : questionGroupMap.values() ){
			if(qg.getReusableGroup() != null && qg.getReusableGroup() == true){
				//Validate the group specifies a reusable group title question ID
				if(!StringUtils.hasLength(qg.getReusableGroupTitleQuestionId())){
					errors.reject(null,"A reusuable group has been defined, but no reusableGroupTitleQuestion ID has been specified. Group ID :" + qg.getId() );
				    continue;
				}
				//Now need to ensure the the reusable group question ID is indeed inside the reusable group,
				//and that it is a TEXT response type, and is specified mandatory
				String groupTitleQID = qg.getReusableGroupTitleQuestionId();
				Question titleQ = qg.getChildQuestionById(groupTitleQID);
				if(titleQ == null){
					errors.reject(null,"ReusableGroupTitleQuestion not found. Group ID :" + qg.getId() + "  Question ID:" +  groupTitleQID);
					continue;
				}
				if(! ResponseType.TEXT.equals( titleQ.getResponseType() ) ){
					errors.reject(null,"Reusable Group Title Question response type must be of type TEXT. Group ID :" + qg.getId() + "  Question ID:" +  groupTitleQID + "   Response Type:" + titleQ.getResponseType().name() );
					continue;
				}
			}
		}
	}
	
	public void validateSameAsOptions(Question q, Map<String, Question> questionMap, Errors errors){
		for(SameAsOption sameAsOption : q.getSameAsOptionList().getOptionList() ) {
			String qId = sameAsOption.getQuestionId();
            if(!StringUtils.hasLength(qId) ){
            	errors.reject(null, "Question " + q.getId() + " has a SameAs Option Specified with no question Id");
            }else if(!StringUtils.hasLength(sameAsOption.getText())){
            	errors.reject(null, "Question " + q.getId() + " has a SameAs Option for " + qId + " but no option text specified.");
            }else if(! questionMap.containsKey(qId) ){
            	errors.reject(null, "Question " + q.getId() + " has a SameAs Option Specified for question Id " + qId + " which does not exist");
            }else{
            	Question sameAsQ = questionMap.get(qId);
            	if(! ResponseType.CONTROLLED_VOCAB.equals(sameAsQ.getResponseType()) && 
            			! ResponseType.CONTROLLED_VOCAB_SUGGEST.equals( sameAsQ.getResponseType()) ){
            		errors.reject(null, "Question " + q.getId() + " has a SameAs Option Specified for question Id " + qId + " which is not of type CONTROLLED_VOCAB");
            	}else if(! q.getTraitName().equals(sameAsQ.getTraitName() )){
            		errors.reject(null, "Question " + q.getId() + " has a SameAs Option Specified for question Id " + qId + " which does not have trait name " + q.getTraitName());
            	}else if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(sameAsQ.getResponseType()) 
            			&& ResponseType.CONTROLLED_VOCAB.equals(q.getResponseType()) ){
            		errors.reject(null, "Question " + q.getId() + " has a SameAs Option Specified for question Id " + qId + " which is CONTROLLED_VOCAB_SUGGEST, " + q.getId() + " needs to be SUGGEST too");
            	}
            }
		}
	}
	
	public void validatePrepopulate(Question q, Map<String, Question> questionMap, Errors errors){
		if( ! StringUtils.hasLength( q.getPrepopulateQuestionId() ) ){
			return;
		}
		if(! ResponseType.TEXT.equals(q.getResponseType()) &&
				! ResponseType.TEXT_BOX.equals(q.getResponseType()) &&
				! ResponseType.CONTROLLED_VOCAB.equals(q.getResponseType()) &&
				! ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(q.getResponseType()) ){
			errors.reject(null, "Question " + q.getId() + " is not of a response type suitable for prepopulate.  Should be [TEXT,TEXT_BOX,CONTROLLED_VOCAB,CONTROLLED_VOCAB_SUGGEST]");
		}else if(! questionMap.containsKey(q.getPrepopulateQuestionId()) ){
			errors.reject(null, "Question " + q.getId() + " wants to prepopulate from a question that does not exist :" + q.getPrepopulateQuestionId());
		}else if( ! q.getResponseType().equals(questionMap.get(q.getPrepopulateQuestionId()).getResponseType()) ){
			errors.reject(null, "Question " + q.getId() +" prepopulate source question " + q.getPrepopulateQuestionId() + " needs to be of same Response Type " +q.getResponseType() +":" + questionMap.get(q.getPrepopulateQuestionId()).getResponseType());
		}else if (q.getResponseType().name().contains("CONTROLLED_VOCAB")){
			//ensure both questions refer to the same trait
			if(! q.getTraitName().equals(questionMap.get(q.getPrepopulateQuestionId()).getTraitName())){
				errors.reject(null, "Question " + q.getId() +" prepopulate source question " + q.getPrepopulateQuestionId() + " need to reference the same traitName " +q.getTraitName() +":" + questionMap.get(q.getPrepopulateQuestionId()).getTraitName());
			}
		}
	}
	
	//Discussed with Matt, we decided each metatag should only appear once in the questionnaire,
	//at least from a configuration perspective.
	public void validateMetaModel(QuestionnaireConfig config, Errors errors){
		List<Question> questionList = config.getAllQuestions(true);
		Map<String,String> metatagToQuestionIdMap = new HashMap<String,String>();
		for(Question q : questionList){
			if(StringUtils.hasLength( q.getMetatag() ) && metatagToQuestionIdMap.containsKey(q.getMetatag())){
				errors.reject("", "Metatag " +q.getMetatag()  + " used more than once. Question " + metatagToQuestionIdMap.get(q.getMetatag()) + " and question " + q.getId());
			}else if(StringUtils.hasLength( q.getMetatag() )){
				metatagToQuestionIdMap.put(q.getMetatag(), q.getId());
			}
		}
	}
	
	public void validateNoMoreThanOneSiteFile(QuestionnaireConfig config, Errors errors){
		List<Question> questionList = config.getAllQuestions(true);
		List<Question> siteFileQuestions = new ArrayList<Question>();
		for(Question q : questionList){
			if(ResponseType.SITE_FILE.equals(q.getResponseType())){
				siteFileQuestions.add(q);
			}
		}
		if(siteFileQuestions.size() > 1){
			String questStr = "";
			for(Question sfq : siteFileQuestions){
				if(questStr.length() > 0){
					questStr += ",";
				}
				questStr += sfq.getId();
			}
			String errorMessage = "SITE_FILE response type used more than once. Questions " + questStr;
			errors.reject(null, errorMessage);
		}
	}
	
	
	
	
	
	
	
}
