package au.edu.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.jaxb.PageBreak;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;

/**
 * Keep this in the users session, to keep track of answers, page layout, conditionalDisplay etc etc. 
 * 
 * @author Ben Till
 *
 */
public class DisplayQuestionnaire {

	private static final Logger logger = LoggerFactory.getLogger(DisplayQuestionnaire.class);
	
	public static final String CV_OTHER_VALUE = "OTHER";
	
    private int currentPageNumber = 0;
	
	private List<QuestionnairePage> pages = new ArrayList<QuestionnairePage>();
	
	private List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();
	
	private Long questionnaireConfigEntityId;
	
	//When modifying a submission or post review.
	private Long submissionId = null;
	
	//The ID of the saved 'copy' of the submission - SubmissionData files are duplicated.
	private Long savedSubmissionId = null;
	
	private QuestionnaireConfig config;
	
	private Map<String, DisplayCondition> groupDisplayConditions = new HashMap<String, DisplayCondition>();
    private Map<String, DisplayCondition> questionDisplayConditions  = new HashMap<String, DisplayCondition>();
    
    //Forward prepopulate map, source question ID to prepop question ID
    private Map<String, List<String>> forwardPopulateMap = new HashMap<String, List<String>>();
	
    private Map<String, ImageAnswer> imageAnswerMap = new HashMap<String, ImageAnswer>();

    private Map<String, ImageSeriesAnswer> imageSeriesAnswerMap = new HashMap<String, ImageSeriesAnswer>();
    
    private SubmissionReviewModel lastReview = null;
    
    private Map<String, SubmissionData > questionUploadSubmissionDataMap = new HashMap<String, SubmissionData>();
    
    private Map<String,List<String>> reusableGroupIdOptionListMap = new HashMap<String,List<String>>();
    
    private Map<String,String> selectedReusableGroupMap = new HashMap<String,String>();
    
    private Boolean groupAdminEdit = Boolean.FALSE;
    
	public Map<String, ImageAnswer> getImageAnswerMap() {
		return imageAnswerMap;
	}

	public void setImageAnswerMap(Map<String, ImageAnswer> imageAnswerMap) {
		this.imageAnswerMap = imageAnswerMap;
	}

	public DisplayQuestionnaire() {
		super();
	}

	public DisplayQuestionnaire(QuestionnaireConfig config) {
		super();
		this.config = config;
		this.questionnaireConfigEntityId = config.getSmsQuestionnaireId();
		initialise(config);
	}
	
	public DisplayQuestionnaire(QuestionnaireConfig config, Long questionnaireConfigId) {
		super();
		this.config = config;
		this.questionnaireConfigEntityId = questionnaireConfigId;
		initialise(config);
	}
	/**
	 * Use this constructor for cloning
	 * @param config
	 * @param questionnaireConfigId
	 * @param submissionEntity
	 */
	public DisplayQuestionnaire(QuestionnaireConfig config, Long questionnaireConfigId, Submission submissionEntity) {
		super();
		this.config = config;
		this.questionnaireConfigEntityId = questionnaireConfigId;
		initialise(config);
		repopulateAnswersForClone(submissionEntity, config.getSubmissionTitleQuestionId());
	}
	
	public DisplayQuestionnaire(QuestionnaireConfig config, Submission submissionEntity) {
		super();
		this.config = config;
		this.questionnaireConfigEntityId = config.getSmsQuestionnaireId();
		initialise(config);
		this.submissionId = submissionEntity.getId();
		repopulateAnswers(submissionEntity);
		if(SubmissionStatus.SAVED.equals(submissionEntity.getStatus()) || SubmissionStatus.REJECTED_SAVED.equals(submissionEntity.getStatus() ) ){
			this.submissionId = submissionEntity.getDraftForSubmissionId();
			this.savedSubmissionId = submissionEntity.getId();
		}
	}
	
