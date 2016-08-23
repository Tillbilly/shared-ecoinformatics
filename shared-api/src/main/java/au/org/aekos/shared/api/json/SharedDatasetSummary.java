package au.org.aekos.shared.api.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

import com.google.gson.Gson;

/**
 * The Response object used to deserialise JSON from REST-GET requests for Dataset Details
 * @author btill
 */
public class SharedDatasetSummary {

	private final String datasetId;
	private final String title;
	private final Date submissionDate;  //Use the Gson default date format ... I`ll work out what that is in a minute . . .
	private final Date lastReviewDate;
	private final List<SubmissionSummaryRow> rowDataList = new ArrayList<SubmissionSummaryRow>();
	private final List<String> wktGeometryList = new ArrayList<String>();
	private final Boolean isUnderEmbargo;
	private final List<SpeciesFileNameEntry> speciesFiles = new LinkedList<SpeciesFileNameEntry>();
	private final List<DatasetLink> links = new ArrayList<DatasetLink>();
	private boolean newerVersionPresent;
	
	/**
	 * Constructs a summary that indicates that a dataset was found and has a status that allows it to
	 * be displayed to the public.
	 * 
	 * @param datasetId			ID of the dataset
	 * @param title				title of the dataset
	 * @param submissionDate	date the dataset was submitted
	 * @param lastReviewDate	date the dataset was published by the reviewer
	 * @param isUnderEmbargo	<code>true</code> if this dataset is currently under embargo and cannot be downloaded, <code>false</code> otherwise
	 */
	public SharedDatasetSummary(String datasetId, String title, Date submissionDate, Date lastReviewDate, boolean isUnderEmbargo) {
		this.datasetId = datasetId;
		this.title = title;
		this.submissionDate = submissionDate;
		this.lastReviewDate = lastReviewDate;
		this.isUnderEmbargo = isUnderEmbargo;
	}
	
	public String getDatasetId() {
		return datasetId;
	}
	public String getTitle() {
		return title;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public Date getLastReviewDate() {
		return lastReviewDate;
	}
	public List<SubmissionSummaryRow> getRowDataList() {
		return rowDataList;
	}
	public void addRowData(SubmissionSummaryRow rowData) {
		this.rowDataList.add(rowData);
	}
	public void addAllRowData(Collection<SubmissionSummaryRow> rowData) {
		this.rowDataList.addAll(rowData);
	}
	public List<String> getWktGeometryList() {
		return wktGeometryList;
	}
	public void addWktGeometry(String wktGeometry) {
		this.wktGeometryList.add(wktGeometry);
	}
	public void addAllWktGeometry(Collection<String> wktGeometries) {
		this.wktGeometryList.addAll(wktGeometries);
	}

	public String getJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public Boolean getIsUnderEmbargo() {
		return isUnderEmbargo;
	}
	
	public void addSpeciesFileNameEntry(SpeciesFileNameEntry entry) {
		speciesFiles.add(entry);
	}

	public List<SpeciesFileNameEntry> getSpeciesFiles() {
		return Collections.unmodifiableList(speciesFiles);
	}

	public void addAllSpeciesFileNameEntry(List<SpeciesFileNameEntry> listOfEntries) {
		for (SpeciesFileNameEntry currEntry : listOfEntries) {
			speciesFiles.add(currEntry);
		}
	}

	public void addLink(DatasetLink link) {
		links.add(link);
	}

	public List<DatasetLink> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public boolean isNewerVersionPresent() {
		return newerVersionPresent;
	}

	public void setNewerVersionPresent(boolean newerVersionPresent) {
		this.newerVersionPresent = newerVersionPresent;
	}
}
