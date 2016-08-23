package au.edu.aekos.shared.service.citation;

import static au.edu.aekos.shared.SubmissionAnswerBuilders.accessedTodayDate;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.date;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.dateAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multipleQuestionGroup;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.questionSet;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textBoxAnswer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorTest;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.citation.CitationService.CitationDisplayType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.security.SecurityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class CitationServiceImplTest {

	private static final String CONTACT_EMAIL = "ben@shared.com";

	@Autowired
	private CitationServiceImpl objectUnderTest;
	
	@Autowired private MetaInfoExtractorFactory metaInfoExtractorFactory;
	@Autowired private QuestionnaireConfigService configService;
	@Autowired private QuestionnaireConfigEntityDao entityDao;
	@Autowired private SecurityService authService;
	@Autowired private SubmissionDao submissionDao;
	
	@Value(value="${aekos.enquiries.email}")
	private String accessStatementContactEmail;
	
	private static final String QUESTIONNAIRE_NAME = "rifcsTestQuestionnaire.xml";
	
	@After
	public void after() {
		MetaInfoExtractorTest.cleanCache();
	}
	
	/**
	 * Can we build an access statement when all the information is present?
	 */
	@Test
	public void testBuildAccessStatement01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.buildAccessStatement(metaInfoExtractor);
		assertThat(result, is("These data can be freely downloaded via the Advanced Ecological Knowledge and Observation System (ÆKOS) "
				+ "Data Portal and used subject to the Creative Commons - Attribution 3.0 Australia. Attribution and citation is required "
				+ "as described under License and Citation. We ask you to send citations of publications arising from work that use these "
				+ "data to TERN Eco-informatics at "+accessStatementContactEmail+" and citation and copies of publications to " + CONTACT_EMAIL));
	}

	/**
	 * Can we build a rights statement when all the information is present?
	 */
	@Test
	public void testBuildRightsStatement01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.buildRightsStatement(metaInfoExtractor);
		assertThat(result, is("(C)2015 Org1, Org2. Rights owned by Org1, Org2. Rights licensed subject to Creative Commons - Attribution 3.0 Australia."));
	}
	
	/**
	 * Can we build a publisher string when all the information is present?
	 */
	@Test
	public void testBuildPublisherStringForRifcs01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.buildPublisherStringForRifcs(metaInfoExtractor);
		assertThat(result, is("ÆKOS Data Portal, rights owned by Org1, Org2"));
	}
	
	/**
	 * Can we get the dataset name?
	 */
	@Test
	public void testRetrieveDatasetName01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.retrieveDatasetName(metaInfoExtractor);
		assertThat(result, is("Most awesome submission"));
	}
	
	/**
	 * Can we get the license string?
	 */
	@Test
	public void testRetrieveLicenseString01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.retrieveLicenseString(metaInfoExtractor);
		assertThat(result, is("Creative Commons - Attribution 3.0 Australia"));
	}
	
	/**
	 * Can we build a PRINT based citation string?
	 */
	@Test
	public void testBuildCitationString01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.buildCitationString(metaInfoExtractor, CitationDisplayType.PDF);
		assertThat(result, is("Davis, R.M., McCracken, P.Q. (2015). Most awesome submission, Version 1.2.wow. "
				+ "http://doi.org/10.33327/05/537C5C5553F9E. ÆKOS Data Portal, rights owned by Org1, Org2. Accessed "+accessedTodayDate()+"."));
	}
	
	/**
	 * Can we build a WEB based citation string?
	 */
	@Test
	public void testBuildCitationString02() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		MetaInfoExtractor metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.buildCitationString(metaInfoExtractor, CitationDisplayType.AEKOS_PORTAL);
		assertThat(result, is("Davis, R.M., McCracken, P.Q. (2015). Most awesome submission, Version 1.2.wow. "
				+ "http://doi.org/10.33327/05/537C5C5553F9E. &AElig;KOS Data Portal, rights owned by Org1, Org2. Accessed "+accessedTodayDate()+"."));
	}
	
	private Submission prepareSubmission() throws Exception {
		QuestionnaireConfig qc = configService.readQuestionnaireConfig(QUESTIONNAIRE_NAME, true, false);
		SharedUser su = authService.getCreateDefaultUser("testCitationUser");
		
		Submission result = new Submission();
		result.setSubmitter(su);
		result.setQuestionnaireConfig(entityDao.findById(qc.getSmsQuestionnaireId()));
		result.setTitle("My magnificent submission");
		result.setLastReviewDate(date("2015-12-12"));
		result.setDoi("10.33327/05/537C5C5553F9E");
		
		result.getAnswers().add(textAnswer("1", "My Fantastic Dataset"));
		result.getAnswers().add(textAnswer("1.1", "Most awesome submission"));
		result.getAnswers().add(textAnswer("1.2", "1.2.wow"));
		result.getAnswers().add(textAnswer("2", "Dataset Contact Name"));
		result.getAnswers().add(textAnswer("3", "Dataset Organisation Yall"));
		result.getAnswers().add(
			multipleQuestionGroup("3.1",
				questionSet(
					textAnswer("3.1.1", "Org1")
				),
				questionSet(
					textAnswer("3.1.1", "Org2")
				)
			)
		);
		result.getAnswers().add(textBoxAnswer("4", "Dataset Contact Address my Friends"));
		result.getAnswers().add(textAnswer("5", "Ph: 123 456 666"));
		result.getAnswers().add(textAnswer("6", CONTACT_EMAIL));

		SubmissionAnswer answer7 = new SubmissionAnswer();
		answer7.setResponseType(ResponseType.MULTISELECT_TEXT);
		answer7.setQuestionId("7");
		SubmissionAnswer answer7_1 = new SubmissionAnswer();
		answer7_1.setResponse("for - 1");
		answer7_1.setResponseType(ResponseType.TEXT);
		answer7.getMultiselectAnswerList().add(answer7_1);
		SubmissionAnswer answer7_2 = new SubmissionAnswer();
		answer7_2.setResponse("for - 2");
		answer7_2.setResponseType(ResponseType.TEXT);
		answer7.getMultiselectAnswerList().add(answer7_2);
		result.getAnswers().add(answer7);
		
		SubmissionAnswer answer8 = new SubmissionAnswer();
		answer8.setResponseType(ResponseType.MULTISELECT_TEXT);
		answer8.setQuestionId("8");
		SubmissionAnswer answer8_1 = new SubmissionAnswer();
		answer8_1.setResponse("seo - 1");
		answer8_1.setResponseType(ResponseType.TEXT);
		answer8.getMultiselectAnswerList().add(answer8_1);
		SubmissionAnswer answer8_2 = new SubmissionAnswer();
		answer8_2.setResponse("seo - 2");
		answer8_2.setResponseType(ResponseType.TEXT);
		answer8.getMultiselectAnswerList().add(answer8_2);
		result.getAnswers().add(answer8);
		
		SubmissionAnswer answer9 = new SubmissionAnswer();
		answer9.setResponseType(ResponseType.MULTISELECT_TEXT);
		answer9.setQuestionId("9");
		SubmissionAnswer answer9_1 = new SubmissionAnswer();
		answer9_1.setResponse("theme 1");
		answer9_1.setResponseType(ResponseType.TEXT);
		answer9.getMultiselectAnswerList().add(answer9_1);
		SubmissionAnswer answer9_2 = new SubmissionAnswer();
		answer9_2.setResponse("theme 2");
		answer9_2.setResponseType(ResponseType.TEXT);
		answer9.getMultiselectAnswerList().add(answer9_2);
		result.getAnswers().add(answer9);
		
		result.getAnswers().add(textAnswer("10", "Dataset abstract all about my dataset"));
		result.getAnswers().add(dateAnswer("11", "21/11/2000"));
		result.getAnswers().add(dateAnswer("12", "13/10/2010"));
		result.getAnswers().add(textAnswer("13", "Study Area Description"));
		
		result.getAnswers().add(
			multipleQuestionGroup("16",
				questionSet(
					textAnswer("16.1", "R.M."),
					textAnswer("16.2", "Davis"),
					textAnswer("16.3", "Lead Professor")
				),
				questionSet(
					textAnswer("16.1", "P.Q."),
					textAnswer("16.2", "McCracken"),
					textAnswer("16.3", "Big Boss")
				)
			)
		);
		
		result.getAnswers().add(textAnswer("17", "2010-2011"));
		result.getAnswers().add(textAnswer("18", "Creative Commons - Attribution 3.0 Australia"));
		
		SubmissionAnswer answer19 = new SubmissionAnswer();
		answer19.setResponseType(ResponseType.CONTROLLED_VOCAB_SUGGEST);
		answer19.setResponse("My Organisation");
		answer19.setQuestionId("19");
		result.getAnswers().add(answer19);
		return result;
	}
}
