package au.edu.aekos.shared.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.web.util.SharedFileUtils;

@Service
public class FileServiceImpl implements FileService {
	
	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Value("${submission.upload.tempdir}" )
	private String uploadDir;
	
	public File writeUploadedFileToDisk(MultipartFile file) throws IOException{
		//First, find a unique filename in the tempdir
		String cleansedFilename = SharedFileUtils.cleanseUploadedFileName(file.getOriginalFilename() );
		File newFile = SharedFileUtils.createUniqueFile(cleansedFilename, uploadDir);
		FileOutputStream outputStream = new FileOutputStream(newFile);
		InputStream is = file.getInputStream();
		IOUtils.copy(is, outputStream);
		is.close();
        outputStream.close();
		return newFile;
	}
	
	public File cloneToNewFile(File file, String originalFileName) throws IOException{
		File newFile = SharedFileUtils.createUniqueFile(originalFileName, uploadDir);
		logger.info("Cloning file " + file.getPath() + " to " + newFile.getPath() );
		InputStream is = new FileInputStream(file);
		OutputStream os = new FileOutputStream(newFile);
		IOUtils.copy(is, os);
		is.close();
		os.close();
		return newFile;
	}
	
	public SubmissionData initSubmissionDataEntityFromFile(MultipartFile file, File fileOnDisk, String description, SubmissionDataType dataType, String questionId){
		SubmissionData subData = new SubmissionData();
		subData.setFileDescription(description);
		subData.setFileName(SharedFileUtils.cleanseUploadedFileName(file.getOriginalFilename() ));
		subData.setFileSizeBytes(fileOnDisk.length());
		subData.setSubmissionDataType(dataType);
		subData.setQuestionId(questionId);
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		fssl.setObjectName(fileOnDisk.getName());
		fssl.setFspath(fileOnDisk.getPath().replaceAll(fileOnDisk.getName(), ""));
	    subData.getStorageLocations().add(fssl);
	    return subData;
	}
}
