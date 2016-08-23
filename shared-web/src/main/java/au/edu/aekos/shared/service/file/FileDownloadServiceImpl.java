package au.edu.aekos.shared.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.service.s3.ObjectStoreService;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {

	private Logger logger = LoggerFactory.getLogger(FileDownloadServiceImpl.class);
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private ObjectStoreService objectStoreService;
	
	@Transactional
	public SubmissionData getSubmissionDataEntity(Long submissionId,
			Long submissionDataId) {
		return submissionDao.retrieveSubmissionDataForSubmission(submissionId, submissionDataId);
	}

	@Override @Transactional
	public void streamFileToOutputStream(OutputStream os,
			SubmissionData submissionData) throws Exception {
		logger.info("Attempting to stream file " + submissionData.getFileName());
		//First try return a copy from the local file system, if it exists
		FileSystemStorageLocation fssl = submissionData.getFileSystemStorageLocation();
		if(fssl != null){
			File f = new File(fssl.getFspath() + fssl.getFileName() );
			if( f.exists() && f.canRead() ){
				 logger.info("Streaming file " + submissionData.getFileName() + " from file system");
				 InputStream is = new FileInputStream(f);
				 if( f.length() >= Integer.MAX_VALUE ){
				     IOUtils.copyLarge(is, os);
				 }else{
					 IOUtils.copy(is, os);
				 }
				 is.close();
				 return;
			}
			logger.info("System File " + f.getPath() + " can not be read for download");
		}
		logger.info("Attempting to find object store location for " + submissionData.getFileName());
		objectStoreService.streamObjectStoreFile(os, submissionData);
		logger.info("File stream successful");
	}
	
}
