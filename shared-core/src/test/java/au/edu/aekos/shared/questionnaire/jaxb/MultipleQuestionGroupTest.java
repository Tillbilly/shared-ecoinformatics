package au.edu.aekos.shared.questionnaire.jaxb;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class MultipleQuestionGroupTest {

	@Test
	public void testConfigs() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    QuestionnaireConfig qc = (QuestionnaireConfig) un.unmarshal( new File( "src/test/resources/multiple_question_group.xml" ) );
	    assertEquals(3, qc.getMultipleQuestionGroupById("2").size());
	}
	
	/**
	 * Is the parent correctly wired in
	 */
	@Test
	public void testParentRef01() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    QuestionnaireConfig qc = (QuestionnaireConfig) un.unmarshal( new File( "src/test/resources/QuestionGroupTest_testParentRef01.xml" ) );
	    MultipleQuestionGroup mqg = qc.getQuestionGroupMap().get("3").getChildMultipleQuestionGroupById("3.1");
	    assertEquals("3", mqg.findAncestorOfType(QuestionGroup.class).getId());
	    assertEquals(qc, mqg.findAncestorOfType(QuestionnaireConfig.class));
	}
}
