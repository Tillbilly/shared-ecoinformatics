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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.service.file.FileService;
import au.edu.aekos.shared.service.file.SiteFileService;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;
import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;
import au.edu.aekos.shared.web.map.CoordinateSystemConfig;
import au.edu.aekos.shared.web.util.SharedFileUtils;

@Controller
public class SiteFileController {
	
	private static final Logger logger = LoggerFactory.getLogger(SiteFileController.class);
	
	@Autowired
	private SiteFileService siteFileService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CoordinateSystemConfig coordinateSystemList;
	
	@RequestMapping(value="/uploadSiteFile", method = RequestMethod.GET )
	public String viewUploadSiteFilePage(@RequestParam(required=false,value="filename",defaultValue="") String filename, 
			                             @RequestParam(required=true) String questionId,
			                             Model model, HttpSession session ){
		model.addAttribute("coordList", coordinateSystemList.getCoordSysList() );
		model.addAttribute("siteFileName", filename);
		model.addAttribute("questionId", questionId);
		return "uploadSiteFile";
	}
	
	@RequestMapping(value="/uploadSiteFileAjax", method = RequestMethod.POST )
	public void ajaxUploadSiteFile(@RequestParam("siteFileName") CommonsMultipartFile file, 
            @RequestParam("description") String description,
            @RequestParam("coordSys") String coordSys,
            @RequestParam(defaultValue="", value="otherCoordSys") String otherCoordSys,
            @RequestParam("questionId") String questionId,
            Model model, 
            HttpServletResponse resp, HttpSession session) throws IOException{
		File fileOnDisk = fileService.writeUploadedFileToDisk(file);
		JsonUploadSiteFileResponse jsonResponse = siteFileService.parseSiteFileToJson(fileOnDisk, coordSys, true);
		if(!StringUtils.hasLength(jsonResponse.getError())) {
		    writeSiteFileInformationToSession(file, fileOnDisk, description,coordSys, otherCoordSys, session, questionId);
		}
		
		jsonResponse.setFileName(SharedFileUtils.cleanseUploadedFileName(file.getOriginalFilename() ));
		jsonResponse.setDescription(description);
		jsonResponse.setCoordSys(coordSys);
		jsonResponse.setCoordSysOther(otherCoordSys);
		resp.setContentType(SharedConstants.IE_SAFE_RESPONSE_TYPE);
		resp.getOutputStream().print(jsonResponse.getJsonString());
		resp.flushBuffer();
	}
	
	@RequestMapping(value="/retrieveSiteFileAjax", method = RequestMethod.GET )
	public void viewUploadSiteFilePage(@RequestParam(required=true,value="filename") String filename, HttpSession session, HttpServletResponse response ) throws IOException{
		DisplayQuestionnaire quest = (DisplayQuestionnaire) session.getAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE);
		JsonUploadSiteFileResponse jsonResponse = new JsonUploadSiteFileResponse();
		if(quest.getSiteFile() != null 
				&& quest.getSiteFile().getFileName().equals(filename) 
				&& quest.getSiteFile().getFileSystemStorageLocation() != null ){
			File fileOnDisk = new File( quest.getSiteFile().getFileSystemStorageLocation().getFspath() +  quest.getSiteFile().getFileSystemStorageLocation().getFileName() );
			if(!fileOnDisk.canRead()){
				jsonResponse.setError("Error occured with filename:" + filename);
			}else{
				jsonResponse = siteFileService.parseSiteFileToJson(fileOnDisk, quest.getSiteFileCoordinateSystem(), true);
				jsonResponse.setFileName(quest.getSiteFile().getFileName());
				jsonResponse.setDescription(quest.getSiteFile().getFileDescription());
				jsonResponse.setCoordSys(quest.getSiteFileCoordinateSystem());
				jsonResponse.setCoordSysOther(quest.getSiteFileCoordSysOther());
			}
		}else{
			jsonResponse.setError("Error occured with filename:" + filename);
		}
		response.setContentType("application/json");
		response.getOutputStream().print(jsonResponse.getJsonString());
		response.flushBuffer();
	}
	
	private void writeSiteFileInformationToSession(CommonsMultipartFile file, File fileOnDisk, String description, String coordSys, String otherCoordSysString, HttpSession session, String questionId){
		DisplayQuestionnaire quest = (DisplayQuestionnaire) session.getAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE);
		SubmissionData subData = fileService.initSubmissionDataEntityFromFile(file, fileOnDisk, description, SubmissionDataType.SITE_FILE, questionId);
	    quest.setSiteFile(subData);
	    quest.setSiteFileCoordinateSystem(coordSys);
	    quest.setSiteFileCoordSysOther(otherCoordSysString);
	}
	
	@RequestMapping(value="/viewSiteFile", method = RequestMethod.GET )
	public String viewSiteFilePage(@RequestParam(required=true,value="filename") String filename, 
			                       @RequestParam(required=true,value="siteFileDataId") Long siteFileDataId, 
			                       Model model){
		JsonUploadSiteFileResponse jsonSiteFile = siteFileService.retrieveJsonSiteFileResponse(filename, siteFileDataId);
		model.addAttribute("siteFileName", filename);
		model.addAttribute("siteFileJson", jsonSiteFile.getJsonString() );
		return "viewSiteFile";
	}
}
