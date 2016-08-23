package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
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
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
public class SubmissionDaoTest {

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	/**
	 * Can we save submission without a SharedUser?
	 */
	@Test(expected=ConstraintViolationException.class)
	@Transactional
	public void testSubmissionDao_saveWithoutUser(){
		Submission sub = new Submission();
	    submissionDao.save(sub);
	    submissionDao.flush();
	    submissionDao.delete(sub);
	}
	
	@Test @Transactional
	public void testSubmissionDao_createSubmission_basic(){
		SharedUser su = sharedUserDao.findUserByUsername("fred bassett");
		if(su == null){
		    su = new SharedUser();
		    su.setUsername("fred bassett");
		    sharedUserDao.save(su);
		}
		Submission sub = new Submission();
		sub.setStatus(SubmissionStatus.SUBMITTED);
		sub.setSubmitter(su);
	    submissionDao.save(sub);
	    submissionDao.flush();
	    
	    Long id = sub.getId();
	    Submission s = submissionDao.findById(id);
	    assertEquals("fred bassett", s.getSubmitter().getUsername());
	    submissionDao.delete(sub);
	}
	
	@Test @Transactional
	public void testSubmissionDao_createSubmission(){
		SharedUser su = sharedUserDao.findUserByUsername("fred bassett");
		if(su == null){
			su = new SharedUser();
			su.setUsername("fred bassett");
			sharedUserDao.save(su);
		}
		assertNotNull(su);
		
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
	    
	    Long id = sub.getId();
	    Submission s = submissionDao.findById(id);
	    assertEquals("fred bassett", s.getSubmitter().getUsername());
	    assertEquals( s.getAnswers().size() , 2);
	    submissionDao.delete(sub);
	}
	
	@Test @Transactional
	public void testRetrieveSubmissionsForReview(){
		List<Submission> submissionListx = submissionDao.retrieveSubmissionsForReview(null);
		System.out.println("***************************" + submissionListx.size());
		assertEquals("Should be 0 otherwise other tests haven't cleaned up after themselves. Exitsting submissions: " 
				+ extractSubmissionDetails(submissionListx), 0, submissionListx.size());
		SharedUser su = new SharedUser();
		String usernameToExclude = "fredBassett1";
		su.setUsername(usernameToExclude);
		sharedUserDao.save(su);
		sharedUserDao.flush();
		Submission sub = new Submission();
		sub.setStatus(SubmissionStatus.SUBMITTED);
		sub.setSubmitter(su);
	    submissionDao.save(sub);
	    submissionDao.flush();
  		Submission sub2 = new Submission();
  		sub2.setStatus(SubmissionStatus.RESUBMITTED);
  		sub2.setSubmitter(su);
  	    submissionDao.save(sub2);
  	    submissionDao.flush();
		
		List<Submission> submissionList = submissionDao.retrieveSubmissionsForReview(null);
		System.out.println("***************************" + submissionList.size());
		assertEquals(submissionList.size(), 2); 
		submissionList = submissionDao.retrieveSubmissionsForReview(usernameToExclude);
		Assert.assertEquals(0, submissionList.size());
		submissionDao.delete(sub);
		submissionDao.delete(sub2);
	}

	/**
	 * Somewhere out there is a test that doesn't clean up after itself.
	 * It's really hard to reproduce the test failure on demand so this will help
	 * track down the offender when it does happen.
	 * 
	 * @param submissionListx	submissions to extract details from
	 * @return					concatenated string of details for an error message
	 */
	private String extractSubmissionDetails(List<Submission> submissionListx) {
		StringBuilder result = new StringBuilder();
		for (Submission currSub : submissionListx) {
			result.append("sub[title=");
			result.append(currSub.getTitle());
			result.append(", status=");
			result.append(currSub.getStatus());
			result.append(", user=");
			result.append(currSub.getSubmittingUsername());
			result.append("] ");
		}
		return result.toString();
	}
}
