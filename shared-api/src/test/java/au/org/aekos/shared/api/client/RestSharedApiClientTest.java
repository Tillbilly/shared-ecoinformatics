package au.org.aekos.shared.api.client;

import static au.org.aekos.shared.api.SolrObjectBuilders.aDocument;
import static au.org.aekos.shared.api.SolrObjectBuilders.aField;
import static au.org.aekos.shared.api.SolrObjectBuilders.createMultipleSolrDocumentsWithTheField;
import static au.org.aekos.shared.api.SolrObjectBuilders.createSolrTestData;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import au.org.aekos.shared.api.AbstractSharedSolrTest;
import au.org.aekos.shared.api.client.SharedApiClient.DatasetPendingPublishException;
import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;
import au.org.aekos.shared.api.json.SharedDatasetSummary;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfoResponse;
import au.org.aekos.shared.api.model.dataset.DatasetId;
import au.org.aekos.shared.api.model.dataset.SharedGridField;
import au.org.aekos.shared.api.model.dataset.SharedSearchResult;
import au.org.aekos.shared.api.model.search.RequestDatasetSearch;
import au.org.aekos.shared.api.model.search.ResponseDatasetSearch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-test.xml"})
public class RestSharedApiClientTest extends AbstractSharedSolrTest{

	private static final int TWENTY_ONE_COPIES = 21;

	@Autowired
	@Qualifier("indexNamesProps")
	private Properties props;
	
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    
    @Autowired
    private RestSharedApiClient objectUnderTest;

    @Before
    public void setUp() {
    	mockServer = MockRestServiceServer.createServer(restTemplate);
    }
	
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
    
    @Value("${shared.api.client.endpoint}")
    private String testEndpoint;
	
	/**
	 * Can we conduct a search of the SHaRED system?
	 */
	@Test
	public void testDatasetSearch01() throws Exception {
		createSolrTestData(solrServer, 
			aDocument(
				aField(prop("index-names.id"), new Long(123L)),
				aField(prop("index-names.field-of-research"), "404")
			),
			aDocument(
				aField(prop("index-names.id"), new Long(456L)),
				aField(prop("index-names.field-of-research"), "503")
			),
			aDocument(
				aField(prop("index-names.id"), new Long(789L)),
				aField(prop("index-names.dataset.name"), "Animi voluptatem Tempor ut quia illo velit velit nihil"),
				aField(prop("index-names.dataset.abstract"), "Omnis quibusdam"),
				aField(prop("index-names.dataset.version"), "666"),
				aField(prop("index-names.project.name"), "Voluptas omnis"),
				aField(prop("index-names.study-location-count"), new Integer(3)),
				aField(prop("index-names.visit-date.first"), sdf.parse("2014-02-10T13:30:00Z")),
				aField(prop("index-names.visit-date.last"), sdf.parse("2014-02-24T13:30:00Z")),
				aField(prop("index-names.keywords"), "Qui illum, quia commodo tempor"),
				aField(prop("index-names.field-of-research"), "404"),
				aField(prop("index-names.field-of-research"), "503"),
				aField(prop("index-names.socio-economic-objectives"), "9613"),
				aField(prop("index-names.ecoloigical-theme"), "Ecotoxicology"),
				aField(prop("index-names.ecoloigical-theme"), "Debitis praesentium consectetur"),
				aField(prop("index-names.conservation-themes"), "Migratory species"),
				aField(prop("index-names.conservation-themes"), "Obcaecati veniam aliquip aut"),
				aField(prop("index-names.dataset.custodian"), "ABARES"),
				aField(prop("index-names.dataset.custodian"), "TERN"),
				aField(prop("index-names.method.name"), "The Be-Awesome Method"),
				aField(prop("index-names.license-type"), "TERN-BY 1.0"),
				aField(prop("index-names.spatial-location"), "POINT(138.7128906 -23.9834339)"),
				aField(prop("index-names.image-url"), "/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a.jpg"),
				aField(prop("index-names.thumbnail-url"), "/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a_tn.jpg")
			)
		);
		objectUnderTest.setSharedSolrServer(solrServer);
		RequestDatasetSearch request = new RequestDatasetSearch(prop("index-names.field-of-research") + ":404", 0, 10);
		ResponseDatasetSearch result = objectUnderTest.datasetSearch(request);
		assertTrue(result.isSuccess());
		assertEquals(2L, result.getTotalNumFound());
		assertEquals(2L, result.size());
		assertEquals(0L, result.getOffset());
		SharedSearchResult fullyPopulatedResult = result.getDatasets().get(1);
		assertThat(fullyPopulatedResult.getDatasetId(), is("789"));
		assertThat(fullyPopulatedResult.getTitleDescription(), is("Animi voluptatem Tempor ut quia illo velit velit nihil"));
		assertThat(fullyPopulatedResult.getAbstractDescription(), is("Omnis quibusdam"));
		assertThat(fullyPopulatedResult.getImageUrl(), is("/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a.jpg"));
		assertThat(fullyPopulatedResult.getThumbnailUrl(), is("/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a_tn.jpg"));
		assertThat(fullyPopulatedResult.getWkt(), is("POINT(138.7128906 -23.9834339)"));
		assertThat(fullyPopulatedResult.getGridFields(), hasItems(
			new SharedGridField("Version Number", "666"),
			new SharedGridField("Project Name", "Voluptas omnis"),
			new SharedGridField("Number of Sites", "3"),
			new SharedGridField("Start Date", "10/02/2014"),
			new SharedGridField("End Date", "24/02/2014"),
			new SharedGridField("Keywords", "Qui illum, quia commodo tempor"),
			new SharedGridField("ANZRC FOR Codes", "404, 503"),
			new SharedGridField("ANZRC SEO Codes", "9613"),
			new SharedGridField("Ecological Themes", "Ecotoxicology, Debitis praesentium consectetur"),
			new SharedGridField("Conservation/NRM Themes", "Migratory species, Obcaecati veniam aliquip aut"),
			new SharedGridField("Dataset Custodian", "ABARES, TERN"),
			new SharedGridField("Method Name", "The Be-Awesome Method"),
			new SharedGridField("License Type", "TERN-BY 1.0")));
		assertThat(fullyPopulatedResult.getGridFields().size(), is(13));
	}
	
