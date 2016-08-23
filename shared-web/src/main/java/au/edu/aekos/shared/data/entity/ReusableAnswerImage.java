package au.edu.aekos.shared.data.entity;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "RU_ANSWER_IMAGE")
public class ReusableAnswerImage {

	@Id
	@GeneratedValue
	private Long id;
	
    @Column
    private String imageName;
	
	@Column
	private String description;
	
	@Column
	private Date date;
	
	@Column 
	private String imageType;
	
	@Column
	private String imageObjectId;
	
	@Column 
	private byte[] image;
	
	@Column
	private String imageThumbnailId;
	
	@Column
	private byte[] imageThumbnail;
	
	@OneToOne(mappedBy="answerImage")
	private ReusableAnswer answer;
	
	public ReusableAnswerImage() {
		super();
	}

	public ReusableAnswerImage(AnswerImage answerImage, ReusableAnswer reusableAnswer) {
		this.imageName = answerImage.getImageName();
		this.description = answerImage.getDescription();
		this.date = answerImage.getDate();
		this.imageType = answerImage.getImageType();
		this.imageObjectId = answerImage.getImageObjectId();
		if(answerImage.getImage() != null){
		    this.image = answerImage.getImage().clone();
		}
		this.imageThumbnailId = answerImage.getImageThumbnailId();
		if(answerImage.getImageThumbnail() != null ){
			this.imageThumbnail = answerImage.getImageThumbnail().clone();
		}
		this.answer = reusableAnswer;
	}

	public String getImageObjectId() {
		return imageObjectId;
	}

	public void setImageObjectId(String imageObjectId) {
		this.imageObjectId = imageObjectId;
	}

	public String getImageThumbnailId() {
		return imageThumbnailId;
	}

	public void setImageThumbnailId(String imageThumbnailId) {
		this.imageThumbnailId = imageThumbnailId;
	}

	public boolean containsImageId(String imageId){
		if(imageObjectId != null && imageObjectId.equals(imageId)){
			return true;
		}
		if(imageThumbnailId != null && imageThumbnailId.equals(imageId)){
			return true;
		}
		return false;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ReusableAnswer getAnswer() {
		return answer;
	}

	public void setAnswer(ReusableAnswer answer) {
		this.answer = answer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public byte[] getImageThumbnail() {
		return imageThumbnail;
	}

	public void setImageThumbnail(byte[] imageThumbnail) {
		this.imageThumbnail = imageThumbnail;
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
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.hashCode(image);
		result = prime * result
				+ ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result
				+ ((imageObjectId == null) ? 0 : imageObjectId.hashCode());
		result = prime * result + Arrays.hashCode(imageThumbnail);
		result = prime
				* result
				+ ((imageThumbnailId == null) ? 0 : imageThumbnailId.hashCode());
		result = prime * result
				+ ((imageType == null) ? 0 : imageType.hashCode());
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
		ReusableAnswerImage other = (ReusableAnswerImage) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (!Arrays.equals(image, other.image))
			return false;
		if (imageName == null) {
			if (other.imageName != null)
				return false;
		} else if (!imageName.equals(other.imageName))
			return false;
		if (imageObjectId == null) {
			if (other.imageObjectId != null)
				return false;
		} else if (!imageObjectId.equals(other.imageObjectId))
			return false;
		if (!Arrays.equals(imageThumbnail, other.imageThumbnail))
			return false;
		if (imageThumbnailId == null) {
			if (other.imageThumbnailId != null)
				return false;
		} else if (!imageThumbnailId.equals(other.imageThumbnailId))
			return false;
		if (imageType == null) {
			if (other.imageType != null)
				return false;
		} else if (!imageType.equals(other.imageType))
			return false;
		return true;
	}
}
