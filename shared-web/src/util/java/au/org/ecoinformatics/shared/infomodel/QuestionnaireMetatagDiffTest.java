package au.org.ecoinformatics.shared.infomodel;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;

import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;


/**
 * This test util compares metatags / questions in the latest questionnaire in the questionnaire project,
 * and the current questionnaire in this project, to aid evolution config efforts.
 * 
 * Ideally run this test BEFORE copying the questionnaire over.
 * 
 * @author btill
 */
public class QuestionnaireMetatagDiffTest {
	
	@Test
	public void testDiffQuestionnaires() throws JAXBException{
		File questionnaireXml = new File("src/main/resources/sharedQuestionnaire.xml");
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal(questionnaireXml );
        QuestionnaireConfig configExisting = (QuestionnaireConfig) o;
        File questionnaireXmlNew = new File("../questionnaire/sharedQuestionnaire.xml");
	    Object o2 = un.unmarshal(questionnaireXmlNew );
        QuestionnaireConfig configNew = (QuestionnaireConfig) o2;
        Assert.assertNotNull(configNew);
        Map<String,String> existingMetatagMap = configExisting.getMetatagToQuestionIdMap();
        Map<String,String> newMetatagMap = configNew.getMetatagToQuestionIdMap();
        System.out.println("Evolution checking ***********");
        for(String newMeta : newMetatagMap.keySet()){
        	if( ! existingMetatagMap.containsKey(newMeta)){
        		System.out.println("Metatag " + newMeta + " for Q " + newMetatagMap.get(newMeta) + " does not exist in current production questionnaire");
        	}
        }
        System.out.println("Retro checking ***********");
        for(String meta : existingMetatagMap.keySet()){
        	if(! newMetatagMap.containsKey(meta)){
        		System.out.println("Metatag " + meta + " for Q " + existingMetatagMap.get(meta) + " does not exist in new questionnaire");
        	}
        }
	}
	
	

}
