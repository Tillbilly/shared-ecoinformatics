package au.edu.aekos.shared.web.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;


public class SubmissionDataFileModelTest {
	
	/**
	 * Can we format the human readable string correctly?
	 */
	@Test
	public void testGetFileSizeString01(){
		SubmissionDataFileModel objectUnderTest = new SubmissionDataFileModel();
		long roundsUpToOnePointTwoGigabytes = 1189302782L;
		objectUnderTest.setFileSizeBytes(new Long(roundsUpToOnePointTwoGigabytes));
		String result = objectUnderTest.getFileSizeString();
		assertEquals("1.2 GB", result);
	}
	
	/**
	 * Can we construct from a {@link SubmissionData}?
	 */
	@Test
	public void testConstructor01(){
		SubmissionData source = new SubmissionData();
		Date someDate = new Date();
		source.setEmbargoDate(someDate);
		source.setFileDescription("some desc");
		source.setFileName("testFileName.txt");
		source.setFileSizeBytes(123L);
		source.setFormat("csv");
		source.setFormatVersion("v1.1");
		source.setId(666L);
		source.setQuestionId("Q12.3");
		SubmissionDataFileModel objectUnderTest = new SubmissionDataFileModel(source);
		assertEquals(someDate, objectUnderTest.getEmbargoDate());
		assertEquals("some desc", objectUnderTest.getFileDescription());
		assertEquals("testFileName.txt", objectUnderTest.getFileName());
		assertEquals(new Long(123L), objectUnderTest.getFileSizeBytes());
		assertEquals(SubmissionDataType.DATA.toString(), objectUnderTest.getFileType());
		assertEquals("Data", objectUnderTest.getFileTypeTitle());
		assertEquals("csv", objectUnderTest.getFormat());
		assertEquals("v1.1", objectUnderTest.getFormatVersion());
		assertEquals(new Long(666L), objectUnderTest.getId());
		assertEquals("Q12.3", objectUnderTest.getQuestionId());
	}
	
	/**
	 * Can return something when no format title is available?
	 */
	@Test
	public void testGetFormatTitle01(){
		SubmissionDataFileModel objectUnderTest = new SubmissionDataFileModel();
		objectUnderTest.setFormatTitle(null);
		String result = objectUnderTest.getFormatTitle();
		assertEquals(SubmissionDataFileModel.NO_FORMAT_TITLE_DEFAULT, result);
	}
	
	/**
	 * Can we get the format title when it is available?
	 */
	@Test
	public void testGetFormatTitle02(){
		SubmissionDataFileModel objectUnderTest = new SubmissionDataFileModel();
		objectUnderTest.setFormatTitle("So much better than just a code");
		String result = objectUnderTest.getFormatTitle();
		assertEquals("So much better than just a code", result);
	}
	
	/**
	 * Can return something when no format version is available?
	 */
	@Test
	public void testGetFormatVersion01(){
		SubmissionDataFileModel objectUnderTest = new SubmissionDataFileModel();
		objectUnderTest.setFormatVersion(null);
		String result = objectUnderTest.getFormatVersion();
		assertEquals(SubmissionDataFileModel.NO_FORMAT_VERSION_DEFAULT, result);
	}
	
	/**
	 * Can we get the format version when it is available?
	 */
	@Test
	public void testGetFormatVersion02(){
		SubmissionDataFileModel objectUnderTest = new SubmissionDataFileModel();
		objectUnderTest.setFormatVersion("v1.1");
		String result = objectUnderTest.getFormatVersion();
		assertEquals("v1.1", result);
	}
}
