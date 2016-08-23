package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import org.junit.Assert;

public class TestTextBoxAndResponseInputClassJAXB {

	@Test
	public void testConfigs() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/qTextBoxAndResponseInputClass.xml" ) );
	    Assert.assertTrue(true);	
	    QuestionnaireConfig config = (QuestionnaireConfig) o;
	    Assert.assertTrue(true);
	}
	
	
	
}
