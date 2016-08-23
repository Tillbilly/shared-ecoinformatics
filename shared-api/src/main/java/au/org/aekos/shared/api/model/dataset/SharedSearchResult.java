package au.org.aekos.shared.api.model.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharedSearchResult {
	
	private final String datasetId;
	private final String thumbnailUrl;
	private final String imageUrl;
	private final String titleDescription;
	private final String abstractDescription;
	private final String wkt;
	private final List<SharedGridField> gridFields = new ArrayList<SharedGridField>();
	private final boolean embargoedToday;
	private final String licenseType;
	
	public SharedSearchResult(String datasetId, String thumbnailUrl, String imageUrl, String titleDescription
			, String abstractDescription, String wkt, boolean embargoedToday, String licenseType) {
		this.datasetId = datasetId;
		this.thumbnailUrl = thumbnailUrl;
		this.imageUrl = imageUrl;
		this.titleDescription = titleDescription;
		this.abstractDescription = abstractDescription;
		this.wkt = wkt;
		this.embargoedToday = embargoedToday;
		this.licenseType = licenseType;
	}

	public List<SharedGridField> getGridFields() {
		return Collections.unmodifiableList(gridFields);
	}

	public void addGridField(SharedGridField gridField) {
		this.gridFields.add(gridField);
	}

	public String getDatasetId() {
		return datasetId;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getTitleDescription() {
		return titleDescription;
	}

	public String getAbstractDescription() {
		return abstractDescription;
	}

	public String getWkt() {
		return wkt;
	}

	public boolean isEmbargoedToday() {
		return embargoedToday;
	}
	
	public String getLicenseType() {
		return licenseType;
	}
	
	public boolean hasLicenseType() {
		return licenseType != null && licenseType.trim().length() > 0;
	}
	
	@Override
	public String toString() {
		return String.format("SharedSearchResult [datasetId=%s, titleDescription=%s]", datasetId, titleDescription);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abstractDescription == null) ? 0 : abstractDescription.hashCode());
		result = prime * result + ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + (embargoedToday ? 1231 : 1237);
		result = prime * result + ((gridFields == null) ? 0 : gridFields.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((thumbnailUrl == null) ? 0 : thumbnailUrl.hashCode());
		result = prime * result + ((titleDescription == null) ? 0 : titleDescription.hashCode());
		result = prime * result + ((wkt == null) ? 0 : wkt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedSearchResult other = (SharedSearchResult) obj;
		if (abstractDescription == null) {
			if (other.abstractDescription != null)
				return false;
		} else if (!abstractDescription.equals(other.abstractDescription))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (embargoedToday != other.embargoedToday)
			return false;
		if (gridFields == null) {
			if (other.gridFields != null)
				return false;
		} else if (!gridFields.equals(other.gridFields))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (thumbnailUrl == null) {
			if (other.thumbnailUrl != null)
				return false;
		} else if (!thumbnailUrl.equals(other.thumbnailUrl))
			return false;
		if (titleDescription == null) {
			if (other.titleDescription != null)
				return false;
		} else if (!titleDescription.equals(other.titleDescription))
			return false;
		if (wkt == null) {
			if (other.wkt != null)
				return false;
		} else if (!wkt.equals(other.wkt))
			return false;
		return true;
	}
}
