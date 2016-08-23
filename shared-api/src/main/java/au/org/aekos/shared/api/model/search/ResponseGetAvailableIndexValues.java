package au.org.aekos.shared.api.model.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;

public class ResponseGetAvailableIndexValues {

	private final SolrServerException failureReason;
	private final boolean success;
	private final List<AvailableIndexFieldEntry> payload;
	private final int totalResultsAvailableSize;

	public ResponseGetAvailableIndexValues(SolrServerException e) {
		this.success = false;
		this.failureReason = e;
		this.payload = Collections.emptyList();
		this.totalResultsAvailableSize = 0;
	}

	public ResponseGetAvailableIndexValues(List<AvailableIndexFieldEntry> payload, int totalResultsAvailableSize) {
		this.success = true;
		this.failureReason = null;
		this.payload = payload;
		this.totalResultsAvailableSize = totalResultsAvailableSize;
	}
	
	/**
	 * @param totalResultsAvailable		count of the total number of items in the index for this query
	 * @param facetFieldCounts			payload data of index terms and their occurrence counts
	 * @return							new response object
	 */
	public static ResponseGetAvailableIndexValues newInstance(int totalResultsAvailable, List<Count> facetFieldCounts) {
		return new ResponseGetAvailableIndexValues(buildPayload(facetFieldCounts), totalResultsAvailable);
	}

	/**
	 * Extracts all the required information from the Solr response and maps it to our domain object
	 * 
	 * @param facetCounts		response to extract information from
	 * @return					mapped information
	 */
	static List<AvailableIndexFieldEntry> buildPayload(List<Count> facetCounts) {
		List<AvailableIndexFieldEntry> result = new ArrayList<AvailableIndexFieldEntry>();
		for (Count currFacetValue : facetCounts) {
			result.add(new AvailableIndexFieldEntry(currFacetValue.getName(), currFacetValue.getCount()));
		}
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<AvailableIndexFieldEntry> getPayload() {
		return Collections.unmodifiableList(payload);
	}

	public Throwable getFailureReason() {
		return failureReason;
	}

	public int getTotalResultsAvailableSize() {
		return totalResultsAvailableSize;
	}
}
