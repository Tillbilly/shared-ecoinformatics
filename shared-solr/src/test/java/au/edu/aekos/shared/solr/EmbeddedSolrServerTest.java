package au.edu.aekos.shared.solr;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmbeddedSolrServerTest {

	private EmbeddedSolrServer getEmbeddedServer(){
		String indexLocation = "embedded-4.5/solr-orig";
		System.setProperty("solr.solr.home",indexLocation );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(indexLocation);
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	@Test  //id, name, price
	public void atestAddSomeInitialDocs(){
		
		EmbeddedSolrServer server = getEmbeddedServer();
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField( "id", "id1", 1.0f );
	    doc1.addField( "doi", "doc1", 1.0f );
	    try {
			UpdateResponse ur = server.add(doc1);
			System.out.println(ur.getStatus() );
			NamedList<Object> nl = ur.getResponse();
			for(int x = 0; x < nl.size() ; x ++){
				String str = nl.getName(x);
				System.out.println(str);
				@SuppressWarnings("unused")
				Object o = nl.get(str);
				System.out.println(o.toString() );
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	    
	    System.out.println("debug");
		SolrInputDocument doc2 = new SolrInputDocument();
	    doc2.addField( "id", "id2", 1.0f );
	    doc2.addField( "doi", "doc2", 1.0f );
	    try {
			UpdateResponse ur = server.add(doc2);
			System.out.println(ur.getStatus() );
			NamedList<Object> nl = ur.getResponse();
			for(int x = 0; x < nl.size() ; x ++){
				String str = nl.getName(x);
				System.out.println(str);
				@SuppressWarnings("unused")
				Object o = nl.get(str);
				System.out.println(o.toString() );
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	    server.shutdown();
	}
	
	@Test
	public void testAQuery(){
		
		
		EmbeddedSolrServer server = getEmbeddedServer();
		SolrQuery query = new SolrQuery();
	    //query.add("id", "id1");
	    query.setQuery("id:id1 OR id:id2");
	    System.out.println(query.toString());
	   // query.setQuery( "*:*" );
	    //query.addSortField( "price", SolrQuery.ORDER.asc );

	    //Query the server 

	    QueryResponse rsp;
		try {
			rsp = server.query( query );
			System.out.println(rsp.getStatus() );
			SolrDocumentList docs = rsp.getResults();
			System.out.println("docs size " + docs.size());			
			for(SolrDocument d : docs){
				Map<String, Object> fieldValueMap = d.getFieldValueMap();
				for( String key : fieldValueMap.keySet() ){
					System.out.println(key);
					Object o = fieldValueMap.get(key);
					//System.out.println( o.getClass().getName() );
					System.out.println( o.toString() );
				}
				
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
			
		}
        server.shutdown();
	}
	
	@Test 
	public void testAQuery2(){
		
		
		EmbeddedSolrServer server = getEmbeddedServer();
		SolrQuery query = new SolrQuery();
	    //query.add("id", "id1");
	    query.setQuery("name:doc1");
	    System.out.println(query.toString());
	   // query.setQuery( "*:*" );
	    //query.addSortField( "price", SolrQuery.ORDER.asc );

	    //Query the server 

	    QueryResponse rsp;
		try {
			rsp = server.query( query );
			System.out.println(rsp.getStatus() );
			SolrDocumentList docs = rsp.getResults();
			System.out.println("docs size " + docs.size());			
			for(SolrDocument d : docs){
				Map<String, Object> fieldValueMap = d.getFieldValueMap();
				for( String key : fieldValueMap.keySet() ){
					System.out.println(key);
					Object o = fieldValueMap.get(key);
					//System.out.println( o.getClass().getName() );
					System.out.println( o.toString() );
				}
				
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
			
		}
        server.shutdown();
	}
	
	@Test 
	public void testAQuery3(){
		
		
		EmbeddedSolrServer server = getEmbeddedServer();
		SolrQuery query = new SolrQuery();
	    //query.add("id", "id1");
	    query.setQuery("*:*");
	    System.out.println(query.toString());
	    
	    QueryResponse rsp;
		try {
			rsp = server.query( query );
			System.out.println(rsp.getStatus() );
			SolrDocumentList docs = rsp.getResults();
			System.out.println("docs size " + docs.size());			
			for(SolrDocument d : docs){
				Map<String, Object> fieldValueMap = d.getFieldValueMap();
				for( String key : fieldValueMap.keySet() ){
					System.out.println(key);
					Object o = fieldValueMap.get(key);
					//System.out.println( o.getClass().getName() );
					System.out.println( o.toString() );
				}
				
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
			
		}
        server.shutdown();
	}
	
	
}
