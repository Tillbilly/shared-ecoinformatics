package au.edu.aekos.shared.cache.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;

public class QuestionnaireConfigElement implements Serializable{

	private static final long serialVersionUID = 1L;
	private QuestionnaireConfig questionnaireConfig;
	private Map<String, Question> questionMap = new HashMap<String, Question>();
	private Map<String, QuestionGroup> groupMap = new HashMap<String, QuestionGroup>();
	private Map<String, MultipleQuestionGroup> multipleQuestionGroupMap = new HashMap<String, MultipleQuestionGroup>();
	public QuestionnaireConfigElement() {
		super();
	}
	public QuestionnaireConfigElement(QuestionnaireConfig questionnaireConfig) {
		this.questionnaireConfig = questionnaireConfig;
		buildQuestionMap();
	}
	public QuestionnaireConfig getQuestionnaireConfig() {
		return questionnaireConfig;
	}
	public void setQuestionnaireConfig(QuestionnaireConfig questionnaireConfig) {
		this.questionnaireConfig = questionnaireConfig;
	}
	public Map<String, Question> getQuestionMap() {
		return questionMap;
	}
	public void setQuestionMap(Map<String, Question> questionMap) {
		this.questionMap = questionMap;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Map<String, QuestionGroup> getGroupMap() {
		return groupMap;
	}
	public void setGroupMap(Map<String, QuestionGroup> groupMap) {
		this.groupMap = groupMap;
	}
	private void buildQuestionMap(){
		for(Object item : questionnaireConfig.getItems().getEntryList()){
			if(item instanceof Question){
				Question q = (Question) item;
				questionMap.put(q.getId(), q);
			}else if(item instanceof QuestionGroup){
				QuestionGroup qg  = (QuestionGroup) item;
				processQuestionGroupForMap(qg);
			}else if(item instanceof MultipleQuestionGroup){
				MultipleQuestionGroup mqg  = (MultipleQuestionGroup) item;
				multipleQuestionGroupMap.put(mqg.getId(), mqg);
			}
		}
	}
	
	private void processQuestionGroupForMap(QuestionGroup group){
		groupMap.put(group.getId(), group);
		if(group.getItems() != null && group.getItems().getEntryList() != null && group.getItems().getEntryList().size() > 0){
			for(Object item : group.getItems().getEntryList()){
				if(item instanceof Question){
					Question q = (Question) item;
					questionMap.put(q.getId(), q);
				}else if(item instanceof QuestionGroup){
					QuestionGroup qg  = (QuestionGroup) item;
					processQuestionGroupForMap(qg);
				}else if(item instanceof MultipleQuestionGroup){
					MultipleQuestionGroup mqg  = (MultipleQuestionGroup) item;
					multipleQuestionGroupMap.put(mqg.getId(), mqg);
				}
			}
		}
	}
	public Map<String, MultipleQuestionGroup> getMultipleQuestionGroupMap() {
		return multipleQuestionGroupMap;
	}
	public void setMultipleQuestionGroupMap(
			Map<String, MultipleQuestionGroup> multipleQuestionGroupMap) {
		this.multipleQuestionGroupMap = multipleQuestionGroupMap;
	}
	
}
