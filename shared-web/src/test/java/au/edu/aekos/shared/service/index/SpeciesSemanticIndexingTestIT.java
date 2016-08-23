package au.edu.aekos.shared.service.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.ControlledVocabularyServiceImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:indexwriter-context.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
@Transactional
public class SpeciesSemanticIndexingTestIT {

	@Autowired
	SpeciesTermIndexSemanticExpander expander;
	
	@Autowired @Qualifier("floraCommonSpeciesSemanticWriter")
	SemanticCommonSpeciesNameWriter floraSemanticCommonSpeciesNameWriter;
	
	@Autowired @Qualifier("faunaCommonWriter")
	MultiWriter faunaMultiWriter;
	
	@Test
	public void testGetExpandedTermsForSpeciesName_atriplex(){
		Set<String> termSet = expander.getExpandedTermsForSpeciesName("atriplex", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME );
		Assert.assertNotNull(termSet);
		Iterator<String> iter = termSet.iterator();
		Assert.assertTrue(iter.hasNext());
		String term = iter.next();
		Assert.assertEquals("Atriplex sp.", term);
	}
	
	@Test
	public void testGetExpandedTermsForSpeciesName_atriplexVar(){
		Set<String> termSet = expander.getExpandedTermsForSpeciesName("atriplex var", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME );
		Assert.assertNotNull(termSet);
	    Assert.assertTrue(termSet.contains("Atriplex sp."));	
	}
	
	@Test
	public void testFloraSemanticCommonSpeciesNameWriter() throws IOException{
		//What we'd like to do is fuzzy match a common species name, get the scientific name, match that to the scientific name on the taxa vocab,
		//then get the sp.
		SubmissionIndexField sif = new SubmissionIndexField();
		sif.setSpeciesIndexFieldName("my_test_txt");
		SolrInputDocument doc = new SolrInputDocument();
		floraSemanticCommonSpeciesNameWriter.performTermIndexing("Whitebark", sif, doc);
		System.out.println(doc.toString());
		List<String> matches = Arrays.asList("Eucalyptus apodophylla","Eucalyptus sp.","Endiandra compressa","Endiandra sp.");
		for(Object value : doc.getFieldValues("my_test_txt") ){
			String str = (String) value;
			Assert.assertTrue(matches.contains(str));
		}
		Assert.assertEquals(4, doc.getFieldValues("my_test_txt").size() );
		
		doc = new SolrInputDocument();
		floraSemanticCommonSpeciesNameWriter.performTermIndexing("Whytebark", sif, doc);
		System.out.println(doc.toString());
		for(Object value : doc.getFieldValues("my_test_txt") ){
			String str = (String) value;
			Assert.assertTrue(matches.contains(str));
		}
		Assert.assertEquals(4, doc.getFieldValues("my_test_txt").size() );
	}
	
	@Test
	public void testFaunaMultiWriter(){
		SubmissionAnswer sa = new SubmissionAnswer();
		sa.setResponseType(ResponseType.MULTISELECT_CONTROLLED_VOCAB);
		sa.setQuestionId("1");
		
		SubmissionAnswer childAnswer1 = new SubmissionAnswer();
		childAnswer1.setResponseType(ResponseType.CONTROLLED_VOCAB);
		childAnswer1.setResponse("Red Fox");
		sa.getMultiselectAnswerList().add(childAnswer1);
		
		Map<String, String> metaTagToQuestionIdMap = new HashMap<String, String>();
		metaTagToQuestionIdMap.put("SHD.faunaCommonName", "1");
		
		Map<String, SubmissionAnswer> questionIdToSubmissionAnswerMap = new HashMap<String, SubmissionAnswer>();
		questionIdToSubmissionAnswerMap.put("1", sa);
		
		MetaInfoExtractor mie = new MetaInfoExtractor(null,null, metaTagToQuestionIdMap, questionIdToSubmissionAnswerMap, null, null, null, false);
		SolrInputDocument doc = new SolrInputDocument();
		SubmissionIndexField indexField = new SubmissionIndexField();
		indexField.setSharedTag("SHD.faunaCommonName");
		indexField.setIndexFieldName("studySpeciesFaunaCommon_txt");
		indexField.setSpeciesIndexFieldName("studySpeciesFauna_txt");
		
		faunaMultiWriter.writeValueToDocument(doc, indexField, mie);
		
		Assert.assertNotNull( doc.toString() );
		System.out.println(doc.toString());
		
		SubmissionIndexField indexField2 = new SubmissionIndexField();
		indexField2.setSharedTag("SHD.faunaCommonName");
		indexField2.setIndexFieldName("species_txt");
		indexField2.setSpeciesIndexFieldName("species_txt");
		
        faunaMultiWriter.writeValueToDocument(doc, indexField2, mie);
		
		Assert.assertNotNull( doc.toString() );
		System.out.println(doc.toString());
	}
}
