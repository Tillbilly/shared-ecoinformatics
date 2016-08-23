package au.edu.aekos.shared.valid;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.springframework.validation.Errors;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.SubmissionFileModel;
import au.edu.aekos.shared.questionnaire.SubmissionFiles;

public class SubmissionFilesValidatorTest {

	/**
	 * Does validation fail when a file has no description?
	 */
	@Test
	public void testValidateFileDetails01() throws Exception {
		SubmissionFilesValidator objectUnderTest = new SubmissionFilesValidator();
		SubmissionFiles submissionFiles = getSubmissionFiles_testValidateFileDetails01();
		Errors errors = mock(Errors.class);
		DisplayQuestionnaire questionnaire = new DisplayQuestionnaire();
		objectUnderTest.validateFileDetails(submissionFiles, errors, questionnaire);
		verify(errors).rejectValue(eq("submissionFileList[0].description"), anyString());
	}

	private SubmissionFiles getSubmissionFiles_testValidateFileDetails01() {
		SubmissionFiles submissionFiles = new SubmissionFiles();
		SubmissionFileModel dataFile = new SubmissionFileModel();
		submissionFiles.getSubmissionFileList().add(dataFile);
		dataFile.setDeleted(false);
		dataFile.setDescription("");
		return submissionFiles;
	}
	
	/**
	 * Do we drop straight through the method when there are no files to validate?
	 */
	@Test
	public void testValidateFileDetails02() throws Exception {
		SubmissionFilesValidator objectUnderTest = new SubmissionFilesValidator();
		SubmissionFiles submissionFiles = new SubmissionFiles();
		Errors errors = mock(Errors.class);
		DisplayQuestionnaire questionnaire = new DisplayQuestionnaire();
		objectUnderTest.validateFileDetails(submissionFiles, errors, questionnaire);
		verifyZeroInteractions(errors);
	}
	
	/**
	 * Does validation pass when a file has a description?
	 */
	@Test
	public void testValidateFileDetails03() throws Exception {
		SubmissionFilesValidator objectUnderTest = new SubmissionFilesValidator();
		SubmissionFiles submissionFiles = getSubmissionFiles_testValidateFileDetails03();
		Errors errors = mock(Errors.class);
		DisplayQuestionnaire questionnaire = new DisplayQuestionnaire();
		objectUnderTest.validateFileDetails(submissionFiles, errors, questionnaire);
		verifyZeroInteractions(errors);
	}

	private SubmissionFiles getSubmissionFiles_testValidateFileDetails03() {
		SubmissionFiles submissionFiles = new SubmissionFiles();
		SubmissionFileModel dataFile = new SubmissionFileModel();
		submissionFiles.getSubmissionFileList().add(dataFile);
		dataFile.setDeleted(false);
		dataFile.setDescription("blah blah, some really great description");
		return submissionFiles;
	}
}
