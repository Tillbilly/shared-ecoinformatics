package au.edu.aekos.shared.data.infomodel;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sf.ehcache.CacheManager;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.web.json.JsonFeature;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
@Transactional
public class MetaInfoExtractorTest {

	private static final String AUTHOR1_GIVEN_NAME_RESPONSE = "Author1 Given";
	private static final String AUTHOR1_SURNAME_RESPONSE = "Surname1";
	private static final String AUTHOR2_GIVEN_NAME_RESPONSE = "Author2 Given";
	private static final String AUTHOR2_SURNAME_RESPONSE = "Surname2";
	private static final String METHOD_NAME_RESPONSE1 = "MethodName1";
	private static final String METHOD_NAME_RESPONSE2 = "MethodName2";
	private static final String DATASET_KEYWORD_RESPONSE1 = "Data";
	private static final String DATASET_KEYWORD_RESPONSE2 = "Monkeys";
	private static final String DATASET_KEYWORD_RESPONSE3 = "Bananas";

	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;

	@AfterClass
	public static void afterClass() {
		cleanCache();
	}
	
	/**
	 * Is the expected value returned when we request it for a metatag?
	 */
	@Test
	public void testGetSingleResponseForMetatag01() throws Exception{
		Submission sub = createATestSubmission("testGetSingleResponseForMetatag01");
		MetaInfoExtractor mie = metaInfoExtractorFactory.getInstance(sub.getId());
	    assertEquals("DATASET NAME testGetSingleResponseForMetatag01", mie.getSingleResponseForMetatag("SHD.datasetFullName"));
	}

	/**
	 * Does requesting a metatag with an alternate in the evolution config get the value of the alternate?
	 */
	@Test
	public void testGetSingleResponseForMetatag02() throws Exception{
		Submission sub = createATestSubmission("testGetSingleResponseForMetatag02");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.getSingleResponseForMetatag("SHD.metatagWithAlternate");
		assertEquals("DATASET NAME testGetSingleResponseForMetatag02", result);
	}
	
	/**
	 * Does requesting a metatag with a default in the evolution config get the default value?
	 */
	@Test
	public void testGetSingleResponseForMetatag03() throws Exception{
		Submission sub = createATestSubmission("testGetSingleResponseForMetatag03");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.getSingleResponseForMetatag("SHD.metatagWithDefault");
		assertEquals("DEFAULT.FROM.EVOLUTION", result);
	}
	
	/**
	 * Does requesting a metatag that's marked as ignored in the evolution config return null?
	 */
	@Test
	public void testGetSingleResponseForMetatag04() throws Exception{
		Submission sub = createATestSubmission("testGetSingleResponseForMetatag04");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
		String result = objectUnderTest.getSingleResponseForMetatag("SHD.metatagIgnored");
		assertNull(result);
	}
	
	/**
	 * Are the expected values returned when we request them for a multiselect metatag?
	 */
	@Test
	public void testGetResponsesFromMultiselectTag01() throws Exception{
		Submission sub = createATestSubmission("testGetResponsesFromMultiselectTag01");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
	    List<String> result = objectUnderTest.getResponsesFromMultiselectTag("SHD.datasetKeyword");
	    assertThat(result.size(), is(3));
	    assertThat(result, hasItems(DATASET_KEYWORD_RESPONSE1, DATASET_KEYWORD_RESPONSE2, DATASET_KEYWORD_RESPONSE3));
	}
	
	/**
	 * Is the expected exception thrown when we request a metatag that doesn't exist?
	 */
	@Test(expected = MetaInfoExtractorException.class)
	public void testGetSubmissionAnswerForMetatag01() throws Exception{
		Submission sub = createATestSubmission("testGetSubmissionAnswerForMetatag01");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
        objectUnderTest.getSubmissionAnswerForMetatag("SHT.bogus");
	}
	
	/**
	 * Can we get responses via the SubmissionAnswer object?
	 */
	@Test
	public void testGetSubmissionAnswerForMetatag02() throws Exception{
		Submission sub = createATestSubmission("testGetSubmissionAnswerForMetatag02");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
		SubmissionAnswer result = objectUnderTest.getSubmissionAnswerForMetatag("SHD.datasetKeyword");
		assertTrue("This answer should have a result because we set them in the setup", result.hasResponse());
		List<String> resultResponses = objectUnderTest.getResponsesFromMultiselectAnswer(result);
		assertThat(resultResponses.size(), is(3));
	    assertThat(resultResponses, hasItems(DATASET_KEYWORD_RESPONSE1, DATASET_KEYWORD_RESPONSE2, DATASET_KEYWORD_RESPONSE3));
	}
	
	/**
	 * Can we get responses from a question set?
	 */
	@Test
	public void testGetResponseListFromQuestionSet01() throws Exception{
		Submission sub = createATestSubmission("testGetResponseListFromQuestionSet01");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
		List<String> result = objectUnderTest.getResponseListFromQuestionSet("SHD.methodName");
		assertThat(result.size(), is(2));
		assertThat(result, hasItems(METHOD_NAME_RESPONSE1, METHOD_NAME_RESPONSE2));
	}
	
