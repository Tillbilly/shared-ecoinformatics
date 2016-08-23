package au.edu.aekos.shared.service.file;

import java.io.OutputStream;

import au.edu.aekos.shared.data.entity.SubmissionData;


public interface FileDownloadService {

	SubmissionData getSubmissionDataEntity(Long submissionId, Long submissionDataId);
	
	void streamFileToOutputStream(OutputStream os, SubmissionData submissionData) throws Exception;
	
	
	
	
}
