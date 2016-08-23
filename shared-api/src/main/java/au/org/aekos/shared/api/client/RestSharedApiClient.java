package au.org.aekos.shared.api.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;
import au.org.aekos.shared.api.json.SharedDatasetSummary;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfoResponse;
import au.org.aekos.shared.api.model.dataset.DatasetId;
import au.org.aekos.shared.api.model.search.RequestDatasetSearch;
import au.org.aekos.shared.api.model.search.RequestGetAvailableIndexValues;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearch;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearchFactory;
import au.org.aekos.shared.api.model.search.ResponseGetAvailableIndexValues;

import com.google.gson.Gson;

/**
 * A REST client to SHaRED. It requires an <code>endpoint</code>
 * to operate, which can be autowired through the property key <code>shared.api.client.endpoint</code>.
 */
@Component
public class RestSharedApiClient implements SharedApiClient {

	private static final String ID_FIELD_NAME = "id";
	private static final String DATASET_DETAILS_URI_FRAGMENT = "/integration/datasetSummary/";
	private static final int ONLY_RETURN_METADATA = 0;
	private static final Logger logger = LoggerFactory.getLogger(RestSharedApiClient.class);
	private RestTemplate restTemplate;
    private SolrServer sharedSolrServer;
    private ResponseDatasetSearchFactory responseDatasetSearchFactory;
    /**
     * Solr allows us to filter the fields that are returned in the documents.
     * This field enables that feature which reduces chattiness and saves us bandwidth.
     */
    private String[] filterFields;

	/**
	 * The endpoint that the shared-web app lives at, e.g: http://shared.aekos.org.au:8080/shared-web
	 */
	@Value("${shared.api.client.endpoint}")
	private String apiServerEndpoint;
	
	@Override
	public SharedDatasetSummary getDatasetDetails(long datasetId) throws NoDatasetFoundException, DatasetPendingPublishException {
		try {
			URI uri = new URI(apiServerEndpoint + DATASET_DETAILS_URI_FRAGMENT + datasetId);
			logger.info("Using SHaRED enpoint URI: " + uri);
			String response = restTemplate.getForObject(uri, String.class);
			Gson gson = new Gson();
			ResponseGetDatasetSummary responseObj = gson.fromJson(response, ResponseGetDatasetSummary.class);
			if (!responseObj.isSuccess()) {
				throw new NoDatasetFoundException("There was an error on the SHaRED server while retrieving dataset details "
						+ "for ID: " + datasetId + ", Server error message: " + responseObj.getErrorMessage());
			}
			if (responseObj.isPendingPublish()) {
				throw new DatasetPendingPublishException(responseObj.getPayload().getTitle());
			}
			return responseObj.getPayload();
		} catch (URISyntaxException use) {
			throw new NoDatasetFoundException("Programmer error: Something is wrong with the supplied enpoint: ["+apiServerEndpoint+"]", use);
		} catch (IllegalArgumentException iae) {
			throw new NoDatasetFoundException("Something is wrong with the supplied enpoint: ["+apiServerEndpoint+"]", iae);
		} catch (HttpClientErrorException hcee) {
			throw new NoDatasetFoundException("Something is wrong with the supplied enpoint: ["+apiServerEndpoint+"]", hcee);
		}
	}

	String buildDatasetDetailsUrlString(List<Long> ids) {
		StringBuilder result = new StringBuilder(apiServerEndpoint + "/integration/submissionDetails/");
		for (Iterator<Long> it = ids.iterator();it.hasNext();) {
			Long currId = it.next();
			result.append(currId);
			if (it.hasNext())
				result.append(",");
		}
		return result.toString();
	}
	
