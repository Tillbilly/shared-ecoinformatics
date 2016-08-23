package au.edu.aekos.shared.service.index;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import au.edu.aekos.shared.data.dao.CustomVocabularyDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.CustomVocabulary;
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
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.web.json.JsonFeature;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
@Transactional
public class PublishSubmissionToSolrTestIT {
	
	private static final int SUCCESS = 0;
	private static final Logger logger = Logger.getLogger(PublishSubmissionToSolrTestIT.class);

	@Autowired
	private SolrIndexService objectUnderTest;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private CustomVocabularyDao customVocabularyDao;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	/**
	 * Can we add a document to Solr, then query it?
	 */
	@Test
	public void testAddSubmissionToSolr01() throws Throwable{
		initialiseCustomTraits();
		Submission s = createATestSubmission1("test");
		Integer result = objectUnderTest.addSubmissionToSolr(s);
		assertEquals(SUCCESS, result.intValue());
		assertThatDocumentCanBeQueried(s);
		deleteDocumentFromSolr(s);
		submissionDao.delete(s);
	}

	private void deleteDocumentFromSolr(Submission s) throws Throwable {
		SolrServer server = null;
		try {
			Long id = s.getId();
			logger.info("Deleting the Solr document with ID: " + id);
			server = objectUnderTest.getSolrServer();
			server.deleteById(String.valueOf(id));
			server.commit();
			logger.info("Solr document deleted.");
		}finally{
			if(server != null){
				server.shutdown();
			}
		}
	}

	private void assertThatDocumentCanBeQueried(Submission s) throws SolrServerException {
	    SolrServer server = objectUnderTest.getSolrServer();
	    QueryResponse rsp = server.query(new SolrQuery("id:" + s.getId().toString()));
	    assertEquals(1, rsp.getResults().size());
	    SolrDocument doc = rsp.getResults().get(0);
	    logger.info("Solr document contents:");
	    for(Entry<String, Object> entry : doc.entrySet()){
	    	logger.info(entry.getKey() + "=" + entry.getValue());
	    }
	}
	