	/**
	 * Can we conduct a search of the SHaRED system and get a full page of results back?
	 */
	@Test
	public void testDatasetSearch02() throws Exception {
		createMultipleSolrDocumentsWithTheField(solrServer, TWENTY_ONE_COPIES, 
			aField(prop("index-names.license-type"), "TERN-BY 1.0")
		);
		objectUnderTest.setSharedSolrServer(solrServer);
		RequestDatasetSearch request = new RequestDatasetSearch(prop("index-names.license-type") + ":TERN*", 0, 20);
		ResponseDatasetSearch result = objectUnderTest.datasetSearch(request);
		assertTrue(result.isSuccess());
		assertEquals(21L, result.getTotalNumFound());
		assertEquals(0L, result.getOffset());
		assertThat(result.size(), is(20));
	}

	/**
	 * Can we conduct a search of the SHaRED system and get the second page?
	 */
	@Test
	public void testDatasetSearch03() throws Exception {
		createMultipleSolrDocumentsWithTheField(solrServer, TWENTY_ONE_COPIES, 
			aField(prop("index-names.license-type"), "TERN-BY 1.0")
		);
		objectUnderTest.setSharedSolrServer(solrServer);
		RequestDatasetSearch request = new RequestDatasetSearch(prop("index-names.license-type") + ":TERN*", 20, 20);
		ResponseDatasetSearch result = objectUnderTest.datasetSearch(request);
		assertTrue(result.isSuccess());
		assertEquals(21L, result.getTotalNumFound());
		assertEquals(20L, result.getOffset());
		assertThat(result.size(), is(1));
	}
	
	/**
	 * Can we survive no results being returned from the Solr search?
	 */
	@Test
	public void testDatasetSearch04() throws Exception {
		/* Don't create any Solr documents */
		objectUnderTest.setSharedSolrServer(solrServer);
		RequestDatasetSearch request = new RequestDatasetSearch("org_s:notThere", 0, 10);
		ResponseDatasetSearch result = objectUnderTest.datasetSearch(request);
		assertTrue(result.isSuccess());
		assertEquals(0L, result.getTotalNumFound());
		assertEquals(request.getStartingItemIndex(), result.getOffset());
	}
	
	/**
	 * Can we conduct a search of the SHaRED system and get a full page of results back?
	 */
	@Test
	public void testGetTotalResultCountForQuery01() throws Exception {
		createMultipleSolrDocumentsWithTheField(solrServer, TWENTY_ONE_COPIES, 
			aField(prop("index-names.license-type"), "TERN-BY 1.0")
		);
		objectUnderTest.setSharedSolrServer(solrServer);
		int result = objectUnderTest.getTotalResultCountForQuery(prop("index-names.license-type") + ":TERN*");
		assertThat(result, is(21));
	}
	
	/**
	 * Can the client receive a success (with published status) response and correctly handle it?
	 */
	@Test
	public void testGetSubmissionDetails01() throws Throwable {
		ResponseGetDatasetSummary response = ResponseGetDatasetSummary.newInstanceSuccess(
				new SharedDatasetSummary("1337", "dataset1", new Date(), new Date(), false));
		mockServer
			.expect(requestTo(testEndpoint+"/integration/datasetSummary/1337"))
			.andExpect(method(HttpMethod.GET))
        	.andRespond(withSuccess(response.getJsonString(), MediaType.TEXT_PLAIN));
		long submissionId = 1337L;
		SharedDatasetSummary result = objectUnderTest.getDatasetDetails(submissionId);
		assertEquals("1337", result.getDatasetId());
	}
	
