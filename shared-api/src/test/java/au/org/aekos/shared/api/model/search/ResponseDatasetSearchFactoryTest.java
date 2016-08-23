package au.org.aekos.shared.api.model.search;

import static au.org.aekos.shared.api.SolrObjectBuilders.aDocument;
import static au.org.aekos.shared.api.SolrObjectBuilders.aField;
import static au.org.aekos.shared.api.SolrObjectBuilders.createSolrTestData;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.org.aekos.shared.api.AbstractSharedSolrTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-test.xml"})
public class ResponseDatasetSearchFactoryTest extends AbstractSharedSolrTest {
	
	@Autowired
	@Qualifier("indexNamesProps")
	private Properties props;
	
	@Autowired
	private ResponseDatasetSearchFactory objectUnderTest;
	
	/**
	 * Can we create a new instance from a Solr result?
	 */
	@Test
	public void testNewInstance01() throws Exception {
		createSolrTestData(solrServer, 
			aDocument(
				aField(prop("index-names.id"), new Long(8491L)),
				aField(prop("index-names.dataset.name"), "super wicked wombats")
			),
			aDocument(
				aField(prop("index-names.id"), new Long(3556L)),
				aField(prop("index-names.dataset.name"), "Ben Test")
			),
			aDocument(
				aField(prop("index-names.id"), new Long(2788L)),
				aField(prop("index-names.dataset.name"), "Parks")
			)
		);
		QueryResponse solrResp = solrServer.query(new SolrQuery("*:*"));
		ResponseDatasetSearch result = objectUnderTest.newResponseInstance(solrResp);
		assertThat(result.isSuccess(), is(true));
		assertThat(result.getOffset(), is(0L));
		assertThat(result.getTotalNumFound(), is(3L));
		assertThat(result.getDatasets().size(), is(3));
	}

	private String prop(String propertyKey) {
		return props.getProperty(propertyKey);
	}
}
