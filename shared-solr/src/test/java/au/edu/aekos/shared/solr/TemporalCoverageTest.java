package au.edu.aekos.shared.solr;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;
import org.apache.solr.core.CoreContainer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TemporalCoverageTest {
	
	public SolrServer getServer(){
		String indexLocation = "embedded-4.5/solr";
		System.setProperty("solr.solr.home",indexLocation );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(indexLocation);
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	
	//Test temporal range query
	@Test
	public void test1LoadSomeDateRangeDocs() throws SolrServerException, IOException, ParseException{
		SolrServer server = getServer();
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", "dateRangeTest1", 1.0f );
	    doc.addField("startDate_tdt", sdf.parse("01/12/2010"));
	    doc.addField("endDate_tdt", sdf.parse("01/12/2012"));
		UpdateResponse ur = server.add(doc);
		Assert.assertEquals(0,ur.getStatus() );
		
		SolrInputDocument doc2 = new SolrInputDocument();
	    doc2.addField( "id", "dateRangeTest2", 1.0f );
	    doc2.addField("startDate_tdt", sdf.parse("01/11/2011"));
	    doc2.addField("endDate_tdt", sdf.parse("01/12/2013"));
		ur = server.add(doc2);
		Assert.assertEquals(0,ur.getStatus() );
		
		SolrInputDocument doc3 = new SolrInputDocument();
	    doc3.addField( "id", "dateRangeTest3", 1.0f );
	    doc3.addField("startDate_tdt", sdf.parse("01/11/2008"));
	    doc3.addField("endDate_tdt", sdf.parse("01/12/2009"));
		ur = server.add(doc3);
		Assert.assertEquals(0,ur.getStatus() );
		
		SolrInputDocument doc4 = new SolrInputDocument();
	    doc4.addField( "id", "dateRangeTest4", 1.0f );
	    doc4.addField("startDate_tdt", sdf.parse("02/12/2009"));
	    doc4.addField("endDate_tdt", sdf.parse("01/12/2014"));
		ur = server.add(doc4);
		Assert.assertEquals(0,ur.getStatus() );
		
		server.shutdown(); //Removes the query lock on the embedded index, required.
	}
	
	//Round date funtions
	public Date roundDayStart(Date startDate){
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(startDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public Date roundDayEnd(Date endDate){
		Calendar c = GregorianCalendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	public String constructDateRangeQuery(String startDateField,
			                              String endDateField,
			                              Date startDateRange,
			                              Date endDateRange){
		String query = "";
		if(endDateRange != null){
			query += startDateField + ":[* TO "+ DateUtil.getThreadLocalDateFormat().format(endDateRange) + "]" ;
		    if(startDateRange != null){
		    	query += " AND ";
		    }
		}
		if(startDateRange != null){
			query += endDateField + ":["+ DateUtil.getThreadLocalDateFormat().format(startDateRange) + " TO *]" ;
		}
		return query;
	}
	
	
	
	@Test
	public void test2PerformSomeDateRangeQueries() throws ParseException, SolrServerException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String query = constructDateRangeQuery("startDate_tdt","endDate_tdt", sdf.parse("01/01/2011"), sdf.parse("02/02/2012") );
		System.out.println(query);
		//Retrieve all docs
		query = constructDateRangeQuery("startDate_tdt","endDate_tdt", sdf.parse("01/01/2007"), sdf.parse("02/02/2015") );
		SolrServer server = getServer();
		SolrQuery sq = new SolrQuery();
		sq.setQuery(query);
		QueryResponse rsp = server.query( sq );
		Assert.assertEquals(4, rsp.getResults().size());
		
		query = constructDateRangeQuery("startDate_tdt","endDate_tdt", sdf.parse("01/01/2007"), sdf.parse("01/12/2008") );
		sq.setQuery(query);
		rsp = server.query( sq );
		Assert.assertEquals(1, rsp.getResults().size());
		Assert.assertEquals("dateRangeTest3", (String) rsp.getResults().get(0).getFieldValue("id"));
		
		query = constructDateRangeQuery("startDate_tdt","endDate_tdt", sdf.parse("01/11/2013"),null );
		sq.setQuery(query);
		rsp = server.query( sq );
		Assert.assertEquals(2, rsp.getResults().size());
		List<String> expectedResults = Arrays.asList("dateRangeTest2","dateRangeTest4");
		for(SolrDocument doc : rsp.getResults()){
			Assert.assertTrue(expectedResults.contains((String)doc.getFieldValue("id")));
		}
		
		server.shutdown();
		
	}
	
	
	

}
