package au.edu.aekos.shared.web.controllers;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.service.file.FileDownloadService;

@Controller
public class FileDownloadController {
	
	private Logger logger = LoggerFactory.getLogger(FileDownloadController.class);
	
	@Autowired
	FileDownloadService fileDownloadService;
	
	@RequestMapping(value="/downloadFile", method=RequestMethod.GET )
	public void downloadSubmissionFile(@RequestParam(value="submissionId", required=true) Long submissionId, 
			                           @RequestParam(value="dataFileId", required=true) Long dataFileId, HttpServletResponse response ) throws IOException{
		logger.info("Attempting to download data file ID " + dataFileId + " for Submission Id " + submissionId.toString() );
		SubmissionData sd = fileDownloadService.getSubmissionDataEntity(submissionId, dataFileId);
		if(sd == null ){
			logger.info("Submission Data NOT FOUND. Download data file ID " + dataFileId + " for Submission Id " + submissionId.toString() );
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No data file id:" + dataFileId + " for submission id:" + submissionId );
		}	
		response.addHeader("Content-Disposition", "attachment; filename=" + sd.getFileName() );    //Content-Disposition: attachment; filename=Report0.pdf
		Cookie c = new Cookie("fileDownload", "true");
		c.setPath("/");
		response.addCookie(c);
		try{
		   fileDownloadService.streamFileToOutputStream(response.getOutputStream(), sd);
		}catch(Exception e){
			logger.error("Error downloading file. Download data file ID " + dataFileId + " for Submission Id " + submissionId.toString(), e );
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No data file id:" + dataFileId + " for submission id:" + submissionId  + e.getMessage() );
		}
		logger.info("Download " + sd.getFileName() + " success" );
		response.flushBuffer();
	}
	
}
