package au.edu.aekos.shared.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TextFieldAndCatchAllTest{

	public EmbeddedSolrServer getServer(){
		String indexLocation = "embedded-4.5/solr-orig";
		System.setProperty("solr.solr.home",indexLocation );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(indexLocation);
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	public SolrInputDocument buildTextDoc(String id, String title, String description, String... textFieldPairs){
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", id );
	    doc.addField( "title", title);
	    String titleSrch = title.replaceAll(" ", "");
	    doc.addField("titlesort_t", titleSrch);
	    doc.addField("description", description);
	    if(textFieldPairs != null && textFieldPairs.length > 1){
	    	for(int x = 0; x < textFieldPairs.length - 1 ; x = x + 2 ){
	    		doc.addField(textFieldPairs[x], textFieldPairs[x + 1]);
	    	}
	    }
	   return doc;
	}
	
	@Test
	public void test1LoadUpSomeTextFilledDocs() throws SolrServerException, IOException{
		
		 List<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
		 SolrInputDocument doc= buildTextDoc("tx1","My Document 1","This is a test document for perusal by academic researchers",
				                             "bulktext1_t","Books and libraries, journals",
				                             "bulktext2_t","internet is fun",
				                             "sorttext_t","Z");
		 docList.add(doc);
		 doc= buildTextDoc("tx2","Monkey Document","This is a test document for monkeys and gorillas",
                 "bulktext1_t","bananas and milk",
                 "bulktext2_t","jungle time is fun time",
                 "sorttext_t","Y");
         docList.add(doc);
         doc= buildTextDoc("tx3","Money Dogument","This is a tech dogument for hounds, mutts, dogs and horses",
                 "bulktext1_t","bones and blood",
                 "bulktext2_t","Please take me for a walk",
                 "sorttext_t","X");
         docList.add(doc);
         EmbeddedSolrServer server = getServer();
		UpdateResponse ur = server.add(docList);
		Assert.assertEquals(0, ur.getStatus());
		server.shutdown();
		
	}
	
	@Test
	public void test2SpaceTokenisedTextSearch() throws SolrServerException{
		EmbeddedSolrServer server = getServer();
		SolrQuery query = new SolrQuery();
	    query.setQuery("title:Document");
	    QueryResponse rsp= server.query( query );
	    System.out.println ( rsp.toString() );
	    //Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(2, rsp.getResults().size());
	    
	    query = new SolrQuery();
	    query.setQuery("title:Do*");
	    rsp= server.query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertEquals(3, rsp.getResults().size());
	    
	    query = new SolrQuery();
	    query.setQuery("title:do*");  //Case insensitive all match
	    rsp= server.query( query );
	    Assert.assertEquals(3, rsp.getResults().size());
	    server.shutdown();
	}
	
	@Test
	public void test3SearchResultOrdering() throws SolrServerException{
		EmbeddedSolrServer server = getServer();
		SolrQuery query = new SolrQuery();
	    query.setQuery("title:*");
	    query.addOrUpdateSort("sorttext_t", ORDER.asc);
	    QueryResponse rsp= server.query( query );
	    //System.out.println ( rsp.toString() );
	    Assert.assertEquals(3, rsp.getResults().size());
	    for(SolrDocument doc : rsp.getResults()){
	    	System.out.println( doc.getFieldValue("title") );
	    }
	    
	    query = new SolrQuery();
	    query.setQuery("title:*");
	    query.addOrUpdateSort("titlesort_t", ORDER.asc);
	    rsp= server.query( query );
	    //System.out.println ( rsp.toString() );
	    Assert.assertEquals(3, rsp.getResults().size());
	    for(SolrDocument doc : rsp.getResults()){
	    	System.out.println( doc.getFieldValue("title") );
	    }
	    server.shutdown();
	}
	
	@Test
	public void test4CatchAllObscure() throws SolrServerException{
		SolrQuery query = new SolrQuery();
	    query.setQuery("text:jungle");
	    query.addOrUpdateSort("sorttext_t", ORDER.asc);
	    EmbeddedSolrServer server = getServer();
	    QueryResponse rsp= server.query( query );
	    Assert.assertEquals(1, rsp.getResults().size());
	    server.shutdown();
	}
	
	@Test //cleanUp
	public void test5RemoveDocsCreated(){
		EmbeddedSolrServer server = getServer();
		try {
			server.deleteById(Arrays.asList("tx1","tx2","tx3"));
			server.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			server.shutdown();
		}
	}
	
	
	
}
