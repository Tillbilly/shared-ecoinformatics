package au.edu.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.ReusableAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

/**
 * Answer object for initial form based 'many question' submission
 * @author Ben Till
 *
 */
public class Answer {
	
	private String questionId;
	private String response;
	//The suggested response is when a user selects 'other' from a controlled vocab list,
	//and wishes to suggest what the response should be.
	private String suggestedResponse;
	private ResponseType responseType;
	
	//Used for the questionnaire summary view before submission
	private String displayResponse;
	
	//List of multiselect answers
	private List<Answer> multiselectAnswerList = new ArrayList<Answer>();
	
	//List of answer sets
	private List< Map<String, Answer> > answerSetList = new ArrayList< Map<String, Answer> >();
	
	public static final Answer NULL_ANSWER = getNullInstance();
	public static final String OTHER = "OTHER";
	
	public Answer(){}
	
	public Answer(Question q){
		this.questionId = q.getId();
		this.responseType = q.getResponseType();
		if(getIsMultiSelect()){
			multiselectAnswerList.add(new Answer(q, true));
		}
	}
	
	public Answer(Question q, boolean replaceMultiselectWithRaw){
		this.questionId = q.getId();
		this.responseType = q.getResponseType();
		if(getIsMultiSelect() && replaceMultiselectWithRaw){
			this.responseType = ResponseType.getRawType(q.getResponseType());
		}
	}
	
	public Answer(MultipleQuestionGroup qg){
		this.questionId = qg.getId();
		this.responseType = ResponseType.MULTIPLE_QUESTION_GROUP;
		Map<String, Answer> answerSet = new HashMap<String, Answer>();
		for(Object obj : qg.getElements()){
			if(obj instanceof au.edu.aekos.shared.questionnaire.jaxb.Question){
				Answer a = new Answer((Question) obj );
				answerSet.put(a.getQuestionId(), a);
			}
		}
		answerSetList.add(answerSet);
	}
	
