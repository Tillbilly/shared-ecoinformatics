package au.edu.aekos.shared.service.integration;

import static au.edu.aekos.shared.SubmissionAnswerBuilders.controlledVocabSuggestAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.date;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.dateAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multipleQuestionGroup;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextChildAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.questionSet;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textBoxAnswer;
import static au.edu.aekos.shared.service.integration.SharedWebTestSupport.submissionData;
import static au.edu.aekos.shared.service.integration.SharedWebTestSupport.submissionWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionLinkType;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorTest;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.citation.CitationServiceImpl;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.SubmissionLinkService;
import au.org.aekos.shared.api.json.DatasetLink;
import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;
import au.org.aekos.shared.api.json.SharedDatasetSummary;
import au.org.aekos.shared.api.json.SpeciesFileNameEntry;
import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class SubmissionInfoModelSummaryServiceImplTest {

	@Autowired
	private SubmissionInfoModelSummaryServiceImpl objectUnderTest;
	
	@Autowired private QuestionnaireConfigService configService;
	@Autowired private QuestionnaireConfigEntityDao entityDao;
	@Autowired private SecurityService authService;
	@Autowired private SubmissionDao submissionDao;
	@Autowired private SubmissionLinkService linkService;

	private static final String QUESTIONNAIRE_NAME = "rifcsTestQuestionnaire.xml";
	
	@AfterClass
	public static void afterClass() {
		MetaInfoExtractorTest.cleanCache();
	}
	
	/**
	 * Can we extract the species file names when they exist?
	 */
	@Test
	public void testExtractSpeciesFilenames01() {
		Submission submission = submissionWith(
				submissionData(111l, "speciesFauna.txt", SubmissionDataType.SPECIES_LIST),
				submissionData(222l, "some.submission.data.csv", SubmissionDataType.DATA),
				submissionData(333l, "speciesFlora.txt", SubmissionDataType.SPECIES_LIST),
				submissionData(444l, "info.pdf", SubmissionDataType.RELATED_DOC));
		List<SpeciesFileNameEntry> result = objectUnderTest.extractSpeciesFilenames(submission);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getId(), is(111l));
		assertThat(result.get(1).getId(), is(333l));
	}
	
	/**
	 * Can we build a portal submission summary with a citation and DOI as we expect?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub.getId(), false);
		assertTrue(result.isSuccess());
		SharedDatasetSummary summary = result.getPayload();
		String citation = getCitationRow(summary.getRowDataList());
		assertThat(citation, is("Davis, R.M., McCracken, P.Q. (2015). Most awesome submission, Version 1.2.wow. "
				+ "http://doi.org/10.33327/05/537C5C5553F9E. &AElig;KOS Data Portal, "
				+ "rights owned by Org1, Org2. Accessed "+today()+"."));
		String doi = getDoiRow(summary.getRowDataList());
		assertThat(doi, is("http://doi.org/10.33327/05/537C5C5553F9E"));
	}
	
	/**
	 * Can we build a portal submission summary with link information in the "normal" direction as we expect?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal02() throws Exception {
		Submission sub1 = prepareSubmission("Submission One");
		submissionDao.save(sub1);
		Submission sub2 = prepareSubmission("Submission Two");
		submissionDao.save(sub2);
		linkService.linkSubmissions(sub1.getId(), sub2.getId(), SubmissionLinkType.IS_NEW_VERSION_OF, "One is a new version of Two");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1.getId(), false);
		assertTrue(result.isSuccess());
		SharedDatasetSummary summary = result.getPayload();
		assertThat(summary.getLinks().size(), is(1));
		DatasetLink link = summary.getLinks().get(0);
		assertThat(link.getOtherDatasetName(), is("Submission Two"));
		assertThat(link.getOtherDatasetId(), is(sub2.getId()));
		assertThat(link.getLinkTypeTitle(), is(SubmissionLinkType.IS_NEW_VERSION_OF.getNormalTitle()));
		assertThat(link.getLinkDescription(), is("One is a new version of Two"));
	}
	
	/**
	 * Can we build a portal submission summary with link information in the "inverse" direction as we expect?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal03() throws Exception {
		Submission sub1 = prepareSubmission("Submission One");
		submissionDao.save(sub1);
		Submission sub2 = prepareSubmission("Submission Two");
		submissionDao.save(sub2);
		linkService.linkSubmissions(sub2.getId(), sub1.getId(), SubmissionLinkType.IS_NEW_VERSION_OF, "One is a new version of Two");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1.getId(), false);
		assertTrue(result.isSuccess());
		SharedDatasetSummary summary = result.getPayload();
		assertThat(summary.getLinks().size(), is(1));
		DatasetLink link = summary.getLinks().get(0);
		assertThat(link.getOtherDatasetName(), is("Submission Two"));
		assertThat(link.getOtherDatasetId(), is(sub2.getId()));
		assertThat(link.getLinkTypeTitle(), is(SubmissionLinkType.IS_NEW_VERSION_OF.getInverseTitle()));
		assertThat(link.getLinkDescription(), is("One is a new version of Two"));
	}
	
	/**
	 * Are links sorted as we expect?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal04() throws Exception {
		Submission sub1 = prepareSubmission("Submission One");
		submissionDao.save(sub1);
		Submission sub2 = prepareSubmission("Submission Two");
		submissionDao.save(sub2);
		Submission sub3 = prepareSubmission("Submission Three");
		sub3.setLastReviewDate(date("2015-05-05"));
		submissionDao.save(sub3);
		Submission sub4 = prepareSubmission("Submission Four");
		sub4.setLastReviewDate(date("2014-04-04"));
		submissionDao.save(sub4);
		linkService.linkSubmissions(sub1.getId(), sub4.getId(), SubmissionLinkType.RELATED, "One is related to Four");
		linkService.linkSubmissions(sub1.getId(), sub2.getId(), SubmissionLinkType.IS_NEW_VERSION_OF, "One is a new version of Two");
		linkService.linkSubmissions(sub1.getId(), sub3.getId(), SubmissionLinkType.RELATED, "One is related to Three");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1.getId(), false);
		assertTrue(result.isSuccess());
		SharedDatasetSummary summary = result.getPayload();
		assertThat(summary.getLinks().size(), is(3));
		DatasetLink link1 = summary.getLinks().get(0);
		assertThat(link1.getLinkTypeTitle(), is(SubmissionLinkType.IS_NEW_VERSION_OF.getNormalTitle()));
		assertThat(link1.getOrder(), is(0));
		DatasetLink link2 = summary.getLinks().get(1);
		assertThat(link2.getLinkTypeTitle(), is(SubmissionLinkType.RELATED.getNormalTitle()));
		assertThat(link2.getOtherDatasetId(), is(sub3.getId()));
		assertThat(link2.getOrder(), is(1));
		DatasetLink link3 = summary.getLinks().get(2);
		assertThat(link3.getLinkTypeTitle(), is(SubmissionLinkType.RELATED.getNormalTitle()));
		assertThat(link3.getOtherDatasetId(), is(sub4.getId()));
		assertThat(link3.getOrder(), is(2));
	}
	
	/**
	 * Are only links between PUBLISHED submissions returned?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal05() throws Exception {
		Submission sub1 = prepareSubmission("Submission One");
		submissionDao.save(sub1);
		Submission sub2 = prepareSubmission("Submission Two");
		sub2.setStatus(SubmissionStatus.SUBMITTED);
		submissionDao.save(sub2);
		Submission sub3 = prepareSubmission("Submission Three");
		submissionDao.save(sub3);
		linkService.linkSubmissions(sub1.getId(), sub2.getId(), SubmissionLinkType.IS_NEW_VERSION_OF, "one of the submissions isn't published");
		linkService.linkSubmissions(sub1.getId(), sub3.getId(), SubmissionLinkType.RELATED, "both submissions are published");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1.getId(), false);
		assertTrue(result.isSuccess());
		SharedDatasetSummary summary = result.getPayload();
		assertThat(summary.getLinks().size(), is(1));
		DatasetLink link1 = summary.getLinks().get(0);
		assertThat(link1.getLinkTypeTitle(), is(SubmissionLinkType.RELATED.getNormalTitle()));
		assertThat(link1.getOtherDatasetId(), is(sub3.getId()));
	}
	
	/**
	 * Is the expected flag set when this submission has a newer version present (indicated by the link in the correct direction)?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal06() throws Exception {
		Long sub1Id = prepareSubmissionAndGetId("Submission One");
		Long sub2Id = prepareSubmissionAndGetId("Submission Two");
		linkService.linkSubmissions(sub1Id, sub2Id, SubmissionLinkType.HAS_NEW_VERSION, "One has new version: Two");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1Id, false);
		SharedDatasetSummary summary = result.getPayload();
		assertTrue(summary.isNewerVersionPresent());
	}

	/**
	 * Is the expected flag NOT set when this submission is at the other end of a HAS_NEWER_VERSION link?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal07() throws Exception {
		Long sub1Id = prepareSubmissionAndGetId("Submission One");
		Long sub2Id = prepareSubmissionAndGetId("Submission Two");
		linkService.linkSubmissions(sub2Id, sub1Id, SubmissionLinkType.HAS_NEW_VERSION, "Two has new version: One");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1Id, false);
		SharedDatasetSummary summary = result.getPayload();
		assertFalse(summary.isNewerVersionPresent());
	}
	
	/**
	 * Is the expected flag NOT set when this submission DOES NOT has a newer version present (no link exists)?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal08() throws Exception {
		Long sub1Id = prepareSubmissionAndGetId("Submission One");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1Id, false);
		SharedDatasetSummary summary = result.getPayload();
		assertFalse(summary.isNewerVersionPresent());
	}
	
	/**
	 * Is the expected flag NOT set when this submission DOES NOT has a newer version present (links exist but of the wrong type)?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal09() throws Exception {
		Long sub1Id = prepareSubmissionAndGetId("Submission One");
		Long sub2Id = prepareSubmissionAndGetId("Submission Two");
		linkService.linkSubmissions(sub1Id, sub2Id, SubmissionLinkType.RELATED, "One is related to Two");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1Id, false);
		SharedDatasetSummary summary = result.getPayload();
		assertFalse(summary.isNewerVersionPresent());
	}
	
	/**
	 * Is the expected flag NOT set when this submission is the source of a IS_NEW_VERSION_OF link?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal10() throws Exception {
		Long sub1Id = prepareSubmissionAndGetId("Submission One");
		Long sub2Id = prepareSubmissionAndGetId("Submission Two");
		linkService.linkSubmissions(sub1Id, sub2Id, SubmissionLinkType.IS_NEW_VERSION_OF, "One is a new version of Two");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1Id, false);
		SharedDatasetSummary summary = result.getPayload();
		assertFalse(summary.isNewerVersionPresent());
	}
	
	/**
	 * Is the expected flag set when this submission is NOT the source of a IS_NEW_VERSION_OF link (so it is an old version itself)?
	 */
	@Test
	public void testBuildSubmissionSummaryForPortal11() throws Exception {
		Long sub1Id = prepareSubmissionAndGetId("Submission Old");
		Long sub2Id = prepareSubmissionAndGetId("Submission New");
		linkService.linkSubmissions(sub2Id, sub1Id, SubmissionLinkType.IS_NEW_VERSION_OF, "New is a new version of Old");
		ResponseGetDatasetSummary result = objectUnderTest.buildSubmissionSummaryForPortal(sub1Id, false);
		SharedDatasetSummary submissionOldSummary = result.getPayload();
		assertTrue(submissionOldSummary.isNewerVersionPresent());
	}
	
	/**
	 * Can we build the summary rows for the PDF with a citation as we expect?
	 */
	@Test
	public void testRetrieveSubmissionSummaryRows01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		List<SubmissionSummaryRow> result = objectUnderTest.retrieveSubmissionSummaryRows(sub.getId(), false);
		String citation = getCitationRow(result);
		assertThat(citation, is("Davis, R.M., McCracken, P.Q. (2015). Most awesome submission, Version 1.2.wow. "
				+ "http://doi.org/10.33327/05/537C5C5553F9E. ÆKOS Data Portal, "
				+ "rights owned by Org1, Org2. Accessed "+today()+"."));
		String doi = getDoiRow(result);
		assertThat(doi, is("http://doi.org/10.33327/05/537C5C5553F9E"));
	}
	
	private String today() {
		return CitationServiceImpl.todayInCitationDateFormat();
	}

	private String getCitationRow(List<SubmissionSummaryRow> list) {
		for (SubmissionSummaryRow currRow : list) {
			if ("Citation".equalsIgnoreCase(currRow.getTitle())) {
				return currRow.getValue();
			}
		}
		throw new RuntimeException("Data error: couldn't find the 'citation' data row.");
	}

	private String getDoiRow(List<SubmissionSummaryRow> list) {
		for (SubmissionSummaryRow currRow : list) {
			if ("Submission DOI".equalsIgnoreCase(currRow.getTitle())) {
				return currRow.getValue();
			}
		}
		throw new RuntimeException("Data error: couldn't find the 'doi' data row.");
	}
	
	private Submission prepareSubmission() throws Exception {
		return prepareSubmission("My subInfoModel submission");
	}
	
	private Long prepareSubmissionAndGetId(String title) throws Exception {
		Submission sub = prepareSubmission(title);
		submissionDao.save(sub);
		return sub.getId();
	}
	
	private Submission prepareSubmission(String title) throws Exception {
		QuestionnaireConfig qc = configService.readQuestionnaireConfig(QUESTIONNAIRE_NAME, true, false);
		SharedUser su = authService.getCreateDefaultUser("testSubInfoModelUser");
		
		Submission result = new Submission();
		result.setStatus(SubmissionStatus.PUBLISHED);
		result.setSubmitter(su);
		result.setQuestionnaireConfig(entityDao.findById(qc.getSmsQuestionnaireId()));
		result.setTitle(title);
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
		result.getAnswers().add(textAnswer("6", "ben@shared.com"));

		SubmissionAnswer answer7 = multiselectTextAnswer("7",
			multiselectTextChildAnswer("for - 1"),
			multiselectTextChildAnswer("for - 2")
		);
		result.getAnswers().add(answer7);
		
		SubmissionAnswer answer8 = multiselectTextAnswer("8",
			multiselectTextChildAnswer("seo - 1"),
			multiselectTextChildAnswer("seo - 2")
		);
		result.getAnswers().add(answer8);
		
		SubmissionAnswer answer9 = multiselectTextAnswer("9",
			multiselectTextChildAnswer("theme 1"),
			multiselectTextChildAnswer("theme 2")
		);
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
		result.getAnswers().add(controlledVocabSuggestAnswer("19", "My Organisation"));
		result.getAnswers().add(multiselectTextAnswer("20",
			multiselectTextChildAnswer("ecological theme uno"),
			multiselectTextChildAnswer("ecological theme dos")
		));
		result.getAnswers().add(multiselectTextAnswer("21",
			multiselectTextChildAnswer("something threatening"),
			multiselectTextChildAnswer("some pressure")
		));
		result.getAnswers().add(multiselectTextAnswer("22",
			multiselectTextChildAnswer("conserving"),
			multiselectTextChildAnswer("managing")
		));
		result.getAnswers().add(
			multipleQuestionGroup("109.1",
				questionSet(
					controlledVocabSuggestAnswer("109.1.1", "fieldManual"),
					textAnswer("109.1.3", "Something else"),
					controlledVocabSuggestAnswer("109.1.4", "ark"),
					textAnswer("109.1.5", "http://library.manchester.ac.uk/ark:/98765/archive/object35")
				),
				questionSet(
					controlledVocabSuggestAnswer("109.1.1", "publishedPaper"),
					textAnswer("109.1.3", "That other thing that I did"),
					controlledVocabSuggestAnswer("109.1.4", "doi"),
					textAnswer("109.1.5", "10.4227/05/53B1F1C02C8E7")
				),
				questionSet(
					controlledVocabSuggestAnswer("109.1.1", "Other"),
					textAnswer("109.1.2", "someSpecialOtherIdentifierType"),
					textAnswer("109.1.3", "beyond 0 and 1"),
					controlledVocabSuggestAnswer("109.1.4", "doi"),
					textAnswer("109.1.5", "20.3338/39/53B1F1C55D1A0")
				)
			)
		);
		return result;
	}
}
