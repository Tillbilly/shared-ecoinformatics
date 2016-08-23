package au.edu.aekos.shared.cache.image;

import java.io.Serializable;

public class ImageCacheKey implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long submissionId;
	private String questionId;
	private String imageName;
	
	public ImageCacheKey(Long submissionId, String questionId, String imageName) {
		super();
		this.submissionId = submissionId;
		this.questionId = questionId;
		this.imageName = imageName;
	}
	
	public ImageCacheKey() {
		super();
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
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result
				+ ((questionId == null) ? 0 : questionId.hashCode());
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
		ImageCacheKey other = (ImageCacheKey) obj;
		if (imageName == null) {
			if (other.imageName != null)
				return false;
		} else if (!imageName.equals(other.imageName))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (submissionId == null) {
			if (other.submissionId != null)
				return false;
		} else if (!submissionId.equals(other.submissionId))
			return false;
		return true;
	}
}
