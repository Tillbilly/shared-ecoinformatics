package au.edu.aekos.shared.upload;

import com.google.gson.Gson;


public class UploadImageJsonResponse {
	
    private String message;
    private String errorMessage;
    private String imageId;
    private String imageName;
    private String thumbnailId;
    private Long size;
    private String questionId;
    
	public UploadImageJsonResponse(String message, String imageId,
			String imageName, String thumbnailId, Long size) {
		super();
		this.message = message;
		this.imageId = imageId;
		this.imageName = imageName;
		this.thumbnailId = thumbnailId;
		this.size = size;
	}
	
	public UploadImageJsonResponse(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}
	public UploadImageJsonResponse() {
		super();
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getThumbnailId() {
		return thumbnailId;
	}
	public void setThumbnailId(String thumbnailId) {
		this.thumbnailId = thumbnailId;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}

	public String getJsonString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
}
