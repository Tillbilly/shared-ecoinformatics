package au.edu.aekos.shared.web.model;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class MultiselectAnswerModel {
	private Long id;
	private ResponseType responseType;
	private String responseText;
	private String suggestionText;
	private String imageObjectId;
	private String imageThumbId;
	
	public MultiselectAnswerModel() {
		super();
	}
	public MultiselectAnswerModel(SubmissionAnswer submissionAnswer) {
		this.id = submissionAnswer.getId();
		this.responseText = submissionAnswer.getResponse();
		if(StringUtils.hasLength(submissionAnswer.getDisplayResponse()) ){
			this.responseText = submissionAnswer.getDisplayResponse();
		}
		this.suggestionText = submissionAnswer.getSuggestedResponse();
		this.responseType = submissionAnswer.getResponseType();
		if(ResponseType.IMAGE.equals(submissionAnswer.getResponseType()) && submissionAnswer.getAnswerImage() != null){
            this.imageObjectId = submissionAnswer.getAnswerImage().getImageObjectId();			
			this.imageThumbId = submissionAnswer.getAnswerImage().getImageThumbnailId();
		}
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
	public String getSuggestionText() {
		return suggestionText;
	}
	public void setSuggestionText(String suggestionText) {
		this.suggestionText = suggestionText;
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
	public Long getSubmissionAnswerId() {
		return id;
	}
	public void setSubmissionAnswerId(Long submissionAnswerId) {
		this.id = submissionAnswerId;
	}
	
}