	public void initialise(QuestionnaireConfig config){
		this.config = config;
	    @SuppressWarnings("rawtypes")
		List elements = config.getItems().getEntryList();
	    QuestionnairePage page = new QuestionnairePage();
	    page.setPageNumber(1);
	    page.setPageTitle(config.getFirstPageTitle());
	    for(int x = 0; x < elements.size(); x++){
	    	Object element = elements.get(x);
	    	if(element instanceof PageBreak){
	    		page = processPageBreak((PageBreak) element, page );
	    	}else{
	    		page.getElements().add(element);
	    		if(element instanceof QuestionGroup){
	    			QuestionGroup qg = (QuestionGroup) element;
	    			if(qg.getReusableGroup() != null && qg.getReusableGroup()){
	    				reusableGroupIdOptionListMap.put(qg.getId(), new ArrayList<String>());
	    			}
	    		}
	    	}
	    }
	    
	    if(page.getElements().size() > 0){
	    	page.initPageAnswers();
		    pages.add(page);
		}
	    setCurrentPageNumber(1);
	    initialiseDisplayConditionMaps();
	    initialiseForwardPopulationMap(config);
	}
	
	private QuestionnairePage processPageBreak(PageBreak pb, QuestionnairePage currentPage){
		if(currentPage.getElements().size() > 0){
			currentPage.initPageAnswers();
		    pages.add(currentPage);
		}
		QuestionnairePage page = new QuestionnairePage();
		page.setPageTitle(pb.getPageTitle());
		page.setPageNumber(pages.size() + 1);
		return page;
	}
	
	private void initialiseDisplayConditionMaps(){
		Map<String, Answer> answerMap = getAllAnswers();
		for(QuestionnairePage page : pages){
			List<String> currentPageQuestions = page.getPageQuestionIdList();
			for(Object obj : page.getElements() ){
				if(obj instanceof Question){
					setQuestionDisplayCondition((Question) obj,answerMap, currentPageQuestions, page );
				}else if(obj instanceof QuestionGroup){
					setQuestionGroupDisplayCondition((QuestionGroup) obj,answerMap, currentPageQuestions, page );
				}
			}
		}
	}
	
	private void initialiseForwardPopulationMap(QuestionnaireConfig config){
		forwardPopulateMap.clear();
		for(Question q : config.getAllQuestions(false) ){
			if(StringUtils.hasLength( q.getPrepopulateQuestionId()) ){
				if(! forwardPopulateMap.containsKey( q.getPrepopulateQuestionId() )){
					forwardPopulateMap.put(q.getPrepopulateQuestionId(), new ArrayList<String>());
				}
				forwardPopulateMap.get(q.getPrepopulateQuestionId()).add(q.getId());
			}
		}
		
		//Now need to add the prepopulate info to the individual pages for jquery generation,
		//removes same page target question Ids
		List<String> keysToRemove = new ArrayList<String>();
		for(QuestionnairePage page : pages){
			page.getForwardPopulatePageMap().clear();
			List<String> pageQuestIds = page.getPageQuestionIdList();
			for(Map.Entry<String, List<String>> entry : forwardPopulateMap.entrySet()){
				if(pageQuestIds.contains(entry.getKey())){
					List<String> popQList = entry.getValue();
					Iterator<String> iter = popQList.iterator();
					while(iter.hasNext()){
						String tq = iter.next();
						if(pageQuestIds.contains( tq ) ){
							if(! page.getForwardPopulatePageMap().containsKey( entry.getKey() )){
								page.getForwardPopulatePageMap().put(entry.getKey(), new ArrayList<String>());
							}
							page.getForwardPopulatePageMap().get(entry.getKey()).add(tq);
							iter.remove();
						}
					}
					if(popQList.size() == 0){
						keysToRemove.add(entry.getKey());
					}
				}
			}
		}
		for(String qIdToRemove : keysToRemove){
			forwardPopulateMap.remove(qIdToRemove);
		}
	}
	
