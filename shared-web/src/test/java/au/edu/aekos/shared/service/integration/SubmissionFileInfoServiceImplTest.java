package au.edu.aekos.shared.service.integration;

import static au.edu.aekos.shared.service.integration.SharedWebTestSupport.getObjectUnderTestWithData;
import static au.edu.aekos.shared.service.integration.SharedWebTestSupport.submissionData;
import static au.edu.aekos.shared.service.integration.SharedWebTestSupport.submissionWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.org.aekos.shared.api.json.SpeciesFileNameEntry;

public class SubmissionFileInfoServiceImplTest {

	/**
	 * Can we get only the species file types when other types are present?
	 */
	@Test
	public void testRetrieveSpeciesFileNames01() {
		SubmissionFileInfoServiceImpl objectUnderTest = getObjectUnderTestWithData(
			submissionData(111l, "speciesFauna.txt", SubmissionDataType.SPECIES_LIST),
			submissionData(222l, "some.submission.data.csv", SubmissionDataType.DATA),
			submissionData(333l, "speciesFlora.txt", SubmissionDataType.SPECIES_LIST),
			submissionData(444l, "info.pdf", SubmissionDataType.RELATED_DOC));
		List<SpeciesFileNameEntry> result = objectUnderTest.retrieveSpeciesFileNames(123L);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getId(), is(111l));
		assertThat(result.get(1).getId(), is(333l));
	}

	/**
	 * Can we get the species file types when no other types are present?
	 */
	@Test
	public void testRetrieveSpeciesFileNames02() {
		SubmissionFileInfoServiceImpl objectUnderTest = getObjectUnderTestWithData(
				submissionData(111l, "speciesFauna.txt", SubmissionDataType.SPECIES_LIST),
				submissionData(222l, "speciesFlora.txt", SubmissionDataType.SPECIES_LIST));
		List<SpeciesFileNameEntry> result = objectUnderTest.retrieveSpeciesFileNames(123L);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getId(), is(111l));
		assertThat(result.get(1).getId(), is(222l));
	}
	
	/**
	 * Can we survive no species files being present?
	 */
	@Test
	public void testRetrieveSpeciesFileNames03() {
		SubmissionFileInfoServiceImpl objectUnderTest = getObjectUnderTestWithData(
				submissionData(111l, "some.submission.data.csv", SubmissionDataType.DATA),
				submissionData(222l, "info.pdf", SubmissionDataType.RELATED_DOC));
		List<SpeciesFileNameEntry> result = objectUnderTest.retrieveSpeciesFileNames(123L);
		assertThat(result.size(), is(0));
	}
	
	/**
	 * Can we survive no files being present at all?
	 */
	@Test
	public void testRetrieveSpeciesFileNames04() {
		SubmissionFileInfoServiceImpl objectUnderTest = getObjectUnderTestWithData();
		List<SpeciesFileNameEntry> result = objectUnderTest.retrieveSpeciesFileNames(123L);
		assertThat(result.size(), is(0));
	}
	
	/**
	 * Can we survive no submission being found?
	 */
	@Test
	public void testRetrieveSpeciesFileNames05() {
		SubmissionFileInfoServiceImpl objectUnderTest = new SubmissionFileInfoServiceImpl();
		SubmissionService mockSubmissionService = mock(SubmissionService.class);
		objectUnderTest.setSubmissionService(mockSubmissionService);
		when(mockSubmissionService.retrieveSubmissionById(123L)).thenReturn(null);
		List<SpeciesFileNameEntry> result = objectUnderTest.retrieveSpeciesFileNames(123L);
		assertThat(result.size(), is(0));
	}
	
	/**
	 * Can we use the submission when it is supplied directly?
	 */
	@Test
	public void testRetrieveSpeciesFileNames06() {
		SubmissionFileInfoServiceImpl objectUnderTest = new SubmissionFileInfoServiceImpl();
		Submission submission = submissionWith(
			submissionData(111l, "speciesFauna.txt", SubmissionDataType.SPECIES_LIST),
			submissionData(222l, "some.submission.data.csv", SubmissionDataType.DATA),
			submissionData(333l, "speciesFlora.txt", SubmissionDataType.SPECIES_LIST),
			submissionData(444l, "info.pdf", SubmissionDataType.RELATED_DOC));
		List<SpeciesFileNameEntry> result = objectUnderTest.retrieveSpeciesFileNames(submission);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getId(), is(111l));
		assertThat(result.get(1).getId(), is(333l));
	}
	
	/**
	 * Can we map a {@link SubmissionData} to a {@link SpeciesFileNameEntry}?
	 */
	@Test
	public void testMapSubmissionDataToSpeciesFileNameEntry01() {
		SubmissionData source = submissionData(111l, "some.submission.data.csv", SubmissionDataType.DATA);
		SpeciesFileNameEntry result = SubmissionFileInfoServiceImpl.mapSubmissionDataToSpeciesFileNameEntry(source);
		SpeciesFileNameEntry expected = new SpeciesFileNameEntry(111l, "some.submission.data.csv");
		assertThat(result, is(expected));
	}
}
