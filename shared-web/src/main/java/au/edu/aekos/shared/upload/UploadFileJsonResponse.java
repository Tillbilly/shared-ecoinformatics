package au.edu.aekos.shared.upload;

import java.io.File;

import au.edu.aekos.shared.web.model.SubmissionDataFileType;
import au.edu.aekos.shared.web.util.SharedFileUtils;

import com.google.gson.Gson;

public class UploadFileJsonResponse {

	private final String storedFilename;
	private final String origFilename;
	private final Long size;
	private final String humanReadableSize;
	private final String fileType;
	private final String fileTypeTitle;
	private final String description;
	private final boolean success;
	private final String failureMessage;
	private final String fileFormatVersion;
	private final String fileFormat;
	private final String fileFormatTitle;

	public UploadFileJsonResponse(String uploadedFileName, File storedFile, String fileType, String description, String fileFormatVersion, String fileFormat, String fileFormatTitle){
		this.origFilename = uploadedFileName;
		this.storedFilename = storedFile.getName();
		this.size = storedFile.length();
		this.humanReadableSize = SharedFileUtils.humanReadableByteCount(this.size, true);
		this.description = description;
		this.fileType = fileType;
		this.fileTypeTitle = SubmissionDataFileType.getTitleForCode(fileType);
		this.fileFormatVersion = fileFormatVersion;
		this.fileFormat = fileFormat;
		this.fileFormatTitle = fileFormatTitle;
		this.success = true;
		this.failureMessage = null;
	}

	public UploadFileJsonResponse(String failureMessage) {
		this.failureMessage = failureMessage;
		this.origFilename = null;
		this.storedFilename = null;
		this.size = null;
		this.humanReadableSize = null;
		this.description = null;
		this.fileType = null;
		this.fileTypeTitle = null;
		this.fileFormatVersion = null;
		this.fileFormat = null;
		this.fileFormatTitle = null;
		this.success = false;
	}

	public String getStoredFilename() {
		return storedFilename;
	}
	public String getOrigFilename() {
		return origFilename;
	}
	public Long getSize() {
		return size;
	}
	public String getDescription() {
		return description;
	}
	public String getFileType() {
		return fileType;
	}
	public boolean isSuccess() {
		return success;
	}
	public String getFailureMessage() {
		return failureMessage;
	}
	public String getFileTypeTitle() {
		return fileTypeTitle;
	}
	public String getHumanReadableSize() {
		return humanReadableSize;
	}
	public String getFileFormatVersion() {
		return fileFormatVersion;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public String getFileFormatTitle() {
		return fileFormatTitle;
	}

	public String getJsonString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((failureMessage == null) ? 0 : failureMessage.hashCode());
		result = prime * result + ((fileFormat == null) ? 0 : fileFormat.hashCode());
		result = prime * result + ((fileFormatTitle == null) ? 0 : fileFormatTitle.hashCode());
		result = prime * result + ((fileFormatVersion == null) ? 0 : fileFormatVersion.hashCode());
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		result = prime * result + ((fileTypeTitle == null) ? 0 : fileTypeTitle.hashCode());
		result = prime * result + ((humanReadableSize == null) ? 0 : humanReadableSize.hashCode());
		result = prime * result + ((origFilename == null) ? 0 : origFilename.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((storedFilename == null) ? 0 : storedFilename.hashCode());
		result = prime * result + (success ? 1231 : 1237);
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
		UploadFileJsonResponse other = (UploadFileJsonResponse) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (failureMessage == null) {
			if (other.failureMessage != null)
				return false;
		} else if (!failureMessage.equals(other.failureMessage))
			return false;
		if (fileFormat == null) {
			if (other.fileFormat != null)
				return false;
		} else if (!fileFormat.equals(other.fileFormat))
			return false;
		if (fileFormatTitle == null) {
			if (other.fileFormatTitle != null)
				return false;
		} else if (!fileFormatTitle.equals(other.fileFormatTitle))
			return false;
		if (fileFormatVersion == null) {
			if (other.fileFormatVersion != null)
				return false;
		} else if (!fileFormatVersion.equals(other.fileFormatVersion))
			return false;
		if (fileType == null) {
			if (other.fileType != null)
				return false;
		} else if (!fileType.equals(other.fileType))
			return false;
		if (fileTypeTitle == null) {
			if (other.fileTypeTitle != null)
				return false;
		} else if (!fileTypeTitle.equals(other.fileTypeTitle))
			return false;
		if (humanReadableSize == null) {
			if (other.humanReadableSize != null)
				return false;
		} else if (!humanReadableSize.equals(other.humanReadableSize))
			return false;
		if (origFilename == null) {
			if (other.origFilename != null)
				return false;
		} else if (!origFilename.equals(other.origFilename))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (storedFilename == null) {
			if (other.storedFilename != null)
				return false;
		} else if (!storedFilename.equals(other.storedFilename))
			return false;
		if (success != other.success)
			return false;
		return true;
	}
}
