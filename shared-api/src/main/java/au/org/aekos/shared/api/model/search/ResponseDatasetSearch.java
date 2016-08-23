package au.org.aekos.shared.api.model.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.org.aekos.shared.api.model.dataset.SharedSearchResult;

public class ResponseDatasetSearch {

	private final long totalNumFound;
	private final long offset;
	private final List<SharedSearchResult> datasets = new ArrayList<SharedSearchResult>();
	private final boolean success;
	private final Throwable failureReason;

	public ResponseDatasetSearch(Throwable e) {
		this.success = false;
		this.failureReason = e;
		this.totalNumFound = -1L;
		this.offset = -1L;
	}

	public ResponseDatasetSearch(long totalNumFound, long offset, List<SharedSearchResult> datasets) {
		this.totalNumFound = totalNumFound;
		this.offset = offset;
		this.success = true;
		this.failureReason = null;
		this.datasets.addAll(datasets);
	}

	/**
	 * @return	<code>true</code> if the search completely successfully, <code>false</code> otherwise.
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return	When <code>isSuccess() == false</code>, this field contains the reason for failure
	 */
	public Throwable getFailureReason() {
		return failureReason;
	}
	
	/**
	 * @return	The total number of results found for the submitted query, <b>NOT</b> to be confused with {@link DatasetSearchResult#getSize()}
	 */
	public long getTotalNumFound() {
		return totalNumFound;
	}
	
	/**
	 * @return	The offset of this set of results
	 */
	public long getOffset() {
		return offset;
	}
	
	/**
	 * @return	The count of the actual number of items in this result
	 */
	public int size() {
		return datasets.size();
	}

	/**
	 * @return	The search result items (payload of this response)
	 */
	public List<SharedSearchResult> getDatasets() {
		return Collections.unmodifiableList(datasets);
	}
	
	/**
	 * Creates a new response that represents an empty response.
	 * 
	 * @param offset	offset requested in the request
	 * @return			a new empty response instance
	 */
	public static ResponseDatasetSearch newEmptyInstance(long offset) {
		List<SharedSearchResult> emptyList = Collections.emptyList();
		return new ResponseDatasetSearch(0, offset, emptyList);
	}

	public boolean isEmpty() {
		return datasets.size() == 0;
	}
}
