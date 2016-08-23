package au.edu.aekos.shared.solr;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;
import org.apache.solr.core.CoreContainer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import au.edu.aekos.shared.solr.util.SolrDateUtils;


/**********************************************************************
 * A test used to try out different field types in the schema, 
 * and different ways of constructing searches.
 *
 * Tests now run off an Embedded server, the shutdown is required in each test
 * to ensure the index write lock is removed after a test is run.
 *
 ***********************************************************************/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SolrSchemaDynamicFieldTest {
	
	public SolrServer getServer(){
		
		String indexLocation = "embedded-4.5/solr";
		System.setProperty("solr.solr.home",indexLocation );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(indexLocation);
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	/*
	<field name="id" type="string" indexed="true" stored="true" required="true" multiValued="false" />
    <field name="submissionid" type="string" indexed="true" stored="false" multiValued="false" /> 
	<field name="title" type="text_general" indexed="true" stored="true" multiValued="false"/>
	<field name="description" type="text_general" indexed="true" stored="true"/>
	<dynamicField name="*_i"  type="int"    indexed="true"  stored="true"/>
    <dynamicField name="*_is" type="int"    indexed="true"  stored="true"  multiValued="true"/>
    <dynamicField name="*_s"  type="string"  indexed="true"  stored="true" />
    <dynamicField name="*_ss" type="string"  indexed="true"  stored="true" multiValued="true"/>
    <dynamicField name="*_l"  type="long"   indexed="true"  stored="true"/>
    <dynamicField name="*_ls" type="long"   indexed="true"  stored="true"  multiValued="true"/>
    <dynamicField name="*_t"  type="text_general"    indexed="true"  stored="true"/>
    <dynamicField name="*_txt" type="text_general"   indexed="true"  stored="true" multiValued="true"/>
    <dynamicField name="*_en"  type="text_en"    indexed="true"  stored="true" multiValued="true"/>
    <dynamicField name="*_b"  type="boolean" indexed="true" stored="true"/>
    <dynamicField name="*_bs" type="boolean" indexed="true" stored="true"  multiValued="true"/>
    <dynamicField name="*_f"  type="float"  indexed="true"  stored="true"/>
    <dynamicField name="*_fs" type="float"  indexed="true"  stored="true"  multiValued="true"/>
    <dynamicField name="*_d"  type="double" indexed="true"  stored="true"/>
    <dynamicField name="*_ds" type="double" indexed="true"  stored="true"  multiValued="true"/>
    <!-- Type used to index the lat and lon components for the "location" FieldType -->
    <dynamicField name="*_coordinate"  type="tdouble" indexed="true"  stored="false" />
    <dynamicField name="*_dt"  type="date"    indexed="true"  stored="true"/>
    <dynamicField name="*_dts" type="date"    indexed="true"  stored="true" multiValued="true"/>
    <dynamicField name="*_p"  type="location" indexed="true" stored="true"/>
    <!-- some trie-coded dynamic fields for faster range queries -->
    <dynamicField name="*_ti" type="tint"    indexed="true"  stored="true"/>
    <dynamicField name="*_tl" type="tlong"   indexed="true"  stored="true"/>
    <dynamicField name="*_tf" type="tfloat"  indexed="true"  stored="true"/>
    <dynamicField name="*_td" type="tdouble" indexed="true"  stored="true"/>
    <dynamicField name="*_tdt" type="tdate"  indexed="true"  stored="true"/>
    <dynamicField name="*_pi"  type="pint"    indexed="true"  stored="true"/>
    <dynamicField name="*_c"   type="currency" indexed="true"  stored="true"/>
    <dynamicField name="ignored_*" type="ignored" multiValued="true"/>
    <dynamicField name="attr_*" type="text_general" indexed="true" stored="true" multiValued="true"/>
    <dynamicField name="random_*" type="random" />
	*/
	
	@Test 
	public void test1AddFieldsNotInSchema() throws SolrServerException{
		SolrServer server = getServer();
		
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", "id1", 1.0f );
	    doc.addField( "name", "doc1", 1.0f );
	    doc.addField( "price", 10 );
	    Date f = new Date();
	    doc.addField("testDate", f);
	    try {
			UpdateResponse ur = server.add(doc);
			System.out.println(ur.getStatus() );
	    }catch(SolrException e){
	        server.shutdown();
	        return;
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    server.shutdown();  //Removes the writelock on the index.
	    Assert.fail("Should throw an exception about an undefined field name or something");
	}
	
	@Test
	public void test2AddFieldsNotInSchema2(){
		SolrServer server = getServer();
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", "id1", 1.0f );
	    doc.addField( "name", "doc1", 1.0f );
	    doc.addField( "price", 10 );
	    Date f = new Date();
	    doc.addField("testDate", f);
	    try {
			UpdateResponse ur = server.add(doc);
			System.out.println(ur.getStatus() );
	    }catch(SolrServerException e){
	        e.printStackTrace();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(SolrException e){
			e.printStackTrace();
			server.shutdown();
			Assert.assertTrue(e.getMessage().contains("unknown field 'name'"));
			return;
		}
	    server.shutdown();
	    Assert.fail("Should throw an exception about an undefined field name or something");
	}
	
	@Test
	public void test3AddDynamicDateField() throws SolrServerException, IOException{
		SolrServer server = getServer();
		
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", "id_date", 1.0f );
	    Date f = new Date();
	    doc.addField("testDate_dt", f);
	  
		UpdateResponse ur = server.add(doc);
		Assert.assertEquals(0,ur.getStatus() );
		server.shutdown();
	}
	
	@Test
	public void test4QueryDynamicDateField() throws SolrServerException{

		SolrServer server = getServer();
		SolrQuery query = new SolrQuery();
	    //query.add("id", "id1");
	    query.setQuery("id:id_date AND testDate_dt:[* TO NOW]");
	    System.out.println(query.toString());
	    QueryResponse rsp= server.query( query );
	    System.out.println ( rsp.toString() );
	    Assert.assertEquals(1, rsp.getResults().size());
	    System.out.println(rsp.getResults().get(0).toString());
	    server.shutdown();
	}
	
	@Test
	public void test5QueryDynamicDateFieldFormats2() throws SolrServerException, IOException{
        SolrServer server = getServer();
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", "id_date_dyn", 1.0f );
	    Date d = new Date();
	    doc.addField("testDate_dt", d); //like, right now.	  
		UpdateResponse ur = server.add(doc);
		Assert.assertEquals(0,ur.getStatus() );
		server.commit();
		SolrQuery query = new SolrQuery();
	    query.setQuery("id:id_date_dyn");
	    System.out.println(query.toString());
	    QueryResponse rsp= server.query( query );
	    Assert.assertEquals(1, rsp.getResults().size());
		SolrDocument solrDoc = rsp.getResults().get(0);
		Date ddoc = (Date) solrDoc.getFieldValue("testDate_dt");
		String ddStr = SolrDateUtils.getSimpleDateFormat().format(ddoc);
		System.out.println(ddStr);
		//String rangeDateStr = SolrDateUtils.getDayDateRangeString(d);
		
		
		DateFormat df = DateUtil.getThreadLocalDateFormat();
		System.out.println("ThreadLocal " + df.format(d) );
		
		//minimise the day 
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date dayMin = c.getTime();
		
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		Date dayMax = c.getTime();
		String rangeStr = "[" + DateUtil.getThreadLocalDateFormat().format(dayMin) + " TO " + DateUtil.getThreadLocalDateFormat().format(dayMax) + "]";
		System.out.println(rangeStr);
		SolrQuery query2 = new SolrQuery();
	    query2.setQuery("id:id_date_dyn AND testDate_dt:" + rangeStr);
	    System.out.println(query2.toString());
	    QueryResponse rsp2= server.query( query2 );
	    Assert.assertEquals(1, rsp2.getResults().size());
	    System.out.println(rsp2.getResults().get(0).toString());
	    server.shutdown();
	}
	
	
}
