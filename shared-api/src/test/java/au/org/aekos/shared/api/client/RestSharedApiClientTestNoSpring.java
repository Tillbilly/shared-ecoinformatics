package au.org.aekos.shared.api.client;

import static au.org.aekos.shared.api.SolrObjectBuilders.aDocument;
import static au.org.aekos.shared.api.SolrObjectBuilders.aField;
import static au.org.aekos.shared.api.SolrObjectBuilders.createSolrTestData;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.junit.Test;

import au.org.aekos.shared.api.AbstractSharedSolrTest;
import au.org.aekos.shared.api.model.dataset.SharedSearchResultFactory;
import au.org.aekos.shared.api.model.search.AvailableIndexFieldEntry;
import au.org.aekos.shared.api.model.search.RequestDatasetSearch;
import au.org.aekos.shared.api.model.search.RequestGetAvailableIndexValues;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearch;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearchFactory;
import au.org.aekos.shared.api.model.search.ResponseGetAvailableIndexValues;

public class RestSharedApiClientTestNoSpring extends AbstractSharedSolrTest{

	/**
	 * Can we conduct a failing search of the SHaRED system?
	 */
	@Test
	public void testDatasetSearch01() throws SolrServerException {
		RestSharedApiClient objectUnderTest = getObjectUnderTest_testDatasetSearch02();
		RequestDatasetSearch request = new RequestDatasetSearch();
		ResponseDatasetSearch result = objectUnderTest.datasetSearch(request);
		assertFalse(result.isSuccess());
		assertEquals("fail town!", result.getFailureReason().getMessage());
	}
	
	/**
	 * Can we conduct a search to get available index values?
	 */
	@Test
	public void testGetAvailableIndexValues01() throws Exception {
		createSolrTestData(solrServer, 
			aDocument(
				aField("id", 1L),
				aField("author_ftxt", "Bob Bobson")
			),
			aDocument(
				aField("id", 2L),
				aField("author_ftxt", "Aaron Aaronson")
			),
			aDocument(
				aField("id", 3L),
				aField("author_ftxt", "Bob Bobson")
			),
			aDocument(
				aField("id", 4L),
				aField("author_ftxt", "Benedict Johnson")
			),
			aDocument(
				aField("id", 5L),
				aField("author_ftxt", "Benedict Johnson")
			),
			aDocument(
				aField("id", 6L),
				aField("author_ftxt", "Bzzzz OnSecondPage")
			)
		);
		RestSharedApiClient objectUnderTest = getObjectUnderTest();
		objectUnderTest.setSharedSolrServer(solrServer);
		int pageSizeThatExcludesOneFacet = 2;
		RequestGetAvailableIndexValues request = new RequestGetAvailableIndexValues("author_ftxt", "B", 0, pageSizeThatExcludesOneFacet);
		ResponseGetAvailableIndexValues result = objectUnderTest.getAvailableIndexValues(request);
		assertTrue(result.isSuccess());
		assertEquals(2, result.getPayload().size());
		assertEquals(5, result.getTotalResultsAvailableSize());
		assertTrue(result.getPayload().contains(new AvailableIndexFieldEntry("Bob Bobson", 2L)));
		assertTrue(result.getPayload().contains(new AvailableIndexFieldEntry("Benedict Johnson", 2L)));
	}

	/**
	 * Can we survive a failing search to get available index values?
	 */
	@Test
	public void testGetAvailableIndexValues02() throws Exception {
		RestSharedApiClient objectUnderTest = getObjectUnderTest_testGetAvailableIndexValues02();
		RequestGetAvailableIndexValues request = new RequestGetAvailableIndexValues("datasetFormalName_ft", "notImportant", 0, 10);
		ResponseGetAvailableIndexValues result = objectUnderTest.getAvailableIndexValues(request);
		assertFalse(result.isSuccess());
		assertEquals(0, result.getPayload().size());
		assertEquals("fail town!", result.getFailureReason().getMessage());
	}

	/**
	 * Can we conduct a case insensitive search for indexed values on an _ft field?
	 */
	@Test
	public void testGetAvailableIndexValues03() throws Exception {
		String ftFieldName = "something_ft";
		createSolrTestData(solrServer, 
			aDocument(
				aField("id", new Long(1L)),
				aField(ftFieldName, "Red Fox")
			),
			aDocument(
				aField("id", new Long(2L)),
				aField(ftFieldName, "Brown Wombat")
			),
			aDocument(
				aField("id", new Long(3L)),
				aField(ftFieldName, "Super Special Bred Bear")
			)
		);
		RestSharedApiClient objectUnderTest = getObjectUnderTest();
		objectUnderTest.setSharedSolrServer(solrServer);
		RequestGetAvailableIndexValues request = new RequestGetAvailableIndexValues(ftFieldName, "red", 0, 100);
		ResponseGetAvailableIndexValues result = objectUnderTest.getAvailableIndexValues(request);
		assertThat(result.getPayload().size(), is(2));
	}
	
