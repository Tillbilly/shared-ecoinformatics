package au.edu.aekos.shared.data.entity;

public enum SubmissionDataType {
	DATA,
	SITE_FILE,
	SPECIES_LIST,
	DOCUMENT,
	RELATED_DOC,
	LICENSE_COND;

	public boolean isSpeciesFile() {
		return SPECIES_LIST.equals(this);
	}
}
