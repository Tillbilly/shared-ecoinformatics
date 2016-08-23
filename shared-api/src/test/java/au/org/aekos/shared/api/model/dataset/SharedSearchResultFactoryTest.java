package au.org.aekos.shared.api.model.dataset;

import static au.org.aekos.shared.api.SolrObjectBuilders.aDocument;
import static au.org.aekos.shared.api.SolrObjectBuilders.aField;
import static au.org.aekos.shared.api.SolrObjectBuilders.createSolrTestData;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.org.aekos.shared.api.AbstractSharedSolrTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-test.xml"})
public class SharedSearchResultFactoryTest extends AbstractSharedSolrTest {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

	@Autowired
	@Qualifier("indexNamesProps")
	private Properties props;
	
	@Autowired
	private SharedSearchResultFactory objectUnderTest;
	
	/**
	 * Can we create a new instance from a Solr doc?
	 */
	@Test
	public void testNewInstance01() throws Exception {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.YEAR, 1);
		Date oneYearInTheFuture = calendar.getTime();
		createSolrTestData(solrServer, 
			aDocument(
				aField(prop("index-names.id"), new Long(8491L)),
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
				aField(prop("index-names.license-type"), "Tern Attribution (TERN-BY) Data Licence V1.0"),
				aField(prop("index-names.spatial-location"), "POINT(138.7128906 -23.9834339)"),
				aField(prop("index-names.embargo-date"), oneYearInTheFuture),
				aField(prop("index-names.image-url"), "/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a.jpg"),
				aField(prop("index-names.thumbnail-url"), "/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a_tn.jpg")
			)
		);
		QueryResponse solrResp = solrServer.query(new SolrQuery("*:*"));
		SolrDocument source = solrResp.getResults().get(0);
		SharedSearchResult result = objectUnderTest.newSearchResultInstance(source);
		assertThat(result.getDatasetId(), is("8491"));
		assertThat(result.getTitleDescription(), is("Animi voluptatem Tempor ut quia illo velit velit nihil"));
		assertThat(result.getAbstractDescription(), is("Omnis quibusdam"));
		assertThat(result.getImageUrl(), is("/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a.jpg"));
		assertThat(result.getThumbnailUrl(), is("/getSubmissionImage?submissionId=4757&questionId=3.10&imageId=2521a72e-35bd-41ca-900d-ba0135e4f83a_tn.jpg"));
		assertThat(result.getWkt(), is("POINT(138.7128906 -23.9834339)"));
		assertThat(result.isEmbargoedToday(), is(true));
		assertThat(result.getLicenseType(), is("TERN-BY 1.0"));
		assertThat(result.getGridFields(), hasItems(
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
			new SharedGridField("License Type", "Tern Attribution (TERN-BY) Data Licence V1.0")));
		assertThat(result.getGridFields().size(), is(13));
	}

	/**
	 * Can we tell when we ARE under embargo because the date is in the future?
	 */
	@Test
	public void testDetermineIsEmbargoedOnDate01() {
		Date today = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.YEAR, 1);
		Date oneYearInTheFuture = calendar.getTime();
		SolrDocument solrDoc = new SolrDocument();
		solrDoc.addField(prop("index-names.embargo-date"), oneYearInTheFuture);
		boolean result = objectUnderTest.determineIsEmbargoedOnDate(solrDoc, today);
		assertThat(result, is(true));
	}
	
	/**
	 * Can we tell when we are NOT under embargo because the date is in the past?
	 */
	@Test
	public void testDetermineIsEmbargoedOnDate02() {
		Date today = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date yesterday = calendar.getTime();
		SolrDocument solrDoc = new SolrDocument();
		solrDoc.addField(prop("index-names.embargo-date"), yesterday);
		boolean result = objectUnderTest.determineIsEmbargoedOnDate(solrDoc, today);
		assertThat(result, is(false));
	}
	
	/**
	 * Can we tell when we ARE under embargo because the date is today?
	 */
	@Test
	public void testDetermineIsEmbargoedOnDate03() {
		Date today = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(today);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date justBeforeMidnightToday = calendar.getTime();
		SolrDocument solrDoc = new SolrDocument();
		solrDoc.addField(prop("index-names.embargo-date"), justBeforeMidnightToday);
		boolean result = objectUnderTest.determineIsEmbargoedOnDate(solrDoc, today);
		assertThat(result, is(true));
	}
	
	/**
	 * Can we resolve the TERN-BY license type?
	 */
	@Test
	public void testResolveExportLicenseType01() {
		String indexedLicenseType = "Tern Attribution (TERN-BY) Data Licence V1.0";
		String result = objectUnderTest.resolveExportLicenseType(indexedLicenseType);
		assertThat(result, is("TERN-BY 1.0"));
	}

	/**
	 * Can we resolve the CC-BY license type?
	 */
	@Test
	public void testResolveExportLicenseType02() {
		String indexedLicenseType = "Creative Commons Attribution 3.0 Australia";
		String result = objectUnderTest.resolveExportLicenseType(indexedLicenseType);
		assertThat(result, is("CC-BY 3.0"));
	}
	
	/**
	 * Can we handle an indexed license type that we don't have a mapping for?
	 */
	@Test
	public void testResolveExportLicenseType03() {
		String indexedLicenseType = "Purple Monkey Dishwasher v123";
		String result = objectUnderTest.resolveExportLicenseType(indexedLicenseType);
		assertThat(result, is("Purple Monkey Dishwasher v123"));
	}
	
	private String prop(String propertyKey) {
		return props.getProperty(propertyKey);
	}
}
