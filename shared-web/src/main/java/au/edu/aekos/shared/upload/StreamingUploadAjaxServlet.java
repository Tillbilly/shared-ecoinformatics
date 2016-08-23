package au.edu.aekos.shared.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;
import au.edu.aekos.shared.web.util.SharedFileUtils;

/**
 * This is a Spring component for the Sole reason of wiring in config for the file system path.
 * 
 * We can`t use a Spring Controller, because Spring reads in the entire file before exposing it to the request handler.
 * 
 * Using a raw servlet we can stream the input stream direct to the file system.
 * 
 * 
 * @author Ben Till
 *
 */
public class StreamingUploadAjaxServlet implements HttpRequestHandler {
	public static final String defaultFileSystemPath = "C:\\temp\\submission_uploads\\";
	
	private static final Logger logger = LoggerFactory.getLogger(StreamingUploadAjaxServlet.class);
	
	private String tempFileSystemPath;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		try{
			/*boolean isMultipart = */ServletFileUpload.isMultipartContent(req);
			DisplayQuestionnaire quest = (DisplayQuestionnaire) req.getSession().getAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE);
			if(quest == null ){
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not part of current Questionnaire Session, rejected");
			}
		
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(req);
			File fileOnDisk = null;
			String cleansedFileName = "";
			String description = "";
			String fileType = "";
			String questionId = null;
			String fileFormat = null;
			String fileFormatVersion = null;
			String fileFormatTitle = null;
			while (iter.hasNext()) {
			    FileItemStream item = iter.next();
			    String name = item.getFieldName();
			    InputStream stream = item.openStream();
			    if (item.isFormField()) {
			    	String value = Streams.asString(stream);
			    	logger.info("Form field " + name + " with value " + value + " detected.");
			    	if("description".equals(name) ){
			    		description = value;
			    	}else if("fileType".equals(name)){
			    		fileType = value;
			    	}else if("questionId".equals(name)){
			    		questionId = value;
			    	} else if("fileFormat".equals(name)){
			    		fileFormat = value;
			    	} else if("fileFormatTitle".equals(name)){
			    		fileFormatTitle = value;
			    	}else if("fileFormatVersion".equals(name)){
			    		fileFormatVersion = value;
			    	}
			    } else {
			    	logger.info("File field " + name + " with file name " + item.getName() + " detected.");
			    	cleansedFileName = SharedFileUtils.cleanseUploadedFileName(item.getName());
			    	logger.info("'" + item.getName() +"' cleansed to '" + cleansedFileName +"'");
			    	fileOnDisk = getHandleToUniqueFile( cleansedFileName ) ;
			    	logger.info("Attempting to stream file to " + fileOnDisk.getPath());
			    	OutputStream os = new FileOutputStream(fileOnDisk);
			    	IOUtils.copy(stream, os);
			    	os.close();
			    }
			}
		
			if(fileOnDisk != null){
				addUploadedFileInformationToSessionQuestionnare(cleansedFileName, fileOnDisk, fileType,  description,  quest, questionId, fileFormat, fileFormatVersion);
				UploadFileJsonResponse jsonResponse = new UploadFileJsonResponse(cleansedFileName, fileOnDisk, fileType, description, fileFormatVersion, fileFormat, fileFormatTitle);
				resp.getWriter().print( jsonResponse.getJsonString() );
				resp.flushBuffer();
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}catch (FileUploadException e) {
			e.printStackTrace();
		}
		
	}
	
	//TODO - this has no smarts - just adds an integer to the end of the filename if the file exists - 
	private File getHandleToUniqueFile(String fileName) throws IOException{
		logger.info("Attempting create handle to path " + getTempFileSystemPath() + " for filename " + fileName);
		File f = SharedFileUtils.createUniqueFile(fileName, getTempFileSystemPath());
		logger.info("file created at:" + f.getPath() + " " + f.getName());
		return f;
	}
	
	private void addUploadedFileInformationToSessionQuestionnare(String origFileName, 
			                                                     File savedFile,  
			                                                     String fileType,
			                                                     String description, 
			                                                     DisplayQuestionnaire quest,
			                                                     String questionId,
			                                                     String fileFormat,
			                                                     String fileFormatVersion){
		SubmissionData subData = new SubmissionData();
		subData.setFileDescription(description);
		subData.setFileName(origFileName);
		subData.setFileSizeBytes(savedFile.length());
		subData.setQuestionId(questionId);
		subData.setFormat(fileFormat);
		subData.setFormatVersion(fileFormatVersion);
		
		if(SubmissionDataType.RELATED_DOC.name().equals(fileType) ){
			subData.setSubmissionDataType(SubmissionDataType.RELATED_DOC);
		}
		
		//Now create the FileSystemStorageLocation
		FileSystemStorageLocation fssl = new FileSystemStorageLocation(savedFile, getTempFileSystemPath() );
		subData.getStorageLocations().add(fssl);
		quest.getSubmissionDataList().add(subData);
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if("POST".equals( request.getMethod()) ){
			doPost(request, response);
		}
		
	}

	public String getTempFileSystemPath() {
		return StringUtils.hasLength( tempFileSystemPath ) ? tempFileSystemPath : defaultFileSystemPath;
	}

	public void setTempFileSystemPath(String tempFileSystemPath) {
		this.tempFileSystemPath = tempFileSystemPath;
	}
}
