package au.edu.aekos.shared.data.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.SubmissionServiceTest;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.submission.SubmissionService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubmissionDataInverseSubmissionRelationshipIT {

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Autowired
	private SubmissionDao submissionDao;

	private static final String USER = "test-sdisrit";
	
	private static Long testSubmissionId = null;
	
	@Test @Transactional(propagation=Propagation.REQUIRED)
	public void testInverseRelationshipDoesntNeedToBeSetExplicitly(){
		//populateTestQuestionnaireAnswers1(DisplayQuestionnaire dc, boolean populateNonMandatory)
		SubmissionStatus status = SubmissionStatus.SUBMITTED;
		
		QuestionnaireConfig qc = null;
		try {
			qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		SubmissionServiceTest.populateTestQuestionnaireAnswers1(dc,false);
		
		SubmissionData sd = new SubmissionData();
		sd.setFileName("test");
		dc.getSubmissionDataList().add(sd);
		
		Long submissionId = null;
		try {
			submissionId = submissionService.createNewSubmission(USER, dc, status);
		} catch (Exception e) {
			fail();
		}
		assertNotNull(submissionId);
		System.out.println("Created submission ID " + submissionId );
		testSubmissionId = submissionId;
		//Need this test to end such that we create a new transaction / hibernate session
		//to query the submission Data 
	}
	
	@Test @Transactional(propagation=Propagation.REQUIRED) 
	public void utestInverseRelationshipDoesntNeedToBeSetExplicitly(){
		List<SubmissionData> sdList = submissionDataDao.getAll();
		assertEquals(1, sdList.size());
		SubmissionData sd2 = sdList.get(0);
		assertNotNull(sd2.getSubmission());
		assertEquals(testSubmissionId, sd2.getSubmission().getId());
		submissionDao.delete(sd2.getSubmission());
	}
	
	//Need to save a SubmissionData end transaction, then try to save a new
}
