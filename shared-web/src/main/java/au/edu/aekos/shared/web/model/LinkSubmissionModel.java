package au.edu.aekos.shared.web.model;

import au.edu.aekos.shared.data.entity.SubmissionLinkType;

public class LinkSubmissionModel {

	private Long linkFromSubmissionId;
	private Long linkToSubmissionId;
	private SubmissionLinkType linkType;
	private String description;
	//Used for superuser group linking
	private Long groupId;
	
	public Long getLinkFromSubmissionId() {
		return linkFromSubmissionId;
	}
	public void setLinkFromSubmissionId(Long linkFromSubmissionId) {
		this.linkFromSubmissionId = linkFromSubmissionId;
	}
	public Long getLinkToSubmissionId() {
		return linkToSubmissionId;
	}
	public void setLinkToSubmissionId(Long linkToSubmissionId) {
		this.linkToSubmissionId = linkToSubmissionId;
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
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
}
