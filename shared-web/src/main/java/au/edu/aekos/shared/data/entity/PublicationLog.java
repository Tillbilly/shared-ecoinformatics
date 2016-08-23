package au.edu.aekos.shared.data.entity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.util.StringUtils;

@Entity
@Table(name="PUBLICATION_LOG")
public class PublicationLog {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SUBMISSION_ID", nullable=false)
	@ForeignKey(name="submission_fk")
	private Submission submission;
	
	@Column(name="LOG_TIME")
	private Date logTime = new Date();
	
	@Column(name="DOI_TIME")
	private Date doiProcessTime;
	
	@Column(name="DOI_SUCCESS")
	private Boolean doiSuccess;
	
	@Column(name="DOI_ERROR",columnDefinition="TEXT")
	private String doiErrorMessage;
	
	@Column(name="MINTED_DOI")
	private String doi;
	
	@Column(name="RIFCS_FILE_TIME")
	private Date rifcsFileTime;
	
	@Column(name="RFILE_SUCCESS")
	private Boolean fileGenerated;
	
	@Column(name="RFILE_PATH")
	private String filepath;
	
	@Column(name="RFILE_ERROR", columnDefinition="TEXT")
	private String fileGenerationErrorMessage;
	
	@Column(name="RSCP_TIME")
	private Date rifcsScpTime;
	
	@Column(name="RSCP_SUCCESS")
	private Boolean scpSuccess;
	
	@Column(name="RSCP_ERROR", columnDefinition="TEXT")
	private String scpErrorMessage;
	
	@Column(name="INDEX_TIME")
	private Date aekosIndexTime;
	
	@Column(name="INDEX_SUCCESS")
	private Boolean indexSuccess;
	
	@Column(name="INDEX_ERROR", columnDefinition="TEXT")
	private String indexErrorMessage;
	
	@Column(name="INFO", columnDefinition="TEXT")  //Probably put the unformatted rifcs here
	private String information;

	public PublicationLog() {
		super();
	}
	
	public PublicationLog(PublicationLog lastRun){
		//Copy information if the operation was a success
		if(lastRun.doiSuccess != null && lastRun.doiSuccess){
			this.doi = lastRun.getDoi();
			this.doiSuccess = true;
			if(lastRun.doiProcessTime != null){
			   this.doiProcessTime = new Date(lastRun.doiProcessTime.getTime());
			}
		}
		if(lastRun.indexSuccess != null && lastRun.indexSuccess){
			this.indexSuccess = true;
			if(lastRun.aekosIndexTime != null){
			    this.aekosIndexTime = new Date(lastRun.getAekosIndexTime().getTime());
			}
		}
		if(lastRun.fileGenerated != null && lastRun.fileGenerated){
			this.fileGenerated = Boolean.TRUE;
			this.filepath = lastRun.getFilepath();
			if(lastRun.getRifcsFileTime() != null){
				this.rifcsFileTime = new Date(lastRun.getRifcsFileTime().getTime());
			}
		}
		if(lastRun.scpSuccess != null && lastRun.getScpSuccess()){
			this.scpSuccess = lastRun.getScpSuccess();
			if(lastRun.getRifcsScpTime() != null){
				this.setRifcsScpTime(new Date(lastRun.getRifcsScpTime().getTime()) );
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public Date getDoiProcessTime() {
		return doiProcessTime;
	}

	public void setDoiProcessTime(Date doiProcessTime) {
		this.doiProcessTime = doiProcessTime;
	}

	public Boolean getDoiSuccess() {
		return doiSuccess;
	}

	public void setDoiSuccess(Boolean doiSuccess) {
		this.doiSuccess = doiSuccess;
	}

	public String getDoiErrorMessage() {
		return doiErrorMessage;
	}

	public void setDoiErrorMessage(String doiErrorMessage) {
		this.doiErrorMessage = doiErrorMessage;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public Date getRifcsFileTime() {
		return rifcsFileTime;
	}

	public void setRifcsFileTime(Date rifcsFileTime) {
		this.rifcsFileTime = rifcsFileTime;
	}

	public Boolean getFileGenerated() {
		return fileGenerated;
	}

	public void setFileGenerated(Boolean fileGenerated) {
		this.fileGenerated = fileGenerated;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFileGenerationErrorMessage() {
		return fileGenerationErrorMessage;
	}

	public void setFileGenerationErrorMessage(String fileGenerationErrorMessage) {
		this.fileGenerationErrorMessage = fileGenerationErrorMessage;
	}

	public Date getRifcsScpTime() {
		return rifcsScpTime;
	}

	public void setRifcsScpTime(Date rifcsScpTime) {
		this.rifcsScpTime = rifcsScpTime;
	}

	public Boolean getScpSuccess() {
		return scpSuccess;
	}

	public void setScpSuccess(Boolean scpSuccess) {
		this.scpSuccess = scpSuccess;
	}

	public String getScpErrorMessage() {
		return scpErrorMessage;
	}

	public void setScpErrorMessage(String scpErrorMessage) {
		this.scpErrorMessage = scpErrorMessage;
	}

	public Date getAekosIndexTime() {
		return aekosIndexTime;
	}

	public void setAekosIndexTime(Date aekosIndexTime) {
		this.aekosIndexTime = aekosIndexTime;
	}

	public Boolean getIndexSuccess() {
		return indexSuccess;
	}

	public void setIndexSuccess(Boolean indexSuccess) {
		this.indexSuccess = indexSuccess;
	}

	public String getIndexErrorMessage() {
		return indexErrorMessage;
	}

	public void setIndexErrorMessage(String indexErrorMessage) {
		this.indexErrorMessage = indexErrorMessage;
	}

	public String getInformation() {
		return information;
	}

	//Basically, replaces new lines with <br/>
	public String getHtmlFormattedInformation(){
		if(StringUtils.hasLength(information) && information.contains("\n")){
		    String [] lines = information.split("\\n");
		    StringBuilder informationBldr = new StringBuilder();
		    for(String line : lines){
		    	informationBldr.append(line).append("<br/>");
		    }
		    return informationBldr.toString();
		}
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
	
	public void setDoiMintFailure(String errorMessage){
		this.doiSuccess = Boolean.FALSE;
		setDoiErrorMessage(errorMessage);
	}
	
	public void setIndexFailure(Exception e){
		setIndexSuccess(Boolean.FALSE);
		setIndexErrorMessage(e.getClass().getName() + "  " + e.getMessage());
		writeExceptionStackTraceToInformation(e);
	}
	
	public void setRifcsFileFailure(Exception e){
		setFileGenerated(Boolean.FALSE);
		setFileGenerationErrorMessage(e.getClass().getName() + "  " + e.getMessage());
		writeExceptionStackTraceToInformation(e);
	}
	
	public void writeExceptionStackTraceToInformation(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		information = sw.toString();
	}
	
	
}
