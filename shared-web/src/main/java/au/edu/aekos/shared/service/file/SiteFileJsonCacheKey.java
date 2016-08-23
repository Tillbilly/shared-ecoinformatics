package au.edu.aekos.shared.service.file;

import java.io.Serializable;

public class SiteFileJsonCacheKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fileName;
	private Long siteFileDataId;
	public SiteFileJsonCacheKey(String fileName, Long siteFileDataId) {
		super();
		this.fileName = fileName;
		this.siteFileDataId = siteFileDataId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result
				+ ((siteFileDataId == null) ? 0 : siteFileDataId.hashCode());
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
		SiteFileJsonCacheKey other = (SiteFileJsonCacheKey) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (siteFileDataId == null) {
			if (other.siteFileDataId != null)
				return false;
		} else if (!siteFileDataId.equals(other.siteFileDataId))
			return false;
		return true;
	}
	
	
}
