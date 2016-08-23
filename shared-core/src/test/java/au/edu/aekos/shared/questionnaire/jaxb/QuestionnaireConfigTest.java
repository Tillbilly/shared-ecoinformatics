package au.edu.aekos.shared.questionnaire.jaxb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class QuestionnaireConfigTest {

	/**
	 * Is a question found when one exists?
	 */
	@Test
	public void testGetQuestionById01() {
	    QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionById01.xml");
	    Question result = qc.getQuestionById("3.2");
	    assertEquals("3.2", result.getId());
	}
	
	/**
	 * Is null returned when no question exists?
	 */
	@Test
	public void testGetQuestionById02() {
		QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionById01.xml");
	    Question result = qc.getQuestionById("3.notThere");
	    assertNull(result);
	}
	
	/**
	 * Is a metatag found when the question ID exists?
	 */
	@Test
	public void testGetMetatagByQuestionById01() {
		QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionById01.xml");
	    String result = qc.getMetatagByQuestionId("3.3");
	    assertEquals("SHD.authorSurname", result);
	}
	
	/**
	 * Is the correct question ID - metatag map built?
	 */
	@Test
	public void testGetQuestionIdToMetatagMap01() {
		QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionById01.xml");
	    Map<String, String> result = qc.getQuestionIdToMetatagMap();
	    assertThat(result.size(), is(2));
	    assertThat(result.get("3.2"), is("SHD.authorGivenNames"));
	    assertThat(result.get("3.3"), is("SHD.authorSurname"));
	}
	
	/**
	 * Can we find an ancestor when it exists?
	 */
	@Test
	public void testGetQuestionAncestor01() {
		QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionAnscestor01-questionnaire.xml");
	    MultipleQuestionGroup result = qc.getQuestionAncestor("12.1", MultipleQuestionGroup.class);
	    assertEquals("12", result.getId());
	}
	
	/**
	 * Is null returned when no ancestor exists?
	 */
	@Test
	public void testGetQuestionAncestor02() {
		QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionAnscestor01-questionnaire.xml");
	    MultipleQuestionGroup result = qc.getQuestionAncestor("666.1", MultipleQuestionGroup.class);
	    assertNull(result);
	}
	
	/**
	 * Can we survive a null question ID?
	 */
	@Test
	public void testGetQuestionAncestor03() {
		QuestionnaireConfig qc = getConfig("QuestionnaireConfigTest_testGetQuestionAnscestor01-questionnaire.xml");
	    MultipleQuestionGroup result = qc.getQuestionAncestor(null, MultipleQuestionGroup.class);
	    assertNull(result);
	}

	private QuestionnaireConfig getConfig(String filename) {
		try {
			JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
			Unmarshaller un = context.createUnmarshaller();
			return (QuestionnaireConfig) un.unmarshal( new File("src/test/resources/" + filename) );
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
