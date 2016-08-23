package au.edu.aekos.shared.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionHistoryDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionHistory;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.submission.SubmissionSearchService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.service.submission.SubmissionServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)
public class SubmissionServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(SubmissionServiceTest.class);
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SubmissionSearchService submissionSearchService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionHistoryDao submissionHistoryDao;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	private static final String USER = "test-sst";
	private static final String USER2 = "test-default2";
	
	@Test @Transactional 
	public void testHasChangedMultiselectAnswerLists(  ){
		SubmissionServiceImpl subServ = new SubmissionServiceImpl();
		Answer answer = new Answer();
		
		SubmissionAnswer subAnswer = new SubmissionAnswer();
		
		assertFalse( subServ.hasChangedMultiselectAnswerLists(answer, subAnswer) );
		
		Answer a1 = new Answer();
		a1.setResponse("yes");
		Answer a2 = new Answer();
		a2.setResponse("no");
		answer.getMultiselectAnswerList().add(a1);
		answer.getMultiselectAnswerList().add(a2);
		
		assertTrue( subServ.hasChangedMultiselectAnswerLists(answer, subAnswer) );
		
		SubmissionAnswer sa1 = new SubmissionAnswer();
		sa1.setResponse("yes");
		SubmissionAnswer sa2 = new SubmissionAnswer();
		sa2.setResponse("no");
		
		subAnswer.getMultiselectAnswerList().add(sa1);
		subAnswer.getMultiselectAnswerList().add(sa2);
		
		assertFalse( subServ.hasChangedMultiselectAnswerLists(answer, subAnswer) );
		
		sa2.setResponse("nono");
		assertTrue( subServ.hasChangedMultiselectAnswerLists(answer, subAnswer) );
	}
	
	/**
	 * Test the service fails with null parameters
	 */
	@Test @Transactional
	public void testCreateNewSubmission01() throws Exception {
		try {
			submissionService.createNewSubmission(null, null, null);
			fail();
		} catch (Exception e) {
			// Success!
		}
	}
	
	/**
	 * Can we create a new submission?
	 */
	@Test @Transactional
	public void testCreateNewSubmission02() throws Exception {
		String username = USER;
		SubmissionStatus status = SubmissionStatus.SUBMITTED;
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		Long submissionId = submissionService.createNewSubmission(username, dc, status);
		assertNotNull(submissionId);
		logger.info("Created submission ID " + submissionId);
		Submission sub = submissionDao.findById(submissionId);
		assertNotNull(sub);
		assertEquals(6, sub.getAnswers().size() );
	}
	
	@Test @Transactional
    public void testUpdatePartiallySavedSubmission() throws Exception{
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		Long submissionId = submissionService.createNewSubmission(USER, dc, SubmissionStatus.INCOMPLETE);
		dc.setSubmissionId(submissionId);
		Map<String,Answer> answerMap = dc.getAllAnswers();
		Answer ans = answerMap.get("1.0");
		ans.setResponse("UPDATED");
		submissionService.updateSubmission(USER, dc, SubmissionStatus.PUBLISHED);
    	Submission sub = submissionDao.findById(submissionId);
    	assertNotNull(sub);
    	List<SubmissionHistory> shList = submissionHistoryDao.retrieveSubmissionHistoryForSubmission(submissionId);
    	assertNotNull(shList);
    	assertEquals(1, shList.size());
    	SubmissionHistory submissionHistory = shList.get(0);
    	List<SubmissionAnswer> submissionAnswerList = submissionHistory.getAnswers();
    	assertEquals(6, submissionAnswerList.size());
	}
	
	@Test @Transactional
    public void testUpdatePartiallySavedSubmission02() throws Exception{
		DisplayQuestionnaire dq = new DisplayQuestionnaire(configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false));
		assertNotNull(dq);
		populateTestQuestionnaireAnswers1(dq,true);
		Long submissionId = submissionService.createNewSubmission(USER, dq, SubmissionStatus.INCOMPLETE);
		dq.setSubmissionId(submissionId);
		Map<String,Answer> answerMap = dq.getAllAnswers();
		Answer ans = answerMap.get("1.0");
		
		ans.setResponse("UPDATED");
		submissionService.updateSubmission(USER, dq, SubmissionStatus.INCOMPLETE);
    	Submission sub = submissionDao.findById(submissionId);
    	assertEquals("UPDATED", sub.getAnswersMappedByQuestionId().get("1.0").getResponse());
    	
    	ans.setResponse("UPDATED2");
		submissionService.updateSubmission(USER, dq, SubmissionStatus.INCOMPLETE);
    	sub = submissionDao.findById(submissionId);
    	assertEquals("UPDATED2", sub.getAnswersMappedByQuestionId().get("1.0").getResponse());
    	
    	ans.setResponse("UPDATED3");
		submissionService.updateSubmission(USER, dq, SubmissionStatus.DRAFT);
    	sub = submissionDao.findById(submissionId);
    	assertEquals("UPDATED3", sub.getAnswersMappedByQuestionId().get("1.0").getResponse());
	}
	
	@Test @Transactional
	public void testUpdateMultipleAutoCompleteSavedSubmission() throws Exception {
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc, false);
		Long submissionId = submissionService.createNewSubmission(USER, dc, SubmissionStatus.DRAFT);
		dc.setSubmissionId(submissionId);
		Map<String, Answer> answerMap = dc.getAllAnswers();
		Answer ans = answerMap.get("1.0");
		ans.setResponse("UPDATED");
		submissionService.updateSubmission(USER, dc, SubmissionStatus.DRAFT);
		Submission sub = submissionDao.findById(submissionId);
		assertNotNull(sub);
		List<SubmissionHistory> shList = submissionHistoryDao.retrieveSubmissionHistoryForSubmission(submissionId);
		assertNotNull(shList);
		// No need to update History hence should be zero
		assertEquals(0, shList.size());
	}
	
	@Test @Transactional
	public void testGetSubmissionsForUser() throws Exception{
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		Long submissionId = submissionService.createNewSubmission(USER2, dc, SubmissionStatus.INCOMPLETE);
		assertNotNull(submissionId);
		List<Submission> subList = submissionSearchService.getSubmissionsForUser("blerg");
		assertEquals(0, subList.size());
		List<Submission> subList1 = submissionSearchService.getSubmissionsForUser(USER2);
		assertEquals(1, subList1.size());
	}
	
	@Test @Transactional
	public void testRetrieveSubmissionById() throws Exception{
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		Long submissionId = submissionService.createNewSubmission(USER, dc, SubmissionStatus.INCOMPLETE);
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		assertEquals(submissionId, sub.getId());
	}
	
	@Test @Transactional
	public void testGetDefaultSubmissionTitle(){
		String title = submissionService.getDefaultSubmissionTitle(USER);
		assertNotNull(title);
	}
	
	@Test @Transactional
	public void testPopulateDisplayQuestionnaireFromSubmissionEntity() throws Exception{
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		Long submissionId = submissionService.createNewSubmission(USER, dc, SubmissionStatus.INCOMPLETE);
		dc.setSubmissionId(submissionId);
		DisplayQuestionnaire dc2 = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(submissionId);
		Map<String, Answer> dc1AnswerMap = dc.getAllAnswers();
		Map<String, Answer> dc2AnswerMap = dc2.getAllAnswers();
		for(String qId : dc1AnswerMap.keySet()){
			assertNotNull( dc2AnswerMap.containsKey(qId) );
			Answer a1 = dc1AnswerMap.get(qId);
			Answer a2 = dc2AnswerMap.get(qId);
			if(! a1.equals(a2)){
			    fail();
			}
		}
	}
	
	/**
	 * Is a submission returned when the requested ID exists?
	 */
	@Test @Transactional
	public void testRetrieveSubmissionById01() throws Exception{
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		assertNotNull(dc);
		populateTestQuestionnaireAnswers1(dc,false);
		Long existingId = submissionService.createNewSubmission(USER, dc, SubmissionStatus.INCOMPLETE);
		Submission submission = submissionService.retrieveSubmissionById(existingId);
		assertNotNull(submission);
		assertEquals(existingId, submission.getId());
	}
	
	/**
	 * Is a null returned when the requested ID DOES NOT exist?
	 */
	@Test @Transactional
	public void testRetrieveSubmissionById02() throws Exception{
		Long nonExistentId = 666L;
		Submission submission = submissionService.retrieveSubmissionById(nonExistentId);
		assertNull(submission);
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
}
