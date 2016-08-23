package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionHistory;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class SubmissionHistoryTest {

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionHistoryDao submissionHistoryDao;
	
	@Test
	public void testSubmissionHistory(){
		Long submissionId = setUpSubmission();
		Submission s = submissionDao.findById(submissionId);
		SubmissionHistory history = submissionHistoryDao.createSubmissionHistory(s);
		assertNotNull(history);
		int numHistAnswers = history.getAnswers().size();
		s.getAnswers().remove(1);
		submissionDao.save(s);
		submissionDao.flush();
		SubmissionHistory history1 = submissionHistoryDao.findById(history.getId());
		assertEquals(numHistAnswers, history1.getAnswers().size());
	}
	
	@Test
	public void testSubmissionHistory_DataSorage(){
		Long submissionId = setUpSubmissionWithSubmissionData();
		Submission s = submissionDao.findById(submissionId);
		SubmissionHistory history = submissionHistoryDao.createSubmissionHistory(s);
		assertNotNull(history);
		int numHistAnswers = history.getAnswers().size();
		s.getAnswers().remove(1);
		submissionDao.save(s);
		submissionDao.flush();
		SubmissionHistory history1 = submissionHistoryDao.findById(history.getId());
		assertEquals(numHistAnswers, history1.getAnswers().size());
	}
	
	private Long setUpSubmission(){
		SharedUser su = new SharedUser();
		su.setUsername("fred bassett1");
		sharedUserDao.save(su);
		//try save submission without a SharedUser
		Submission sub = new Submission();
		sub.setStatus(SubmissionStatus.SUBMITTED);
		sub.setSubmitter(su);
		
		//Lets add some answers
		SubmissionAnswer a1 = new SubmissionAnswer();
		a1.setQuestionId("1");
		a1.setResponse("The Response");
		a1.setResponseType(ResponseType.TEXT);
		sub.getAnswers().add(a1);
		
		SubmissionAnswer a2 = new SubmissionAnswer();
		a2.setQuestionId("2");
		a2.setResponse("The Response number 2");
		a2.setResponseType(ResponseType.TEXT);
		sub.getAnswers().add(a2);
		
		sub.setSubmissionDate(new Date());
		//sub.setQuestionnaireVersion("1");
		
	    submissionDao.save(sub);
	    submissionDao.flush();
	    
	    return sub.getId();
	}
	
	private Long setUpSubmissionWithSubmissionData(){
		SharedUser su = new SharedUser();
		su.setUsername("fred bassett3");
		sharedUserDao.save(su);
		//try save submission without a SharedUser
		Submission sub = new Submission();
		sub.setStatus(SubmissionStatus.SUBMITTED);
		sub.setSubmitter(su);
		
		//Lets add some answers
		SubmissionAnswer a1 = new SubmissionAnswer();
		a1.setQuestionId("1");
		a1.setResponse("The Response");
		a1.setResponseType(ResponseType.TEXT);
		sub.getAnswers().add(a1);
		
		SubmissionAnswer a2 = new SubmissionAnswer();
		a2.setQuestionId("2");
		a2.setResponse("The Response number 2");
		a2.setResponseType(ResponseType.TEXT);
		sub.getAnswers().add(a2);
		
		sub.setSubmissionDate(new Date());
		//sub.setQuestionnaireVersion("1");
		
		SubmissionData sd = new SubmissionData();
		sd.setFileDescription("An archive containg dbfs, access and a shapefile");
		sd.setFileName("insect_tomology.zip");
		sd.setEmbargoDate(new Date());
		
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setFspath("a1042238/{subId}/insect_tomology.zip");
		sd.getStorageLocations().add(fssl);
		
		sub.getSubmissionDataList().add(sd);
		
	    submissionDao.save(sub);
	    submissionDao.flush();
	    
	    return sub.getId();
	}
}
