package au.edu.aekos.shared.service.rifcs;

import static au.edu.aekos.shared.SubmissionAnswerBuilders.controlledVocabSuggestAnswer;

import static au.edu.aekos.shared.SubmissionAnswerBuilders.date;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.dateAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multipleQuestionGroup;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextChildAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.questionSet;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textBoxAnswer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xmlunit.builder.Input;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorTest;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.security.SecurityService;

import com.google.common.io.Files;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class RifcsApiServiceImplTest {

	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private QuestionnaireConfigEntityDao entityDao;
	
	@Autowired
	private SecurityService authService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private RifcsApiServiceImpl objectUnderTest;
	
	@Autowired
	private RifcsFileServiceImpl rifcsFileService;
	
	@Autowired @Qualifier("testBuildRifcsXmlFromSubmission01_expected")
	private String testBuildRifcsXmlFromSubmission01_expected;
	
	@Autowired @Qualifier("testBuildRifcsXmlFromSubmission02_expected")
	private String testBuildRifcsXmlFromSubmission02_expected;
	
	@Autowired @Qualifier("testBuildRifcsXmlFromSubmission03_expected")
	private String testBuildRifcsXmlFromSubmission03_expected;
	
	@Autowired @Qualifier("testBuildRifcsXmlFromSubmission04_expected")
	private String testBuildRifcsXmlFromSubmission04_expected;
	
	@Autowired @Qualifier("testBuildRifcsXmlFromSubmission05_expected")
	private String testBuildRifcsXmlFromSubmission05_expected;
	
	@Autowired @Qualifier("testBuildRifcsXmlFromSubmission06_expected")
	private String testBuildRifcsXmlFromSubmission06_expected;
	
	@Autowired @Qualifier("testGenerateRifcsFileForSubmission01_expected")
	private String testGenerateRifcsFileForSubmission01_expected;
	
	private static final String QUESTIONNAIRE_NAME = "rifcsTestQuestionnaire.xml";
	private static final String GEO_FEATURE_SET_JSON = "{ \"questionId\" : \"1.2.4\", \"features\" : [ "
			+ "{\"id\":\"PLY_1\",\"geometry\":\"POLYGON((139.35009765625 -32.479614257813,139.57836914063 -33.496459960938,140.90649414063 -33.517211914063,141.44604492188 -32.147583007813,141.09326171875 -31.753295898438,140.61596679688 -32.749389648438,140.0556640625 -30.425170898438,138.12573242188 -31.109985351563,139.35009765625 -32.479614257813))\",\"description\":\"friendly\"},"
			+ "{\"id\":\"PLY_2\",\"geometry\":\"POLYGON((137.5654296875 -29.138549804688,137.482421875 -30.445922851563,135.92602539063 -29.180053710938,137.5654296875 -29.138549804688))\",\"description\":\"\"},"
			+ "{\"id\":\"PLY_3\",\"geometry\":\"POLYGON((141.84033203125 -29.553588867188,141.84033203125 -29.512084960938,141.86108398438 -29.512084960938,141.86108398438 -29.553588867188,141.84033203125 -29.553588867188))\",\"description\":\"\"},"
			+ "{\"id\":\"PLY_4\",\"geometry\":\"POLYGON((141.84033203125 -30.757202148438,141.84033203125 -29.595092773438,143.35522460938 -29.595092773438,143.35522460938 -30.757202148438,141.84033203125 -30.757202148438))\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_1\",\"geometry\":\"POINT(142.98168945313 -34.886840820313)\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_2\",\"geometry\":\"POINT(141.59130859375 -35.384887695313)\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_3\",\"geometry\":\"POINT(139.82739257813 -27.955688476563)\",\"description\":\"\"}] }";
	
	@AfterClass
	public static void afterClass() {
		MetaInfoExtractorTest.cleanCache();
	}
	
	/**
	 * Can we build the RIF-CS XML string from a submission that has everything populated?
	 */
	@Test
	public void testBuildRifcsXmlFromSubmission01() throws Exception{
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		String result = objectUnderTest.buildRifcsXmlFromSubmission(sub);
		String replacedExpectedRifcsRecordText = testBuildRifcsXmlFromSubmission01_expected
				.replace("REPLACE_ME_ID", String.valueOf(sub.getId())); // we can't control the ID the submission gets
		assertThat(result, isIdenticalTo(Input.from(replacedExpectedRifcsRecordText)));
	}
	
	/**
	 * Can we build the RIF-CS XML string from a submission that is missing a version?
	 */
	@Test
	public void testBuildRifcsXmlFromSubmission02() throws Exception{
		Submission sub = prepareSubmission();
		String versionQuestionId = "1.2";
		removeQuestionFromSubmission(sub, versionQuestionId);
		submissionDao.save(sub);
		String result = objectUnderTest.buildRifcsXmlFromSubmission(sub);
		String replacedExpectedRifcsRecordText = testBuildRifcsXmlFromSubmission02_expected
				.replace("REPLACE_ME_ID", String.valueOf(sub.getId())); // we can't control the ID the submission gets
		assertThat(result, isIdenticalTo(Input.from(replacedExpectedRifcsRecordText)));
	}

	/**
	 * Can we build the RIF-CS XML string from a submission that is missing the information for a DOI RelatedInfo?
	 */
	@Test
	public void testBuildRifcsXmlFromSubmission03() throws Exception{
		Submission sub = prepareSubmission();
		String relatedInfoQuestionId = "109.1";
		removeQuestionFromSubmission(sub, relatedInfoQuestionId);
		submissionDao.save(sub);
		String result = objectUnderTest.buildRifcsXmlFromSubmission(sub);
		String replacedExpectedRifcsRecordText = testBuildRifcsXmlFromSubmission03_expected
				.replace("REPLACE_ME_ID", String.valueOf(sub.getId())); // we can't control the ID the submission gets
		assertThat(result, isIdenticalTo(Input.from(replacedExpectedRifcsRecordText)));
	}
	
	/**
	 * Can we handle a missing SHD.associatedMaterialDescription as they aren't mandatory?
	 */
	@Test
	public void testBuildRifcsXmlFromSubmission04() throws Exception{
		Submission sub = prepareSubmissionWithRelatedInfoAnswers(new RelatedInfoAnswersCallback() {
			@Override
			public void addAnswers(Submission sub) {
				sub.getAnswers().add(
					multipleQuestionGroup("109.1",
						questionSet(
							// Don't answer 109.1.3 - the description
							controlledVocabSuggestAnswer("109.1.4", "doi"),
							textAnswer("109.1.5", "10.4227/05/53B1F1C02C8E7")
			)));}
		});
		submissionDao.save(sub);
		String result = getSubstring(objectUnderTest.buildRifcsXmlFromSubmission(sub), "<relatedInfo", "<rights>");
		assertEquals(testBuildRifcsXmlFromSubmission04_expected, result);
	}
	
	/**
	 * Do the indexes of the descriptions stay in sync with their identifiers when some groups don't have descriptions? 
	 */
	@Test
	public void testBuildRifcsXmlFromSubmission05() throws Exception{
		Submission sub = prepareSubmissionWithRelatedInfoAnswers(new RelatedInfoAnswersCallback() {
			@Override
			public void addAnswers(Submission sub) {
				sub.getAnswers().add(
					multipleQuestionGroup("109.1",
						questionSet(
							// Don't answer 109.1.3 - the description
							controlledVocabSuggestAnswer("109.1.4", "doi"),
							textAnswer("109.1.5", "10.4227/05/53B1F1C02C8E7")
						),
						questionSet(
							textAnswer("109.1.3", "That other thing that I did"),
							controlledVocabSuggestAnswer("109.1.4", "doi"),
							textAnswer("109.1.5", "20.3338/39/53B1F1C55D1A0")
						)
			));}
		});
		submissionDao.save(sub);
		String result = getSubstring(objectUnderTest.buildRifcsXmlFromSubmission(sub), "<relatedInfo", "<rights>");
		assertEquals(testBuildRifcsXmlFromSubmission05_expected, result);
	}
	
	/**
	 * Is anything that doesn't have the identifier type or identifier questions answered ignored?
	 */
	@Test
	public void testBuildRifcsXmlFromSubmission06() throws Exception{
		Submission sub = prepareSubmissionWithRelatedInfoAnswers(new RelatedInfoAnswersCallback() {
			@Override
			public void addAnswers(Submission sub) {
				sub.getAnswers().add(
					multipleQuestionGroup("109.1",
						questionSet(
							textAnswer("109.1.3", "That other thing that I did"),
							controlledVocabSuggestAnswer("109.1.4", "doi")
							// Don't answer 109.1.5 - the identifier
						),
						questionSet(
							textAnswer("109.1.3", "That other thing that I did"),
							controlledVocabSuggestAnswer("109.1.4", "doi"),
							textAnswer("109.1.5", "20.3338/39/53B1F1C55D1A0")
						),
						questionSet(
							textAnswer("109.1.3", "That other thing that I did"),
							// Don't answer 109.1.4 - the identifier type
							textAnswer("109.1.5", "20.3338/39/53B1F1C55D1A0")
						)
			));}
		});
		submissionDao.save(sub);
		String result = getSubstring(objectUnderTest.buildRifcsXmlFromSubmission(sub), "<relatedInfo", "<rights>");
		assertEquals(testBuildRifcsXmlFromSubmission06_expected, result);
	}
	
	private String getSubstring(String resultXml, String startToken, String endToken) {
		int startIndex = resultXml.indexOf(startToken);
		int endIndex = resultXml.indexOf(endToken) + endToken.length();
		return resultXml.substring(startIndex, endIndex);
	}

	/**
	 * Can we write the RIF-CS XML out to a file from a submission that has everything populated?
	 */
	@Test
	public void testGenerateRifcsFileForSubmission01() throws Exception{
		Field f = RifcsFileServiceImpl.class.getDeclaredField("xmlFileOutputDir");
		f.setAccessible(true);
		f.set(rifcsFileService, java.nio.file.Files.createTempDirectory("testGenerateRifcsFileForSubmission01Rifcs").toString());
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		File result = objectUnderTest.generateRifcsFileForSubmissionHelper(sub);
		String replacedExpectedRifcsRecordText = testGenerateRifcsFileForSubmission01_expected
				.replace("REPLACE_ME_ID", String.valueOf(sub.getId())); // we can't control the ID the submission gets
		ByteArrayOutputStream to = new ByteArrayOutputStream();
		Files.copy(result, to);
		assertEquals(replacedExpectedRifcsRecordText, to.toString());
	}
	
	private Submission prepareSubmission() throws Exception {
		return prepareSubmissionWithRelatedInfoAnswers(new RelatedInfoAnswersCallback() {
			@Override
			public void addAnswers(Submission sub) {
				sub.getAnswers().add(
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
			}
		});
	}
	
	private Submission prepareSubmissionWithRelatedInfoAnswers(RelatedInfoAnswersCallback relatedInfoCallback) throws Exception {
		QuestionnaireConfig qc = configService.readQuestionnaireConfig(QUESTIONNAIRE_NAME, true, false);
		SharedUser su = authService.getCreateDefaultUser("testRifcsUser");
		QuestionnaireConfigEntity configId = entityDao.findById(qc.getSmsQuestionnaireId());
		return buildSubmissionWithRelatedInfoAnswer(su, configId, relatedInfoCallback);
	}
	
	static Submission buildSubmissionWithRelatedInfoAnswer(SharedUser su, QuestionnaireConfigEntity configId
			, RelatedInfoAnswersCallback relatedInfoCallback) throws ParseException {
		Submission result = new Submission();
		result.setSubmitter(su);
		result.setQuestionnaireConfig(configId);
		result.setTitle("My wacky submission");
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
		result.getAnswers().add(multiselectTextAnswer("7",
			multiselectTextChildAnswer("01"),
			multiselectTextChildAnswer("2"),
			multiselectTextChildAnswer("601"),
			multiselectTextChildAnswer("0602"),
			multiselectTextChildAnswer("60601"),
			multiselectTextChildAnswer("060608"),
			multiselectTextChildAnswer(" 20406")
		));
		result.getAnswers().add(multiselectTextAnswer("8",
			multiselectTextChildAnswer("04"),
			multiselectTextChildAnswer("5"),
			multiselectTextChildAnswer("201"),
			multiselectTextChildAnswer("0202"),
			multiselectTextChildAnswer("20601"),
			multiselectTextChildAnswer("020608")
		));
		result.getAnswers().add(multiselectTextAnswer("9",
			multiselectTextChildAnswer("theme 1"),
			multiselectTextChildAnswer("theme 2")
		));
		result.getAnswers().add(textAnswer("10", "Dataset abstract all about my dataset"));
		result.getAnswers().add(dateAnswer("11", "21/11/2000"));
		result.getAnswers().add(dateAnswer("12", "13/10/2010"));
		result.getAnswers().add(textAnswer("13", "Study Area Description"));

		SubmissionAnswer answer15 = new SubmissionAnswer();
		answer15.setResponseType(ResponseType.GEO_FEATURE_SET);
		answer15.setResponse(GEO_FEATURE_SET_JSON);
		answer15.setQuestionId("15");
		result.getAnswers().add(answer15);
		
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
		relatedInfoCallback.addAnswers(result);
		return result;
	}

	interface RelatedInfoAnswersCallback {
		void addAnswers(Submission sub);
	}
	
	private void removeQuestionFromSubmission(Submission sub, String questionId) {
		for (Iterator<SubmissionAnswer> it = sub.getAnswers().iterator(); it.hasNext();) {
			if (!it.next().getQuestionId().equals(questionId)) {
				continue;
			}
			it.remove();
		}
	}
}
