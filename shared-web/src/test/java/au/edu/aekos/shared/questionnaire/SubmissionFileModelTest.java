package au.edu.aekos.shared.questionnaire;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.NetworkStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;

public class SubmissionFileModelTest {

	/**
	 * Can we handle a value bigger than 1KB
	 */
	@Test
	public void testGetHumanReadableFileSize01() {
		SubmissionData dataEntity = new SubmissionData();
		dataEntity.setFileSizeBytes(1500L);
		SubmissionFileModel objectUnderTest = new SubmissionFileModel(dataEntity);
		String result = objectUnderTest.getHumanReadableFileSize();
		assertEquals("1.5 kB", result);
	}

	/**
	 * Can we handle a value less than 1KB
	 */
	@Test
	public void testGetHumanReadableFileSize02() {
		SubmissionData dataEntity = new SubmissionData();
		dataEntity.setFileSizeBytes(500L);
		SubmissionFileModel objectUnderTest = new SubmissionFileModel(dataEntity);
		String result = objectUnderTest.getHumanReadableFileSize();
		assertEquals("500 B", result);
	}
	
	/**
	 * Can we handle a value significantly bigger than 1KB
	 */
	@Test
	public void testGetHumanReadableFileSize03() {
		SubmissionData dataEntity = new SubmissionData();
		long ninehundredAndEightySevenMegabytes = 987 * 1000 * 1000;
		dataEntity.setFileSizeBytes(ninehundredAndEightySevenMegabytes);
		SubmissionFileModel objectUnderTest = new SubmissionFileModel(dataEntity);
		String result = objectUnderTest.getHumanReadableFileSize();
		assertEquals("987.0 MB", result);
	}
	
	/**
	 * Can we handle a null value
	 */
	@Test
	public void testGetHumanReadableFileSize04() {
		SubmissionData dataEntity = new SubmissionData();
		Long nullSize = null;
		dataEntity.setFileSizeBytes(nullSize);
		SubmissionFileModel objectUnderTest = new SubmissionFileModel(dataEntity);
		String result = objectUnderTest.getHumanReadableFileSize();
		assertEquals("0 B", result);
	}
	
	/**
	 * Can we construct from a {@link SubmissionData}
	 */
	@Test
	public void testConstructor01() {
		SubmissionData dataEntity = new SubmissionData();
		dataEntity.setFileName("file.txt");
		dataEntity.setFileDescription("desc");
		dataEntity.setFileSizeBytes(1234L);
		dataEntity.setSubmissionDataType(SubmissionDataType.DATA);
		dataEntity.setId(666L);
		Set<StorageLocation> storageLocations = new HashSet<StorageLocation>();
		File mockFile = mock(File.class);
		when(mockFile.canRead()).thenReturn(true);
		when(mockFile.getName()).thenReturn("someFile.txt");
		storageLocations.add(new FileSystemStorageLocation(mockFile, null));
		dataEntity.setStorageLocations(storageLocations);
		dataEntity.setFormatVersion("MS Excel 2007");
		SubmissionFileModel objectUnderTest = new SubmissionFileModel(dataEntity);
		assertEquals("file.txt", objectUnderTest.getFilename());
		assertEquals("desc", objectUnderTest.getDescription());
		assertEquals(new Long(1234), objectUnderTest.getFileSizeBytes());
		assertEquals("DATA", objectUnderTest.getFiletype());
		assertEquals("someFile.txt", objectUnderTest.getStoredFilename());
		assertEquals("MS Excel 2007", objectUnderTest.getFileFormatVersion());
	}
	
	/**
	 * Can we find the filename with only one location to search?
	 */
	@Test
	public void testGetStoredFileName01() {
		SubmissionData dataEntity = new SubmissionData();
		Set<StorageLocation> storageLocations = new HashSet<StorageLocation>();
		dataEntity.setStorageLocations(storageLocations);
		File mockFile = mock(File.class);
		when(mockFile.canRead()).thenReturn(true);
		when(mockFile.getName()).thenReturn("someFile.txt");
		storageLocations.add(new FileSystemStorageLocation(mockFile, null));
		String result = SubmissionFileModel.getStoredFileName(dataEntity);
		assertEquals("someFile.txt", result);
	}
	
	/**
	 * Can we find the filename when there are multiple locations but only one of the right type?
	 */
	@Test
	public void testGetStoredFileName02() {
		SubmissionData dataEntity = new SubmissionData();
		Set<StorageLocation> storageLocations = new HashSet<StorageLocation>();
		dataEntity.setStorageLocations(storageLocations);
		storageLocations.add(new ObjectStoreLocation());
		File mockFile = mock(File.class);
		when(mockFile.canRead()).thenReturn(true);
		when(mockFile.getName()).thenReturn("someFile.txt");
		storageLocations.add(new FileSystemStorageLocation(mockFile, null));
		storageLocations.add(new NetworkStorageLocation());
		String result = SubmissionFileModel.getStoredFileName(dataEntity);
		assertEquals("someFile.txt", result);
	}
	
	/**
	 * Can we find the expected filename when there are multiple locations of the right type?
	 */
	@Test
	public void testGetStoredFileName03() {
		SubmissionData dataEntity = new SubmissionData();
		Set<StorageLocation> storageLocations = new HashSet<StorageLocation>();
		dataEntity.setStorageLocations(storageLocations);
		File mockTargetFile = mock(File.class);
		when(mockTargetFile.canRead()).thenReturn(true);
		when(mockTargetFile.getName()).thenReturn("aaaTargetFile.txt");
		storageLocations.add(new FileSystemStorageLocation(mockTargetFile, null));
		File mockOtherFile = mock(File.class);
		when(mockOtherFile.canRead()).thenReturn(true);
		when(mockOtherFile.getName()).thenReturn("zzzOtherFile.txt");
		storageLocations.add(new FileSystemStorageLocation(mockOtherFile, null));
		String result = SubmissionFileModel.getStoredFileName(dataEntity);
		assertEquals("aaaTargetFile.txt", result);
	}
}
