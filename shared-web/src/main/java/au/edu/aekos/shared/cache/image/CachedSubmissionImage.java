package au.edu.aekos.shared.cache.image;

import java.io.Serializable;
import java.util.Arrays;

public class CachedSubmissionImage implements Serializable{

	private static final long serialVersionUID = 1L;
	private byte[] imageBytes;
	private String imageTypeString;
	
	public CachedSubmissionImage(byte[] imageBytes, String imageTypeString) {
		super();
		this.imageBytes = imageBytes;
		this.imageTypeString = imageTypeString;
	}
	
	public CachedSubmissionImage() {
		super();
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}
	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}
	public String getImageTypeString() {
		return imageTypeString;
	}
	public void setImageTypeString(String imageTypeString) {
		this.imageTypeString = imageTypeString;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(imageBytes);
		result = prime * result
				+ ((imageTypeString == null) ? 0 : imageTypeString.hashCode());
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
		CachedSubmissionImage other = (CachedSubmissionImage) obj;
		if (!Arrays.equals(imageBytes, other.imageBytes))
			return false;
		if (imageTypeString == null) {
			if (other.imageTypeString != null)
				return false;
		} else if (!imageTypeString.equals(other.imageTypeString))
			return false;
		return true;
	}
	
}
