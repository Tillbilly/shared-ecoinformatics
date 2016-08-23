package au.org.aekos.shared.api.client;

import java.net.URL;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;

import au.org.aekos.shared.api.json.SharedDatasetSummary;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfoResponse;
import au.org.aekos.shared.api.model.dataset.DatasetId;
import au.org.aekos.shared.api.model.search.RequestDatasetSearch;
import au.org.aekos.shared.api.model.search.RequestGetAvailableIndexValues;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearch;
import au.org.aekos.shared.api.model.search.ResponseGetAvailableIndexValues;

public interface SharedApiClient {

	/**
	 * @param datasetId		ID of the dataset to retrieve
	 * @return				The dataset if it exists, <code>null</code> otherwise
	 * @throws NoDatasetFoundException	when no dataset could be found with the supplied ID
	 * @throws DatasetPendingPublishException	when the dataset was found but is in the process of being published
	 */
	SharedDatasetSummary getDatasetDetails(long datasetId) throws NoDatasetFoundException, DatasetPendingPublishException;

	/**
	 * Performs a search of all published data sets in the SHaRED repository
	 * 
	 * @param request	Details of what to search for
	 * @return			A response wrapper containing indicators for success/failure and the search result payload 
	 */
	ResponseDatasetSearch datasetSearch(RequestDatasetSearch request);

	/**
	 * Finds all available values in the search index for the supplied field
	 * 
	 * @param request	Indicates what field to search for
	 * @return			A reponse wrapper than indicates
	 */
	ResponseGetAvailableIndexValues getAvailableIndexValues(RequestGetAvailableIndexValues request);
	
	/**
	 * Finds the SubmissionFileInfoResponse for a Submisssion ID.
	 * This can be used to download the submission from the Aekos Side.
	 * 
	 * @param submissionId SubmissionID of record 
	 * @param emailId email Id to store in the Shared repo for Statistics
	 * @return SubmissionFileInfoResponse
	 */
	SubmissionFileInfoResponse getSubmissionFileInfoResponse(long submissionId, String emailId);
	
	/**
	 * gets the SubmissionReportURL for a Submisssion ID.
	 * This can be used to download the submission info from the Aekos Side.
	 * 
	 * @param submissionId SubmissionID of the record
	 * @return URL
	 */
	URL getSummaryReportURL(long submissionId);
	
	/**
	 * gets the CitationPDFURL for a Submisssion ID.
	 * This can be used to download the PDF the Aekos Side.
	 * 
	 * @param submissionId Submission ID of the record to download the Citation PDF
	 * @return URL
	 */
	URL getCitationPDFURL(long submissionId);

	/**
	 * Queries the index using the supplied query string to see how many results are available.
	 * 
	 * @param query		query string to use against index
	 * @return			total number of results available for the query
	 * @throws SolrServerException 
	 */
	int getTotalResultCountForQuery(String query) throws SolrServerException;
	
	/**
	 * Gets the IDs of all the indexed datasets. Intended to be used for building URLs for a sitemap.
	 * 
	 * @return	all indexed IDs
	 * @throws SolrServerException	when something goes wrong with Solr
	 */
	Set<DatasetId> getAllIndexedIds() throws SolrServerException;
	
	public class NoDatasetFoundException extends RuntimeException {
		
		private static final long serialVersionUID = 1L;

		public NoDatasetFoundException(String message) {
			super(message);
		}

		public NoDatasetFoundException(String message, Throwable t) {
			super(message);
		}
	}
	
	/**
	 * Indicates that a dataset was found but it is still in the workflow of
	 * being published. This means we need to acknowledge that it exists
	 * without actually giving the details of it. In practice this means
	 * we need to respond to requests for it with an HTTP 200 but not
	 * show the actual data so the DOI minting service can verify
	 * that the URL is correct.
	 */
	public class DatasetPendingPublishException extends Exception {
		
		private static final long serialVersionUID = 1L;
		private final String datasetTitle;

		public DatasetPendingPublishException(String datasetTitle) {
			this.datasetTitle = datasetTitle; // FIXME I know you're not meant to use exception for non-exceptional execution/to pass information
		}

		public String getDatasetTitle() {
			return datasetTitle;
		}
	}
}