	public void updateFromFormSubmission(Answer submittedAnswer){
		this.response = submittedAnswer.getResponse();
		this.suggestedResponse = submittedAnswer.getSuggestedResponse();
		if(getIsMultiSelect()){
			multiselectAnswerList.clear();
			ResponseType rawRS = ResponseType.getRawType (this.responseType);
			for(Answer msAnswer : submittedAnswer.multiselectAnswerList   ){
				if(StringUtils.hasLength( msAnswer.getResponse() ) ){
					msAnswer.setResponseType(rawRS);
					multiselectAnswerList.add(msAnswer);
				}
			}
			if(multiselectAnswerList.size() == 0){
				Answer dummyAnswer = new Answer();
				dummyAnswer.setResponseType(rawRS);
				multiselectAnswerList.add(dummyAnswer);
			}
		}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(this.responseType) ){
			answerSetList.clear();
			//Always keep the first entry, even if it is empty
			boolean firstEntry = true;
			for(Map<String, Answer> setAnswerMap : submittedAnswer.getAnswerSetList() ){
				if(firstEntry){
					answerSetList.add(setAnswerMap);
					firstEntry = false;
				}else{
					boolean hasNonNullEntry = false;
					for(Answer ans : setAnswerMap.values() ){
						if(ans.hasResponse()){
							hasNonNullEntry = true;
							break;
						}
					}
					if(hasNonNullEntry){
						answerSetList.add(setAnswerMap);
					}
				}
			}
		}
	}
	
	public void assignFromReusableAnswer(ReusableAnswer ra) {
		this.response = ra.getResponse();
		this.suggestedResponse = ra.getSuggestedResponse();
		if(getIsMultiSelect()){
			multiselectAnswerList.clear();
			ResponseType rawRS = ResponseType.getRawType (this.responseType);
			for(ReusableAnswer ruAnswer : ra.getMultiselectAnswerList()   ){
				if(StringUtils.hasLength( ruAnswer.getResponse() ) ){
					Answer msAnswer = new Answer();
					msAnswer.setResponse(ruAnswer.getResponse());
					msAnswer.setResponseType(rawRS);
					multiselectAnswerList.add(msAnswer);
				}
			}
			if(multiselectAnswerList.size() == 0){
				Answer dummyAnswer = new Answer();
				dummyAnswer.setResponseType(rawRS);
				multiselectAnswerList.add(dummyAnswer);
			}
		}
	}
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	public String getSuggestedResponse() {
		return suggestedResponse;
	}
	public void setSuggestedResponse(String suggestedResponse) {
		this.suggestedResponse = suggestedResponse;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	public ResponseType getResponseType() {
		return responseType;
	}
	public boolean getIsMultiSelect(){
		return ResponseType.getIsMultiselect(this.responseType);
	}
	public List<Answer> getMultiselectAnswerList() {
		return multiselectAnswerList;
	}
	public void setMultiselectAnswerList(List<Answer> multiselectAnswerList) {
		this.multiselectAnswerList = multiselectAnswerList;
	}
	
	public void setResponseToNull(){
		this.response = null;
		this.suggestedResponse = null;
		this.multiselectAnswerList.clear();
		if(getIsMultiSelect()){
			Answer defaultMultiAnswer = new Answer();
			defaultMultiAnswer.setQuestionId(this.questionId);
			defaultMultiAnswer.setResponseType(ResponseType.getRawType(this.responseType) );
			multiselectAnswerList.add(defaultMultiAnswer);
		}
	}
	
	public boolean hasResponse(){
		if(ResponseType.getIsMultiselect(responseType)){
			if(multiselectAnswerList == null || multiselectAnswerList.size() == 0 ||
				! StringUtils.hasLength( multiselectAnswerList.get(0).getResponse() ) ){
				return false;
			}
			return true;
		}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(responseType)){
			if( answerSetList != null && answerSetList.size() > 0 ){
                for(Map<String,Answer> answerSet : answerSetList){
                	for( Answer ans : answerSet.values() ){
                		if(ans.hasResponse()){
                			return true;
                		}
                	}
                }
			}
			return false;
		}
		else{
			return StringUtils.hasLength(response);
		}
	}

	@Override
	public String toString() {
		if (responseType == null) {
			return "(Unknown response type)";
		}
		switch (responseType) {
			case TEXT:
			case TEXT_BOX:
			case YES_NO:
			case CONTROLLED_VOCAB:
			case GEO_FEATURE_SET:
				return StringUtils.hasLength(response) ? response : "(no answer)";
			case MULTISELECT_CONTROLLED_VOCAB:
			case MULTISELECT_TEXT:
			case TREE_SELECT:
				return "[" + org.apache.commons.lang.StringUtils.join(multiselectAnswerList, ", ") + "]";
			case CONTROLLED_VOCAB_SUGGEST:
				if (StringUtils.hasLength(suggestedResponse)) {
					return response + "->" + suggestedResponse;
				}
				return response;
			default:
				return "[" + responseType + " answer]";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((multiselectAnswerList == null) ? 0 : multiselectAnswerList
						.hashCode());
		result = prime * result
				+ ((questionId == null) ? 0 : questionId.hashCode());
		result = prime * result
				+ ((response == null) ? 0 : response.hashCode());
		result = prime * result
				+ ((responseType == null) ? 0 : responseType.hashCode());
		result = prime
				* result
				+ ((suggestedResponse == null) ? 0 : suggestedResponse
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Answer other = (Answer) obj;
		if (multiselectAnswerList == null) {
			if (other.multiselectAnswerList != null)
				return false;
		} else if (!multiselectAnswerList.equals(other.multiselectAnswerList))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		if (responseType != other.responseType)
			return false;
		if (suggestedResponse == null) {
			if (other.suggestedResponse != null)
				return false;
		} else if (!suggestedResponse.equals(other.suggestedResponse))
			return false;
		return true;
	}
	public String getDisplayResponse() {
		return displayResponse;
	}
	public void setDisplayResponse(String displayResponse) {
		this.displayResponse = displayResponse;
	}
	public List<Map<String, Answer>> getAnswerSetList() {
		return answerSetList;
	}
	public void setAnswerSetList(List<Map<String, Answer>> answerSetList) {
		this.answerSetList = answerSetList;
	}
	
	private static Answer getNullInstance() {
		Answer result = new Answer();
		result.answerSetList = Collections.emptyList();
		result.displayResponse = "";
		result.multiselectAnswerList = Collections.emptyList();
		result.questionId = "";
		result.response = "";
		result.responseType = null;
		result.suggestedResponse = "";
		return result;
	}

	/**
	 * Indicates if the user has selected "Other" or not. If they have, there should be a value in {@link Answer#suggestedResponse}
	 * 
	 * @return	<code>true</code> is the user has selected "Other" from a drop down, <code>false</code> otherwise.
	 */
	public boolean isAnsweredAsOther() {
		return OTHER.equalsIgnoreCase(response);
	}
}
