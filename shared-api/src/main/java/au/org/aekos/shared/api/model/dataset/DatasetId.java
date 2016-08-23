package au.org.aekos.shared.api.model.dataset;

public class DatasetId {

	private final int value;

	public DatasetId(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
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
		DatasetId other = (DatasetId) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
