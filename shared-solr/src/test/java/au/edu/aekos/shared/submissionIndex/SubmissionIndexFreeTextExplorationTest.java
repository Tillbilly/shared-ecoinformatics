package au.edu.aekos.shared.submissionIndex;

import static au.edu.aekos.shared.solr.index.SubmissionSolrDocumentBuilder.addFaunaSpecies;
import static au.edu.aekos.shared.solr.index.SubmissionSolrDocumentBuilder.addFloraSpecies;
import static au.edu.aekos.shared.solr.index.SubmissionSolrDocumentBuilder.addSpatialFeature;
import static au.edu.aekos.shared.solr.index.SubmissionSolrDocumentBuilder.initialiseSHaREDDocument;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubmissionIndexFreeTextExplorationTest extends ParentEmbeddedServerTest {
	
	@Override
	public String getIndexLocation() {
		return "embedded-4.5/solr";
	}
	
	private void createSubmissionDocuments() throws ParseException, SolrServerException, IOException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SolrInputDocument doc1 = initialiseSHaREDDocument("1", "doi1", "Great Expectations", "Great Expectations 2008", 
				                                                                        "Calicivirus occurence data on wine production vineyards", 
				                                                                        sdf.parse("01/01/2008"), sdf.parse("31/12/2008"));
		addFloraSpecies(Arrays.asList("Grape vine","Cervisae","Vitus vinifera"), doc1);
		addSpatialFeature("POINT(139.0 -36.0)", doc1);
		
		SolrInputDocument doc2 = initialiseSHaREDDocument("2", "doi2", "Kangaroo mating in the Tanami", "Kangaroo mating in the Tanami Desert", 
				"Mating occurence data captured by keen observers. Zoological nutters.", sdf.parse("15/03/2010"), sdf.parse("01/02/2011"));
		addFaunaSpecies(Arrays.asList("Macropod sp.","Red Kangaroo","Macropus rufus"), doc2);
		addSpatialFeature("POLYGON((128.0 -20.0,129.0 -20.0,129.0 -19.0,128.0 -19.0,128.0 -20.0))", doc2);
		
		SolrInputDocument doc3 = initialiseSHaREDDocument("3", "doi3", "QLD Government Pests and Weeds", "QLD Pastoral Pests and Weeds 2012", 
				                                          "Survey of invasive pests and weeds in the northern QLD and east atherton tablelands farming districts", 
				                                          sdf.parse("08/11/2008"), sdf.parse("31/03/2009"));
		addFloraSpecies(Arrays.asList("Weeds","Cannabis Sativa","Cannabis Indica var. Dr Grinspoon", "Mary Jane"), doc3);
		SolrInputDocument doc4 = initialiseSHaREDDocument("4", "doi4", "Test Set", "Test Set", 
                "Survey stuff, going to boost on flora field", 
                sdf.parse("08/11/2008"), sdf.parse("31/03/2009"));
        addFloraSpecies(Arrays.asList("Calicivirus"), doc4);
		server.add(doc1);
		server.add(doc2);
		server.add(doc3);
		server.add(doc4);
		//server.commit();
		server.commit(true, true, true);
		
	}
	
	@Test
	public void test1CreateSomeSubmissionDocuments(){
		try{
			createSubmissionDocuments();
		}catch(Exception ex){
			Assert.fail();
		}
	}
	
	@Test
	public void test2SomeFreeTextExploration() throws SolrServerException{
		SolrQuery query = new SolrQuery();
	    //query.add("id", "id1");
	    query.setQuery("text:\"invasive pests\"");
		QueryResponse response = server.query(query);
		Assert.assertTrue(response.getResults().size() > 0 );
		SolrDocument doc = response.getResults().get(0);
		Assert.assertEquals("3",doc.getFieldValue("id"));
		query.setQuery("text:2012 AND text:QLD");
		response = server.query(query);
		Assert.assertTrue(response.getResults().size() > 0 );
		doc = response.getResults().get(0);
		Assert.assertEquals("3",doc.getFieldValue("id"));
		query.setQuery("title:Great*^10 OR title:Tanami*");
		response = server.query(query);
		Assert.assertTrue(response.getResults().size() > 0 );
		Assert.assertEquals(2, response.getResults().size() );
		doc = response.getResults().get(0);
		Assert.assertEquals("1",doc.getFieldValue("id"));
	}
	
	@Test
	public void test3TermBoost() throws SolrServerException{
		SolrQuery query = new SolrQuery();
	    //query.add("id", "id1");
	    query.setQuery("text:Calicivirus OR species_txt:Calicivirus^10");
		QueryResponse response = server.query(query);
		Assert.assertTrue(response.getResults().size() == 2 );
		SolrDocument doc = response.getResults().get(0);
		Assert.assertEquals("4",doc.getFieldValue("id"));
	}
	
	
	
	@Test
	public void ztestCleanUp() throws SolrServerException{
		try {
			server.deleteById(Arrays.asList("1","2","3","4"));
			server.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//What happens when we delete a doc that doesn't exist?
		try {
			UpdateResponse ur = server.deleteById(Arrays.asList("11","222","333","444"));
			ur.toString();
			server.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