	private Submission createATestSubmission1(String submissionName) throws Exception {
		QuestionnaireConfig config = configService.getQuestionnaireConfig("sharedQuestionnaireTest.xml", "122", false);
		SharedUser su = new SharedUser();
		su.setUsername("TestyMcTest1");
		sharedUserDao.save(su);
		//Add an answer for each question that we wish to index
		Submission submission = new Submission();
		submission.setSubmitter(su);
		submission.setDoi("PublishSubmissionToSolrTestIT.java-" + submissionName);
		submission.setTitle(submissionName);
		submission.setQuestionnaireConfig( configService.getQuestionnaireConfigEntity(config.getSmsQuestionnaireId()) );
		submission.setStatus(SubmissionStatus.PUBLISHED);
		
		//SHD.submissionName   1.1
		submission.getAnswers().add(createSubmissionAnswer("1.1", submissionName, null, ResponseType.TEXT, submission));
		//SHD.datasetFullName  2.1
		submission.getAnswers().add(createSubmissionAnswer("2.1", "DATASET NAME " + submissionName, null, ResponseType.TEXT, submission));
		//SHD.datasetShortName 2.2
		submission.getAnswers().add(createSubmissionAnswer("2.2", "DS " + submissionName, null, ResponseType.TEXT, submission));
		
		//SHD.methodName        5.6 -> 5.6.1
		//SHD.methodAbstract    5.6 -> 5.6.2
		SubmissionAnswer msA = createSubmissionAnswer("5.6", null, null, ResponseType.MULTIPLE_QUESTION_GROUP, submission);
		
		QuestionSetEntity qse1 = new QuestionSetEntity();
		qse1.setMultipleQuestionGroupId("5.6");
		qse1.getAnswerMap().put("5.6.1", createSubmissionAnswer("5.6.1", "MethodName1", null, ResponseType.TEXT, null) );
		qse1.getAnswerMap().put("5.6.2", createSubmissionAnswer("5.6.2", "MethodName1 abstract description", null, ResponseType.TEXT_BOX, null) );
		msA.getQuestionSetList().add(qse1);
		
		QuestionSetEntity qse2 = new QuestionSetEntity();
		qse2.setMultipleQuestionGroupId("5.6");
		qse2.getAnswerMap().put("5.6.1", createSubmissionAnswer("5.6.1", "MethodName2", null, ResponseType.TEXT, null) );
		qse2.getAnswerMap().put("5.6.2", createSubmissionAnswer("5.6.2", "MethodNam2 abstract description bonkers", null, ResponseType.TEXT_BOX, null) );
		msA.getQuestionSetList().add(qse2);
		submission.getAnswers().add(msA);
		
		//SHD.datasetKeyword    5.1.1
		SubmissionAnswer keywords = createSubmissionAnswer("5.1.1", null, null, ResponseType.MULTISELECT_TEXT, null);
		keywords.getMultiselectAnswerList().add(createSubmissionAnswer("5.1.1", "Data", null, ResponseType.TEXT, null));
		keywords.getMultiselectAnswerList().add(createSubmissionAnswer("5.1.1", "Monkeys", null, ResponseType.TEXT, null));
		keywords.getMultiselectAnswerList().add(createSubmissionAnswer("5.1.1", "Bananas", null, ResponseType.TEXT, null));
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
		sd.setFileName("src/test/resources/sites.csv");
		sd.setFileDescription("Test site file");
		sd.setSiteFileCoordinateSystem("EPSG:4283");
		sd.setSubmissionDataType(SubmissionDataType.SITE_FILE);
		
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setObjectName("sites.csv");
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
	
	private void initialiseCustomTraits() throws SQLException{
		List<String> cvLines = new ArrayList<String>();
		cvLines.add("rifcsIdentifierType,abn,,Australian Business Number,Australian Business Number");
		cvLines.add("rifcsIdentifierType,arc,,Australian Research Council identifier,Australian Research Council identifier");
		cvLines.add("rifcsIdentifierType,ark,,ARK Persistent Identifier Scheme,ARK Persistent Identifier Scheme");
		cvLines.add("rifcsRelatedInformationType,publication,,publication,\"Any formally published document, whether available in digital or online form or not.\"");
		cvLines.add("rifcsRelatedInformationType,website,,website,\"Any publicly accessible web location containing information related to the collection, activity, party or service.\"");
		cvLines.add("rifcsRelatedInformationType,reuseInformation,,reuseInformation,\"Information that supports reuse of data, such as data definitions, instrument calibration or settings, units of measurement, sample descriptions, experimental parameters, methodology, data analysis techniques, or data derivation rules.\"");
		cvLines.add("rifcsRelatedInformationType,dataQualityInformation,,dataQualityInformation,Data quality statements or summaries of data quality issues affecting the data.");
		cvLines.add("rifcsRelatedInformationIdentifierType,ark,,ARK Persistent Identifier Scheme,ARK Persistent Identifier Scheme");
		cvLines.add("rifcsRelatedInformationIdentifierType,doi,,Digital Object Identifier,Digital Object Identifier");
		cvLines.add("rifcsRelatedInformationIdentifierType,ean13,,International Article Number,International Article Number");
		cvLines.add("rifcsRelatedInformationIdentifierType,eissn,,Electronic International Standard Serial Number,Electronic International Standard Serial Number");
		cvLines.add("rifcsRelatedInformationIdentifierType,handle,,HANDLE System Identifier,HANDLE System Identifier");
		cvLines.add("rifcsRelatedInformationIdentifierType,infouri,,info' URI scheme,info' URI scheme");
		cvLines.add("licenseType,Creative Commons By Attribution,,Creative Commons By Attribution,\"This license lets others distribute, remix, tweak, and build upon your work, even commercially, as long as they credit you for the original creation. This is the most accommodating of licenses offered. Recommended for maximum dissemination and use of licensed materials.\"");
		cvLines.add("licenseType,Creative Commons By Attribution-NoDerivs,,Creative Commons By Attribution-NoDerivs,\"This license allows for redistribution, commercial and non-commercial, as long as it is passed along unchanged and in whole, with credit to you.\"");
		cvLines.add("rifcsPartyRelationType,isParticipantIn,,is Participant In,Is enrolled in the related activity");
		cvLines.add("rifcsPartyRelationType,enriches,,enriches,Provides additional value to a collection");
		cvLines.add("anzsrcfor,10101,,Algebra and Number Theory,Algebra and Number Theory");
		cvLines.add("anzsrcfor,10102,,Algebraic and Differential Geometry,Algebraic and Differential Geometry");
		cvLines.add("taxonomicRank,FAMILY,,Family,Family");
		cvLines.add("taxonomicRank,GENUS,,Genus,Genus");
		cvLines.add("taxonomicRank,SPECIES,,Species,Species");
		cvLines.add("anzsrcseo,869802,,Management of Greenhouse Gas Emissions from Manufacturing Activities,Management of Greenhouse Gas Emissions from Manufacturing Activities");
		cvLines.add("anzsrcseo,869803,,Management of Liquid Waste from Manufacturing Activities (excl. Water),Management of Liquid Waste from Manufacturing Activities (excl. Water)");
		cvLines.add("researchProgramme,LTERN,,Long-Term Ecological Research Network,Long-Term Ecological Research Network");
		cvLines.add("researchProgramme,LTERN - Victorian Tall Eucalypt Forest,,Long-Term Ecological Research Network - Victorian Tall Eucalypt Forest,Long-Term Ecological Research Network - Victorian Tall Eucalypt Forest");
		
		List<CustomVocabulary> customVocabularyList = new ArrayList<CustomVocabulary>();
		for(String line : cvLines){
			CustomVocabulary cv = controlledVocabularyService.buildCustomVocabFromFileLine(line);
			customVocabularyList.add(cv);
		}
		customVocabularyDao.batchLoad(customVocabularyList);
	}
}
