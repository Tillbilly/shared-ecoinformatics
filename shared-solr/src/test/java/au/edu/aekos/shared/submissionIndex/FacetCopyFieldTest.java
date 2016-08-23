package au.edu.aekos.shared.submissionIndex;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FacetCopyFieldTest extends ParentEmbeddedServerTest {
	
	@Override
	public String getIndexLocation() {
		return "embedded-4.5/solr";
	}
	
	@Test
	public void test1CreateSomeDocs() throws SolrServerException, IOException{
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField("id", 1);
	    doc1.addField("tester_ft", "Blagger");
	    doc1.addField("testerx_ftxt", "Blaggerholick");
	    doc1.addField("testerx_ftxt", "Blagger montgomery");
	    server.add(doc1);
	    
	    
	    server.commit(true, true, true);
		
	}
	
	@Test
	public void test2FacetQuery() throws SolrServerException{
		SolrQuery query = new SolrQuery();
		query.setQuery("id:1");
		QueryResponse response = server.query(query);
		Assert.assertNotNull(response);
		SolrDocument doc = response.getResults().get(0);
		Map<String, Object> fieldMap = doc.getFieldValueMap();
		Assert.assertTrue(fieldMap.containsKey("tester_facet_s"));
		Assert.assertTrue(fieldMap.containsKey("testerx_facet_ss"));
		
		for(Object obj : doc.getFieldValues("testerx_facet_ss")){
			String str = (String) obj;
			Assert.assertTrue("Blaggerholick".equals(str) || "Blagger montgomery".equals(str) );
		}
	}
	
	@Test
	public void test3CleanUp() throws SolrServerException{
		try {
			server.deleteById(Arrays.asList("1"));
			server.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
