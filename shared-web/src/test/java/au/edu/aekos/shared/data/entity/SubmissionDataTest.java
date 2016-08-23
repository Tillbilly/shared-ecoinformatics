package au.edu.aekos.shared.data.entity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class SubmissionDataTest {

	/**
	 * Can we get the pretty title for the file type?
	 */
	@Test
	public void testGetFileTypeTitle01() {
		SubmissionData objectUnderTest = new SubmissionData();
		objectUnderTest.setSubmissionDataType(SubmissionDataType.RELATED_DOC);
		String result = objectUnderTest.getFileTypeTitle();
		assertThat(result, is("Associated/Supplementary Material"));
	}

	/**
	 * Can we tell when this submission data does represent a species file?
	 */
	@Test
	public void testIsSpeciesFile01() {
		SubmissionData objectUnderTest = new SubmissionData();
		objectUnderTest.setSubmissionDataType(SubmissionDataType.SPECIES_LIST);
		boolean result = objectUnderTest.isSpeciesFile();
		assertThat(result, is(true));
	}
	
	/**
	 * Can we tell when this submission data does NOT represent a species file?
	 */
	@Test
	public void testIsSpeciesFile02() {
		SubmissionData objectUnderTest = new SubmissionData();
		objectUnderTest.setSubmissionDataType(SubmissionDataType.DATA);
		boolean result = objectUnderTest.isSpeciesFile();
		assertThat(result, is(false));
	}
}
