package au.edu.aekos.shared.questionnaire;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;
import au.edu.aekos.shared.questionnaire.jaxb.DisplayConditionSubject;

public class QuestionVisibilityUtils {

	public static boolean checkDisplayConditionSubjectVisible(DisplayConditionSubject subject, PageAnswersModel pageAnswers, DisplayQuestionnaire config ){
    	if( subject.getDisplayCondition() == null || subject.getDisplayCondition().getQuestionId() == null){
    	    return true;	
    	}
    	String condQuestionId = subject.getDisplayCondition().getQuestionId();
    	Answer ans = getConditionalAnswer(condQuestionId, pageAnswers, config );
    	return compareConditionalDisplayToAnswer(ans, subject.getDisplayCondition());
    }
    
    public static boolean compareConditionalDisplayToAnswer(Answer ans, ConditionalDisplay condition){
    	if(ans == null){
    		return false;
    	}
    	if(condition.getResponseNull() == true && StringUtils.hasLength( ans.getResponse() ) ){
    		return false;
    	}
    	if(condition.getResponseNotNull() == true && ! StringUtils.hasLength( ans.getResponse() ) ){
    		return false;
    	}
    	if(StringUtils.hasLength(condition.getResponseValue()) && ! condition.getResponseValue().equals(ans.getResponse()) ){
    	    return false;
    	}
    	return true;
    }
    
    public static Answer getConditionalAnswer(String condQuestionId, PageAnswersModel pageAnswers, DisplayQuestionnaire config ){
    	//Check if conditional question answer in current page answers, or in previous page answers. 
    	Answer conditionalAnswer = pageAnswers.getAnswers().get(condQuestionId);
    	if(conditionalAnswer == null ){
    		//Need to check conditional answer is not meant to be on the current page,
    		//if it is and null need to return null, otherwise find it on previous pages.
    		int pageNumber = pageAnswers.getPageNumber();
    		QuestionnairePage page = config.getPages().get(pageNumber - 1);
    		if(page.containsQuestionId(condQuestionId)){
    			return null;
    		}
    		conditionalAnswer = config.getAllAnswers().get(condQuestionId);
    	}
    	return conditionalAnswer;
    }
	
	
}
