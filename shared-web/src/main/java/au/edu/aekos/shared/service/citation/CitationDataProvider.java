package au.edu.aekos.shared.service.citation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CitationDataProvider {

	private final List<String> authors = new LinkedList<String>();
	private final String datasetPublicationYear;
	private final String datasetVersion;
	private final String datasetName;
	private final List<String> legalContactOrgs = new LinkedList<String>();
	private final String newLineFragment;
	private final String accessDate;
	private final String doi;

	public CitationDataProvider(List<String> authors, String datasetPublicationYear, String datasetVersion, String datasetName,
			List<String> legalContactOrgs, String doi, String accessDate, String newLineFragment) {
		this.authors.addAll(authors);
		this.datasetPublicationYear = datasetPublicationYear;
		this.datasetVersion = datasetVersion;
		this.datasetName = datasetName;
		this.legalContactOrgs.addAll(legalContactOrgs);
		this.accessDate = accessDate;
		this.doi = doi;
		this.newLineFragment = newLineFragment;
	}

	public List<String> getAuthors() {
		return Collections.unmodifiableList(authors);
	}

	public String getDatasetPublicationYear() {
		return datasetPublicationYear;
	}

	public String getDatasetVersion() {
		return datasetVersion;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public List<String> getLegalContactOrgs() {
		return Collections.unmodifiableList(legalContactOrgs);
	}

	public boolean hasAuthors() {
		return !authors.isEmpty();
	}

	public boolean hasDatasetVersion() {
		return datasetVersion != null && datasetVersion.trim().length() > 0;
	}

	public boolean hasLegalContactOrgs() {
		return !legalContactOrgs.isEmpty();
	}

	public boolean hasDatasetName() {
		return datasetName != null && datasetName.trim().length() > 0;
	}

	public String getNewLineFragment() {
		return newLineFragment;
	}

	public boolean hasDatasetPublicationYear() {
		return datasetPublicationYear != null && datasetPublicationYear.trim().length() > 0;
	}

	public String getAccessDate() {
		return accessDate;
	}

	public String getDoi() {
		return doi;
	}
}
