package au.edu.aekos.shared.web.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class SubmissionDataFileTypeTest {

	/**
	 * Can we get the title for an existing value?
	 */
	@Test
	public void testGetTitleForCode01() {
		String enumCode = "DATA";
		String result = SubmissionDataFileType.getTitleForCode(enumCode);
		assertEquals(SubmissionDataFileType.DATA.getTitle(), result);
	}

	/**
	 * Can we get something when the value doesn't exist?
	 */
	@Test
	public void testGetTitleForCode02() {
		String enumCode = "NOT-THERE";
		String result = SubmissionDataFileType.getTitleForCode(enumCode);
		assertEquals(SubmissionDataFileType.NO_TITLE, result);
	}
}
