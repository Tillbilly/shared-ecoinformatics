package au.edu.aekos.shared.data.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

@Entity(name="submissionLink")
@Table(name="SUBMISSION_LINK")
public class SubmissionLink {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SOURCE_SUBMISSION_ID")
	@ForeignKey(name="source_sub_fk")
	@Index(name="SOURCE_SUBMISSION_IX")
	private Submission sourceSubmission;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LINKED_SUBMISSION_ID")
	@ForeignKey(name="linked_sub_fk")
	private Submission linkedSubmission;
	
	@Enumerated(EnumType.STRING)
	private SubmissionLinkType linkType;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column
	private Boolean sourceLink;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="LINKED_BY_USER")
	@ForeignKey(name="linked_by_fk")
	private SharedUser linkedByUser;
	
	@Column
	private Date linkDate;
	
	@OneToOne
	@JoinColumn(name="INVERSE_LINK_ID")
	@ForeignKey(name="inverse_link_fk")
	private SubmissionLink inverseLink;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Submission getSourceSubmission() {
		return sourceSubmission;
	}

	public void setSourceSubmission(Submission sourceSubmission) {
		this.sourceSubmission = sourceSubmission;
	}

	public Submission getLinkedSubmission() {
		return linkedSubmission;
	}

	public void setLinkedSubmission(Submission linkedSubmission) {
		this.linkedSubmission = linkedSubmission;
	}

	public SubmissionLinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(SubmissionLinkType linkType) {
		this.linkType = linkType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(Boolean sourceLink) {
		this.sourceLink = sourceLink;
	}

	public SharedUser getLinkedByUser() {
		return linkedByUser;
	}

	public void setLinkedByUser(SharedUser linkedByUser) {
		this.linkedByUser = linkedByUser;
	}

	public Date getLinkDate() {
		return linkDate;
	}

	public void setLinkDate(Date linkDate) {
		this.linkDate = linkDate;
	}

	public SubmissionLink getInverseLink() {
		return inverseLink;
	}

	public void setInverseLink(SubmissionLink inverseLink) {
		this.inverseLink = inverseLink;
	}
	
}
