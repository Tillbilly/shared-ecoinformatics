package au.edu.aekos.shared.service.file;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;

public interface FileService {

	File writeUploadedFileToDisk(MultipartFile file) throws IOException;
	
	SubmissionData initSubmissionDataEntityFromFile(MultipartFile file, File fileOnDisk, String description, SubmissionDataType dataType, String questionId);
	
	File cloneToNewFile(File file, String originalFileName) throws IOException;
}
