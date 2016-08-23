package au.edu.aekos.shared.service.s3;

import java.io.File;
import java.io.OutputStream;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.org.ecoinformatics.s3client.S3RESTHttpClient;

public class ObjectStoreServiceStub implements ObjectStoreService {

	@Override
	public void asyncUploadFileToObjectStore(File fileOnLocalDisk,
			String objectStoreIdentifier, Long submissionDataId)
			throws ObjectStoreServiceException {
		
	}

	@Override
	public void systemAsyncUploadFileToObjectStore(File fileOnLocalDisk,
			String originalFileName, String objectStoreIdentifier,
			Long submissionDataId, boolean deleteFileAfterwards)
			throws ObjectStoreServiceException {
		
	}

	@Override
	public String getPrimaryObjectStore() {
		return null;
	}

	@Override
	public String getBackupObjectStore() {
		return null;
	}

	@Override
	public int deleteObject(String key, String objectStoreIdentifier)
			throws ObjectStoreServiceException {
		return 0;
	}

	@Override
	public File retrieveObjectToFile(String key, String objectStoreIdentifier)
			throws ObjectStoreServiceException, Exception {
		return null;
	}

	@Override
	public void streamObjectStoreFile(OutputStream os,
			SubmissionData submissionData) throws ObjectStoreServiceException {
		
	}

	@Override
	public S3RESTHttpClient getClientForIdentifier(String identifier) {
		return null;
	}

}
