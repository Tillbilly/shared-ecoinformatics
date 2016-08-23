package au.edu.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;

/**
 * Used to control the actual display of Questions and Question Groups on a page.
 * 
 * Any display condition preceding this page is captured in the 'show' field.
 * 
 * There are 4 dynamic conditions to determine whether to show based on questions
 * on the current page.
 * 
 * 
 * A preceding question is present and visible. ( m kae this work
 * 
 * and
 * 
 * A question has an answer ( is not null )
 * A question has a specific answer )
 * 
 * or
 * 
 * A question does not have an answer ( new, i.e. if no site file response then ask geo feature set questions )
 * 
 * We`ll need to put a change notification on any question that
 * is a prerequisite.
 * 
 * @author a1042238
 *
 */
public class DisplayCondition {
	//Show indicates whether the question should be shown at all - not used atm
	private boolean show = true;
	/**
	 * This is the question that is checked to determine whether to display this question.
	 */
	private String questionId;
	/**
	 * This is the question ID the displayCondition is for.
	 * Used for operations with display condition chains
	 */
	private String ownerId;
	
	
	//Reference to the Answer object - in the session
	private boolean onCurrentPage = false;
	private Answer conditionalAnswer = new Answer();
	private ConditionalDisplay conditionalDisplay;
	private DisplayCondition parentDisplayCondition;
	private List<DisplayCondition> childDisplayConditionList = new ArrayList<DisplayCondition>();
	
	public DisplayCondition(boolean show) {
		super();
		this.show = show;
	}
	
	public DisplayCondition() {
		super();
	}

	public boolean isShow() {
		return show;
	}

	

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Answer getConditionalAnswer() {
		return conditionalAnswer;
	}

	public void setConditionalAnswer(Answer conditionalAnswer) {
		this.conditionalAnswer = conditionalAnswer;
	}

	public boolean isOnCurrentPage() {
		return onCurrentPage;
	}

	public void setOnCurrentPage(boolean onCurrentPage) {
		this.onCurrentPage = onCurrentPage;
	}

	public ConditionalDisplay getConditionalDisplay() {
		return conditionalDisplay;
	}

	public void setConditionalDisplay(ConditionalDisplay conditionalDisplay) {
		this.conditionalDisplay = conditionalDisplay;
		this.questionId = conditionalDisplay.getQuestionId();
	}
	
	public boolean getIsVisibleOnlyCondition(){
		if(  ( getResponseNotNull() == null || ! getResponseNotNull() ) 
		     && ( conditionalDisplay.getResponseValue() == null )  
		     && ( getResponseNull() == null || ! getResponseNull() ) ){
			return true;
		}
		return false;
	}
	
    public Boolean getResponseNotNull(){
    	return conditionalDisplay.getResponseNotNull();
    }
    public String getResponseValue(){
    	return conditionalDisplay.getResponseValue();
    }
    public Boolean getResponseNull(){
    	return conditionalDisplay.getResponseNull();
    }
    
    public boolean getIsVisible(){
        if(parentDisplayCondition != null && ! parentDisplayCondition.getIsVisible()){
            return false;        	
        }
    	
    	if(getResponseNull() ){
    		return ! StringUtils.hasLength(conditionalAnswer.getResponse());
    	}else if(this.getResponseNotNull() && ! StringUtils.hasLength(getResponseValue())){
    		return StringUtils.hasLength(conditionalAnswer.getResponse());
    	}else if(StringUtils.hasLength(getResponseValue())){
    		return getResponseValue().equals(conditionalAnswer.getResponse());
    	}
    	return true;
    }

	public DisplayCondition getParentDisplayCondition() {
		return parentDisplayCondition;
	}

	public void setParentDisplayCondition(DisplayCondition parentDisplayCondition) {
		this.parentDisplayCondition = parentDisplayCondition;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public List<DisplayCondition> getChildDisplayConditionList() {
		return childDisplayConditionList;
	}

	public void setChildDisplayConditionList(
			List<DisplayCondition> childDisplayConditionList) {
		this.childDisplayConditionList = childDisplayConditionList;
	}
	
}
