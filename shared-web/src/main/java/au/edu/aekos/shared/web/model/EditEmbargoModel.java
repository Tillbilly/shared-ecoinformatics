package au.edu.aekos.shared.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class EditEmbargoModel {
	
	Logger logger = LoggerFactory.getLogger(EditEmbargoModel.class);
	
	private String submissionTitle;
	
	private String username;
	
	private Long submissionId;
	
	private String dateString;

	public static final String EMBARGO_DATE_FORMAT = "dd/MM/yyyy";
	
	public EditEmbargoModel() {
		super();
	}
	public EditEmbargoModel(String dateString) {
		this.dateString = dateString;
	}
	public EditEmbargoModel(String username, Long submissionId, String title, Date date) {
		this.username = username;
		this.submissionId = submissionId;
		this.submissionTitle = title;
		if(date != null){
			SimpleDateFormat sdf = new SimpleDateFormat(EMBARGO_DATE_FORMAT);
			this.dateString = sdf.format(date);
		}else{
			this.dateString = null;
		}
	}
	public String getDateString() {
		return dateString;
	}
	public Date getEmbargoDate() throws ParseException{
		if(!StringUtils.hasLength(dateString)){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(EMBARGO_DATE_FORMAT);
		return sdf.parse(dateString);
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public String getSubmissionTitle() {
		return submissionTitle;
	}
	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}
	public Long getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	

}
