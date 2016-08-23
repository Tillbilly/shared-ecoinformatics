package au.org.aekos.shared.api.model.dataset;

public class SharedResponse {

	private final String metaTag, codeValue, displayValue;

	public SharedResponse(String metaTag, String codeValue, String displayValue) {
		this.metaTag = metaTag;
		this.codeValue = codeValue;
		this.displayValue = displayValue;
	}

	public String getMetaTag() {
		return metaTag;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeValue == null) ? 0 : codeValue.hashCode());
		result = prime * result + ((displayValue == null) ? 0 : displayValue.hashCode());
		result = prime * result + ((metaTag == null) ? 0 : metaTag.hashCode());
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
		SharedResponse other = (SharedResponse) obj;
		if (codeValue == null) {
			if (other.codeValue != null)
				return false;
		} else if (!codeValue.equals(other.codeValue))
			return false;
		if (displayValue == null) {
			if (other.displayValue != null)
				return false;
		} else if (!displayValue.equals(other.displayValue))
			return false;
		if (metaTag == null) {
			if (other.metaTag != null)
				return false;
		} else if (!metaTag.equals(other.metaTag))
			return false;
		return true;
	}

	/**
	 * @return	<code>true</code> if this response has a null or zero length response, <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return ( codeValue == null || codeValue.length() == 0 );
	}
}
