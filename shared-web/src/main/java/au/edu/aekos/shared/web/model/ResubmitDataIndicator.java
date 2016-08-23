package au.edu.aekos.shared.web.model;

public class ResubmitDataIndicator {

	private Long dataId;
	//private Boolean hasDescriptionChanged = Boolean.FALSE;
	private Boolean wasRejected = Boolean.FALSE;
	private Boolean hasBeenDeleted = Boolean.FALSE;
	private Boolean newFile = Boolean.FALSE;
	
	public Long getDataId() {
		return dataId;
	}
	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	public Boolean getWasRejected() {
		return wasRejected;
	}
	public void setWasRejected(Boolean wasRejected) {
		this.wasRejected = wasRejected;
	}
	public Boolean getHasBeenDeleted() {
		return hasBeenDeleted;
	}
	public void setHasBeenDeleted(Boolean hasBeenDeleted) {
		this.hasBeenDeleted = hasBeenDeleted;
	}
	public Boolean getNewFile() {
		return newFile;
	}
	public void setNewFile(Boolean newFile) {
		this.newFile = newFile;
	}
	
	
}
