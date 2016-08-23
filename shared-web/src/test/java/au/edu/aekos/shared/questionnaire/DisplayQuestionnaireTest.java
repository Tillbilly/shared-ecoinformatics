package au.edu.aekos.shared.questionnaire;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;

public class DisplayQuestionnaireTest {

	/**
	 * Can we tell when the title question has NOT been answered
	 */
	@Test
	public void testIsTitleQuestionAnswered01() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle01-questionnaire.xml", "1");
		objectUnderTest.getAllAnswers().get("1.1").setResponse("");
		boolean result = objectUnderTest.isTitleQuestionAnswered();
		assertEquals(false, result);
	}
	
	/**
	 * Can we tell when the title question has been answered
	 */
	@Test
	public void testIsTitleQuestionAnswered02() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle01-questionnaire.xml", "1");
		objectUnderTest.getAllAnswers().get("1.1").setResponse("Some Title");
		boolean result = objectUnderTest.isTitleQuestionAnswered();
		assertEquals(true, result);
	}
	
	/**
	 * Is an answer found when one exists?
	 */
	@Test
	public void testFindAnswerByQuestionId01() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle01-questionnaire.xml", "1");
		Answer result = objectUnderTest.findAnswerByQuestionId("1.1");
		assertEquals("1.1", result.getQuestionId());
	}
	
	/**
	 * Is null returned when no answer exists?
	 */
	@Test
	public void testFindAnswerByQuestionId02() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle01-questionnaire.xml", "1");
		final String nonExistantId = "2.2";
		Answer result = objectUnderTest.findAnswerByQuestionId(nonExistantId);
		assertNull(result);
	}
	
	/**
	 * Can we get the first page title?
	 */
	@Test
	public void testGetPageTitleByPageNumber01() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle02-questionnaire.xml", "1");
		int firstPageNumber = 1;
		String result = objectUnderTest.getPageTitleByPageNumber(firstPageNumber);
		assertEquals("Dataset description", result);
	}
	
	/**
	 * Can we get the second page title?
	 */
	@Test
	public void testGetPageTitleByPageNumber02() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle02-questionnaire.xml", "1");
		int secondPageNumber = 2;
		String result = objectUnderTest.getPageTitleByPageNumber(secondPageNumber);
		assertEquals("Coverage", result);
	}
	
	/**
	 * Can we survive asking for a title of a non-existent page?
	 */
	@Test
	public void testGetPageTitleByPageNumber03() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle02-questionnaire.xml", "1");
		int nonExistentPageNumber = 666;
		String result = objectUnderTest.getPageTitleByPageNumber(nonExistentPageNumber);
		assertNull(result);
	}
	
	/**
	 * Tell how many pages we have?
	 */
	@Test
	public void testGetNumberOfPages01() {
		DisplayQuestionnaire objectUnderTest = getDisplayQuestionnaire("DisplayQuestionnaireTest_testGetFirstPageTitle02-questionnaire.xml", "1");
		int result = objectUnderTest.getNumberOfPages();
		assertEquals(2, result);
	}
	
	/**
	 * Can we tell when there is submission data?
	 */
	@Test
	public void testHasSubmissionData01() {
		DisplayQuestionnaire objectUnderTest = new DisplayQuestionnaire();
		objectUnderTest.setSubmissionDataList(Arrays.asList(new SubmissionData()));
		boolean result = objectUnderTest.hasSubmissionData();
		assertTrue(result);
	}
	
	/**
	 * Can we tell when there is no submission data due to an empty list?
	 */
	@Test
	public void testHasSubmissionData02() {
		DisplayQuestionnaire objectUnderTest = new DisplayQuestionnaire();
		List<SubmissionData> emptyList = Collections.emptyList();
		objectUnderTest.setSubmissionDataList(emptyList);
		boolean result = objectUnderTest.hasSubmissionData();
		assertFalse(result);
	}
	
	/**
	 * Can we tell when there is no submission data due to a null?
	 */
	@Test
	public void testHasSubmissionData03() {
		DisplayQuestionnaire objectUnderTest = new DisplayQuestionnaire();
		objectUnderTest.setSubmissionDataList(null);
		boolean result = objectUnderTest.hasSubmissionData();
		assertFalse(result);
	}
	
	private DisplayQuestionnaire getDisplayQuestionnaire(String filename, String version) {
		try {
			JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
			Unmarshaller un = context.createUnmarshaller();
			QuestionnaireConfig config =  (QuestionnaireConfig) un.unmarshal( new File("src/test/resources/" + filename) );
			return new DisplayQuestionnaire(config);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
