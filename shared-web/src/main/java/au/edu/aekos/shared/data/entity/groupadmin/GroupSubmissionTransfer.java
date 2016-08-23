package au.edu.aekos.shared.data.entity.groupadmin;

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
import org.hibernate.annotations.Index;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;

@Entity(name="groupSubmissionTransfer")
@Table(name="SUBMISSION_TRANSFER")
public class GroupSubmissionTransfer {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne   //This might end up pointing to an old saved submission - TODO need to update this on successive saves??
	@JoinColumn(name="SUBMISSION_ID")
	@ForeignKey(name="submission_transfer_fk")
	@Index(name="SUBMISSION_TRANSFER_IX")
	private Submission submission;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TRANSFER_FROM")
	@ForeignKey(name="transfer_from_user_fk")
	private SharedUser transferFromUser;
	
	@ManyToOne
	@JoinColumn(name="TRANSFER_TO")
	@ForeignKey(name="transfer_to_user_fk")
	private SharedUser transferToUser;
	
	@ManyToOne
	@JoinColumn(name="TRANSFERRED_BY")
	@ForeignKey(name="transferred_by_user_fk")
	private SharedUser transferredByUser;
	
	@Column
	private String message;
	
	@Column
	private Date transferDate;

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

	public SharedUser getTransferFromUser() {
		return transferFromUser;
	}

	public void setTransferFromUser(SharedUser transferFromUser) {
		this.transferFromUser = transferFromUser;
	}

	public SharedUser getTransferToUser() {
		return transferToUser;
	}

	public void setTransferToUser(SharedUser transferToUser) {
		this.transferToUser = transferToUser;
	}

	public SharedUser getTransferredByUser() {
		return transferredByUser;
	}

	public void setTransferredByUser(SharedUser transferredByUser) {
		this.transferredByUser = transferredByUser;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

}