    private void setQuestionDisplayCondition(Question q, Map<String, Answer> answerMap, List<String> currentPageQuestionIdList, QuestionnairePage page ){
    	if(q.getDisplayCondition() != null && StringUtils.hasLength( q.getDisplayCondition().getQuestionId() ) ){
    		DisplayCondition displayCondition = new DisplayCondition();
    		displayCondition.setOwnerId(q.getId());
    		if(! answerMap.containsKey( q.getDisplayCondition().getQuestionId() ) ){
    			displayCondition.setShow(false);
    		}else{
	    		Answer conditionalAnswer = answerMap.get(q.getDisplayCondition().getQuestionId());
	    		displayCondition.setConditionalAnswer( conditionalAnswer );
	    		displayCondition.setConditionalDisplay(q.getDisplayCondition());
	    		if(currentPageQuestionIdList.contains( q.getDisplayCondition().getQuestionId()) ){
	    			displayCondition.setOnCurrentPage(true);
	    		}
    		}
    		//Check if the conditional question also has display conditions
    		if( questionDisplayConditions.containsKey( displayCondition.getQuestionId() ) ){
    			DisplayCondition depDC = questionDisplayConditions.get(displayCondition.getQuestionId());
    			displayCondition.setParentDisplayCondition(depDC);
    			depDC.getChildDisplayConditionList().add(displayCondition);
    		}
    		
    		questionDisplayConditions.put(q.getId(), displayCondition);
    		page.getQuestionDisplayConditions().put(q.getId(), displayCondition);
    	}
    }
    
    private void setQuestionGroupDisplayCondition(QuestionGroup group, Map<String, Answer> answerMap, List<String> currentPageQuestionIdList,  QuestionnairePage page ){
    	if(group.getDisplayCondition() != null && StringUtils.hasLength( group.getDisplayCondition().getQuestionId() ) ){
    		DisplayCondition displayCondition = new DisplayCondition();
    		if(! answerMap.containsKey( group.getDisplayCondition().getQuestionId() ) ){
    			displayCondition.setShow(false);
    		}else{
	    		Answer conditionalAnswer = answerMap.get(group.getDisplayCondition().getQuestionId());
	    		displayCondition.setConditionalAnswer( conditionalAnswer );
	    		displayCondition.setConditionalDisplay(group.getDisplayCondition());
	    		if(currentPageQuestionIdList.contains( group.getDisplayCondition().getQuestionId()) ){
	    			displayCondition.setOnCurrentPage(true);
	    		}
    		}
    		groupDisplayConditions.put(group.getId(), displayCondition);
    		page.getGroupDisplayConditions().put(group.getId(), displayCondition);
    	}
    	
    	//Now iterate thru the group elements
    	for(Object obj : group.getElements()){
    		if(obj instanceof Question){
				setQuestionDisplayCondition((Question) obj,answerMap, currentPageQuestionIdList, page );
			}else if(obj instanceof QuestionGroup){
				setQuestionGroupDisplayCondition((QuestionGroup) obj,answerMap, currentPageQuestionIdList, page );
			}
    	}
    }
	
    private Answer findAnswerByQuestionIdHelper(String string) {
		for(QuestionnairePage page : pages){
			if(page.getPageAnswers() == null || page.getPageAnswers().getAnswers() == null) {
				continue;
			}
			for(String questionId: page.getPageAnswers().getAnswers().keySet()){
				if (questionId.equalsIgnoreCase(string)) {
					return page.getPageAnswers().getAnswers().get(questionId);
				}
			}
		}
		return null;
	}
    
	public Map<String, Answer> getAllAnswers(){
		Map<String, Answer> allAnswerMap = new HashMap<String, Answer> ();
		for(QuestionnairePage page : pages){
			if(page.getPageAnswers() != null && page.getPageAnswers().getAnswers() != null ){
				for(String questionId: page.getPageAnswers().getAnswers().keySet()){
					allAnswerMap.put(questionId, page.getPageAnswers().getAnswers().get(questionId));
				}
			}
		}
		return allAnswerMap;
	}

