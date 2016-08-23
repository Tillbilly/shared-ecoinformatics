package au.edu.aekos.shared.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResponseFieldRestrictionTest {
	
	private static Logger logger = LoggerFactory.getLogger(ResponseFieldRestrictionTest.class);
	
	public EmbeddedSolrServer getServer(){
		String indexLocation = "embedded-4.5/solr-orig";
		System.setProperty("solr.solr.home",indexLocation );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(indexLocation);
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	@Test
	public void test1() throws SolrServerException, IOException, InterruptedException{
		EmbeddedSolrServer server = getServer();
		//Load up any old docs to do the test
		server.add(buildTextDoc("reduce1","reduce1 test","reduce1 description","tester_t","tester","tester2_t","tester","groupMe_t","aaa"));
		server.add(buildTextDoc("reduce2","reduce2 test","reduce2 description","tester_t","tester 2","tester2_t","tester 2","groupMe_t","bbb"));
		server.add(buildTextDoc("reduce3","reduce3 test","reduce3 description","tester_t","tester 2","tester2_t","tester 2","groupMe_t","aaa"));
		server.commit(true,true,true);
		server.shutdown();
		//Thread.sleep(5000l);
	}
	
	@Test
	public void test2() throws SolrServerException, IOException, InterruptedException{
		EmbeddedSolrServer server = getServer();
		SolrQuery query = new SolrQuery();
	    query.setQuery("id:reduce1");
	    query.setFields("id","tester2_t");
	    //query.setGetFieldStatistics(field)
	    QueryResponse rsp= server.query( query );
	    System.out.println ( rsp.toString() );
	    //assertTrue(rsp.toString().contains("id=geo3"));
	    assertEquals(1, rsp.getResults().size() );
	    assertTrue( rsp.getResults().get(0).containsKey("id") );
	    assertTrue( rsp.getResults().get(0).containsKey("tester2_t") );
	    assertFalse( rsp.getResults().get(0).containsKey("tester_t") );
	    assertFalse( rsp.getResults().get(0).containsKey("title") );
	    server.shutdown();
	}

	/**
	 * Can we submit a query to find all docs that have a 'tester_t' field
	 * and group them based on the value in the 'groupMe_t' field?
	 */
	@Test
	public void test3() throws Exception{
		EmbeddedSolrServer server = getServer();
		SolrQuery query = new SolrQuery();
	    query.setQuery("tester_t:*");
	    query.setParam("group", true);
	    query.setParam("group.field", "groupMe_t");
	    QueryResponse rsp = server.query(query);
	    logger.info(rsp.toString());
	    List<GroupCommand> grouped = rsp.getGroupResponse().getValues();
	    List<Group> groupsForGroupMe_tField = grouped.get(0).getValues();
	    Group aaaGroup = groupsForGroupMe_tField.get(0);
		assertEquals(2, aaaGroup.getResult().getNumFound());
		Group bbbGroup = groupsForGroupMe_tField.get(1);
		assertEquals(1, bbbGroup.getResult().getNumFound());
	    server.shutdown();
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
}
