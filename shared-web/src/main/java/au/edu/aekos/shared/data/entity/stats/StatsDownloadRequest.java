package au.edu.aekos.shared.data.entity.stats;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ST_DOWNREQ")
public class StatsDownloadRequest {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="SUBMISSION_ID")
	private Long submissionId;
	
	@Column
	private String email;
	
	@Column(name="DATE")
	private Date requestDate = new Date();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

}
