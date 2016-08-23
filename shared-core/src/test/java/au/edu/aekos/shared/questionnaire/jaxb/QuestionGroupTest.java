package au.edu.aekos.shared.questionnaire.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class QuestionGroupTest {

	/**
	 * Is the MultipleQuestionGroup returned when it exists?
	 */
	@Test
	public void testGetChildMultipleQuestionGroupById01() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    QuestionnaireConfig qc = (QuestionnaireConfig) un.unmarshal( new File( "src/test/resources/QuestionGroupTest_testFindMultipleQuestionGroupById01.xml" ) );
		QuestionGroup objectUnderTest = qc.getQuestionGroupMap().get("15");
		MultipleQuestionGroup result = objectUnderTest.getChildMultipleQuestionGroupById("15.1");
		assertEquals("15.1", result.getId());
		assertEquals(2, result.size());
	}
	
	/**
	 * Is the null returned when no MultipleQuestionGroup exists?
	 */
	@Test
	public void testGetChildMultipleQuestionGroupById02() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    QuestionnaireConfig qc = (QuestionnaireConfig) un.unmarshal( new File( "src/test/resources/QuestionGroupTest_testFindMultipleQuestionGroupById01.xml" ) );
		QuestionGroup objectUnderTest = qc.getQuestionGroupMap().get("15");
		String nonExistantId = "666";
		MultipleQuestionGroup result = objectUnderTest.getChildMultipleQuestionGroupById(nonExistantId);
		assertNull(result);
	}
}