	@Override
	public ResponseDatasetSearch datasetSearch(RequestDatasetSearch request) {
	    try {
	    	SolrQuery query = new SolrQuery();
	    	query.setQuery(request.getQueryString());
	    	query.setStart(request.getStartingItemIndex());
	    	query.setFields(filterFields);
	    	query.setRows(request.getMinimumItemCount());
			QueryResponse solrResp = sharedSolrServer.query(query);
			return responseDatasetSearchFactory.newResponseInstance(solrResp);
		} catch (SolrServerException e) {
			logger.error("Failed to query the SHaRED Solr instance", e);
			return new ResponseDatasetSearch(e);
		}
	}

	@Override
	public ResponseGetAvailableIndexValues getAvailableIndexValues(RequestGetAvailableIndexValues request) {
	    try {
			SolrQuery query = new SolrQuery();
			// TODO consider using ngram matching like in PickersIndexingServiceLucene in the AEKOS indexing project
			String queryString;
			if (request.isSearchStringSupplied()) {
				queryString = buildSolrQueryStringWithSearchTerm(request.getIndexFieldName(), request.getSearchString());
			} else {
				queryString = buildSolrQueryStringWithoutSearchTerm(request.getIndexFieldName());
			}
			query.setQuery(queryString);
			query.setFields(request.getIndexFieldName());
			query.setParam("facet", true);
		    query.setParam("facet.field", request.getFacetFieldName());
		    query.setParam("facet.offset", request.getResultOffset());
		    query.setParam("facet.limit", request.getPageSize());
		    query.setRows(ONLY_RETURN_METADATA);
		    logger.info("SHaRED Solr available index fields query: " + query.toString());
			QueryResponse rsp = sharedSolrServer.query(query);
			int totalResultsAvailable = (int) rsp.getResults().getNumFound();
			List<Count> unfilteredFacets = rsp.getFacetField(request.getFacetFieldName()).getValues();
			if (request.isSearchStringSupplied()) {
				List<Count> filteredFacetFieldCounts = filterFacetResults(unfilteredFacets, request.getSearchString());
				return ResponseGetAvailableIndexValues.newInstance(totalResultsAvailable, filteredFacetFieldCounts);
			}
			return ResponseGetAvailableIndexValues.newInstance(totalResultsAvailable, unfilteredFacets);
		} catch (SolrServerException e) {
			logger.error("Failed to query the SHaRED Solr instance", e);
			return new ResponseGetAvailableIndexValues(e);
		}
	}
	
	@Override
	public SubmissionFileInfoResponse getSubmissionFileInfoResponse(long submissionId, String emailId) {
		SubmissionFileInfoResponse responseObj = null;
		try {
			URI uri = new URI(buildSubmissionFileInfoRequest(submissionId, emailId));
			logger.info("Using SHaRED enpoint URI: " + uri);
			String response = restTemplate.getForObject(uri, String.class);
			Gson gson = new Gson();
			responseObj = gson.fromJson(response, SubmissionFileInfoResponse.class);
			if (responseObj == null) 
				throw new RuntimeException("Error while getting SubmissionFileInfo - Exception with Submission Id: " + submissionId );
			
		} catch (URISyntaxException use) {
			throw new RuntimeException("Something is wrong with the supplied enpoint: ["+apiServerEndpoint+"]", use);
		} catch (IllegalArgumentException iae) {
			throw new RuntimeException("Something is wrong with the supplied enpoint: ["+apiServerEndpoint+"]", iae);
		} catch (HttpClientErrorException hcee) {
			throw new RuntimeException("Something is wrong with the supplied enpoint: ["+apiServerEndpoint+"]", hcee);
		}
		return responseObj;

	}
	
	@Override
	public URL getSummaryReportURL(long submissionId) {
		URL url;
		try {
			url = new URL(buildSubmissionReportUrlString(submissionId));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL = " + buildSubmissionReportUrlString(submissionId) );
		}
		return url;
	}
	
	@Override
	public URL getCitationPDFURL(long submissionId) {
		URL url;
		try {
			url = new URL(buildCitationPDFUrlString(submissionId));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL = " + buildSubmissionReportUrlString(submissionId) );
		}
		return url;
	}
	
