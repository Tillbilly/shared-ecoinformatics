package au.edu.aekos.shared.web.controllers.integration;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.service.integration.SubmissionFileInfoService;
import au.edu.aekos.shared.service.integration.SubmissionInfoModelSummaryService;
import au.edu.aekos.shared.service.integration.VocabIntegrationService;
import au.edu.aekos.shared.service.stats.StatisticsService;
import au.org.aekos.shared.api.json.JsonNewAekosVocabResponse;
import au.org.aekos.shared.api.json.JsonSharedVocabResponse;
import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfoResponse;

import com.google.gson.Gson;

@Controller
public class AekosIntegrationController {

	private static final Logger logger = LoggerFactory.getLogger(AekosIntegrationController.class);
	private static final boolean YES_CHECK_IF_PUBLISHED = true;

	@Autowired
	private VocabIntegrationService vocabIntegrationService;
	
	@Autowired
	private SubmissionInfoModelSummaryService submissionSummaryService;
	
	@Autowired
	private SubmissionFileInfoService submissionFileInfoService;
	
	@Autowired
	private StatisticsService statisticsService;
	
	@RequestMapping("integration/newAekosVocabEntries")
	public void getNewAekosVocabEntries(HttpServletResponse response) throws IOException{
		JsonNewAekosVocabResponse jsonResponse = vocabIntegrationService.getAllNewAekosIndexValues();
		writeJsonStringToHttpServletResponse(jsonResponse.getJsonString(), response );
	}
	
	@RequestMapping("integration/newAekosVocabEntries/{vocabName}")
	public void getNewAekosVocabEntries(@PathVariable String vocabName, HttpServletResponse response) throws IOException{
		JsonNewAekosVocabResponse jsonResponse = vocabIntegrationService.getNewAekosIndexValues(vocabName);
		writeJsonStringToHttpServletResponse(jsonResponse.getJsonString(), response );
	}
	
	@RequestMapping("integration/sharedVocabEntries")
	public void getSharedVocabEntries(HttpServletResponse response) throws IOException{
		JsonSharedVocabResponse vocabResponse = vocabIntegrationService.getAllSharedVocabs();
		writeJsonStringToHttpServletResponse(vocabResponse.getJsonString(), response );
	}
	
	@RequestMapping("integration/sharedVocabEntries/{vocabName}")
	public void getSharedVocabEntries(@PathVariable String vocabName, HttpServletResponse response) throws IOException{
		JsonSharedVocabResponse vocabResponse = vocabIntegrationService.getSharedVocab(vocabName);
		writeJsonStringToHttpServletResponse(vocabResponse.getJsonString(), response );
	}

	/**
	 * Submissions must be published in order to return their full information model
	 * @param submissionId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/integration/datasetSummary/{datasetId}", method=RequestMethod.GET)
	public void getSubmissionSummary(@PathVariable Long datasetId,  HttpServletResponse response) throws IOException{
		//Check submissionId exists, and that it is published
		logger.info("Request for Dataset Details with ID: " + datasetId);
		ResponseGetDatasetSummary respObj = submissionSummaryService.buildSubmissionSummaryForPortal(datasetId, YES_CHECK_IF_PUBLISHED);
		writeJsonStringToHttpServletResponse(respObj.getJsonString() ,response);
	}
	
	/**
	 * Returns json serialised file info for the given submissionId, used by aekos portal file extract.
	 * @param submissionId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/integration/submissionFileInfo/{submissionId}", method=RequestMethod.GET)
	public void getSubmissionFileInfo(@PathVariable Long submissionId, @RequestParam(value="email",required=false) String email, HttpServletResponse response) throws IOException{
		SubmissionFileInfoResponse sfiResponse = submissionFileInfoService.retrieveSubmissionFileInfo(submissionId);
		Gson gson = new Gson();
		writeJsonStringToHttpServletResponse(gson.toJson(sfiResponse), response);
		logger.info("Submission File Info Request for submissionId " + submissionId.toString());
		logger.debug(gson.toJson(sfiResponse));
		//If an email address is submitted, record a download request stat
		if(StringUtils.hasLength(email)){
			statisticsService.asyncRecordDownloadRequest(submissionId, email);
		}
	}
	
	private void writeJsonStringToHttpServletResponse(String json, HttpServletResponse response) throws IOException{
		response.setContentType("application/json");
		response.getWriter().print(json);
		response.getWriter().flush();
		response.flushBuffer();
	}
}