	public boolean isTitleQuestionAnswered() {
		String titleQID = config.getSubmissionTitleQuestionId();
		Answer titleQuestionAnswer = findAnswerByQuestionIdHelper(titleQID);
		return titleQuestionAnswer.hasResponse();
	}

	public Answer findAnswerByQuestionId(String string) {
		return findAnswerByQuestionIdHelper(string);
	}

	private void repopulateAnswers(Submission submissionEntity){
		Map<String, Answer> answerMap = getAllAnswers();
		for(SubmissionAnswer subAnswer : submissionEntity.getAnswers() ){
			if(StringUtils.hasLength( subAnswer.getQuestionId() ) &&
					answerMap.containsKey(subAnswer.getQuestionId()) ){
				Answer displayAnswer = answerMap.get(subAnswer.getQuestionId() );
				displayAnswer.setResponse(subAnswer.getResponse());
				displayAnswer.setSuggestedResponse(subAnswer.getSuggestedResponse());
				if( displayAnswer.getIsMultiSelect() &&  subAnswer.hasResponse() ){
					displayAnswer.getMultiselectAnswerList().clear();
					for(SubmissionAnswer multiSa : subAnswer.getMultiselectAnswerList()){
						Answer msDisplayAnswer = new Answer();
						msDisplayAnswer.setQuestionId(multiSa.getQuestionId());
						msDisplayAnswer.setResponseType(multiSa.getResponseType());
						msDisplayAnswer.setResponse(multiSa.getResponse());
						msDisplayAnswer.setSuggestedResponse(multiSa.getSuggestedResponse());
						displayAnswer.getMultiselectAnswerList().add(msDisplayAnswer);
					}
				}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals( displayAnswer.getResponseType() ) 
						&& subAnswer.getQuestionSetList() != null && subAnswer.getQuestionSetList().size() > 0 ){
					displayAnswer.getAnswerSetList().clear();
					for(QuestionSetEntity questionSet : subAnswer.getQuestionSetList() ){
						Map<String, Answer> answerSet = new HashMap<String,Answer>();
						for(Map.Entry<String, SubmissionAnswer> entry : questionSet.getAnswerMap().entrySet() ){
							Answer setAnswer = new Answer();
							setAnswer.setQuestionId(entry.getKey());
							setAnswer.setResponse(entry.getValue().getResponse());
							setAnswer.setSuggestedResponse(entry.getValue().getSuggestedResponse());
							setAnswer.setResponseType(entry.getValue().getResponseType());
							setAnswer.setDisplayResponse(entry.getValue().getDisplayResponse());
							answerSet.put(entry.getKey(), setAnswer);
						}
						displayAnswer.getAnswerSetList().add(answerSet);
					}
				}
				
			} else{
				logger.error("Submission " + submissionEntity.getId() + " answer " 
			                   + subAnswer.getId() +" does not correspond with config question " 
						       + subAnswer.getQuestionId());
			}
		}
	}
	
	private void repopulateAnswersForClone(Submission submissionEntity, String titleQuestionId){
		Map<String, Answer> answerMap = getAllAnswers();
		for(SubmissionAnswer subAnswer : submissionEntity.getAnswers() ){
			if(StringUtils.hasLength( subAnswer.getQuestionId() ) &&
					answerMap.containsKey(subAnswer.getQuestionId()) && 
					! titleQuestionId.equals(subAnswer.getQuestionId())  &&
					! ResponseType.SITE_FILE.equals(subAnswer.getResponseType() ) &&
					! ResponseType.DOCUMENT.equals(subAnswer.getResponseType() ) &&
					! ResponseType.IMAGE.equals(subAnswer.getResponseType() ) &&
					! ResponseType.MULTISELECT_IMAGE.equals(subAnswer.getResponseType() ) &&
					! ResponseType.LICENSE_CONDITIONS.equals(subAnswer.getResponseType() ) &&
					! ResponseType.MULTIPLE_DOCUMENT.equals(subAnswer.getResponseType() ) &&
					! ResponseType.SPECIES_LIST.equals(subAnswer.getResponseType() ) &&
					! ResponseType.SPECIES_LIST.equals(subAnswer.getResponseType() ) ){
				
				Answer displayAnswer = answerMap.get(subAnswer.getQuestionId() );
				displayAnswer.setResponse(subAnswer.getResponse());
				displayAnswer.setSuggestedResponse(subAnswer.getSuggestedResponse());
				if( displayAnswer.getIsMultiSelect() &&  subAnswer.hasResponse() ){
					displayAnswer.getMultiselectAnswerList().clear();
					for(SubmissionAnswer multiSa : subAnswer.getMultiselectAnswerList()){
						Answer msDisplayAnswer = new Answer();
						msDisplayAnswer.setQuestionId(multiSa.getQuestionId());
						msDisplayAnswer.setResponseType(multiSa.getResponseType());
						msDisplayAnswer.setResponse(multiSa.getResponse());
						msDisplayAnswer.setSuggestedResponse(multiSa.getSuggestedResponse());
						displayAnswer.getMultiselectAnswerList().add(msDisplayAnswer);
					}
				}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals( displayAnswer.getResponseType() ) 
						&& subAnswer.getQuestionSetList() != null && subAnswer.getQuestionSetList().size() > 0 ){
					displayAnswer.getAnswerSetList().clear();
					for(QuestionSetEntity questionSet : subAnswer.getQuestionSetList() ){
						Map<String, Answer> answerSet = new HashMap<String,Answer>();
						for(Map.Entry<String, SubmissionAnswer> entry : questionSet.getAnswerMap().entrySet() ){
							Answer setAnswer = new Answer();
							setAnswer.setQuestionId(entry.getKey());
							setAnswer.setResponse(entry.getValue().getResponse());
							setAnswer.setSuggestedResponse(entry.getValue().getSuggestedResponse());
							setAnswer.setResponseType(entry.getValue().getResponseType());
							setAnswer.setDisplayResponse(entry.getValue().getDisplayResponse());
							answerSet.put(entry.getKey(), setAnswer);
						}
						displayAnswer.getAnswerSetList().add(answerSet);
					}
				}
				
			} else{
				logger.error("Submission " + submissionEntity.getId() + " answer " 
			                   + subAnswer.getId() +" does not correspond with config question " 
						       + subAnswer.getQuestionId());
			}
		}
	}
	
	
	
	public QuestionnaireConfig getConfig() {
		return config;
	}

	public void setConfig(QuestionnaireConfig config) {
		this.config = config;
	}

	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public List<QuestionnairePage> getPages() {
		return pages;
	}

	public void setPages(List<QuestionnairePage> pages) {
		this.pages = pages;
	}
	
	
	public String getQuestionnaireVersion(){
		return config.getVersion();
	}
	
	public String getTitle(){
		return config.getTitle();
	}
	
	public String getSubtitle(){
		return config.getSubtitle();
	}
	
	public String getIntroduction(){
		return config.getIntroduction();
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

	public List<SubmissionData> getSubmissionDataList() {
		return submissionDataList;
	}

	public void setSubmissionDataList(List<SubmissionData> submissionDataList) {
		this.submissionDataList = submissionDataList;
	}

	public Long getQuestionnaireConfigEntityId() {
		return questionnaireConfigEntityId;
	}

	public void setQuestionnaireConfigEntityId(Long questionnaireConfigEntityId) {
		this.questionnaireConfigEntityId = questionnaireConfigEntityId;
	}

	public String getSubmissionTitle(){
		String titleQuestion = config.getSubmissionTitleQuestionId();
		if( ! StringUtils.hasLength(titleQuestion)){
			return null;
		}
		
		Map<String,Answer> answers = getAllAnswers();
		if(answers.containsKey(titleQuestion)){
			Answer a = answers.get(titleQuestion);
			return a.getResponse();
		}
		return null;
	}
	
	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	
	public Map<String, Question> getAllQuestionsMapFromConfig(){
		Map<String, Question> allQuestionMap = new HashMap<String,Question>();
		for(Object obj : config.getItems().getEntryList() ){
			if(obj instanceof Question){
				Question q = (Question) obj;
				allQuestionMap.put(q.getId(), q);
			}else if(obj instanceof QuestionGroup){
				QuestionGroup qg = (QuestionGroup) obj;
				writeQuestionGroupQuestionsToMap(qg, allQuestionMap);
			}
		}
		return allQuestionMap;
	}
	
	private void writeQuestionGroupQuestionsToMap(QuestionGroup qg, Map<String,Question> questionMap){
		for(Object el : qg.getElements()){
			if(el instanceof Question){
				Question q = (Question) el;
				questionMap.put(q.getId(), q);
			}else if(el instanceof QuestionGroup){
				QuestionGroup innerQG = (QuestionGroup) el;
				writeQuestionGroupQuestionsToMap(innerQG, questionMap);
			}
		}
	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((config == null) ? 0 : config.hashCode());
		result = prime * result + currentPageNumber;
		result = prime
				* result
				+ ((groupDisplayConditions == null) ? 0
						: groupDisplayConditions.hashCode());
		result = prime * result
				+ ((imageAnswerMap == null) ? 0 : imageAnswerMap.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		result = prime
				* result
				+ ((questionDisplayConditions == null) ? 0
						: questionDisplayConditions.hashCode());
		result = prime
				* result
				+ ((questionnaireConfigEntityId == null) ? 0
						: questionnaireConfigEntityId.hashCode());
		result = prime
				* result
				+ ((submissionDataList == null) ? 0 : submissionDataList
						.hashCode());
		result = prime * result
				+ ((submissionId == null) ? 0 : submissionId.hashCode());
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
		DisplayQuestionnaire other = (DisplayQuestionnaire) obj;
		if (config == null) {
			if (other.config != null)
				return false;
		} else if (!config.equals(other.config))
			return false;
		if (currentPageNumber != other.currentPageNumber)
			return false;
		if (groupDisplayConditions == null) {
			if (other.groupDisplayConditions != null)
				return false;
		} else if (!groupDisplayConditions.equals(other.groupDisplayConditions))
			return false;
		if (imageAnswerMap == null) {
			if (other.imageAnswerMap != null)
				return false;
		} else if (!imageAnswerMap.equals(other.imageAnswerMap))
			return false;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		if (questionDisplayConditions == null) {
			if (other.questionDisplayConditions != null)
				return false;
		} else if (!questionDisplayConditions
				.equals(other.questionDisplayConditions))
			return false;
		if (questionnaireConfigEntityId == null) {
			if (other.questionnaireConfigEntityId != null)
				return false;
		} else if (!questionnaireConfigEntityId
				.equals(other.questionnaireConfigEntityId))
			return false;
		if (submissionDataList == null) {
			if (other.submissionDataList != null)
				return false;
		} else if (!submissionDataList.equals(other.submissionDataList))
			return false;
		if (submissionId == null) {
			if (other.submissionId != null)
				return false;
		} else if (!submissionId.equals(other.submissionId))
			return false;
		return true;
	}

	public SubmissionReviewModel getLastReview() {
		return lastReview;
	}

	public void setLastReview(SubmissionReviewModel lastReview) {
		this.lastReview = lastReview;
	}

	private String getSiteFileQuestionId(){
		Map<String, Question > allQuestionMap = getAllQuestionsMapFromConfig();
		for(Map.Entry<String,Question> entry : allQuestionMap.entrySet() ){
			if(ResponseType.SITE_FILE.equals( entry.getValue().getResponseType() ) ){
				return entry.getKey();
			}
		}
		return null;
	}
	
	
	public SubmissionData getSiteFile() {
		String siteFileQuestionId = getSiteFileQuestionId();
		if(StringUtils.hasLength(siteFileQuestionId)){
			return questionUploadSubmissionDataMap.get(siteFileQuestionId);
		}
		return null;
	}

	public void setSiteFile(SubmissionData siteFile) {
		String siteFileQuestionId = getSiteFileQuestionId();
		if(StringUtils.hasLength(siteFileQuestionId)){
		    questionUploadSubmissionDataMap.put(siteFileQuestionId, siteFile);
		}else{
			logger.error("No site file specified in the questionnaire, can't assign site file");
		}
	}

	public String getSiteFileCoordinateSystem() {
		if(getSiteFile() == null){
			return null;
		}
		return getSiteFile().getSiteFileCoordinateSystem();
	}

	public void setSiteFileCoordinateSystem(String siteFileCoordinateSystem) {
		if(getSiteFile() == null){
			return;
		}
		this.getSiteFile().setSiteFileCoordinateSystem( siteFileCoordinateSystem );
	}

	public String getSiteFileCoordSysOther() {
		if(getSiteFile() == null){
			return null;
		}
		return getSiteFile().getSiteFileCoordSysOther();
	}

	public void setSiteFileCoordSysOther(String siteFileCoordSysOther) {
		if(getSiteFile() == null){
			return;
		}
		getSiteFile().setSiteFileCoordSysOther(siteFileCoordSysOther);
	}
	
	public String getTitleQuestionId(){
		if(this.config != null ){
			return config.getSubmissionTitleQuestionId();
		}
		return "";
	}

	public Map<String, List<String>> getReusableGroupIdOptionListMap() {
		return reusableGroupIdOptionListMap;
	}

	public void setReusableGroupIdOptionListMap(
			Map<String, List<String>> reusableGroupIdOptionListMap) {
		this.reusableGroupIdOptionListMap = reusableGroupIdOptionListMap;
	}

	public Map<String, String> getSelectedReusableGroupMap() {
		return selectedReusableGroupMap;
	}

	public void setSelectedReusableGroupMap(
			Map<String, String> selectedReusableGroupMap) {
		this.selectedReusableGroupMap = selectedReusableGroupMap;
	}

	public Map<String, ImageSeriesAnswer> getImageSeriesAnswerMap() {
		return imageSeriesAnswerMap;
	}

	public void setImageSeriesAnswerMap(
			Map<String, ImageSeriesAnswer> imageSeriesAnswerMap) {
		this.imageSeriesAnswerMap = imageSeriesAnswerMap;
	}

	public void setForwardPopulateMap(Map<String, List<String>> forwardPopulateMap) {
		this.forwardPopulateMap = forwardPopulateMap;
	}

	public Map<String, List<String>> getForwardPopulateMap() {
		return forwardPopulateMap;
	}

	public Map<String, SubmissionData> getQuestionUploadSubmissionDataMap() {
		return questionUploadSubmissionDataMap;
	}

	public void setQuestionUploadSubmissionDataMap(
			Map<String, SubmissionData> questionUploadSubmissionDataMap) {
		this.questionUploadSubmissionDataMap = questionUploadSubmissionDataMap;
	}

	/**
	 * @param pageNumber		The <b>number</b> (not index, so 1-based) of the page to find the title for
	 * @return					The page title if the page exists, null otherwise
	 */
	public String getPageTitleByPageNumber(int pageNumber) {
		final int pageIndex = pageNumber -1;
		try {
			return pages.get(pageIndex).getPageTitle();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public int getNumberOfPages() {
		return pages.size();
	}

	public Long getSavedSubmissionId() {
		return savedSubmissionId;
	}

	public void setSavedSubmissionId(Long savedSubmissionId) {
		this.savedSubmissionId = savedSubmissionId;
	}

	public boolean hasSubmissionData() {
		return submissionDataList != null && submissionDataList.size() > 0;
	}

	public Boolean getGroupAdminEdit() {
		return groupAdminEdit;
	}

	public void setGroupAdminEdit(Boolean groupAdminEdit) {
		this.groupAdminEdit = groupAdminEdit;
	}
}
