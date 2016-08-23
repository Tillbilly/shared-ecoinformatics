package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestionSetModel {

	private Long questionSetEntityId;
	
	private Map<String,QuestionModel> questionModelMap = new LinkedHashMap<String, QuestionModel>();

	public Map<String, QuestionModel> getQuestionModelMap() {
		return questionModelMap;
	}

	public void setQuestionModelMap(Map<String, QuestionModel> questionModelMap) {
		this.questionModelMap = questionModelMap;
	}

	public Long getQuestionSetEntityId() {
		return questionSetEntityId;
	}

	public void setQuestionSetEntityId(Long questionSetEntityId) {
		this.questionSetEntityId = questionSetEntityId;
	}
	
	public List<QuestionModel> getQuestionModelList(){
		List<QuestionModel> questionModelList = new ArrayList<QuestionModel>();
		for(Map.Entry<String, QuestionModel> entry : questionModelMap.entrySet() ){  //Should be in order - using LinkedHashMap impl
			questionModelList.add(entry.getValue());
		}
		return questionModelList;
	}
	
	public boolean getHasResponse(){
		List<QuestionModel> questionModelList = getQuestionModelList();
		if(questionModelList == null || questionModelList.size() == 0){
			return false;
		}
		for(QuestionModel qm : questionModelList){
			if(qm.getHasResponse()){
				return true;
			}
		}
		return false;
	}
	
	
}
