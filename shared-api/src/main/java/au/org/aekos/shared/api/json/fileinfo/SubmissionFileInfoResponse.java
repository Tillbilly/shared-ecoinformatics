package au.org.aekos.shared.api.json.fileinfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class SubmissionFileInfoResponse {

	private String errorMessage;
	
	private Long submissionId;
	
	private Integer numFiles;
	
	private String submissionTitle;
	
	private List<SubmissionFileInfo> files = new ArrayList<SubmissionFileInfo>();

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<SubmissionFileInfo> getFiles() {
		return files;
	}
	public void setFiles(List<SubmissionFileInfo> files) {
		this.files = files;
	}
	public Long getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	public Integer getNumFiles() {
		return numFiles;
	}
	public void setNumFiles(Integer numFiles) {
		this.numFiles = numFiles;
	}
	
	public String getSubmissionTitle() {
		return submissionTitle;
	}
	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}
	
	public String getJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
