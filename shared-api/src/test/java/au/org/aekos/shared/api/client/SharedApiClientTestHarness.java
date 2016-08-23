package au.org.aekos.shared.api.client;

import java.net.URISyntaxException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.org.aekos.shared.api.json.SharedDatasetSummary;
import au.org.aekos.shared.api.model.dataset.SharedSearchResult;
import au.org.aekos.shared.api.model.search.AvailableIndexFieldEntry;
import au.org.aekos.shared.api.model.search.RequestDatasetSearch;
import au.org.aekos.shared.api.model.search.RequestGetAvailableIndexValues;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearch;
import au.org.aekos.shared.api.model.search.ResponseGetAvailableIndexValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SharedApiClientTestHarness {
	private static final String APPLICATION_CONTEXT_XML_LOCATION = "/applicationContext-testHarness.xml";
	private static final Logger logger = LoggerFactory.getLogger(SharedApiClientTestHarness.class);
	private static final boolean SUCCESS = true;
	private static final boolean FAIL = false;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws BeansException, URISyntaxException {
		ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML_LOCATION);
		SharedApiClient client = context.getBean(SharedApiClient.class);
		boolean stage1Success = executeGetDatasetDetails(client);
		if (!stage1Success) { return; }
		boolean stage2Success = executeDatasetSearch(client);
		if (!stage2Success) { return; }
		executeGetAvailableIndexValues(client);
	}

	private static boolean executeGetDatasetDetails(SharedApiClient client) {
		final long submissionId = 212315L;
		try {
			SharedDatasetSummary result = client.getDatasetDetails(submissionId);
			logger.info("--executeGetDatasetDetails SUCCESS--");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement uglyJsonElement = new JsonParser().parse(result.getJsonString());
			logger.info(gson.toJson(uglyJsonElement));
			return SUCCESS;
		} catch (Exception e) {
			logger.error("--executeGetDatasetDetails FAIL--", e);
			return FAIL;
		}
	}
	
	private static boolean executeDatasetSearch(SharedApiClient client) {
		RequestDatasetSearch request = new RequestDatasetSearch("id:*", 0, 5);
		try {
			ResponseDatasetSearch result = client.datasetSearch(request);
			if (!result.isSuccess()) {
				logger.info("executeDatasetSearch FAIL: " + result.getFailureReason().getMessage());
				return FAIL;
			}
			logger.info(String.format("--executeDatasetSearch SUCCESS-- Found %d records", result.getDatasets().size()));
			for (SharedSearchResult currDataset : result.getDatasets()) {
				logger.info(currDataset.toString());
			}
			return SUCCESS;
		} catch (Exception e) {
			logger.error("--executeDatasetSearch FAIL--", e);
			return FAIL;
		}
	}
	
	private static void executeGetAvailableIndexValues(SharedApiClient client) {
		final String indexFieldName = "title";
		String searchString = "u";
		RequestGetAvailableIndexValues request = new RequestGetAvailableIndexValues(indexFieldName, searchString, 0, 100);
		ResponseGetAvailableIndexValues result = client.getAvailableIndexValues(request);
		if (!result.isSuccess()) {
			logger.error("--executeGetAvailableIndexValues FAIL--");
			return;
		}
		for (AvailableIndexFieldEntry currValue : result.getPayload()) {
			logger.info(String.format("%s, occurrences: %s", currValue.getTitle(), currValue.getOccurrences()));
		}
	}
	
	public static class SolrFactory {
		public SolrServer createSolrServerWithPreemptiveAuth() {
			HttpSolrServer solrServer = new HttpSolrServer("http://115.146.85.142:8080/solr");
			DefaultHttpClient dhc = (DefaultHttpClient) solrServer.getHttpClient();
			dhc.getCredentialsProvider().setCredentials(
	                new AuthScope("115.146.85.142", 8080),
	                new UsernamePasswordCredentials("solr", "solr"));
			return solrServer;
		}
	}
}
