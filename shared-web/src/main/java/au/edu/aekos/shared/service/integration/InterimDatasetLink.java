package au.edu.aekos.shared.service.integration;

import java.util.Date;

import au.edu.aekos.shared.data.entity.SubmissionLinkType;

public class InterimDatasetLink implements Comparable<InterimDatasetLink> {

	private final String otherDatasetTitle;
	private final long otherDatasetId;
	private final SubmissionLinkType linkType;
	private final String linkDescription;
	private final Date targetDatasetPublished;
	private final boolean isSourceOfLink;
	
	public InterimDatasetLink(String otherDatasetTitle, long otherDatasetId, SubmissionLinkType linkType,
			String linkDescription, Date targetDatasetPublished, boolean isSourceOfLink) {
		this.otherDatasetTitle = otherDatasetTitle;
		this.otherDatasetId = otherDatasetId;
		this.linkType = linkType;
		this.linkDescription = linkDescription;
		this.targetDatasetPublished = targetDatasetPublished;
		this.isSourceOfLink = isSourceOfLink;
	}

	public String getOtherDatasetTitle() {
		return otherDatasetTitle;
	}

	public long getOtherDatasetId() {
		return otherDatasetId;
	}

	public SubmissionLinkType getLinkType() {
		return linkType;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public Date getTargetDatasetPublished() {
		return targetDatasetPublished;
	}

	public boolean isSourceOfLink() {
		return isSourceOfLink;
	}

	/**
	 * Calculates an order for this link. We use a bit shift with the highest weighting
	 * given to links that indicate there is a newer version, then the secondary weighting
	 * based on the type of the link.
	 * 
	 * @return	number that indicates the order/position for this link
	 */
	private int getOrderBasedOnLinkType() {
		int result = 1;
		boolean isSourceOfHasNewVersion = SubmissionLinkType.HAS_NEW_VERSION.equals(linkType) && isSourceOfLink;
		boolean isTargetOfIsNewVersionOf = SubmissionLinkType.IS_NEW_VERSION_OF.equals(linkType) && !isSourceOfLink;
		if (isSourceOfHasNewVersion || isTargetOfIsNewVersionOf) {
			result = result << SubmissionLinkType.values().length +1;
		}
		result = result << (SubmissionLinkType.values().length - linkType.ordinal());
		return result;
	}
	
	@Override
	public int compareTo(InterimDatasetLink o) {
		int linkTypeComparison = Integer.compare(o.getOrderBasedOnLinkType(), getOrderBasedOnLinkType());
		if (linkTypeComparison != 0) {
			return linkTypeComparison;
		}
		return o.targetDatasetPublished.compareTo(targetDatasetPublished);
	}
}