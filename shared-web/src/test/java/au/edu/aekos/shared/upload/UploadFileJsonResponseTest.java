package au.edu.aekos.shared.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import au.edu.aekos.shared.web.model.SubmissionDataFileType;

import com.google.gson.Gson;

public class UploadFileJsonResponseTest {

	/**
	 * Can we serialise to JSON and then read it back?
	 */
	@Test
	public void testGenerateJson(){
		UploadFileJsonResponse resp = new UploadFileJsonResponse("myFile.txt", new File("stored-myfile.txt"), "DATA", "desc", "1.2", "access", "MS Access");
		String jsonString = resp.getJsonString();
		System.out.println(jsonString);
		Assert.assertNotNull(jsonString);
		Gson gson = new Gson();
		UploadFileJsonResponse respBack = gson.fromJson(jsonString, UploadFileJsonResponse.class);
		Assert.assertEquals(resp, respBack);
	}
	
	/**
	 * Can we construct a response that indicates success?
	 */
	@Test
	public void testConstructor01() {
		File file = new File("somefile.txt");
		UploadFileJsonResponse objectUnderTest = new UploadFileJsonResponse("fileName", file, "DATA", "desc", "v1.1", "excel", "MS Excel 2007");
		assertTrue(objectUnderTest.isSuccess());
		assertEquals("fileName", objectUnderTest.getOrigFilename());
		assertEquals("DATA", objectUnderTest.getFileType());
		assertEquals(SubmissionDataFileType.DATA.getTitle(), objectUnderTest.getFileTypeTitle());
		assertEquals("desc", objectUnderTest.getDescription());
		assertEquals("v1.1", objectUnderTest.getFileFormatVersion());
		assertEquals("excel", objectUnderTest.getFileFormat());
		assertEquals("MS Excel 2007", objectUnderTest.getFileFormatTitle());
	}
	
	/**
	 * Can we construct a response that indicates failure?
	 */
	@Test
	public void testConstructor02() {
		UploadFileJsonResponse objectUnderTest = new UploadFileJsonResponse("Something exploded!!!");
		assertFalse(objectUnderTest.isSuccess());
		assertEquals("Something exploded!!!", objectUnderTest.getFailureMessage());
	}
}
