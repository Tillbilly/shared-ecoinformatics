package au.edu.aekos.shared.submissionIndex;

import java.io.IOException;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FacetCopyStringTest extends ParentEmbeddedServerTest {
	
	@Override
	public String getIndexLocation() {
		return "embedded-4.5/solr";
	}
	
	@Test
	public void test1CreateSomeDocs() throws SolrServerException, IOException{
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField("id", 1);
	    doc1.addField("projectName_ft", "My project 1");
	    server.add(doc1);
	    
	    SolrInputDocument doc2 = new SolrInputDocument();
	    doc2.addField("id", 2);
	    doc2.addField("projectName_ft", "My project 1");
	    server.add(doc2);
	    
	    SolrInputDocument doc3 = new SolrInputDocument();
	    doc3.addField("id", 3);
	    doc3.addField("projectName_ft", "Pandora");
	    server.add(doc3);
	    
	    SolrInputDocument doc4 = new SolrInputDocument();
	    doc4.addField("id", 4);
	    doc4.addField("projectName_ft", "Pandora");
	    server.add(doc4);
	    
	    SolrInputDocument doc5 = new SolrInputDocument();
	    doc5.addField("id", 5);
	    doc5.addField("projectName_ft", "Pandora");
	    server.add(doc5);
	    server.commit(true, true, true);
		
	}
	
	@Test
	public void test2FacetQuery() throws SolrServerException{
		SolrQuery query = new SolrQuery();
		query.setQuery("projectName_ft:*");
		query.setFacet(true);
		query.addFacetField("projectName_facet_s");
		query.setFacetSort("projectName_facet_s");
		QueryResponse response = server.query(query);
		Assert.assertNotNull(response);
		FacetField f = response.getFacetField("projectName_facet_s");
		Count c1 = f.getValues().get(0);
		Assert.assertEquals("My project 1", c1.getName());
		Assert.assertEquals(2l, c1.getCount());
		Count c2 = f.getValues().get(1);
		Assert.assertEquals("Pandora", c2.getName());
		Assert.assertEquals(3l, c2.getCount());
	}
	
	@Test
	public void test3CleanUp() throws SolrServerException{
		try {
			server.deleteById(Arrays.asList("1","2","3","4","5"));
			server.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
