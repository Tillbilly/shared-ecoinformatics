package au.edu.aekos.shared.questionnaire;

import java.util.Date;

public class ImageAnswer {
	
	private Long answerId;
    private String questionId;
    private Integer multiIndex;
	private Date imageDate;
	private String imageDescription;
	private String imageFileName;
	private String imageObjectName;
	private String imageThumbnailName;
	
	public ImageAnswer() {
		super();
	}
	public ImageAnswer(String questionId, String imageDescription,
			String imageFileName, String imageObjectName,
			String imageThumbnailName) {
		super();
		this.questionId = questionId;
		this.imageDescription = imageDescription;
		this.imageFileName = imageFileName;
		this.imageObjectName = imageObjectName;
		this.imageThumbnailName = imageThumbnailName;
	}
	public ImageAnswer(String questionId, Integer multiIndex, String imageDescription,
			String imageFileName, String imageObjectName,
			String imageThumbnailName) {
		super();
		this.questionId = questionId;
		this.imageDescription = imageDescription;
		this.imageFileName = imageFileName;
		this.imageObjectName = imageObjectName;
		this.imageThumbnailName = imageThumbnailName;
		this.multiIndex = multiIndex;
	}
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public Date getImageDate() {
		return imageDate;
	}
	public void setImageDate(Date imageDate) {
		this.imageDate = imageDate;
	}
	public String getImageDescription() {
		return imageDescription;
	}
	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	public String getImageObjectName() {
		return imageObjectName;
	}
	public void setImageObjectName(String imageObjectName) {
		this.imageObjectName = imageObjectName;
	}
	public String getImageThumbnailName() {
		return imageThumbnailName;
	}
	public void setImageThumbnailName(String imageThumbnailName) {
		this.imageThumbnailName = imageThumbnailName;
	}
	public Long getAnswerId() {
		return answerId;
	}
	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}
	public Integer getMultiIndex() {
		return multiIndex;
	}
	public void setMultiIndex(Integer multiIndex) {
		this.multiIndex = multiIndex;
	}
}