	@Override
	public int getTotalResultCountForQuery(String queryString) throws SolrServerException {
		SolrQuery query = new SolrQuery(queryString);
	    query.setRows(ONLY_RETURN_METADATA);
	    logger.info("SHaRED Solr total result count query: " + query.toString());
		QueryResponse rsp = sharedSolrServer.query(query);
		return (int) rsp.getResults().getNumFound();
	}
	
	@Override
	public Set<DatasetId> getAllIndexedIds() throws SolrServerException {
		SolrQuery query = new SolrQuery("*:*");
	    query.setRows(Integer.MAX_VALUE);
	    query.setFields(ID_FIELD_NAME);
		QueryResponse rsp = sharedSolrServer.query(query);
		Set<DatasetId> result = new HashSet<DatasetId>();
		for (SolrDocument currResult : rsp.getResults()) {
			int idValue = Integer.parseInt((String) currResult.getFirstValue(ID_FIELD_NAME));
			result.add(new DatasetId(idValue));
		}
		return result;
	}
	
	/**
	 * Builds submissionFileInfo Request uri
	 */
	String buildSubmissionFileInfoRequest(long id, String emailId) {
		StringBuilder result = new StringBuilder(apiServerEndpoint + "/integration/submissionFileInfo/").append(id).append("?email=").append(emailId);
		return result.toString();
	}
	
	/**
	 * Builds a Solr query that finds all documents with the supplied field
	 * and that also contain the <code>searchString</code> in some way.
	 * 
	 * @param fieldName		Documents must contain this field name
	 * @param searchString	Values of the field must contain this term (see implementation for specifics)
	 * @return				A Solr query string
	 */
	static String buildSolrQueryStringWithSearchTerm(String fieldName, String searchString) {
		return String.format("%1$s:%2$s*^2 %1$s:*%2$s*", fieldName, searchString);
	}

	/**
	 * Builds a Solr query that finds all documents with the supplied field
	 * regardless of the value in that field.
	 * 
	 * @param fieldName		Documents must contain this field name
	 * @return				A Solr query string
	 */
	static String buildSolrQueryStringWithoutSearchTerm(String fieldName) {
		return String.format("%s:*", fieldName);
	}
	
	private String buildCitationPDFUrlString(long submissionId) {
		StringBuilder result = new StringBuilder(apiServerEndpoint + "/reports/published/citation?submissionId=").append(submissionId);
		return result.toString();	
	}

	private String buildSubmissionReportUrlString(long submissionId) {
		StringBuilder result = new StringBuilder(apiServerEndpoint + "/reports/published/submissionSummary?submissionId=").append(submissionId);
		return result.toString();	
	}

	/**
	 * Solr doesn't allow us to filter the facet_fields, which is a problem for multivalued
	 * fields because we'll get back values we most likely don't want. This method performs
	 * that filtering.
	 * 
	 * @param facetResults	facet results from Solr
	 * @param filter		phrase user searched for
	 * @return				<code>facetResults</code> filtered to only the items that contain the string <code>filter</code>
	 */
	List<Count> filterFacetResults(List<Count> facetResults, String filter) {
		List<Count> result = new LinkedList<Count>();
		String lcFilter = filter.toLowerCase();
		for (Count currFacet : facetResults) {
			if (currFacet.getName().toLowerCase().contains(lcFilter)) {
				result.add(currFacet);
			}
		}
		return Collections.unmodifiableList(result);
	}
	
	public void setResponseDatasetSearchFactory(ResponseDatasetSearchFactory responseDatasetSearchFactory) {
		this.responseDatasetSearchFactory = responseDatasetSearchFactory;
	}
	
	public void setFilterFields(String[] filterFields) {
		this.filterFields = filterFields;
	}
	
	public void setSharedSolrServer(SolrServer sharedSolrServer) {
		this.sharedSolrServer = sharedSolrServer;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
}