	/**
	 * Can the client receive an error response and correctly handle it?
	 */
	@Test
	public void testGetSubmissionDetails02() throws Throwable {
		mockServer
			.expect(requestTo(testEndpoint+"/integration/datasetSummary/1337"))
			.andExpect(method(HttpMethod.GET))
        	.andRespond(withServerError());
		long submissionId = 1337L;
		try {
			objectUnderTest.getDatasetDetails(submissionId);
			fail();
		} catch (HttpServerErrorException e) {
			// Success!
		}
	}
	
	/**
	 * Can the client handle when a server side exception occurs?
	 */
	@Test
	public void testGetSubmissionDetails03() throws Throwable {
		ResponseGetDatasetSummary response = ResponseGetDatasetSummary.newInstanceFailure("KA-BOOM!!!");
		mockServer
			.expect(requestTo(testEndpoint+"/integration/datasetSummary/1337"))
			.andExpect(method(HttpMethod.GET))
        	.andRespond(withSuccess(response.getJsonString(), MediaType.TEXT_PLAIN));
		Long submissionId = 1337L;
		try {
			objectUnderTest.getDatasetDetails(submissionId);
			fail();
		} catch (RuntimeException e) {
			// success!
		}
	}
	
	/**
	 * Can the client receive a success (with published pending status) response and correctly handle it?
	 */
	@Test
	public void testGetSubmissionDetails04() throws Throwable {
		ResponseGetDatasetSummary response = ResponseGetDatasetSummary.newInstancePendingPublish(
				new SharedDatasetSummary("1337", "dataset1", null, null, false));
		mockServer
			.expect(requestTo(testEndpoint+"/integration/datasetSummary/1337"))
			.andExpect(method(HttpMethod.GET))
        	.andRespond(withSuccess(response.getJsonString(), MediaType.TEXT_PLAIN));
		long submissionId = 1337L;
		try {
			objectUnderTest.getDatasetDetails(submissionId);
			fail();
		} catch (DatasetPendingPublishException e) {
			// success!
			assertThat(e.getDatasetTitle(), is("dataset1"));
		}
	}
	
	/**
	 * Positive test case for getting submission File Info response
	 */
	@Test
	public void testGetSubmissionFileInfoResponse() {
		SubmissionFileInfoResponse response = new SubmissionFileInfoResponse();
		response.setSubmissionId((long) 3556);
		response.setNumFiles(1);
		
		mockServer
			.expect(requestTo(testEndpoint+"/integration/submissionFileInfo/3556?email=test@test.com"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(response.getJsonString(), MediaType.TEXT_PLAIN));

		SubmissionFileInfoResponse fileInfoResult = objectUnderTest.getSubmissionFileInfoResponse(3556,"test@test.com");
		assertEquals(new Long(3556), fileInfoResult.getSubmissionId());
		assertEquals(new Integer(1), fileInfoResult.getNumFiles());

	}
	
	/**
	 * negative test case for getting submission File Info response
	 */
	@Test
	public void testGetSubmissionFileInfoResponseException() {
		mockServer
			.expect(requestTo(testEndpoint+"/integration/submissionFileInfo/3556?email="))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withServerError());
		try {
			objectUnderTest.getSubmissionFileInfoResponse(3556,"");
			fail();
		} catch (RuntimeException e) {
			//success
		}
	}
	
	/**
	 * getting a URL for submission File
	 */
	@Test
	public void testGetSummaryReportURL() {
		URL url = objectUnderTest.getSummaryReportURL(3556);
		assertEquals("/endpoint/reports/published/submissionSummary?submissionId=3556", url.getFile());
		assertEquals(testEndpoint, "http://"+url.getHost()+"/endpoint");
	}
	
	/**
	 * getting a URL for Citation PDF 
	 * 	 
	 */
	@Test
	public void testGetCitationPDFURL() {
		URL url = objectUnderTest.getCitationPDFURL(3556);
		assertEquals("/endpoint/reports/published/citation?submissionId=3556", url.getFile());
		assertEquals(testEndpoint, "http://"+url.getHost()+"/endpoint");
	}
	
	/**
	 * Can we get all the indexed IDs?
	 */
	@Test
	public void testGetAllIndexedIds01() throws Exception {
		createSolrTestData(solrServer, 
			aDocument(
				aField("id", new Long(123L))
			),
			aDocument(
				aField("id", new Long(456L))
			)
		);
		objectUnderTest.setSharedSolrServer(solrServer);
		Set<DatasetId> result = objectUnderTest.getAllIndexedIds();
		assertThat(result.size(), is(2));
		assertThat(result, hasItems(new DatasetId(123), new DatasetId(456)));
	}
	
	/**
	 * Helper to make getting properties more terse in the test code
	 * 
	 * @param propertyKey	key to retrieve
	 * @return				vaue for requested key
	 */
	private String prop(String propertyKey) {
		return props.getProperty(propertyKey);
	}
}