	/**
	 * Can we get the list of authors?
	 */
	@Test
	public void testGetAuthorNameList01() throws Exception{
		Submission sub = createATestSubmission("testGetAuthorNameList01");
		MetaInfoExtractor objectUnderTest = metaInfoExtractorFactory.getInstance(sub.getId());
		List<String> result = objectUnderTest.getAuthorNameList("SHD.authorGiven", "SHD.authorSurname");
		assertThat(result.size(), is(2));
		String author1 = AUTHOR1_GIVEN_NAME_RESPONSE + " " + AUTHOR1_SURNAME_RESPONSE;
		String author2 = AUTHOR2_GIVEN_NAME_RESPONSE + " " + AUTHOR2_SURNAME_RESPONSE;
		assertThat(result, hasItems(author1, author2));
	}
	
	private Submission createATestSubmission(String submissionName) throws Exception {
		QuestionnaireConfig config = configService.getQuestionnaireConfig("sharedQuestionnaireTest.xml", "122", false);
		SharedUser su = new SharedUser();
		su.setUsername("TestyMcTest");
		sharedUserDao.save(su);
		//Add an answer for each question that we wish to index
		Submission submission = new Submission();
		submission.setSubmitter(su);
		submission.setDoi("TEST-DOI-" + submissionName);
		submission.setTitle(submissionName);
		submission.setQuestionnaireConfig( configService.getQuestionnaireConfigEntity(config.getSmsQuestionnaireId()) );
		submission.setStatus(SubmissionStatus.PUBLISHED);
		
		//SHD.submissionName   1.1
		submission.getAnswers().add(createSubmissionAnswer("1.1", submissionName, null, ResponseType.TEXT, submission));
		//SHD.datasetFullName  2.1
		submission.getAnswers().add(createSubmissionAnswer("2.1", "DATASET NAME " + submissionName, null, ResponseType.TEXT, submission));
		//SHD.datasetShortName 2.2
		submission.getAnswers().add(createSubmissionAnswer("2.2", "DS " + submissionName, null, ResponseType.TEXT, submission));
		
		//Author question
		SubmissionAnswer groupAuthorSA = createSubmissionAnswer("3.4.11", null, null, ResponseType.MULTIPLE_QUESTION_GROUP, submission);
		
		QuestionSetEntity quse1 = new QuestionSetEntity();
		quse1.setMultipleQuestionGroupId("3.4.11");
		quse1.getAnswerMap().put("3.4.11.1", createSubmissionAnswer("3.4.11.1", AUTHOR1_GIVEN_NAME_RESPONSE, null, ResponseType.TEXT, null) );
		quse1.getAnswerMap().put("3.4.11.2", createSubmissionAnswer("3.4.11.2", AUTHOR1_SURNAME_RESPONSE, null, ResponseType.TEXT, null) );
		groupAuthorSA.getQuestionSetList().add(quse1);
		
		QuestionSetEntity quse2 = new QuestionSetEntity();
		quse2.setMultipleQuestionGroupId("3.4.11");
		quse2.getAnswerMap().put("3.4.11.1", createSubmissionAnswer("3.4.11.1", AUTHOR2_GIVEN_NAME_RESPONSE, null, ResponseType.TEXT, null) );
		quse2.getAnswerMap().put("3.4.11.2", createSubmissionAnswer("3.4.11.2", AUTHOR2_SURNAME_RESPONSE, null, ResponseType.TEXT, null) );
		groupAuthorSA.getQuestionSetList().add(quse2);
		submission.getAnswers().add(groupAuthorSA);
		
		//SHD.methodName        5.6 -> 5.6.1
		//SHD.methodAbstract    5.6 -> 5.6.2
		SubmissionAnswer msA = createSubmissionAnswer("5.6", null, null, ResponseType.MULTIPLE_QUESTION_GROUP, submission);
		
		QuestionSetEntity qse1 = new QuestionSetEntity();
		qse1.setMultipleQuestionGroupId("5.6");
		qse1.getAnswerMap().put("5.6.1", createSubmissionAnswer("5.6.1", METHOD_NAME_RESPONSE1, null, ResponseType.TEXT, null) );
		qse1.getAnswerMap().put("5.6.2", createSubmissionAnswer("5.6.2", "MethodName1 abstract description", null, ResponseType.TEXT_BOX, null) );
		msA.getQuestionSetList().add(qse1);
		
		QuestionSetEntity qse2 = new QuestionSetEntity();
		qse2.setMultipleQuestionGroupId("5.6");
		qse2.getAnswerMap().put("5.6.1", createSubmissionAnswer("5.6.1", METHOD_NAME_RESPONSE2, null, ResponseType.TEXT, null) );
		qse2.getAnswerMap().put("5.6.2", createSubmissionAnswer("5.6.2", "MethodNam2 abstract description bonkers", null, ResponseType.TEXT_BOX, null) );
		msA.getQuestionSetList().add(qse2);
		submission.getAnswers().add(msA);
		
		//SHD.datasetKeyword    5.1.1
		SubmissionAnswer keywords = createSubmissionAnswer("5.1.1", null, null, ResponseType.MULTISELECT_TEXT, null);
		keywords.getMultiselectAnswerList().add(createSubmissionAnswer("5.1.1", DATASET_KEYWORD_RESPONSE1, null, ResponseType.TEXT, null));
		keywords.getMultiselectAnswerList().add(createSubmissionAnswer("5.1.1", DATASET_KEYWORD_RESPONSE2, null, ResponseType.TEXT, null));
		keywords.getMultiselectAnswerList().add(createSubmissionAnswer("5.1.1", DATASET_KEYWORD_RESPONSE3, null, ResponseType.TEXT, null));
		submission.getAnswers().add(keywords);
		
		//SHD.datasetAbstract   5.1.2
		submission.getAnswers().add(createSubmissionAnswer("5.1.2", "This is the dataset abstract, its pretty awesome", null, ResponseType.TEXT_BOX, submission));
		
		
		//SHD.taxonName         5.4.1.2
		SubmissionAnswer multiselectCV = createSubmissionAnswer("5.4.1.2", null, null, ResponseType.MULTISELECT_CONTROLLED_VOCAB, submission);
		multiselectCV.getMultiselectAnswerList().add(createSubmissionAnswer("5.4.1.2", "abutilon cryptopetalum", "Abutilon cryptopetalum", ResponseType.CONTROLLED_VOCAB, null));
		multiselectCV.getMultiselectAnswerList().add(createSubmissionAnswer("5.4.1.2", "acacia aneura", "Acacia aneura", ResponseType.CONTROLLED_VOCAB, null));
		submission.getAnswers().add(multiselectCV);     
		//SHD.datasetPublicationYear  3.4.2
		submission.getAnswers().add(createSubmissionAnswer("3.4.2", "2010", null, ResponseType.TEXT, submission));
		//SHD.firstStudyLocationVisit  5.3.1
		submission.getAnswers().add(createSubmissionAnswer("5.3.1", "01/01/2010", null, ResponseType.DATE, submission));
		//SHD.lastStudyLocationVisit   5.3.2
		submission.getAnswers().add(createSubmissionAnswer("5.3.2", "02/02/2010", null, ResponseType.DATE, submission));
		
		//SHD.fieldsOfResearch         5.5.2
		//SHD.studyLocation
		//Add a test site file from the src/test/resources directory
		SubmissionData sd = new SubmissionData();
		sd.setFileName("sites-test-good-9-sites.csv");
		sd.setFileDescription("Test site file");
		sd.setSiteFileCoordinateSystem("EPSG:4283");
		sd.setSubmissionDataType(SubmissionDataType.SITE_FILE);
		
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setObjectName("sites-test-good-9-sites.csv");
		fssl.setFspath("src/test/resources/");
		sd.getStorageLocations().add(fssl);
		submission.getSubmissionDataList().add(sd);
		
		//SHD.studyAreaGeometry
		//Lets make up a study area geometry json String with 2 polygons 
		//it`ll overload the geo_mv field , but that does`nt matter for this part of testing
		JsonGeoFeatureSet jgfs = new JsonGeoFeatureSet();
		jgfs.setQuestionId("5.2.2.2");
		
		JsonFeature jf = new JsonFeature();
		jf.setId("PLY_1");
		jf.setDescription("Description");
		jf.setGeometry("POLYGON((140.0 -30.0,141.0 -30.0,141.0 -29.0,140.0 -30.0))");
		jgfs.getFeatures().add(jf);
		JsonFeature jf2 = new JsonFeature();
		jf2.setId("PLY_2");
		jf2.setDescription("Description");
		jf2.setGeometry("POLYGON((129.0 -25.0,130.0 -25.0,130.0 -24.0,129.0 -24.0,129.0 -25.0))");
		jgfs.getFeatures().add(jf2);
		
		Gson gson = new Gson(); 
		String jsonStr =gson.toJson(jgfs);
		submission.getAnswers().add(createSubmissionAnswer("5.2.2.2", jsonStr, null, ResponseType.GEO_FEATURE_SET, submission));
		
		//SHD.totalNumberStudyLocation   5.2.2.3
		submission.getAnswers().add(createSubmissionAnswer("5.2.2.3", "2", null, ResponseType.TEXT, submission));
		
		submissionDao.save(submission);
		return submission;
	}
	
	private SubmissionAnswer createSubmissionAnswer(String qId, String response, String displayResponse, ResponseType responseType,  Submission submission){
		SubmissionAnswer sa = new SubmissionAnswer();
		sa.setQuestionId(qId);
		sa.setResponse(response);
		sa.setDisplayResponse(displayResponse);
		sa.setResponseType(responseType);
		sa.setSubmission(submission);
		return sa;
	}
	
	public static void cleanCache() {
		// It's not ideal but I can't figure out how to disable ehcache just for testing
		CacheManager.getInstance().getCache(MetaInfoExtractorFactory.CACHE_NAME).removeAll();
	}
}
