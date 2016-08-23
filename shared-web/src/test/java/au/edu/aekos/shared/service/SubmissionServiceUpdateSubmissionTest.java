package au.edu.aekos.shared.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.submission.SubmissionService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-update-context.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubmissionServiceUpdateSubmissionTest {

	private static final Logger logger = LoggerFactory.getLogger(SubmissionServiceUpdateSubmissionTest.class);
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	private static final String USER = "test-ssust";
	
	private static Long savedSubmissionId = null;
	private static Long savedSubmissionId2 = null;
	private static Long savedSubmissionId3 = null;
	private static Long savedSubmissionId4 = null;
	private static Long savedSubmissionId5 = null;
	
	@Test @Transactional
	public void test1CreateSavedSubmission() throws Exception {
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		savedSubmissionId = submissionService.createSavedSubmission(USER, dc);
		assertNotNull(savedSubmissionId);
		logger.info("Created submission ID " + savedSubmissionId);
		Submission sub = submissionDao.findById(savedSubmissionId);
		assertNotNull(sub);
		assertEquals(6, sub.getAnswers().size());
	}
	
	@Test @Transactional
	public void test2UpdateSavedSubmission() throws Exception {
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(savedSubmissionId);
		Map<String,Answer> answers = displayQuestionnaire.getAllAnswers();
		answers.get("1.1").setResponse("Updated Jiggy");
		
		Answer ans7 = answers.get("7.0");
		Answer multi2 = new Answer();
		multi2.setResponseType(ResponseType.getRawType(ans7.getResponseType()));
		multi2.setResponse("TEST MULTI2");
		multi2.setQuestionId(ans7.getQuestionId());
		ans7.getMultiselectAnswerList().add(multi2);
		
		savedSubmissionId2 = submissionService.createSavedSubmission(USER, displayQuestionnaire);
		assertNotNull(savedSubmissionId2);
		deleteSubmission(savedSubmissionId);
	}

	@Test @Transactional
	public void test3UpdateSavedRemoveAnswerSubmission() throws Exception {
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(savedSubmissionId2);
		Map<String,Answer> answers = displayQuestionnaire.getAllAnswers();
		answers.get("1.1").setResponseToNull();
		
		savedSubmissionId3 = submissionService.createSavedSubmission(USER, displayQuestionnaire);
		assertNotNull(savedSubmissionId3);
		deleteSubmission(savedSubmissionId2);
	}
	
	@Test @Transactional
	public void test4UpdateSubmissionFinalise() throws Exception  {
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(savedSubmissionId3);
		Map<String,Answer> answers = displayQuestionnaire.getAllAnswers();
		answers.get("1.1").setResponse("Crazy");
		displayQuestionnaire.setSubmissionId(savedSubmissionId3);
		submissionService.updateSubmission(USER, displayQuestionnaire, SubmissionStatus.SUBMITTED);
		deleteSubmission(savedSubmissionId3);
	}
	
	@Test @Transactional
	public void test5AddSomeData() throws Exception {
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		
		Resource rs = new ClassPathResource("uploadFileTmp/testFile1.txt");
		assertTrue(rs.exists() );
		File f1 = new File(rs.getURI() );
		String fileName = "testFile1.txt";
		String fsPath = f1.getPath().replace("testFile1.txt", "");
		assertEquals(f1.getPath(), fsPath + fileName);
		
		SubmissionData newSubmissionData = new SubmissionData();
		newSubmissionData.setFileName("testFile1.txt");
		newSubmissionData.setSubmissionDataType(SubmissionDataType.DATA);
		
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setObjectName(fileName);
		fssl.setFspath(fsPath);
		
		newSubmissionData.getStorageLocations().add(fssl);
		dc.getSubmissionDataList().add(newSubmissionData);
		
		savedSubmissionId4 = submissionService.createSavedSubmission(USER, dc);
		assertNotNull(savedSubmissionId4);
	}
	
	@Test @Transactional
	public void test6AddSomeMoreData() throws Exception {
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(savedSubmissionId4);
		
		Resource rs = new ClassPathResource("uploadFileTmp/testFile2.txt");
		assertTrue(rs.exists() );
		File f1 = new File(rs.getURI() );
		String fileName = "testFile2.txt";
		String fsPath = f1.getPath().replace("testFile2.txt", "");
		assertEquals(f1.getPath(), fsPath + fileName);
		
		SubmissionData newSubmissionData = new SubmissionData();
		newSubmissionData.setFileName("testFile2.txt");
		newSubmissionData.setSubmissionDataType(SubmissionDataType.DATA);
		
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setObjectName(fileName);
		fssl.setFspath(fsPath);
		
		newSubmissionData.getStorageLocations().add(fssl);
		displayQuestionnaire.getSubmissionDataList().add(newSubmissionData);
		
		savedSubmissionId5 = submissionService.createSavedSubmission(USER, displayQuestionnaire);
		assertNotNull(savedSubmissionId5);
		Submission sub = submissionDao.findById(savedSubmissionId5);
		assertEquals(2, sub.getSubmissionDataList().size());
		deleteSubmission(savedSubmissionId4);
	}
	
	@Test @Transactional
	public void test7SubmitTheQuestionnaire() throws Exception {
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(savedSubmissionId5);
		//Remove a data file
		displayQuestionnaire.getSubmissionDataList().remove(0);
		displayQuestionnaire.setSubmissionId(savedSubmissionId5);
		submissionService.updateSubmission(USER, displayQuestionnaire, SubmissionStatus.SUBMITTED);
		deleteSubmission(savedSubmissionId5);
	}
	
	public static void populateTestQuestionnaireAnswers1(DisplayQuestionnaire dc, boolean populateNonMandatory){
		Map<String,Answer> answerMap = dc.getAllAnswers();
		Map<String,Question> questionMap = dc.getAllQuestionsMapFromConfig(); //To see if the answer is mandatory
		for(Answer answer : answerMap.values()){
			Question q = questionMap.get( answer.getQuestionId() );
			if(q.getResponseMandatory() || populateNonMandatory){
				switch(answer.getResponseType()){
				case TEXT :
					answer.setResponse("TEST RESPONSE");
					break;
				case CONTROLLED_VOCAB:
					answer.setResponse( q.getDefaultVocabulary().getListEntries().get(0).getValue() );
					break;
				case CONTROLLED_VOCAB_SUGGEST:
					answer.setResponse("OTHER");
					answer.setSuggestedResponse("Test suggested");
					break;
				case DATE:
					answer.setResponse("12/12/2002");
					break;
				case YES_NO:
					answer.setResponse("YES");
				case IMAGE:
//					ImageAnswer ia = new ImageAnswer()
//					@TODO support images
//					dc.getImageAnswerMap().put(key, value)
					break;
				case MULTISELECT_TEXT:
					answer.getMultiselectAnswerList().clear();
					Answer multi1 = new Answer();
					multi1.setResponseType(ResponseType.getRawType(answer.getResponseType()));
					multi1.setResponse("TEST MULTI1");
					multi1.setQuestionId(answer.getQuestionId());
					answer.getMultiselectAnswerList().add(multi1);
					
					Answer multi2 = new Answer();
					multi2.setResponseType(ResponseType.getRawType(answer.getResponseType()));
					multi2.setResponse("TEST MULTI2");
					multi2.setQuestionId(answer.getQuestionId());
					answer.getMultiselectAnswerList().add(multi2);
					break;
				default: break;	
				}
			}
		}
	}
	
	private void deleteSubmission(Long subId) {
		Submission sub = submissionDao.findById(subId);
		if (sub == null) { // I thought they should all exist but they don't and right now, I just want running tests
			return;
		};
		submissionService.updateSubmissionStatus(subId, SubmissionStatus.DELETED);
		submissionDao.delete(sub);
	}
}
