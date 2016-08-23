package au.edu.aekos.shared.service.file;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;

public class FileServiceStub implements FileService {

	@Override
	public File writeUploadedFileToDisk(MultipartFile file) throws IOException {
		return new File("stub.file");
	}

	@Override
	public SubmissionData initSubmissionDataEntityFromFile(MultipartFile file, File fileOnDisk, String description, SubmissionDataType dataType,
			String questionId) {
		return null;
	}

	@Override
	public File cloneToNewFile(File file, String originalFileName)
			throws IOException {
		return null;
	}
}
