package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.TraitValue;

public class VocabAnalysisModel {
    private Long submissionId;
    private String questionId;
    private String parentQuestionId;
	private String vocabName;
	private ResponseType responseType;
	private List<AnalysisResponseModel> responseList = new ArrayList<AnalysisResponseModel>(); 
    private Map<String, TraitValue> newVocabEntryMap = new HashMap<String, TraitValue>();
	private String groupQuestionId;
	private Integer groupIndex;
    
	public VocabAnalysisModel() {
		super();
		vocabName = null;
		responseType = null;
		responseList = new ArrayList<AnalysisResponseModel>();
	}

	public String getVocabName() {
		return vocabName;
	}

	public void setVocabName(String vocabName) {
		this.vocabName = vocabName;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public List<AnalysisResponseModel> getResponseList() {
		return responseList;
	}

	public void setResponseList(List<AnalysisResponseModel> responseList) {
		this.responseList = responseList;
	}
	
	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Map<String, TraitValue> getNewVocabEntryMap() {
		return newVocabEntryMap;
	}

	public void setNewVocabEntryMap(Map<String, TraitValue> newVocabEntryMap) {
		this.newVocabEntryMap = newVocabEntryMap;
	}

	public String getParentQuestionId() {
		return parentQuestionId;
	}

	public void setParentQuestionId(String parentQuestionId) {
		this.parentQuestionId = parentQuestionId;
	}

	public String getGroupQuestionId() {
		return groupQuestionId;
	}

	public void setGroupQuestionId(String groupQuestionId) {
		this.groupQuestionId = groupQuestionId;
	}

	public Integer getGroupIndex() {
		return groupIndex;
	}

	public void setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
	}
	
}
