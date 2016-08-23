package au.edu.aekos.shared.solr;

import java.io.IOException;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


//Lets try out the solr4 spatial features, I`ve included spatial4j and JTS
//in the project classpath, for prod we`ll need to open up the solr war and put this libraries
//in the lib directory.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Solr4SpatialTest {

	public SolrServer getEmbeddedServer(){
		String indexLocation = "embedded-4.5/solr";
		System.setProperty("solr.solr.home",indexLocation );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(indexLocation);
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	private SolrServer server = null;
	
	@Before
	public void initServer(){
		server = getEmbeddedServer();
	}
	
	@After
	public void shutdownServer(){
		if(server != null){
			server.shutdown();
		}
		server = null;
	}
	
	private SolrServer getServer(){
		return server;
	}
	
//geo , geo_mv
	@Test
	public void test1LoadSomeDocsWithSpatialFeatures() throws SolrServerException, IOException{
		SolrInputDocument doc = new SolrInputDocument();
		//Add some basic points specified lon , lat
	    doc.addField( "id", "geo1");
	    doc.addField( "geo", "136.5 -35.0");
	    doc.addField("geo_mv", "136.9 -34.0");
	    doc.addField("geo_mv", "136.75 -34.0");
		UpdateResponse ur = getServer().add(doc);
		Assert.assertEquals(0, ur.getStatus());
		//Try add some wkt polygons
		doc = new SolrInputDocument();
		doc.addField( "id", "geo2");
	    doc.addField( "geo", "POLYGON((131.0 -30.0,131.0 -35.0,133.0 -35.0,133.0 -31.0,131.0 -30.0))");
		ur = getServer().add(doc);
		Assert.assertEquals(0, ur.getStatus());
		
		doc = new SolrInputDocument();
		doc.addField( "id", "geo3");
	    doc.addField( "geo", "POLYGON((170.0 -60.0,170.0 -60.1,170.1 -60.1,170.1 -60.0,170.0 -60.0))");
		ur = getServer().add(doc);
		Assert.assertEquals(0, ur.getStatus());
		
		doc = new SolrInputDocument();
		//Add some basic points specified lon , lat
	    doc.addField( "id", "geo77");
	    doc.addField("geo_mv", "POINT(136.9 -34.0)");
	    doc.addField("geo_mv", "POINT(136.75 -34.0)");
		ur = getServer().add(doc);
		Assert.assertEquals(0, ur.getStatus());
		getServer().commit();
	}
	
	//fq={!geofilt pt=45.15,-93.85 sfield=geo d=5}
	@Test
	public void test2GeofiltDistanceQuery() throws SolrServerException{
		//Should pick up geo3
		SolrQuery query = new SolrQuery();
	    query.setQuery("{!geofilt pt=-60.05,170.05 sfield=geo d=1}");
	    QueryResponse rsp= getServer().query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(1, rsp.getResults().size());
	    //Should pick up nothing
	    query = new SolrQuery();
	    query.setQuery("{!geofilt pt=-61.05,170.05 sfield=geo d=1}");
	    rsp= getServer().query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertEquals(0, rsp.getResults().size());
	}
	

    //geo:[45,-94 TO 46,-93] 
	@Test
	public void test3LatLonBBOXRangeQuery() throws SolrServerException{
		//1 result
		SolrQuery query = new SolrQuery();
	    query.setQuery("geo:[-61.0,169.5 TO -59.0,170.5]");
	    QueryResponse rsp= getServer().query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(1, rsp.getResults().size());
		
	    //nothing
	    query = new SolrQuery();
	    query.setQuery("geo:[-62.0,169.5 TO -61.0,170.5]");
	    rsp= getServer().query( query );
	    Assert.assertEquals(0, rsp.getResults().size());
	}


	@Test
	public void test4Intersects() throws SolrServerException{
		SolrQuery query = new SolrQuery();
	    query.setQuery("geo:\"Intersects(POLYGON((169.0 -60.0,169.0 -60.1,170.05 -60.1,170.05 -60.0,169.0 -60.0)))\"");
	    QueryResponse rsp= getServer().query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(1, rsp.getResults().size());
		
	    //And lets do a bbox style intersects
	    query = new SolrQuery();
	    query.setQuery("geo:\"Intersects(169.0 -60.1 170.5 -60.0)\"");
	    rsp= getServer().query( query );
	    //System.out.println ( rsp.toString() );
	    Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(1, rsp.getResults().size());
		
	    //And again for both, but this time should find nothing
	    query = new SolrQuery();
	    query.setQuery("geo:\"Intersects(POLYGON((169.0 -50.0,169.0 -50.1,170.05 -50.1,170.05 -50.0,169.0 -50.0)))\"");
	    rsp= getServer().query( query );
	    Assert.assertEquals(0, rsp.getResults().size());
		
	    //And lets do a bbox style intersects
	    query = new SolrQuery();
	    query.setQuery("geo:\"Intersects(168.0 -60.1 169.0 -60.0)\"");
	    rsp= getServer().query( query );
	    //System.out.println ( rsp.toString() );
	    Assert.assertFalse(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(0, rsp.getResults().size());
	}
	
	@Test
	public void test5IsWithin() throws SolrServerException{
		SolrQuery query = new SolrQuery();
	    query.setQuery("geo:\"IsWithin(POLYGON((169.0 -59.0,169.0 -61.0,172.05 -61.0,172.05 -59.0,169.0 -59.0)))\"");
	    QueryResponse rsp= getServer().query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(1, rsp.getResults().size());
	    
	    //And now a fail  POLYGON((170.0 -60.0,170.0 -60.1,170.1 -60.1,170.1 -60.0,170.0 -60.0))
	    query = new SolrQuery();
	    query.setQuery("geo:\"IsWithin(POLYGON((170.05 -60.0,170.05 -60.1,170.1 -60.1,170.1 -60.0,170.05 -60.0)))\"");
	    rsp= getServer().query( query );
	    Assert.assertEquals(0, rsp.getResults().size());
		
	}
	
	@Test
	public void test6Contains() throws SolrServerException{
		SolrQuery query = new SolrQuery();
	    query.setQuery("geo:\"Contains(POLYGON((170.01 -60.01,170.01 -60.09,170.09 -60.09,170.09 -60.01,170.01 -60.01)))\"");
	    QueryResponse rsp= getServer().query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertTrue(rsp.toString().contains("id=geo3"));
	    Assert.assertEquals(1, rsp.getResults().size());
	    
	    //And now a fail  POLYGON((170.0 -60.0,170.0 -60.1,170.1 -60.1,170.1 -60.0,170.0 -60.0))
	    query = new SolrQuery();
	    query.setQuery("geo:\"Contains(POLYGON((169.0 -60.0,170.05 -60.1,170.1 -60.1,170.1 -60.0,169.0 -60.0))))\"");
	    rsp= getServer().query( query );
	    Assert.assertEquals(0, rsp.getResults().size());
		
	}
	
	@Test
	public void test7CleanUp() throws SolrServerException, IOException{
		getServer().deleteById(Arrays.asList("geo1","geo2","geo3","geo77"));
		getServer().commit();
	}
	
}
