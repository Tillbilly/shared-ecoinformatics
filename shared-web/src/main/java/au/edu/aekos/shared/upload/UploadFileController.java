package au.edu.aekos.shared.upload;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.service.file.FileService;
import au.edu.aekos.shared.valid.FileUploadValidationException;
import au.edu.aekos.shared.valid.QuestionFileUploadValidator;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;
import au.edu.aekos.shared.web.util.SharedFileUtils;

@Controller
public class UploadFileController {
	
	private Logger logger = LoggerFactory.getLogger(UploadFileController.class);
	
	private static final String VERSION_NOT_RECORDED = null;
	private static final String FORMAT_NOT_RECORDED = null;
	private static final String FORMAT_TITLE_NOT_RECORDED = null;
	@Autowired
	private FileService fileService;
	
	@Autowired
	private QuestionFileUploadValidator fileValidator;
	
	@RequestMapping(value = "/uploadFilePopup", method = RequestMethod.GET)
    public String imageUploadPopup(@RequestParam(required=true, value="questionId") String questionId, 
    		                       @RequestParam(required=true, value="responseType") String responseType, 
    		                       Model model) throws IOException {
		model.addAttribute("questionId", questionId);
		model.addAttribute("responseType", responseType);
		return "uploadFilePopup";
	}

	@RequestMapping(value = "/questionFileUpload", method = RequestMethod.POST)
	public void questionImageUploadAjax(
			@RequestParam("file") MultipartFile file,
			@RequestParam("description") String description,
			@RequestParam("questionId") String questionId,
			@RequestParam("responseType") String responseType, Model model,
			HttpSession session, HttpServletResponse resp) throws IOException {
		resp.setContentType(SharedConstants.IE_SAFE_RESPONSE_TYPE);
		File fileOnDisk = fileService.writeUploadedFileToDisk(file);
		SubmissionDataType dataType = getSubmissionDataType(responseType);
        //Validate the file is what it is meant to be
		try{
			fileValidator.validateUploadedFile(fileOnDisk, dataType);
		}catch(FileUploadValidationException ex){
			logger.error("File failed validation",ex);
			UploadFileJsonResponse errorResponse = new UploadFileJsonResponse(ex.getMessage());
			resp.getOutputStream().print(errorResponse.getJsonString());
			resp.flushBuffer();
			return;
		}
		
		SubmissionData subData = fileService.initSubmissionDataEntityFromFile(file, fileOnDisk, description, dataType, questionId);
		DisplayQuestionnaire quest = (DisplayQuestionnaire) session.getAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE);
		if (sessionHasExpired(quest)) {
			UploadFileJsonResponse errorResponse = new UploadFileJsonResponse("Questionnaire not found on session. Has the session expired?");
			resp.getOutputStream().print(errorResponse.getJsonString());
			resp.flushBuffer();
			return;
		}
		quest.getQuestionUploadSubmissionDataMap().put(questionId, subData);   //Don`t know if this gets called
		UploadFileJsonResponse jsonResponse = new UploadFileJsonResponse(SharedFileUtils.cleanseUploadedFileName(file.getOriginalFilename()), fileOnDisk, dataType.name(), description, VERSION_NOT_RECORDED, FORMAT_NOT_RECORDED, FORMAT_TITLE_NOT_RECORDED);
		resp.getOutputStream().print(jsonResponse.getJsonString());
		resp.flushBuffer();
	}

	private boolean sessionHasExpired(DisplayQuestionnaire quest) {
		return quest == null;
	}
	
	private SubmissionDataType getSubmissionDataType(String responseType){
		if("LICENSE_CONDITIONS".equals(responseType)){
			return SubmissionDataType.LICENSE_COND;
		}else if("DOCUMENT".equals(responseType)){
			return SubmissionDataType.DOCUMENT;
		}else if("SPECIES_LIST".equals(responseType)){
			return SubmissionDataType.SPECIES_LIST;
		}else if("SITE_FILE".equals(responseType)){
			return SubmissionDataType.SITE_FILE;
		}
		return SubmissionDataType.DATA;
	}
	
	
	
	
}
