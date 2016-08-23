package au.edu.aekos.shared.web.controllers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.SubmissionFiles;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.submission.SubmissionDataService;

public class NewSubmissionControllerTest {

	/**
	 * Can we map submission data when it exists?
	 */
	@Test
	public void testPrepareSubmissionFiles01() {
		NewSubmissionController objectUnderTest = getObjectUnderTest_testPepareSubmissionFiles01();
		DisplayQuestionnaire questionnaire = getSource_testPrepareSubmissionFiles01();
		SubmissionFiles result = objectUnderTest.prepareSubmissionFiles(questionnaire);
		assertEquals(2, result.size());
		assertEquals("hydro.backup", result.get(0).getFilename());
		assertEquals("PostgreSQL", result.get(0).getFileFormatTitle());
		assertEquals("wombats.txt", result.get(1).getFilename());
		assertEquals("Comma Separated Values", result.get(1).getFileFormatTitle());
	}

	/**
	 * Can we survive mapping submission data when it doesn't exist?
	 */
	@Test
	public void testPrepareSubmissionFiles02() {
		NewSubmissionController objectUnderTest = new NewSubmissionController();
		DisplayQuestionnaire questionnaire = new DisplayQuestionnaire();
		List<SubmissionData> emptyList = Collections.emptyList();
		questionnaire.setSubmissionDataList(emptyList);
		SubmissionFiles result = objectUnderTest.prepareSubmissionFiles(questionnaire);
		assertEquals(0, result.size());
	}
	
	private DisplayQuestionnaire getSource_testPrepareSubmissionFiles01() {
		DisplayQuestionnaire result = new DisplayQuestionnaire();
		
		SubmissionData submissionDataWithId = new SubmissionData();
		submissionDataWithId.setId(123L);
		
		SubmissionData submissionDataWithoutId = new SubmissionData();
		submissionDataWithoutId.setFileName("wombats.txt");
		submissionDataWithoutId.setFormat("csv");
		
		result.setSubmissionDataList(Arrays.asList(submissionDataWithId, submissionDataWithoutId));
		return result;
	}

	private NewSubmissionController getObjectUnderTest_testPepareSubmissionFiles01() {
		NewSubmissionController result = new NewSubmissionController();
		
		ControlledVocabularyService controlledVocabularyService = mock(ControlledVocabularyService.class);
		result.setControlledVocabularyService(controlledVocabularyService);
		when(controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, "csv")).thenReturn("Comma Separated Values");
		when(controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, "pg")).thenReturn("PostgreSQL");
		
		SubmissionDataService submissionDataService = mock(SubmissionDataService.class);
		result.setSubmissionDataService(submissionDataService);
		SubmissionData hydratedDataForId123 = new SubmissionData();
		hydratedDataForId123.setFileName("hydro.backup");
		hydratedDataForId123.setFormat("pg");
		when(submissionDataService.getHydratedSubmissionData(123L)).thenReturn(hydratedDataForId123);
		
		return result;
	}
}
