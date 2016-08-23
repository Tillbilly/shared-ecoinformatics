package au.edu.aekos.shared.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.service.s3.ObjectStoreServiceException;
import au.edu.aekos.shared.service.submission.SubmissionDataService;

public class MockSubmissionDataService implements SubmissionDataService {

	@Override
	public SubmissionData getHydratedSubmissionData(Long submissionDataId) {
		return null;
	}

	@Override
	public void writeSubmissionDataToObjectStore(Submission submission)
			throws ObjectStoreServiceException {

	}

	@Override
	public void deleteSubmissionData(List<SubmissionData> submissionDataList) {

	}

	@Override
	public void deleteFromObjectStore(Set<ObjectStoreLocation> objectsToDelete) {

	}

	@Override
	public void deleteFromObjectStore(ObjectStoreLocation objectStoreLocation) {

	}

	@Override
	public void asyncDeleteNonPersistedSubmissionData(
			List<SubmissionData> submissionDataList) {
		
	}

	@Override
	public File retrieveFileToLocal(SubmissionData submissionData) {
		return new File(submissionData.getFileName());
	}

	@Override
	public void systemCloneSubmissionDataToObjectStore(Long submissionDataId,
			List<String> objectStoreNames) {
		
	}

	@Override
	public SubmissionData cloneToNonPersistedFileSystemData(SubmissionData sd) {
		SubmissionData clone = new SubmissionData();
		clone.setFileName(sd.getFileName());
		clone.setSubmissionDataType(sd.getSubmissionDataType());
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setObjectName(sd.getFileSystemStorageLocation().getObjectName() );
		fssl.setFspath(sd.getFileSystemStorageLocation().getFspath() );
		clone.getStorageLocations().add(fssl);
		return clone;
	}

	@Override
	public void systemDeleteSubmissionData(SubmissionData sd) {
		
	}

	@Override
	public void systemDeleteLogicalStorageLocations(SubmissionData sd) {
		
	}

	@Override
	public void systemDeleteOrphanSubmissionData(SubmissionData sd) {
		
	}

}
