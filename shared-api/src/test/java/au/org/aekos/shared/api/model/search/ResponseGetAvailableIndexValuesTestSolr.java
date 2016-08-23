package au.org.aekos.shared.api.model.search;

import static au.org.aekos.shared.api.SolrObjectBuilders.aDocument;
import static au.org.aekos.shared.api.SolrObjectBuilders.aField;
import static au.org.aekos.shared.api.SolrObjectBuilders.createSolrTestData;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;

import au.org.aekos.shared.api.AbstractSharedSolrTest;

public class ResponseGetAvailableIndexValuesTestSolr extends AbstractSharedSolrTest {

	/**
	 * Can we tell when the search string is supplied
	 */
	@Test
	public void testBuildPayload01() throws Exception {
		String targetField = "author_s";
		createSolrTestData(solrServer, 
			aDocument(
				aField("id", 1L),
				aField(targetField, "Bob Bobson")
			),
			aDocument(
				aField("id", 2L),
				aField(targetField, "Bob Bobson")
			),
			aDocument(
				aField("id", 3L),
				aField(targetField, "Bob Bobson")
			),
			aDocument(
				aField("id", 4L),
				aField(targetField, "Benedict Johnson")
			),
			aDocument(
				aField("id", 5L),
				aField(targetField, "Benedict Johnson")
			)
		);
		SolrQuery query = new SolrQuery("*:*");
		query.setParam("facet", true);
		query.setParam("facet.field", targetField);
		QueryResponse solrResponse = solrServer.query(query);
		List<AvailableIndexFieldEntry> result = ResponseGetAvailableIndexValues.buildPayload(solrResponse.getFacetField(targetField).getValues());
		assertThat(result.size(), is(2));
		assertThat(result.contains(new AvailableIndexFieldEntry("Bob Bobson", 3)), is(true));
		assertThat(result.contains(new AvailableIndexFieldEntry("Benedict Johnson", 2)), is(true));
	}
}
