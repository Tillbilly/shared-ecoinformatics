package au.edu.aekos.shared.submissionIndex;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SpeciesAggregateCopyTest extends ParentEmbeddedServerTest { 
	
	@Override
	public String getIndexLocation() {
		return "embedded-4.5/solr";
	}
	
	@Test
	public void test1CreateSomeDocs() throws SolrServerException, IOException{
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField("id", "69");
	    doc1.addField("studySpeciesFlora_ftxt","study species flora");
	    doc1.addField("studySpeciesFauna_ftxt","study species fauna");
	    doc1.addField("studySpeciesFloraCommon_ftxt","study species flora common");
	    doc1.addField("studySpeciesFaunaCommon_ftxt","study species fauna common");
	  
	    server.add(doc1);
	    server.commit();
	    
	}
	
	@Test
	public void test2CheckDocContainsAllExpectedFields() throws SolrServerException{
		SolrQuery query = new SolrQuery();
		query.setQuery("id:69");
		QueryResponse response = server.query(query);
		
		Assert.assertEquals(1, response.getResults().size() );
		SolrDocument doc = response.getResults().get(0);
		Set<String> fieldNames = new HashSet<String>();
		for(String s :  doc.getFieldNames()){
			System.out.println(s);
			fieldNames.add(s);
		}
		Assert.assertTrue(fieldNames.contains("studySpecies_ftxt"));
		
	/*	studySpeciesFlora_ftxt
		studySpecies_ftxt
		studySpeciesFlora_facet_ss
		studySpeciesFauna_ftxt
		studySpeciesFauna_facet_ss
		studySpeciesFloraCommon_ftxt
		studySpeciesCommon_ftxt
		studySpeciesFloraCommon_facet_ss
		studySpeciesFaunaCommon_ftxt
		studySpeciesFaunaCommon_facet_ss */
		
		
		
		
	}
	
	
	
	
	@Test
	public void test3CleanUp() throws SolrServerException{
		try {
			server.deleteById(Arrays.asList("69"));
			server.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
