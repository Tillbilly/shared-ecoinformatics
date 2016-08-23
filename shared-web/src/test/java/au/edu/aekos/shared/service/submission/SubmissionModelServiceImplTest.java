package au.edu.aekos.shared.service.submission;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;

public class SubmissionModelServiceImplTest {

	/**
	 * Can we map the data files in a Submission to a submission model?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel01() {
		ControlledVocabularyService controlledVocabularyService = mock(ControlledVocabularyService.class);
		when(controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, "excel")).thenReturn("MS Excel 2007");
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		objectUnderTest.setControlledVocabularyService(controlledVocabularyService);
		Submission submission = buildSubmission_testMapSubmittedDataInfoToSubmissionModel01();
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(submission);
		assertEquals(1, result.size());
		assertEquals("testFile", result.get(0).getFileName());
		assertEquals("MS Excel 2007", result.get(0).getFormatTitle());
	}

	private Submission buildSubmission_testMapSubmittedDataInfoToSubmissionModel01() {
		Submission result = new Submission();
		SubmissionData data = new SubmissionData();
		result.setSubmissionDataList(Arrays.asList(data));
		data.setFileName("testFile");
		data.setFormat("excel");
		return result;
	}

	/**
	 * Can we handle the case when no display text for the format is found?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel02() {
		ControlledVocabularyService controlledVocabularyService = mock(ControlledVocabularyService.class);
		when(controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, "hasNoDisplayText")).thenReturn(null);
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		objectUnderTest.setControlledVocabularyService(controlledVocabularyService);
		Submission submission = buildSubmission_testMapSubmittedDataInfoToSubmissionModel02();
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(submission);
		assertEquals(1, result.size());
		assertEquals("(no format)", result.get(0).getFormatTitle());
	}
	
	private Submission buildSubmission_testMapSubmittedDataInfoToSubmissionModel02() {
		Submission result = new Submission();
		SubmissionData data = new SubmissionData();
		result.setSubmissionDataList(Arrays.asList(data));
		data.setFormat("hasNoDisplayText");
		return result;
	}
	
	/**
	 * Can we handle mapping the data files in a Submission to a submission model when there aren't any (empty list)?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel03() {
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		Submission submission = new Submission();
		List<SubmissionData> emptyList = Collections.emptyList();
		submission.setSubmissionDataList(emptyList);
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(submission);
		assertEquals(0, result.size());
	}
	
	/**
	 * Can we handle mapping the data files in a Submission to a submission model when there aren't any (null)?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel04() {
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		Submission submission = new Submission();
		submission.setSubmissionDataList(null);
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(submission);
		assertEquals(0, result.size());
	}
	
	/**
	 * Can we handle mapping the data files in a DisplayQuestionnaire to a submission model when there is data?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel05() {
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		ControlledVocabularyService controlledVocabularyService = mock(ControlledVocabularyService.class);
		when(controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, "excel")).thenReturn("MS Excel");
		objectUnderTest.setControlledVocabularyService(controlledVocabularyService);
		DisplayQuestionnaire source = getSource_testMapSubmittedDataInfoToSubmissionModel05();
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(source);
		assertEquals(1, result.size());
		assertEquals("testFile", result.get(0).getFileName());
		assertEquals("excel", result.get(0).getFormat());
		assertEquals("MS Excel", result.get(0).getFormatTitle());
	}

	private DisplayQuestionnaire getSource_testMapSubmittedDataInfoToSubmissionModel05() {
		DisplayQuestionnaire result = new DisplayQuestionnaire();
		SubmissionData data1 = new SubmissionData();
		data1.setFileName("testFile");
		data1.setFormat("excel");
		result.setSubmissionDataList(Arrays.asList(data1));
		return result;
	}
	
	/**
	 * Can we handle mapping the data files in a DisplayQuestionnaire to a submission model when there aren't any (empty list)?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel06() {
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		DisplayQuestionnaire source = new DisplayQuestionnaire();
		List<SubmissionData> emptyList = Collections.emptyList();
		source.setSubmissionDataList(emptyList);
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(source);
		assertEquals(0, result.size());
	}
	
	/**
	 * Can we handle mapping the data files in a DisplayQuestionnaire to a submission model when there aren't any (null)?
	 */
	@Test
	public void testMapSubmittedDataInfoToSubmissionModel07() {
		SubmissionModelServiceImpl objectUnderTest = new SubmissionModelServiceImpl();
		DisplayQuestionnaire source = new DisplayQuestionnaire();
		source.setSubmissionDataList(null);
		List<SubmissionDataFileModel> result = objectUnderTest.mapSubmittedDataInfoToSubmissionModel(source);
		assertEquals(0, result.size());
	}
}
