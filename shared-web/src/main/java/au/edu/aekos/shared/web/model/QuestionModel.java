package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;

public class QuestionModel implements SubmissionItem {
	private Long submissionId;
	private Long submissionAnswerId;
	private String questionId;
	private String questionText;
	private String questionHelpText;
	private String metatag;
	private ResponseType responseType;
	private String responseText;
	private String suggestionText;
	private boolean titleQuestion = false;
	private boolean visible = true;
	private ConditionalDisplay conditionalDisplay;
	private String controlledVocabTrait;
	//TODO id, filename nomenclature a bit messed up - need to standardise.
	//the name is the original file name, id is the generated image file name.
	private String imageObjectId;
	private String imageThumbId;
	private String siteFileCoordSystem;
	private String siteFileCoordSystemDescription;
	private Long siteFileDataId;
	private Boolean canRenderSiteFile = Boolean.FALSE;
	
	private List<MultiselectAnswerModel> multiselectAnswerList = new ArrayList<MultiselectAnswerModel>();
	
	private List<QuestionSetModel> questionSetModelList = new ArrayList<QuestionSetModel>();
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	//TODO is this actually used?
	public ItemType getItemType() {
		if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(responseType)){
			return ItemType.QUESTION_SET;
		}
		return ItemType.QUESTION;
	}
	
	public Long getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	public Long getSubmissionAnswerId() {
		return submissionAnswerId;
	}
	public void setSubmissionAnswerId(Long submissionAnswerId) {
		this.submissionAnswerId = submissionAnswerId;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public ResponseType getResponseType() {
		return responseType;
	}
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public String getImageObjectId() {
		return imageObjectId;
	}
	public void setImageObjectId(String imageObjectId) {
		this.imageObjectId = imageObjectId;
	}
	public String getImageThumbId() {
		return imageThumbId;
	}
	public void setImageThumbId(String imageThumbId) {
		this.imageThumbId = imageThumbId;
	}
	public String getQuestionHelpText() {
		return questionHelpText;
	}
	public void setQuestionHelpText(String questionHelpText) {
		this.questionHelpText = questionHelpText;
	}
	public String getSuggestionText() {
		return suggestionText;
	}
	public void setSuggestionText(String suggestionText) {
		this.suggestionText = suggestionText;
	}
	public boolean isTitleQuestion() {
		return titleQuestion;
	}
	public void setTitleQuestion(boolean titleQuestion) {
		this.titleQuestion = titleQuestion;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public ConditionalDisplay getConditionalDisplay() {
		return conditionalDisplay;
	}
	
	public void setConditionalDisplay(ConditionalDisplay conditionalDisplay) {
		 this.conditionalDisplay = conditionalDisplay;
	}

	public boolean getIsMultiSelect(){
		return ResponseType.getIsMultiselect(this.responseType);
	}
	
	public List<MultiselectAnswerModel> getMultiselectAnswerList() {
		return multiselectAnswerList;
	}

	public void setMultiselectAnswerList(
			List<MultiselectAnswerModel> multiselectAnswerList) {
		this.multiselectAnswerList = multiselectAnswerList;
	}
	
	public boolean getHasResponse(){
		if(ResponseType.getIsMultiselect(responseType)){
			if(multiselectAnswerList == null || multiselectAnswerList.size() == 0 ||
				! StringUtils.hasLength( multiselectAnswerList.get(0).getResponseText() ) ){
				return false;
			}
			return true;
			
		}else if( ResponseType.MULTIPLE_QUESTION_GROUP.equals(responseType) ){
			for(QuestionSetModel qsm : questionSetModelList){
				if(qsm.getHasResponse()){
					return true;
				}
			}
			return false;
		}
		else{
			return StringUtils.hasLength(responseText);
		}
	}

	public String getSiteFileCoordSystem() {
		return siteFileCoordSystem;
	}

	public void setSiteFileCoordSystem(String siteFileCoordSystem) {
		this.siteFileCoordSystem = siteFileCoordSystem;
	}

	public Long getSiteFileDataId() {
		return siteFileDataId;
	}

	public void setSiteFileDataId(Long siteFileDataId) {
		this.siteFileDataId = siteFileDataId;
	}

	public Boolean getCanRenderSiteFile() {
		return canRenderSiteFile;
	}

	public void setCanRenderSiteFile(Boolean canRenderSiteFile) {
		this.canRenderSiteFile = canRenderSiteFile;
	}
	
	public JsonGeoFeatureSet getJsonGeoFeatureSet(){
		if(! ResponseType.GEO_FEATURE_SET.equals(responseType) || ! StringUtils.hasLength( responseText ) ){
		    return null;
		}
		Gson gson = new Gson();
		return gson.fromJson(responseText, JsonGeoFeatureSet.class);
	}

	public String getSiteFileCoordSystemDescription() {
		return siteFileCoordSystemDescription;
	}

	public void setSiteFileCoordSystemDescription(
			String siteFileCoordSystemDescription) {
		this.siteFileCoordSystemDescription = siteFileCoordSystemDescription;
	}

	public String getControlledVocabTrait() {
		return controlledVocabTrait;
	}

	public void setControlledVocabTrait(String controlledVocabTrait) {
		this.controlledVocabTrait = controlledVocabTrait;
	}

	public List<QuestionSetModel> getQuestionSetModelList() {
		return questionSetModelList;
	}

	public void setQuestionSetModelList(List<QuestionSetModel> questionSetModelList) {
		this.questionSetModelList = questionSetModelList;
	}

	public String getMetatag() {
		return metatag;
	}

	public void setMetatag(String metatag) {
		this.metatag = metatag;
	}
	
	public boolean getHasSuggestedVocabEntry(){
		if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(responseType) 
				&& "OTHER".equals(responseText)
				&& StringUtils.hasLength(suggestionText) ){
			return true;
		}else if(metatag != null && metatag.endsWith("Suggest") && getHasResponse()){
			return true;
		}
		return false;
	}
}
