package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.List;


public class PublishedTextFileViewModel {

	private String errorMessage;
	private String submissionId;
	private String submissionTitle;
	private String submissionDataId;
	private String dataFileType;
	private String filename;
	private List<String> lines = new ArrayList<String>();
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(String submissionId) {
		this.submissionId = submissionId;
	}
	public String getSubmissionTitle() {
		return submissionTitle;
	}
	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}
	public String getSubmissionDataId() {
		return submissionDataId;
	}
	public void setSubmissionDataId(String submissionDataId) {
		this.submissionDataId = submissionDataId;
	}
	public String getDataFileType() {
		return dataFileType;
	}
	public void setDataFileType(String dataFileType) {
		this.dataFileType = dataFileType;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<String> getLines() {
		return lines;
	}
	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	
	
	
	
	
}
