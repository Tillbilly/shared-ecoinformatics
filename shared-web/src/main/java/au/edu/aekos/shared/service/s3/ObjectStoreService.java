package au.edu.aekos.shared.service.s3;

import java.io.File;
import java.io.OutputStream;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.org.ecoinformatics.s3client.S3RESTHttpClient;

public interface ObjectStoreService {
	
	void asyncUploadFileToObjectStore(File fileOnLocalDisk, String objectStoreIdentifier, Long submissionDataId) throws ObjectStoreServiceException ;
	
	void systemAsyncUploadFileToObjectStore(File fileOnLocalDisk, String originalFileName, String objectStoreIdentifier, Long submissionDataId, boolean deleteFileAfterwards) throws ObjectStoreServiceException;

	String getPrimaryObjectStore();
	
	String getBackupObjectStore() ;
	
	int deleteObject(String key, String objectStoreIdentifier) throws ObjectStoreServiceException;
	
	File retrieveObjectToFile(String key, String objectStoreIdentifier) throws ObjectStoreServiceException, Exception;
	
	void streamObjectStoreFile(OutputStream os, SubmissionData submissionData) throws ObjectStoreServiceException;
	
	S3RESTHttpClient getClientForIdentifier(String identifier);
	
}
