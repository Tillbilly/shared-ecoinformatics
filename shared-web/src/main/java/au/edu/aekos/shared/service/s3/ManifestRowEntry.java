package au.edu.aekos.shared.service.s3;

import au.edu.aekos.shared.data.entity.SubmissionDataType;

public class ManifestRowEntry {
	Long submissionId;
	String submissionStatus;
	Long submissionDataId;
	Long storageLocationId;
	String originalFileName;
	String dataType;
	String description;
	String objectId;
	String objectName;
	
	public Long getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	public Long getSubmissionDataId() {
		return submissionDataId;
	}
	public void setSubmissionDataId(Long submissionDataId) {
		this.submissionDataId = submissionDataId;
	}
	public Long getStorageLocationId() {
		return storageLocationId;
	}
	public void setStorageLocationId(Long storageLocationId) {
		this.storageLocationId = storageLocationId;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(SubmissionDataType dataType) {
		this.dataType = dataType.name();
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description.replaceAll("\"", "");
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	public String getRowString(){
		StringBuilder sb = new StringBuilder();
		sb.append(submissionId.toString()).append(",");
		sb.append(submissionStatus).append(",");
		sb.append(submissionDataId.toString()).append(",");
		sb.append(storageLocationId.toString()).append(",");
		sb.append(originalFileName.toString()).append(",");
		sb.append(dataType.toString()).append(",");
		sb.append("\"").append(description != null ? description : "" ).append("\",");
		sb.append(objectId).append(",");
		sb.append(objectName).append("\n");
		return sb.toString();
	}
	public String getSubmissionStatus() {
		return submissionStatus;
	}
	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
	}
	
	

}
