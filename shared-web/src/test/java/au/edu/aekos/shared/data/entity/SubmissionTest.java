package au.edu.aekos.shared.data.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SubmissionTest {
	
	/**
	 * Can we get the username of the submitter
	 */
	@Test
	public void testGetSubmittingUsernamer01() {
		Submission objectUnderTest = new Submission();
		SharedUser submitter = new SharedUser();
		objectUnderTest.setSubmitter(submitter);
		submitter.setUsername("agorilla");
		String result = objectUnderTest.getSubmittingUsername();
		assertEquals("agorilla", result);
	}
	
	/**
	 * Can we tell when there are data files present?
	 */
	@Test
	public void testHasDataFiles01() throws ParseException {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setSubmissionDataList(Arrays.asList(new SubmissionData()));
		boolean result = objectUnderTest.hasDataFiles();
		assertTrue(result);
	}
	
	/**
	 * Can we tell when there are NOT data files present with an empty list?
	 */
	@Test
	public void testHasDataFiles02() throws ParseException {
		Submission objectUnderTest = new Submission();
		List<SubmissionData> emptyList = Collections.emptyList();
		objectUnderTest.setSubmissionDataList(emptyList);
		boolean result = objectUnderTest.hasDataFiles();
		assertFalse(result);
	}
	
	/**
	 * Can we tell when there are NOT data files present with a null list?
	 */
	@Test
	public void testHasDataFiles03() throws ParseException {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setSubmissionDataList(null);
		boolean result = objectUnderTest.hasDataFiles();
		assertFalse(result);
	}
	
	/**
	 * Can we correctly identify approved as a publicly available status?
	 */
	@Test
	public void testIsPubliclyAvailable01() {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setStatus(SubmissionStatus.APPROVED);
		boolean result = objectUnderTest.isPubliclyAvailable();
		assertTrue("Approved is a publicly available status", result);
	}
	
	/**
	 * Can we correctly identify approved as a publicly available status?
	 */
	@Test
	public void testIsPubliclyAvailable02() {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setStatus(SubmissionStatus.PUBLISHED);
		boolean result = objectUnderTest.isPubliclyAvailable();
		assertTrue("Published is a publicly available status", result);
	}
	
	/**
	 * One test (of the many possible) to make sure the negative case works.
	 */
	@Test
	public void testIsPubliclyAvailable03() {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setStatus(SubmissionStatus.PEER_REVIEWED);
		boolean result = objectUnderTest.isPubliclyAvailable();
		assertFalse("Peer reviewed is NOT a publicly available status", result);
	}
	
	/**
	 * Can we correctly identify approved as the 'being published' status?
	 */
	@Test
	public void testIsBeingPublished01() {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setStatus(SubmissionStatus.APPROVED);
		boolean result = objectUnderTest.isBeingPublished();
		assertTrue("Approved is the only 'being published' status", result);
	}
	
	/**
	 * Can we correctly identify a post-publishing status and not 'being published'?
	 */
	@Test
	public void testIsBeingPublished02() {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setStatus(SubmissionStatus.PUBLISHED);
		boolean result = objectUnderTest.isBeingPublished();
		assertFalse("Publishing is NOT a 'being published' status, it's past that point", result);
	}
	
	/**
	 * Can we correctly identify a pre-publishing status and not 'being published'?
	 */
	@Test
	public void testIsBeingPublished03() {
		Submission objectUnderTest = new Submission();
		objectUnderTest.setStatus(SubmissionStatus.PEER_REVIEWED);
		boolean result = objectUnderTest.isBeingPublished();
		assertFalse("Peer reviewed is NOT a 'being published' status", result);
	}
}
