package au.org.aekos.shared.api.model.dataset;

public class LicenseTypeMapping {

	private final String indexedValue;
	private final String exportedValue;
	
	public LicenseTypeMapping(String indexedValue, String exportedValue) {
		this.indexedValue = indexedValue;
		this.exportedValue = exportedValue;
	}

	public String getIndexedValue() {
		return indexedValue;
	}

	public String getExportedValue() {
		return exportedValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exportedValue == null) ? 0 : exportedValue.hashCode());
		result = prime * result + ((indexedValue == null) ? 0 : indexedValue.hashCode());
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
		LicenseTypeMapping other = (LicenseTypeMapping) obj;
		if (exportedValue == null) {
			if (other.exportedValue != null)
				return false;
		} else if (!exportedValue.equals(other.exportedValue))
			return false;
		if (indexedValue == null) {
			if (other.indexedValue != null)
				return false;
		} else if (!indexedValue.equals(other.indexedValue))
			return false;
		return true;
	}
}
