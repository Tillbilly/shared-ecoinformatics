package au.edu.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.DefaultVocabulary;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.web.controllers.QuestionnaireDisplaySupport;
import au.org.aekos.shared.questionnaire.QuestionnaireUtils;

public class QuestionnairePage {

	private String pageTitle;
	private int pageNumber;
    private List<Object> elements = new ArrayList<Object>();
    private PageAnswersModel pageAnswers = new PageAnswersModel();
    private List<String> requiredControlledVocabs = new ArrayList<String>();
    private List<PageCustomVocab> requiredCustomVocabs = new ArrayList<PageCustomVocab>();
    private List<String> requiredTreeSelectVocabs = new ArrayList<String>();
    private Map<String, DefaultVocabulary> defaultVocabularies = new HashMap<String,DefaultVocabulary>();
    
    private Map<String, DisplayCondition> groupDisplayConditions = new HashMap<String, DisplayCondition>();
    private Map<String, DisplayCondition> questionDisplayConditions = new HashMap<String, DisplayCondition>();
    
    private Map<String,List<String>> forwardPopulatePageMap = new HashMap<String,List<String>>();
    
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public List<Object> getElements() {
		return elements;
	}
	public void setElements(List<Object> elements) {
		this.elements = elements;
	}
	public PageAnswersModel getPageAnswers() {
		return pageAnswers;
	}
	public void setPageAnswers(PageAnswersModel pageAnswers) {
		this.pageAnswers = pageAnswers;
	}
	public void initPageAnswers(){
		pageAnswers.setPageNumber(pageNumber);
		for (Object element : elements) {
			if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.Question ){
				addQuestionToPageAnswers( (Question) element );
			}else if( element instanceof au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup ){
				addGroupQuestionsToAnswers((QuestionGroup) element);
			}else if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup ){
				addMultipleQuestionGroupToAnswers( (MultipleQuestionGroup) element );
			}
		}
	}
	
	private void addQuestionToPageAnswers(Question q){
		pageAnswers.getAnswers().put(q.getId(), new Answer(q));
		if(StringUtils.hasLength( q.getTraitName() ) ){
		    registerControlledVocabulary(q);
		}
	}
	
	private void addGroupQuestionsToAnswers(QuestionGroup group){
		if( group.getElements() != null && group.getElements().size() > 0 ){
			for (Object element : group.getElements()) {
				if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.Question ){
					addQuestionToPageAnswers( (Question) element );
				}else if( element instanceof au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup ){
					addGroupQuestionsToAnswers((QuestionGroup) element);
				}else if(element instanceof au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup ){
					addMultipleQuestionGroupToAnswers( (MultipleQuestionGroup) element );
				}
			}
		}
	}
	
	private void addMultipleQuestionGroupToAnswers( MultipleQuestionGroup questionSet ){
		Answer a = new Answer(questionSet);
		pageAnswers.getAnswers().put(a.getQuestionId(), a);
		for(Object obj : questionSet.getElements() ){
			if(obj instanceof au.edu.aekos.shared.questionnaire.jaxb.Question ){
				Question q = (Question) obj;
				if(StringUtils.hasLength(q.getTraitName()) ){
					registerControlledVocabulary(q);
				}
			}
		}
	}
	
	
	private void registerControlledVocabulary(Question q){
		if(q.getResponseType().equals(ResponseType.CONTROLLED_VOCAB)  
				|| q.getResponseType().equals(ResponseType.CONTROLLED_VOCAB_SUGGEST)
				|| q.getResponseType().equals(ResponseType.MULTISELECT_CONTROLLED_VOCAB ) ){
			
			if(q.getIsCustom()){
				requiredCustomVocabs.add( new PageCustomVocab(q.getTraitName(), q.getAlphaSort() ) );
			}else{
			    requiredControlledVocabs.add( q.getTraitName() );
			}
			
			if(q.getDefaultVocabulary() != null ){
				DefaultVocabulary defaultVocab = q.getDefaultVocabulary();
				QuestionnaireUtils.cleanseCharactersFromDefaultVocabulary(defaultVocab);
				defaultVocabularies.put(q.getTraitName(), defaultVocab );
			}
		}else if(q.getResponseType().equals(ResponseType.TREE_SELECT) ){
			requiredTreeSelectVocabs.add(q.getTraitName());
		}
	}
	
	/**
	 * @return List of all question ids contained on this page
	 */
	public List<String> getPageQuestionIdList(){
		List<String> questionIdList = new ArrayList<String>();
		for(Object el : elements){
			if(el instanceof Question){
				Question q = (Question) el;
				questionIdList.add(q.getId());
			}else if(el instanceof QuestionGroup){
				QuestionGroup qg = (QuestionGroup) el;
				questionIdList.addAll(qg.getAllGroupChildQuestionIds());
			}
		}
		return questionIdList;
	}
	
	public List<String> getRequiredControlledVocabs() {
		return requiredControlledVocabs;
	}
	public void setRequiredControlledVocabs(List<String> requiredControlledVocabs) {
		this.requiredControlledVocabs = requiredControlledVocabs;
	}
	public Map<String, DefaultVocabulary> getDefaultVocabularies() {
		return defaultVocabularies;
	}
	public void setDefaultVocabularies(
			Map<String, DefaultVocabulary> defaultVocabularies) {
		this.defaultVocabularies = defaultVocabularies;
	}
	public Map<String, DisplayCondition> getGroupDisplayConditions() {
		return groupDisplayConditions;
	}
	public void setGroupDisplayConditions(
			Map<String, DisplayCondition> groupDisplayConditions) {
		this.groupDisplayConditions = groupDisplayConditions;
	}
	public Map<String, DisplayCondition> getQuestionDisplayConditions() {
		return questionDisplayConditions;
	}
	public void setQuestionDisplayConditions(
			Map<String, DisplayCondition> questionDisplayConditions) {
		this.questionDisplayConditions = questionDisplayConditions;
	}
	
	public Map<String, DisplayCondition> getCurrPageTriggeredGroupDisplayConditionMap(){
		Map<String, DisplayCondition> map = new HashMap<String, DisplayCondition>();
		for(String id : groupDisplayConditions.keySet() ){
			if(groupDisplayConditions.get(id).isOnCurrentPage()){
				map.put(id, groupDisplayConditions.get(id) );
			}
		}
		return map;
	}
	
    public Map<String, DisplayCondition> getCurrPageTriggeredQuestionDisplayConditionMap(){
    	Map<String, DisplayCondition> map = new HashMap<String, DisplayCondition>();
		for(String id : questionDisplayConditions.keySet() ){
			if(questionDisplayConditions.get(id).isOnCurrentPage()){
				map.put(id, questionDisplayConditions.get(id) );
			}
		}
		return map;
	}
	
	public boolean getHasPageTriggerDisplayConditions(){
		Map<String, DisplayCondition> mapGroup = getCurrPageTriggeredGroupDisplayConditionMap();
		Map<String, DisplayCondition> mapQuestion = getCurrPageTriggeredQuestionDisplayConditionMap();
		if(mapGroup.size() + mapQuestion.size() == 0){
			return false;
		}
		return true;
	}
	
	public List<String> getPageTriggerConditionJquery(){
		List<String> jqueryStrings = new ArrayList<String>();
		
		Map<String,DisplayCondition> groupDCTriggerMap = getCurrPageTriggeredGroupDisplayConditionMap();
		if(groupDCTriggerMap.size() > 0){
			//This Map.Entry thing is a findbugs suggestion for efficiency
			for(Map.Entry<String,DisplayCondition> mapEntry : groupDCTriggerMap.entrySet()){
				String jqueryStr = QuestionnaireDisplaySupport.generateJQueryPageTriggerDisplayCondition(mapEntry.getKey(), mapEntry.getValue(), false);
			    if(StringUtils.hasLength(jqueryStr)){
			    	jqueryStrings.add(jqueryStr);
			    }
			}
		}
		
		Map<String,DisplayCondition> questionDCTriggerMap = getCurrPageTriggeredQuestionDisplayConditionMap();
		if(questionDCTriggerMap.size() > 0){
			for( Map.Entry<String, DisplayCondition> mapEntry : questionDCTriggerMap.entrySet() ){
				DisplayCondition dc = mapEntry.getValue();
				String jqueryStr = QuestionnaireDisplaySupport.generateJQueryPageTriggerDisplayCondition(mapEntry.getKey(), dc, true);
			    if(StringUtils.hasLength(jqueryStr)){
			    	jqueryStrings.add(jqueryStr);
			    }
			}
		}
		
		return jqueryStrings;
	}
	
	public boolean getHasPageForwardPopulate(){
		return ( forwardPopulatePageMap.size() > 0 ) ;
	}
	
	public List<String> getPageForwardPopulateJquery(){
		List<String> jqueryStrings = new ArrayList<String>();
		for(Map.Entry<String, List<String>> entry : forwardPopulatePageMap.entrySet()){
			String sourceAnswerId = entry.getKey();
			Answer ans = getPageAnswers().getAnswers().get(sourceAnswerId);
			if(ans != null ){
				for(String targetQid : entry.getValue() ){
					String str = QuestionnaireDisplaySupport.generateJQueryForwardPopulateString(sourceAnswerId, targetQid, ans.getResponseType() );	
				    if(str != null){
				    	jqueryStrings.add(str);
				    }
				}
			}
		}
		return jqueryStrings;
	}
	
	
	public boolean containsQuestionId(String questionId){
		List<String> questionIds = getPageQuestionIdList();
		return questionIds.contains(questionId);
	}
	public List<PageCustomVocab> getRequiredCustomVocabs() {
		return requiredCustomVocabs;
	}
	public void setRequiredCustomVocabs(List<PageCustomVocab> requiredCustomVocabs) {
		this.requiredCustomVocabs = requiredCustomVocabs;
	}
	public Map<String, List<String>> getForwardPopulatePageMap() {
		return forwardPopulatePageMap;
	}
	public void setForwardPopulatePageMap(
			Map<String, List<String>> forwardPopulatePageMap) {
		this.forwardPopulatePageMap = forwardPopulatePageMap;
	}
	public List<String> getRequiredTreeSelectVocabs() {
		return requiredTreeSelectVocabs;
	}
	public void setRequiredTreeSelectVocabs(List<String> requiredTreeSelectVocabs) {
		this.requiredTreeSelectVocabs = requiredTreeSelectVocabs;
	}
	
	public List<Question> getTreeSelectQuestions(){
		List<Question> treeSelectQuestionList = new ArrayList<Question>();
		checkElementsForTreeSelect(getElements(), treeSelectQuestionList);
		return treeSelectQuestionList;
	}
	
	@SuppressWarnings("unchecked")
	private List<Question> getTreeSelectQuestionsForQuestionGroup(QuestionGroup qg){
		List<Question> treeSelectQuestionList = new ArrayList<Question>();
		checkElementsForTreeSelect(qg.getElements(), treeSelectQuestionList);
		return treeSelectQuestionList;
	}
	private void checkElementsForTreeSelect(List<Object> elements, List<Question> treeSelectQuestionList){
		for(Object obj : elements){
			if(obj instanceof Question){
				Question q = (Question) obj;
				if(ResponseType.TREE_SELECT.equals( q.getResponseType()) ){
					treeSelectQuestionList.add(q);
				}
			}else if(obj instanceof QuestionGroup){
				treeSelectQuestionList.addAll(getTreeSelectQuestionsForQuestionGroup((QuestionGroup) obj));
			}
		}
	}
	
    public class PageCustomVocab {
    	public String vocabName;
    	public boolean sortAlpha;
		public PageCustomVocab(String vocabName, boolean sortAlpha) {
			this.vocabName = vocabName;
			this.sortAlpha = sortAlpha;
		}
    }

	@Override
	public String toString() {
		return "QuestionnairePage [pageTitle=" + pageTitle + ", pageNumber=" + pageNumber + "]";
	}
	
    
}
