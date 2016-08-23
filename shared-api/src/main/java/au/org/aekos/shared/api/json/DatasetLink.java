package au.org.aekos.shared.api.json;

import java.util.Date;

public class DatasetLink {

	private final String otherDatasetName;
	private final long otherDatasetId;
	private final String linkTypeTitle;
	private final String linkDescription;
	private final int order;
	private final Date otherDatasetPublicationDate;

	public DatasetLink(String otherDatasetName, long otherDatasetId, String linkTypeTitle,
			String linkDescription, int order, Date otherDatasetPublicationDate) {
		this.otherDatasetName = otherDatasetName;
		this.otherDatasetId = otherDatasetId;
		this.linkTypeTitle = linkTypeTitle;
		this.linkDescription = linkDescription;
		this.order = order;
		this.otherDatasetPublicationDate = otherDatasetPublicationDate;
	}

	public String getOtherDatasetName() {
		return otherDatasetName;
	}

	public long getOtherDatasetId() {
		return otherDatasetId;
	}

	public String getLinkTypeTitle() {
		return linkTypeTitle;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public int getOrder() {
		return order;
	}

	public Date getOtherDatasetPublicationDate() {
		return otherDatasetPublicationDate;
	}
}
