package au.org.aekos.shared.api.json;

public class SpeciesFileNameEntry {

	private final long id;
	private final String fileName;

	public SpeciesFileNameEntry(long id, String fileName) {
		this.id = id;
		this.fileName = fileName;
	}
	
	public long getId() {
		return id;
	}
	public String getFileName() {
		return fileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
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
		SpeciesFileNameEntry other = (SpeciesFileNameEntry) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("SpeciesFileNameEntry [id=%s, fileName=%s]", id, fileName);
	}
}
