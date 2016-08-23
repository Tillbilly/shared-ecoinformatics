package au.edu.aekos.shared.web.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SubmissionModelTest {

	/**
	 * Can we tell when a submission IS NOT owned by someone?
	 */
	@Test
	public void testIsOwnedBy01() {
		SubmissionModel objectUnderTest = new SubmissionModel();
		objectUnderTest.setSubmittedByUsername("agorilla");
		Boolean result = objectUnderTest.isOwnedBy("someoneElse");
		assertFalse(result);
	}
	
	/**
	 * Can we tell when a submission IS owned by someone?
	 */
	@Test
	public void testIsOwnedBy02() {
		SubmissionModel objectUnderTest = new SubmissionModel();
		objectUnderTest.setSubmittedByUsername("agorilla");
		Boolean result = objectUnderTest.isOwnedBy("agorilla");
		assertTrue(result);
	}
}