	/**
	 * Can we conduct "find all values" search of index values?
	 */
	@Test
	public void testGetAvailableIndexValues04() throws Exception {
		String ftFieldName = "something_ft";
		createSolrTestData(solrServer, 
			aDocument(
				aField("id", new Long(1L)),
				aField(ftFieldName, "Red Fox")
			),
			aDocument(
				aField("id", new Long(2L)),
				aField(ftFieldName, "Brown Wombat")
			),
			aDocument(
				aField("id", new Long(3L)),
				aField(ftFieldName, "Super Special Bred Bear")
			)
		);
		RestSharedApiClient objectUnderTest = getObjectUnderTest();
		objectUnderTest.setSharedSolrServer(solrServer);
		RequestGetAvailableIndexValues request = new RequestGetAvailableIndexValues(ftFieldName);
		ResponseGetAvailableIndexValues result = objectUnderTest.getAvailableIndexValues(request);
		assertThat(result.getPayload().size(), is(3));
	}
	
	/**
	 * Can we build the expected Solr query string when we supply a search term?
	 */
	@Test
	public void testBuildSolrQueryStringWithSearchTerm01() throws Exception {
		String fieldName = "title";
		String searchString = "u";
		String result = RestSharedApiClient.buildSolrQueryStringWithSearchTerm(fieldName, searchString);
		assertEquals("title:u*^2 title:*u*", result);
	}
	
	/**
	 * Can we successfully filter facet results when only some match?
	 */
	@Test
	public void testFilterFacetResults01() {
		RestSharedApiClient objectUnderTest = new RestSharedApiClient();
		List<Count> facetResults = Arrays.asList(
				aCount("Acacia Tree", 1),
				aCount("Superfoxy wombat", 1),
				aCount("French Catchfly", 1),
				aCount("Red Fox", 1),
				aCount("Fox Shark", 1));
		String filter = "fox";
		List<Count> result = objectUnderTest.filterFacetResults(facetResults, filter);
		assertThat(result.size(), is(3));
		assertThat(result.get(0).getName(), is("Superfoxy wombat"));
		assertThat(result.get(1).getName(), is("Red Fox"));
		assertThat(result.get(2).getName(), is("Fox Shark"));
	}
	
	/**
	 * Can we successfully filter facet results when all match?
	 */
	@Test
	public void testFilterFacetResults02() {
		RestSharedApiClient objectUnderTest = new RestSharedApiClient();
		List<Count> facetResults = Arrays.asList(
				aCount("Supershark dog", 1),
				aCount("Yellow Shark", 1));
		String filter = "ark";
		List<Count> result = objectUnderTest.filterFacetResults(facetResults, filter);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getName(), is("Supershark dog"));
		assertThat(result.get(1).getName(), is("Yellow Shark"));
	}
	
	/**
	 * Can we successfully filter facet results when none match?
	 */
	@Test
	public void testFilterFacetResults03() {
		RestSharedApiClient objectUnderTest = new RestSharedApiClient();
		List<Count> facetResults = Arrays.asList(
				aCount("Supershark dog", 1),
				aCount("Yellow Shark", 1));
		String filter = "lol";
		List<Count> result = objectUnderTest.filterFacetResults(facetResults, filter);
		assertThat(result.size(), is(0));
	}
	
	/**
	 * Can we build the expected Solr query string when there is no search term?
	 */
	@Test
	public void testBuildSolrQueryStringWithoutSearchTerm01() throws Exception {
		String fieldName = "title";
		String result = RestSharedApiClient.buildSolrQueryStringWithoutSearchTerm(fieldName);
		assertEquals("title:*", result);
	}
    
	private RestSharedApiClient getObjectUnderTest_testDatasetSearch02() throws SolrServerException {
		SolrServer solrServer = mock(SolrServer.class);
		when(solrServer.query(any(SolrQuery.class))).thenThrow(new SolrServerException("fail town!"));
		RestSharedApiClient objectUnderTest = getObjectUnderTest();
		objectUnderTest.setSharedSolrServer(solrServer);
		return objectUnderTest;
	}
		
	private RestSharedApiClient getObjectUnderTest_testGetAvailableIndexValues02() throws Exception {
		SolrServer solrServer = mock(SolrServer.class);
		when(solrServer.query(any(SolrQuery.class))).thenThrow(new SolrServerException("fail town!"));
		RestSharedApiClient result = getObjectUnderTest();
		result.setSharedSolrServer(solrServer);
		return result;
	}

	private RestSharedApiClient getObjectUnderTest() {
		RestSharedApiClient result = new RestSharedApiClient();
		ResponseDatasetSearchFactory responseDatasetSearchFactory = new ResponseDatasetSearchFactory();
		responseDatasetSearchFactory.setSearchResultFactory(new SharedSearchResultFactory());
		result.setResponseDatasetSearchFactory(responseDatasetSearchFactory);
		return result;
	}
	
	private static Count aCount(String name, int occurrences) {
		return new Count(null, name, occurrences);
	}
}
