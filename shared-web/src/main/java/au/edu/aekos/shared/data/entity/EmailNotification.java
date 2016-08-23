package au.edu.aekos.shared.data.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="EMAIL_NOTIFICATIONS")
public class EmailNotification {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="username")
	private SharedUser sharedUser;
	
	@Column
	private String toAddress;
	
	@Column
	private String subject;
	
	@Column(columnDefinition="TEXT")
	private String message;
	
	@Column
	private Date sentDate;
	
	@Column
	private Boolean exceptionOccured = Boolean.FALSE;
	
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	public Boolean getExceptionOccured() {
		return exceptionOccured;
	}

	public void setExceptionOccured(Boolean exceptionOccured) {
		this.exceptionOccured = exceptionOccured;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SharedUser getSharedUser() {
		return sharedUser;
	}

	public void setSharedUser(SharedUser sharedUser) {
		this.sharedUser = sharedUser;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

}
