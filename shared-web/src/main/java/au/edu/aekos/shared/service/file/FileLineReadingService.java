package au.edu.aekos.shared.service.file;

import java.io.File;
import java.util.List;

import au.edu.aekos.shared.data.entity.SubmissionData;

public interface FileLineReadingService {
	
	List<String> readFileLinesAsList(SubmissionData submissionData);
	
	List<String> readFileLinesAsList(File file);

}
