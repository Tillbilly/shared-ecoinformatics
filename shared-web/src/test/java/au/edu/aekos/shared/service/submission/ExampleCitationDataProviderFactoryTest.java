package au.edu.aekos.shared.service.submission;

import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answer;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answerGroup;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answerMap;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answerOther;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.dqWithConfigAndAnswers;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.getMultipleQuestionGroupWithId;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.multipleAnswerGroup;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.citation.CitationDataProvider;
import au.edu.aekos.shared.service.quest.ControlledVocabularyServiceStub;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class ExampleCitationDataProviderFactoryTest {

	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private ExampleCitationDataProviderFactory objectUnderTest;
	
	/**
	 * Can we build a new data provider from a fully answered questionnaire?
	 */
	@Test
	public void testNewExampleCitationDataProvider01() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testNewExampleCitationDataProvider01-questionnaire.xml", "1");
		MultipleQuestionGroup legalContactOrgQuestionGroup = config.getQuestionGroupMap().get("13").getChildMultipleQuestionGroupById("13.4");
		MultipleQuestionGroup authorGroup = getMultipleQuestionGroupWithId(config, "12");
		
		Map<String, Answer> answerMap = answerMap(
			answer("3.1", "Some name"),
			answer("3.4", "v123"),
			multipleAnswerGroup(authorGroup,
				answerGroup(
					answer("12.1", "A.a."), // should preserve case
					answer("12.2", "Aaronson")
				),
				answerGroup(
					answer("12.1", "B.B"),
					answer("12.2", "Bobson")
				)
			),
			multipleAnswerGroup(legalContactOrgQuestionGroup,
				answerGroup(
					answer("13.4.1", ControlledVocabularyServiceStub.ORG_JAMES_COOK_UNIVERSITY)
				),
				answerGroup(
					answer("13.4.1", ControlledVocabularyServiceStub.ORG_UNIVERSITY_OF_ADELAIDE)
				)
			)
		);
		DisplayQuestionnaire dq = dqWithConfigAndAnswers(config, answerMap);
		CitationDataProvider result = objectUnderTest.newExampleCitationDataProvider(dq);
		assertThat(result.getAuthors(), hasItems("Aaronson, A.a.", "Bobson, B.B"));
		assertThat(result.getDatasetName(), is("Some name"));
		assertThat(result.getDatasetVersion(), is("v123"));
		assertThat(result.getLegalContactOrgs(), hasItems("James Cook University", "University of Adelaide"));
		assertThat(result.getAccessDate(), is("dd mmm yyyy, e.g., 01 Jan 2016"));
		assertThat(result.getDoi(), is("[some DOI placeholder]"));
	}

	/**
	 * Is an author list returned when the question uses a {@link MultipleQuestionGroup}
	 */
	@Test
	public void testBuildAuthorList01() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildAuthorList01-questionnaire.xml", "1");
		Map<String, String> metatagMap = config.getMetatagToQuestionIdMap();
		MultipleQuestionGroup questionGroup31 = config.getQuestionGroupMap().get("3").getChildMultipleQuestionGroupById("3.1");
		Map<String, Answer> answerMap = answerMap(
			multipleAnswerGroup(questionGroup31,
				answerGroup(
					answer("3.2", "A.A."),
					answer("3.3", "Aaronson")
				),
				answerGroup(
					answer("3.2", "B.B."),
					answer("3.3", "Bobson")
				)
			)
		);
		List<String> result = objectUnderTest.buildAuthorList(metatagMap, answerMap, config);
		assertEquals(2, result.size());
		assertTrue(result.contains("Aaronson, A.A."));
		assertTrue(result.contains("Bobson, B.B."));
	}

	/**
	 * Do we fail gracefully when the question is a TEXT type
	 */
	@Test
	public void testBuildAuthorList02() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildAuthorList02-questionnaire.xml", "1");
		try {
			HashMap<String, Answer> answerMap = new HashMap<String, Answer>();
			Answer answer = new Answer(config.getQuestionGroupMap().get("3").getChildQuestionById("3.1"));
			answer.setResponseType(ResponseType.TEXT);
			answerMap.put("3.1", answer);
			objectUnderTest.buildAuthorList(config.getMetatagToQuestionIdMap(), answerMap, config);
			fail();
		} catch (InvalidAuthorNameQuestionException e) {
			// success!
		}
	}
	
	/**
	 * Do we fail gracefully when the question is a MUTLISELECT type
	 */
	@Test
	public void testBuildAuthorList03() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildAuthorList03-questionnaire.xml", "1");
		try {
			objectUnderTest.buildAuthorList(config.getMetatagToQuestionIdMap(), new HashMap<String, Answer>(), config);
			fail();
		} catch (InvalidAuthorNameQuestionException e) {
			// success!
		}
	}
	
	/**
	 * Is an empty List returned when the question is not answered
	 */
	@Test
	public void testBuildAuthorList04() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildAuthorList01-questionnaire.xml", "1");
		Map<String, String> metatagMap = config.getMetatagToQuestionIdMap();
		MultipleQuestionGroup questionGroup31 = config.getQuestionGroupMap().get("3").getChildMultipleQuestionGroupById("3.1");
		Map<String, Answer> answerMap = answerMap(
			multipleAnswerGroup(questionGroup31,
				answerGroup(
					answer("3.2", ""),
					answer("3.3", "")
				)
			)
		);
		List<String> result = objectUnderTest.buildAuthorList(metatagMap, answerMap, config);
		assertEquals(Collections.emptyList(), result);
	}
	
	/**
	 * Are responses with a surname included and those with no surname (silently) ignored?
	 */
	@Test
	public void testBuildAuthorList05() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildAuthorList01-questionnaire.xml", "1");
		Map<String, String> metatagMap = config.getMetatagToQuestionIdMap();
		MultipleQuestionGroup questionGroup31 = config.getQuestionGroupMap().get("3").getChildMultipleQuestionGroupById("3.1");
		Map<String, Answer> answerMap = answerMap(
			multipleAnswerGroup(questionGroup31,
				answerGroup(
					answer("3.2", ""),
					answer("3.3", "Aaronson")
				),
				answerGroup(
					answer("3.2", "B.A."),
					answer("3.3", "Bobson")
				),
				answerGroup(
					answer("3.2", "c.R."),
					answer("3.3", "Thingy-Ma-Jig")
				)
			)
		);
		List<String> result = objectUnderTest.buildAuthorList(metatagMap, answerMap, config);
		assertEquals(3, result.size());
		assertTrue(result.contains("Aaronson"));
		assertTrue(result.contains("Bobson, B.A."));
		assertTrue(result.contains("Thingy-Ma-Jig, c.R."));
	}
	
	/**
	 * Is the result as expected when the question is answered and is part a multiple question group
	 */
	@Test
	public void testBuildLegalContactOrgList01() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildLegalContactOrgList01-questionnaire.xml", "1");
		MultipleQuestionGroup legalContactOrgQuestionGroup = config.getQuestionGroupMap().get("13").getChildMultipleQuestionGroupById("13.4");
		Map<String, String> metatagMap = config.getMetatagToQuestionIdMap();
		Map<String, Answer> answerMap = answerMap(
			multipleAnswerGroup(legalContactOrgQuestionGroup,
				answerGroup(
					answer("13.4.1", ControlledVocabularyServiceStub.ORG_JAMES_COOK_UNIVERSITY)
				),
				answerGroup(
					answer("13.4.1", ControlledVocabularyServiceStub.ORG_UNIVERSITY_OF_ADELAIDE)
				)
			)
		);
		List<String> result = objectUnderTest.buildLegalContactOrgList(metatagMap, answerMap, config);
		assertEquals(2, result.size());
		assertThat(result, hasItems("James Cook University", "University of Adelaide"));
	}
	
	/**
	 * Is an empty list returned when the question is not answered and is part a multiple question group
	 */
	@Test
	public void testBuildLegalContactOrgList02() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildLegalContactOrgList01-questionnaire.xml", "1");
		MultipleQuestionGroup legalContactOrgQuestionGroup = config.getQuestionGroupMap().get("13").getChildMultipleQuestionGroupById("13.4");
		Map<String, String> metatagMap = config.getMetatagToQuestionIdMap();
		Map<String, Answer> answerMap = answerMap(
			multipleAnswerGroup(legalContactOrgQuestionGroup,
				answerGroup(
					answer("13.4.1", "")
				)
			)
		);
		List<String> result = objectUnderTest.buildLegalContactOrgList(metatagMap, answerMap, config);
		assertEquals(Collections.emptyList(), result);
	}
	
	/**
	 * Is an empty list returned when the question doesn't exist (i.e. an old questionnaire config)
	 */
	@Test
	public void testBuildLegalContactOrgList03() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildLegalContactOrgList01-questionnaire.xml", "1");
		Map<String, String> metatagMap = Collections.emptyMap();
		Map<String, Answer> answerMap = Collections.emptyMap();
		List<String> result = objectUnderTest.buildLegalContactOrgList(metatagMap, answerMap, config);
		assertEquals(Collections.emptyList(), result);
	}
	
	/**
	 * Do we grab the suggested value when the user answers with OTHER?
	 */
	@Test
	public void testBuildLegalContactOrgList04() {
		QuestionnaireConfig config = getQuestionnaireConfig("ExampleCitationDataProviderFactoryTest_testBuildLegalContactOrgList01-questionnaire.xml", "1");
		MultipleQuestionGroup legalContactOrgQuestionGroup = config.getQuestionGroupMap().get("13").getChildMultipleQuestionGroupById("13.4");
		Map<String, String> metatagMap = config.getMetatagToQuestionIdMap();
		Map<String, Answer> answerMap = answerMap(
			multipleAnswerGroup(legalContactOrgQuestionGroup,
				answerGroup(
					answerOther("13.4.1", "imaginary university")
				),
				answerGroup(
					answer("13.4.1", ControlledVocabularyServiceStub.ORG_UNIVERSITY_OF_ADELAIDE)
				)
			)
		);
		List<String> result = objectUnderTest.buildLegalContactOrgList(metatagMap, answerMap, config);
		assertThat(result.size(), is(2));
		assertThat(result, hasItems("imaginary university", "University of Adelaide"));
	}
	
	/**
	 * Can we format a author name when both names are supplied
	 */
	@Test
	public void testFormatAuthorName01() {
		String givenName = "C.M.";
		String surname = "Carlson";
		String result = objectUnderTest.formatAuthorName(givenName, surname);
		assertEquals("Carlson, C.M.", result);
	}
	
	/**
	 * Can we survive a null given name
	 */
	@Test
	public void testFormatAuthorName02() {
		String givenName = null;
		String surname = "Carlson";
		String result = objectUnderTest.formatAuthorName(givenName, surname);
		assertEquals("Carlson", result);
	}
	
	/**
	 * Can we survive a null surname
	 */
	@Test
	public void testFormatAuthorName03() {
		String givenName = "D.E.";
		String surname = null;
		String result = objectUnderTest.formatAuthorName(givenName, surname);
		assertEquals(", D.E.", result);
	}
	
	/**
	 * Can we survive a zero length given name
	 */
	@Test
	public void testFormatAuthorName04() {
		String givenName = "";
		String surname = "Carlson";
		String result = objectUnderTest.formatAuthorName(givenName, surname);
		assertEquals("Carlson", result);
	}
	
	/**
	 * Can we survive a zero length surname
	 */
	@Test
	public void testFormatAuthorName05() {
		String givenName = "A.T.S.";
		String surname = "";
		String result = objectUnderTest.formatAuthorName(givenName, surname);
		assertEquals(", A.T.S.", result);
	}
	
	/**
	 * Do we use the names exactly as they're given to us?
	 */
	@Test
	public void testFormatAuthorName06() {
		String givenName = "r.L.";
		String surname = "De Loso";
		String result = objectUnderTest.formatAuthorName(givenName, surname);
		assertEquals("De Loso, r.L.", result);
	}
	
	@Test
	public void testFindParentQuestionId(){
		assertEquals("2.2", ExampleCitationDataProviderFactory.getParentQuestionId("2.2.1"));
		assertEquals("223", ExampleCitationDataProviderFactory.getParentQuestionId("223"));
	}
	
	private QuestionnaireConfig getQuestionnaireConfig(String filename, String version){
		QuestionnaireConfig qc = null;
		try {
			qc = questionnaireConfigService.getQuestionnaireConfig(filename, version, false);
			assertNotNull(qc.getSmsQuestionnaireId());
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config", e);
		}
		return qc;
	}
}
