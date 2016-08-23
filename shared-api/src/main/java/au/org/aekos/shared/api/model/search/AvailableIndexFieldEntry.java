package au.org.aekos.shared.api.model.search;


/**
 * Represents a value in an index solr field
 */
public class AvailableIndexFieldEntry {
	private final String title;
	private final long occurrences;
	
	public AvailableIndexFieldEntry(String title, long occurrences) {
		this.title = title;
		this.occurrences = occurrences;
	}
	
	public String getTitle() {
		return title;
	}

	public long getOccurrences() {
		return occurrences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (occurrences ^ (occurrences >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		AvailableIndexFieldEntry other = (AvailableIndexFieldEntry) obj;
		if (occurrences != other.occurrences)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
