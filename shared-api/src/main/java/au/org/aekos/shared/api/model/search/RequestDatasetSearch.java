package au.org.aekos.shared.api.model.search;


public class RequestDatasetSearch {

	private String queryString;
	private int startingItemIndex;
	private int minimumItemCount;
	
	public RequestDatasetSearch() {}
	
	public RequestDatasetSearch(String queryString, int startingItemIndex, int minimumItemCount) {
		this.queryString = queryString;
		this.startingItemIndex = startingItemIndex;
		this.minimumItemCount = minimumItemCount;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public int getStartingItemIndex() {
		return startingItemIndex;
	}

	public void setStartingItemIndex(int startingItemIndex) {
		this.startingItemIndex = startingItemIndex;
	}

	public int getMinimumItemCount() {
		return minimumItemCount;
	}

	public void setMinimumItemCount(int minimumItemCount) {
		this.minimumItemCount = minimumItemCount;
	}
}
