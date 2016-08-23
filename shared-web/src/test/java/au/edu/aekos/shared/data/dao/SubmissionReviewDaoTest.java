package au.edu.aekos.shared.data.dao;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.AnswerReview;
import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.FileReview;
import au.edu.aekos.shared.data.entity.ReviewOutcome;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionReview;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.factory.PersistedTestEntityFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class SubmissionReviewDaoTest {
	
	private final static String STRING_LONGER_THAN_255_CHARS = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In odio tellus, ultricies in velit "
			+ "in, fermentum ornare urna. Donec imperdiet nulla pellentesque metus commodo venenatis. Etiam facilisis suscipit lobortis. Phasellus "
			+ "sollicitudin nibh sed sapien laoreet hendrerit. Quisque imperdiet eget arcu a faucibus. Nulla felis orci, pharetra at enim id, "
			+ "luctus cursus est. Quisque dapibus enim bibendum volutpat aliquam. Pellentesque ornare eros sit amet urna sodales, a hendrerit libero sagittis.";
	
	@Autowired
	private SubmissionReviewDao submissionReviewDao;
	
	@Autowired
	private PersistedTestEntityFactory testEntityFactory;
	
	@Test
	public void testDefaultReviewDaoPersist_Publish() throws Exception{
		Submission sub = testEntityFactory.getDefaultTestSubmissionEntity("ben", SubmissionStatus.SUBMITTED);
		
		SubmissionReview submissionReview = new SubmissionReview();
		submissionReview.setSubmission(sub);
        submissionReview.setNotes("This is a test review");
        submissionReview.setReviewOutcome(ReviewOutcome.PUBLISH);
        submissionReview.setReviewDate(new Date());
        submissionReview.setReviewer(testEntityFactory.createUserWithUsername("reviewMan2069"));
        
        for(SubmissionAnswer ans : sub.getAnswers() ){
            AnswerReview review = new AnswerReview();
        	review.setQuestionId(ans.getQuestionId());
        	review.setAnswerReviewOutcome(AnswerReviewOutcome.PASS);
        	review.setComment("Pass with flying colours");
            submissionReview.getAnswerReviews().add(review);
        }
        
        for(SubmissionData data : sub.getSubmissionDataList()){
        	FileReview fr = new FileReview();
        	fr.setReviewOutcome(SubmissionFileReviewOutcome.PASS);
        	fr.setComments(STRING_LONGER_THAN_255_CHARS);
        	fr.setSubmissionData(data);
        	submissionReview.getFileReviews().add(fr);
        }
		
        submissionReviewDao.save(submissionReview);
        Assert.assertNotNull(submissionReview.getId());
	}
}
