package au.org.aekos.shared.api.s3;

import java.io.File;

import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfo;

public interface SharedS3FileRetrievalService {

	void retrieveObjectToLocal(SubmissionFileInfo fileInfo, File destinationFile) throws SharedS3FileRetrievalException;
	
}
