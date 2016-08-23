package au.edu.aekos.shared.web.model;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.web.util.SharedFileUtils;

public class SubmissionDataFileModel {

	static final String NO_FORMAT_TITLE_DEFAULT = "(no format)";
	static final String NO_FORMAT_VERSION_DEFAULT = "(no version)";
	private Long id;
	private String fileName;
	private String fileDescription;
	private Date embargoDate;  //Request for embargo on the whole submission.  Ok to leave for now, things might change.
	private Long fileSizeBytes;
	private String fileType;
	private String questionId = null; //If a data file is the response to a specific question
	private String format;
	private String formatTitle;
	private String formatVersion;
	
	public SubmissionDataFileModel() {}
	
	public SubmissionDataFileModel(SubmissionData dataEntity){
		this.id= dataEntity.getId();
		this.fileName = dataEntity.getFileName();
		this.fileDescription = dataEntity.getFileDescription();
		this.embargoDate = dataEntity.getEmbargoDate();
		this.fileSizeBytes = dataEntity.getFileSizeBytes();
		this.fileType = dataEntity.getSubmissionDataType().name();
		this.questionId = dataEntity.getQuestionId();
		this.format = dataEntity.getFormat();
		this.formatVersion = dataEntity.getFormatVersion();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDescription() {
		return fileDescription;
	}
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	public Date getEmbargoDate() {
		return embargoDate;
	}
	public void setEmbargoDate(Date embargoDate) {
		this.embargoDate = embargoDate;
	}
	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}
	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}
	
	public String getFileSizeString(){
		return SharedFileUtils.humanReadableByteCount(fileSizeBytes, true);
	}
	
	public boolean isFileAvailableForDownload(){
		if(this.embargoDate == null){
			return true;
		}
		Date today = new Date();
		if(today.compareTo(embargoDate) <= 0 || DateUtils.isSameDay(today, embargoDate)){
            return false;			
		}
		return true;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getFormatVersion() {
		return StringUtils.isEmpty(formatVersion) ? NO_FORMAT_VERSION_DEFAULT : formatVersion;
	}
	public void setFormatVersion(String formatVersion) {
		this.formatVersion = formatVersion;
	}
	public String getFormatTitle() {
		return StringUtils.isEmpty(formatTitle) ? NO_FORMAT_TITLE_DEFAULT : formatTitle;
	}
	public void setFormatTitle(String traitDisplayText) {
		this.formatTitle = traitDisplayText;
	}
	
	public String getFileTypeTitle() {
		return SubmissionDataFileType.getTitleForCode(fileType);
	}
}
