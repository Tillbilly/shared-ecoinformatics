package au.org.aekos.shared.api.model.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import au.org.aekos.shared.api.model.dataset.SharedSearchResult;
import au.org.aekos.shared.api.model.dataset.SharedSearchResultFactory;

public class ResponseDatasetSearchFactory {
	
	private SharedSearchResultFactory searchResultFactory;
	
	/**
	 * @param solrResp	Solr response to extract data from
	 * @return			new response with data from the supplied Solr response
	 */
	public ResponseDatasetSearch newResponseInstance(QueryResponse solrResp) {
		long resultTotalNumFound = solrResp.getResults().getNumFound();
		long resultOffset = solrResp.getResults().getStart();
		List<SharedSearchResult> resultDatasets = new ArrayList<SharedSearchResult>();
		for (SolrDocument currSolrResult : solrResp.getResults()) {
			resultDatasets.add(searchResultFactory.newSearchResultInstance(currSolrResult));
		}
		return new ResponseDatasetSearch(resultTotalNumFound, resultOffset, resultDatasets);
	}

	public void setSearchResultFactory(SharedSearchResultFactory searchResultFactory) {
		this.searchResultFactory = searchResultFactory;
	}
}
