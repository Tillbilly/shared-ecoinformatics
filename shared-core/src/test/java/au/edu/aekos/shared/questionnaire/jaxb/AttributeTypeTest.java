package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;

import org.junit.Test;

public class AttributeTypeTest {

	@Test
	public void testQuestionResponseTypes() throws JAXBException{
		
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/questionnaire_responseType.xml" ) );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        
        for(Object ob : config.getItems().getEntryList() ){
        	if(ob instanceof Question){
        		Question q = (Question) ob;
        		Assert.assertTrue(q.getResponseMandatory());
        		Assert.assertNotNull(q.getResponseType());
        	}
        	
        }
        
        
		
	}
	
}
