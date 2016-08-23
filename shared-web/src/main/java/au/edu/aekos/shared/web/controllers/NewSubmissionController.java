package au.edu.aekos.shared.web.controllers;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.data.entity.ReusableAnswer;
import au.edu.aekos.shared.data.entity.ReusableResponseGroup;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.ImageAnswer;
import au.edu.aekos.shared.questionnaire.ImageSeriesAnswer;
import au.edu.aekos.shared.questionnaire.PageAnswersModel;
import au.edu.aekos.shared.questionnaire.QuestionVisibilityUtils;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;
import au.edu.aekos.shared.questionnaire.SubmissionFileModel;
import au.edu.aekos.shared.questionnaire.SubmissionFiles;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.citation.CitationDataProvider;
import au.edu.aekos.shared.service.citation.CitationStringService;
import au.edu.aekos.shared.service.file.ImageService;
import au.edu.aekos.shared.service.notification.AdminNotificationService;
import au.edu.aekos.shared.service.notification.ReviewerNotificationService;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.PrepopulateService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.quest.ReusableGroupService;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.service.submission.ExampleCitationDataProviderFactory;
import au.edu.aekos.shared.service.submission.SubmissionDataService;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.valid.PageAnswersValidator;
import au.edu.aekos.shared.valid.SubmissionFilesValidator;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;
import au.edu.aekos.shared.web.json.dynatree.DynatreeNode;
import au.edu.aekos.shared.web.model.ProgressIndicatorInfo;
import au.edu.aekos.shared.web.model.SubmissionDataFileType;

import com.google.gson.Gson;

/**
 * Handles requests creating a new submission .
 */
@Controller
public class NewSubmissionController {
	
	private static final String PROGRESS_INDICATOR_INFO_JSP_PARAM = "progressIndicatorInfo";

	private static final Logger logger = LoggerFactory.getLogger(NewSubmissionController.class);
	
	public static final String SESSION_QUESTIONNAIRE = "QUESTIONNAIRE";
	private static final String DEFAULT_CP_CONFIG_FILE_NAME = "questionnaire3.xml";

	private static final String NO_FILE_FORMAT_TITLE_MESSAGE_TEMPLATE = "NSC01: couldn't find a file format title for the code '%s'";
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SubmissionDataService submissionDataService;
	
	@Autowired
	private PageAnswersValidator pageAnswersValidator;
	
	@Autowired
	private SubmissionFilesValidator submissionFilesValidator;
	
	@Autowired
	private SecurityService authenticationService;
	
	@Autowired
	private ReusableGroupService reusableGroupService;
	
	@Autowired
	private PrepopulateService prepopulateService;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private CitationStringService citationStringService;
	
	@Autowired
	private ExampleCitationDataProviderFactory citationDataProviderFactory;
	
	@Autowired
	private SubmissionModelService submissionModelService;
	
	@Autowired
	private ReviewerNotificationService reviewerNotificationService;
	
	@Autowired
	private AdminNotificationService adminNotificationService;
	
	@RequestMapping(value="/cloneSubmission", method = RequestMethod.GET )
	public String cloneSubmission(@RequestParam Long submissionId, Model model, HttpSession session) throws Exception{
		session.setAttribute(SESSION_QUESTIONNAIRE, null);
		populateDisplayQuestionnaireForClonedSubmission(submissionId, session);
		return "cloneSubmission";
	}
	
	@RequestMapping(value="/newSubmission", method = RequestMethod.GET )
	public String newSubmission(Model model, HttpSession session){
		session.setAttribute(SESSION_QUESTIONNAIRE, null);
		return "newSubmission";
	}
	
	@RequestMapping(value = "/questionnaire", method = RequestMethod.GET)
	public String displayQuestionnaire(@RequestParam(value="config",defaultValue=DEFAULT_CP_CONFIG_FILE_NAME) String configName, 
			                           Model model, 
			                           HttpSession session) throws Exception {  //Not sure whether to use the session or a cache ? ?
		DisplayQuestionnaire quest = initialisePageQuestionnaire(configName);
		model.addAttribute("quest", quest);
		return "questLayout";
	}
	
	@RequestMapping(value = "/questionnaire/{pageNumber}", method = RequestMethod.GET )
	public String displayPage(@PathVariable Integer pageNumber, 
			                  @RequestParam(value="new",required=false) Boolean newQuestionnaire, 
			                  Model model, @ModelAttribute("answers") PageAnswersModel answers , HttpSession session) throws Exception{
		prepareModelForPage(pageNumber, model, session);
		return "questionnairePage";
	}
	
	/**
	 * Used for page navigation (forward and backward).
	 * Data is persisted with the submission set to INCOMPLETE status
	 * 
	 * What about RESUBMITTED??
	 * 
	 */
	@RequestMapping(value = "/questionnaire/{pageNumber}", method = RequestMethod.POST)
	public String processPage(@PathVariable Integer pageNumber, @ModelAttribute("answers") PageAnswersModel answers, BindingResult result, Model model, HttpSession session) throws Exception{
		DisplayQuestionnaire questionnaire =  (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		updateQuestionnaireAnswers( answers, pageNumber, questionnaire);
		pageAnswersValidator.validatePageAnswers(answers, result, questionnaire);
		if(result.hasErrors()){
			result.reject("questionnaire.validation.errors.on.page");
			prepareModelForErrorPage(pageNumber, model, session, answers);
			model.addAttribute(PROGRESS_INDICATOR_INFO_JSP_PARAM, ProgressIndicatorInfo.newInstance(questionnaire));
			return "questionnairePage";
		}
		removeClearedImagesFromMultiselects(answers, questionnaire.getImageSeriesAnswerMap() );
		
		if(pageNumber.intValue() == questionnaire.getPages().size() ){
			return prepareUploadSubmissionFiles(model, questionnaire);
		}

		pageNumber = pageNumber.intValue() + 1;
		prepareModelForPage(pageNumber, model, session);
		return "questionnairePage";
	}

	private String prepareUploadSubmissionFiles(Model model, DisplayQuestionnaire questionnaire) {
		model.addAttribute("quest", questionnaire);
		model.addAttribute("submissionFiles", prepareSubmissionFiles(questionnaire));
		model.addAttribute("fileFormatValues", controlledVocabularyService.getTraitValueList(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, true, true));
		model.addAttribute("lastPageNumber", new Integer( questionnaire.getPages().size() ) );
		model.addAttribute("fileTypes", SubmissionDataFileType.values());
		return "uploadSubmissionFiles";
	}

	/**
	 * Used for saving an incomplete submission during the process.<br />
	 * The save action is triggered by the user and the submission <b>WILL</b> be validated before saving.<br />
	 * The user will be taken back to the same page.<br />
	 * Not to be confused with {@link NewSubmissionController#saveSubmission(Model, HttpSession)}
	 */
	@RequestMapping(value = "/questionnaire/userSave/{pageNumber}", method = RequestMethod.POST)
	public String userSave(@PathVariable Integer pageNumber, @ModelAttribute("answers") PageAnswersModel answers, BindingResult result
			, Model model, HttpSession session) throws Exception{
		DisplayQuestionnaire questionnaire =  (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		pageAnswersValidator.validatePageAnswers(answers, result, questionnaire);
		if(result.hasErrors()){
			prepareModelForErrorPage(pageNumber, model, session, answers);
			result.reject("questionnaire.validation.save.errors");
			return "questionnairePage";
		}
		updateQuestionnaireAnswers(answers, pageNumber, questionnaire);
		String username = getUserNameForSavedSubmission(questionnaire);
		Long savedSubmissionId = submissionService.createSavedSubmission(username, questionnaire);
		questionnaire.setSavedSubmissionId(savedSubmissionId);
		prepareModelForPage(pageNumber, model, session);
		return "questionnairePage";
	}

	@RequestMapping(value = "/questionnaire/{pageNumber}/{changedGroup:.+}", method = RequestMethod.POST)
	public String loadReusableGroupResponses(@PathVariable Integer pageNumber, @PathVariable String changedGroup,
			                                 @ModelAttribute("answers") PageAnswersModel answers, BindingResult result, 
			                                 Model model, HttpSession session) throws Exception{
		DisplayQuestionnaire questionnaire = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE) ;
		updateQuestionnaireAnswers( answers, pageNumber, questionnaire);
		applyReusableGroupAnswers(answers, pageNumber, questionnaire, changedGroup );
		prepareModelForPage(pageNumber, model, session);
		return "questionnairePage";
	}
	
	@RequestMapping(value = "/questionnaire/submitFiles", method = RequestMethod.GET)
	public String displaySubmitFiles(Model model, HttpSession session){
		DisplayQuestionnaire questionnaire = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE) ;
		if(questionnaire == null){
			return "home";
		}
		return prepareUploadSubmissionFiles(model, questionnaire);
	}
	
	@RequestMapping(value = "/questionnaire/processSubmissionFiles", method = RequestMethod.POST)
	public String processSubmissionFiles( @ModelAttribute("submissionFiles") SubmissionFiles submissionFiles, BindingResult result, Model model, HttpSession session) throws Exception{
		DisplayQuestionnaire questionnaire = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE) ;
		submissionFilesValidator.validateFileDetails( submissionFiles, result, questionnaire );
		if(result.hasErrors()) {
			// FIXME get these errors displaying on the page
			return prepareUploadSubmissionFiles(model, questionnaire);
		}
		deleteUnwantedFiles( submissionFiles, questionnaire);
		updateChangesToFilesInSession( submissionFiles, questionnaire );
		model.addAttribute("quest", questionnaire);
		model.addAttribute("geoFeatureSetMap", buildGeoFeatureSetMap(questionnaire) );
		model.addAttribute("exampleCitation", buildExampleCitation(questionnaire));
		model.addAttribute("submissionDataFiles", submissionModelService.mapSubmittedDataInfoToSubmissionModel(questionnaire));
		return "questionnaireSummary";
	}

	String buildExampleCitation(DisplayQuestionnaire questionnaire) {
		CitationDataProvider dataProvider = citationDataProviderFactory.newExampleCitationDataProvider(questionnaire);
		return citationStringService.assembleCitationString(dataProvider);
	}
	
	@RequestMapping(value = {"/questionnaire/editIncompleteSubmission"}, method = RequestMethod.GET )
	public String launchPartiallyCompleteSubmissionEdit(@RequestParam(value="submissionId", required=true) Long submissionId, 
			      Model model, @ModelAttribute("answers") PageAnswersModel answers , HttpSession session) throws Exception{
		populateDisplayQuestionnaireFromStoredSubmission(submissionId, session);
		prepareModelForPage(1, model, session);
		return "questionnairePage";
	}
	
	//Lets not have an http endpoint for this one - validation handled in the GroupAdminController
	public String launchGroupAdminSubmissionEdit( Long submissionId, 
			      Model model , HttpSession session) throws Exception{
		populateDisplayQuestionnaireFromStoredSubmission(submissionId, session);
		prepareModelForPage(1, model, session);
		setGroupAdminEditDisplayQuestionnaire(session);
		return "questionnairePage";
	}
	
	//Persist to DB, Send Email, notify publication gatekeeper, display Thankyou screen,
	@RequestMapping(value = "/questionnaire/finaliseSubmission", method = RequestMethod.GET )
	public String finaliseSubmission(Model model, HttpSession session) throws Exception{
		DisplayQuestionnaire dc = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		if(dc == null){
			return "sessionExpired";
		}
		String username = authenticationService.getLoggedInUsername();
		username = ! StringUtils.hasLength(username) ? "default" : username ;
		Long submissionId = dc.getSubmissionId();
		try{
			if(dc.getSubmissionId() == null){
				submissionId = submissionService.createNewSubmission(username, (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE), SubmissionStatus.SUBMITTED ) ;
			}else if(dc.getLastReview() != null){
				submissionService.updateSubmission(username, dc, SubmissionStatus.RESUBMITTED );
			}
			else{
				submissionService.updateSubmission(username, dc, SubmissionStatus.SUBMITTED );
			}
		}catch(Exception ex){
			logger.error("Error occured finalising submission " + submissionId.toString(), ex);
			adminNotificationService.notifyAdminByEmail("Error occured finalising submission " + submissionId.toString(), "Error occured finalising submission", ex);
		    throw ex;
		}
		//Send reviewer notifications
		reviewerNotificationService.notifyReviewersAboutSubmissionForReview(submissionId);
		session.setAttribute(SESSION_QUESTIONNAIRE, null);
		return "thankyouPage";
	}
	
	/**
	 * Used for saving an incomplete submission at the end of the process.<br />
	 * The save action is triggered by the user and the submission <b>WILL</b> be validated before saving.<br />
	 * The user will be taken to a thank you page.<br />
	 */
	@RequestMapping(value = "/questionnaire/saveSubmission", method = RequestMethod.GET )
	public String saveSubmission(Model model, HttpSession session) throws Exception{
		DisplayQuestionnaire dq = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		String username = getUserNameForSavedSubmission(dq);
		Long savedSubmissionId = submissionService.createSavedSubmission(username, dq) ;
		model.addAttribute("submissionId", savedSubmissionId);
		//Clear session to prevent back button effects
		session.setAttribute(SESSION_QUESTIONNAIRE, null);
		return "partialSaved";
	}
	
	@RequestMapping(value="/popupCheck", method = RequestMethod.GET )
	public String popupCheck(Model model, HttpSession session) throws Exception{
		return "popupCheck";
	}
	
	private DisplayQuestionnaire initialisePageQuestionnaire(String cpConfigFileName) throws Exception{
		QuestionnaireConfig qc  = questionnaireConfigService.getQuestionnaireConfig();
		DisplayQuestionnaire dq = new DisplayQuestionnaire(qc);
		reusableGroupService.populateReusableGroupLists(dq, authenticationService.getLoggedInUsername() );
		prepopulateService.prepopulateQuestionnaire(dq);
		return dq;
	}
	
	private void prepareModelForPage(Integer pageNumber, Model model, HttpSession session) throws Exception {
		if(session.getAttribute(SESSION_QUESTIONNAIRE) == null ){
			DisplayQuestionnaire questionnaire = initialisePageQuestionnaire(null);
			session.setAttribute(SESSION_QUESTIONNAIRE, questionnaire);
		}
		DisplayQuestionnaire quest = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		quest.setCurrentPageNumber(pageNumber);
		model.addAttribute(PROGRESS_INDICATOR_INFO_JSP_PARAM, ProgressIndicatorInfo.newInstance(quest));
		if(pageNumber <= quest.getPages().size() ){
			int index = pageNumber - 1;
			QuestionnairePage page = quest.getPages().get(index);
			model.addAttribute("page", page);
			model.addAttribute("answers",page.getPageAnswers());
			model.addAttribute("controlledVocab", controlledVocabularyService.prepareControlledVocabListsForPage(page) );
			prepareDynatreeData(quest, page, page.getPageAnswers(), model);
		}
		model.addAttribute("quest", quest);
	}
	
	private void prepareModelForErrorPage(Integer pageNumber, Model model, HttpSession session, PageAnswersModel answers) throws Exception {
		DisplayQuestionnaire quest = (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		quest.setCurrentPageNumber(pageNumber);
		model.addAttribute(PROGRESS_INDICATOR_INFO_JSP_PARAM, ProgressIndicatorInfo.newInstance(quest));
		if(pageNumber <= quest.getPages().size() ){
			int index = pageNumber - 1;
			QuestionnairePage page = quest.getPages().get(index);
			model.addAttribute("page", page);
			model.addAttribute("answers",answers );
			model.addAttribute("controlledVocab", controlledVocabularyService.prepareControlledVocabListsForPage(page) );
			prepareDynatreeData(quest, page, answers, model);
		}
		model.addAttribute("quest", quest);
	}
	
	private void prepareDynatreeData(DisplayQuestionnaire quest, QuestionnairePage page, PageAnswersModel answers, Model model){
		if(page.getRequiredTreeSelectVocabs() != null && page.getRequiredTreeSelectVocabs().size() > 0){
			Gson gson = new Gson();
			List<Question> treeSelectQuestions = page.getTreeSelectQuestions();
			Map<String, String > dynatreeVocabDataMap = new HashMap<String, String>();
			Map<String,String> questionIdToDivIdMap = new HashMap<String, String>();
			for(Question q : treeSelectQuestions){
				String trait = q.getTraitName();
                List<DynatreeNode> dynatreeNodeList = controlledVocabularyService.getDynatreeNodeVocabRepresentation(trait);				
                if(answers.getAnswers().containsKey(q.getId()) && answers.getAnswers().get(q.getId()).hasResponse() ){
                	repopulateResponsesToDynatreeList(answers.getAnswers().get(q.getId()), dynatreeNodeList);
                }
                if(! q.getParentSelect()){
                	makeNonLeafNodesUnselectable(dynatreeNodeList);
                }
                dynatreeVocabDataMap.put(q.getId(), gson.toJson(dynatreeNodeList));
                questionIdToDivIdMap.put(q.getId(), "dt" + q.getId().replaceAll("\\.", "_"));
			}
			
			model.addAttribute("dynatreeDataMap", dynatreeVocabDataMap);
			model.addAttribute("dynatreeDivMap", questionIdToDivIdMap);
		}
	}
	
	private void repopulateResponsesToDynatreeList(Answer a, List<DynatreeNode> nodeList){
		if(ResponseType.TREE_SELECT.equals(a.getResponseType()) && a.hasResponse()){
			List<String> values = new ArrayList<String>();
			for(Answer ans : a.getMultiselectAnswerList() ){
				if(ans.hasResponse()){
					values.add(ans.getResponse());
				}
			}
			for(DynatreeNode node : nodeList){
               	node.checkKeysAndExpand(values);		
			}
		}
	}
	
	private void makeNonLeafNodesUnselectable(List<DynatreeNode> dynatreeNodeList){
		for(DynatreeNode node : dynatreeNodeList ){
			node.makeNonLeafNodesUnselectable();
		}
	}
	
	private void updateQuestionnaireAnswers( PageAnswersModel answers, Integer pageNumber, DisplayQuestionnaire quest ){
		if(pageNumber <= quest.getPages().size() ){
			int index = pageNumber - 1;
			QuestionnairePage page = quest.getPages().get(index);
			
			//Write the newly updated answers from form submission, to the existing answer objects - 
			//need to keep the references for the display conditions
		    for(String answerId : answers.getAnswers().keySet()){
		    	if(page.getPageAnswers().getAnswers().containsKey(answerId)){
		    		Answer sessionAnswer = page.getPageAnswers().getAnswers().get(answerId);
		    		sessionAnswer.updateFromFormSubmission(answers.getAnswers().get(answerId));
		    		removeDeletedDataFiles(sessionAnswer, quest);
		    		//Update any forward prepopulation
		    		updateForwardPrepopulation(answerId, answers.getAnswers().get(answerId), quest );
		    	}else{
		    		page.getPageAnswers().getAnswers().put(answerId, answers.getAnswers().get(answerId));
		    	}
		    }
		    
		    
		    Map<String, Question> questionMap = quest.getAllQuestionsMapFromConfig();
		  //Check for question visibility, if the question is not visible, need to set the answer to null
		    //Do this in the order of questions
		    for(String questionId : page.getPageQuestionIdList() ){
		    	Question q = questionMap.get(questionId);
		    	if(q.getDisplayCondition() != null && ! QuestionVisibilityUtils.checkDisplayConditionSubjectVisible(q, page.getPageAnswers(), quest) ){
		    		Answer sessionAnswer = page.getPageAnswers().getAnswers().get(questionId);
		    		sessionAnswer.setResponseToNull();
		    	}
		    }
		    
		    //Update selected reusable group options for page
		    page.getPageAnswers().getSelectedReusableGroupMap().clear();
		    if(answers.getSelectedReusableGroupMap().size() > 0 ){
		    	page.getPageAnswers().getSelectedReusableGroupMap().putAll(answers.getSelectedReusableGroupMap() );
		    }
		    
		    //Update the display text map for Controlled Vocab answers
		    for(Map.Entry<String,Answer> entry : page.getPageAnswers().getAnswers().entrySet() ){
		    	Answer a = entry.getValue();
		    	if( ResponseType.CONTROLLED_VOCAB.equals( a.getResponseType() ) || 
		    			ResponseType.CONTROLLED_VOCAB_SUGGEST.equals( a.getResponseType() ) ){
		    		Question q = questionMap.get(a.getQuestionId());
		    		String displayResponse = controlledVocabularyService.getTraitDisplayText( q.getTraitName() , a.getResponse() );
		    		a.setDisplayResponse( StringUtils.hasLength ( displayResponse ) ? displayResponse : a.getResponse() );
		    	}else if( ResponseType.MULTISELECT_CONTROLLED_VOCAB.equals( a.getResponseType() ) ){
		    		Question q = questionMap.get(a.getQuestionId());
		    		String traitName = q.getTraitName();
		    		for(Answer msAnswer : a.getMultiselectAnswerList() ){
		    			String displayResponse = controlledVocabularyService.getTraitDisplayText( traitName , msAnswer.getResponse() );
		    			msAnswer.setDisplayResponse( StringUtils.hasLength ( displayResponse ) ? displayResponse : msAnswer.getResponse() );
		    		}
		    	}
		    }
		}
	}
	
	private void removeDeletedDataFiles(Answer sessionAnswer, DisplayQuestionnaire quest){
		if(! StringUtils.hasLength(sessionAnswer.getResponse())){
			if(ResponseType.SITE_FILE.equals(sessionAnswer.getResponseType())
					|| ResponseType.LICENSE_CONDITIONS.equals(sessionAnswer.getResponseType())
					|| ResponseType.SPECIES_LIST.equals(sessionAnswer.getResponseType())
					|| ResponseType.DOCUMENT.equals(sessionAnswer.getResponseType()) ){
				if(quest.getQuestionUploadSubmissionDataMap().containsKey(sessionAnswer.getQuestionId())){
					SubmissionData sd = quest.getQuestionUploadSubmissionDataMap().get(sessionAnswer.getQuestionId());
					if(sd != null){
						List<SubmissionData> sdList = new ArrayList<SubmissionData>();
						sdList.add(sd);
						submissionDataService.asyncDeleteNonPersistedSubmissionData(sdList);
					}
					quest.getQuestionUploadSubmissionDataMap().remove(sessionAnswer.getQuestionId());
				}
			}
		}
	}
	
	private void updateForwardPrepopulation(String answerId,Answer answer, DisplayQuestionnaire quest){
		if(quest.getForwardPopulateMap().containsKey(answerId) ){
			List<String> forwardQIds = quest.getForwardPopulateMap().get(answerId);
			Map<String, Answer> answerMap = quest.getAllAnswers();
			for(String fqId : forwardQIds){
				Answer forwardAnswer = answerMap.get(fqId);
				if(! forwardAnswer.hasResponse() ){
					forwardAnswer.setResponse(answer.getResponse());
					forwardAnswer.setSuggestedResponse(answer.getSuggestedResponse());
				}
			}
		}
	}
	
	
	private void removeClearedImagesFromMultiselects(PageAnswersModel answers, Map<String, ImageSeriesAnswer> imageSeriesAnswerMap ){
		for(Map.Entry<String, Answer >  entry : answers.getAnswers().entrySet()){
			String questionId = entry.getKey();
			Answer answer = entry.getValue();
			if(ResponseType.MULTISELECT_IMAGE.equals( answer.getResponseType() ) ){
				ImageSeriesAnswer imageSeriesAnswer = imageSeriesAnswerMap.get(questionId);
				if(imageSeriesAnswer == null){
					continue;
				}
				if(answer.getMultiselectAnswerList().size() == 0 && imageSeriesAnswer.getImageAnswerList().size() == 0){
					continue;
				}
				if(answer.getMultiselectAnswerList().size() == 0 && imageSeriesAnswer.getImageAnswerList().size() > 0){
				    for(ImageAnswer imageAnswer : imageSeriesAnswer.getImageAnswerList() ){
				    	imageService.deleteImageAnswerFiles(imageAnswer);
				    }
					imageSeriesAnswer.getImageAnswerList().clear();
				}else if(answer.getMultiselectAnswerList().size() > 0 && imageSeriesAnswer.getImageAnswerList().size() > 0){
					//Based on the index of null answers, need to remove answer list images at that index
					Iterator<ImageAnswer> iter = imageSeriesAnswer.getImageAnswerList().iterator();
					
					for(Answer msA : answer.getMultiselectAnswerList() ){
						if(iter.hasNext()){
							ImageAnswer ia = iter.next();
							//The response is the filename, if its null, the file has been cleared on the ui
							if(!StringUtils.hasLength(msA.getResponse()) ) {
								if( ia != null){
									imageService.deleteImageAnswerFiles(ia);
								}
								iter.remove();
							}
						}
					}
					//This should only happen if there are more ImageAnswers than answers ( i.e. never happen )
					while(iter.hasNext() ){
						ImageAnswer ia = iter.next();
						if(ia != null){
						    imageService.deleteImageAnswerFiles(ia);
						}
						iter.remove();
					}
				}
				logger.debug("Cleansed Images for question " + answer.getQuestionId());
			}
		}
	}
	  
	private void deleteUnwantedFiles( SubmissionFiles submissionFiles, DisplayQuestionnaire questionnaire  ){
		//loop thru submission files to see if any have been deleted.
		if( submissionFiles.getSubmissionFileList() != null && submissionFiles.getSubmissionFileList().size() > 0 ){
			for(SubmissionFileModel subFile : submissionFiles.getSubmissionFileList() ){
				if(subFile.getDeleted().booleanValue()){
//					String fileName = subFile.getFilename();
					String storedFileName = subFile.getStoredFilename();
					Iterator<SubmissionData> sdIterator = questionnaire.getSubmissionDataList().iterator();
					while(sdIterator.hasNext()){
						SubmissionData sd = sdIterator.next();
						//From the session questionnaire there will only be one File Storage Location
						Iterator<StorageLocation> locInter = sd.getStorageLocations().iterator();
						boolean isFile = false;
						while(locInter.hasNext() ){
							StorageLocation sl = locInter.next();
							if(sl.getObjectName().equals(storedFileName)){
								isFile = true;
								FileSystemStorageLocation fssl = (FileSystemStorageLocation) sl;
								File fileToDelete = new File(fssl.getFspath());
								fileToDelete.delete();
								break;
							}
						}
						if(isFile){
							sdIterator.remove();
							break;
						}
					}
				}
			}
		}
	}
	
	//Possible to change the description and / or embargo date - just copy over.
	private void updateChangesToFilesInSession( SubmissionFiles submissionFiles, DisplayQuestionnaire questionnaire ) throws ParseException{
		for(SubmissionFileModel fileModel : submissionFiles.getSubmissionFileList() ){
			if(! fileModel.getDeleted() ){
				//Can either match files on ID - or via the filename if we are dealing with a new file.
				SubmissionData matchedSubData = null;
				for(SubmissionData subData : questionnaire.getSubmissionDataList() ){				
					if( fileModel.getId() != null && fileModel.getId().equals( subData.getId() )){
						matchedSubData = subData;
						break;
					}else if(fileModel.getFilename().equals( subData.getFileName() ) && subData.containsFilesystemStorageName(fileModel.getStoredFilename())){
						matchedSubData = subData;
						break;
					}
				}
				if(matchedSubData != null){
					matchedSubData.setFileDescription(fileModel.getDescription());
				}
			}
		}
	}
	
	private void populateDisplayQuestionnaireForClonedSubmission(Long submissionId, HttpSession session) throws Exception{
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireForCloneFromSubmissionEntity(submissionId);
		session.setAttribute(SESSION_QUESTIONNAIRE, displayQuestionnaire);
	}
	
	private void populateDisplayQuestionnaireFromStoredSubmission(Long submissionId, HttpSession session) throws Exception{
		DisplayQuestionnaire displayQuestionnaire = submissionService.populateDisplayQuestionnaireFromSubmissionEntity(submissionId);
		session.setAttribute(SESSION_QUESTIONNAIRE, displayQuestionnaire);
	}
	
	private void setGroupAdminEditDisplayQuestionnaire(HttpSession session){
		DisplayQuestionnaire questionnaire =  (DisplayQuestionnaire) session.getAttribute(SESSION_QUESTIONNAIRE);
		questionnaire.setGroupAdminEdit(true);
	}
	
	SubmissionFiles prepareSubmissionFiles(DisplayQuestionnaire questionnaire) {
		SubmissionFiles result = new SubmissionFiles();
		if(!questionnaire.hasSubmissionData()) {
			return result;
		}
		for(SubmissionData sd : questionnaire.getSubmissionDataList()) {
			SubmissionFileModel addedData = null;
			if(sd.getId() != null){
				SubmissionData hydratedSD = submissionDataService.getHydratedSubmissionData( sd.getId() );
				addedData = new SubmissionFileModel(hydratedSD);
				result.getSubmissionFileList().add(addedData);
			}else{
				addedData = new SubmissionFileModel(sd);
				result.getSubmissionFileList().add(addedData);
			}
			determineFileFormatTitle(addedData);
		}
		return result;
	}

	private void determineFileFormatTitle(SubmissionFileModel subFile) {
		String fileFormatTitle = controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, subFile.getFileFormat());
		subFile.setFileFormatTitle(fileFormatTitle);
		if (fileFormatTitle == null) {
			logger.warn(String.format(NO_FILE_FORMAT_TITLE_MESSAGE_TEMPLATE, subFile.getFileFormat()));
		}
	}
	
	private void applyReusableGroupAnswers(PageAnswersModel answers, Integer pageNumber, DisplayQuestionnaire sessionQuestionnaire, String changedGroup ) throws Exception{
		String groupName = answers.getSelectedReusableGroupMap().get(changedGroup);
		Map<String, ReusableAnswer> reusableAnswerMap = null;
		if(StringUtils.hasLength(groupName)){
			ReusableResponseGroup rrg = reusableGroupService.getGroupByName(groupName, sessionQuestionnaire.getQuestionnaireConfigEntityId(), authenticationService.getLoggedInUsername(), changedGroup);
			if(rrg == null){
			    throw new Exception("reusable response group not found : groupName:'" 
			                          + groupName + "'  questConfigId:'" + sessionQuestionnaire.getQuestionnaireConfigEntityId() + 
			                          "'  " + authenticationService.getLoggedInUsername() + "  " + changedGroup);
			    
			}
			reusableAnswerMap = rrg.getAnswersMappedByQuestionId();
		}
		QuestionGroup rgConfig = sessionQuestionnaire.getConfig().getQuestionGroupMap().get(changedGroup);
		Map<String, Answer> sessionAnswerMap = sessionQuestionnaire.getAllAnswers();
		for(String questId : rgConfig.getAllGroupChildQuestionIds()){
			Answer sessionAnswer = sessionAnswerMap.get(questId);
			sessionAnswer.setResponseToNull();
			if(reusableAnswerMap != null ){
				ReusableAnswer ra = reusableAnswerMap.get(questId);
				if(ra != null){
					sessionAnswer.assignFromReusableAnswer(ra);
				}
			}
		}
	}
	
	private Map<String, JsonGeoFeatureSet> buildGeoFeatureSetMap(DisplayQuestionnaire quest){
		Map<String, JsonGeoFeatureSet> geoFeatSetMap = new HashMap<String, JsonGeoFeatureSet>();
		for( Map.Entry<String,Answer> entry : quest.getAllAnswers().entrySet() ){
			if(ResponseType.GEO_FEATURE_SET.equals( entry.getValue().getResponseType() ) 
					&& StringUtils.hasLength( entry.getValue().getResponse()) ){
				Gson gson = new Gson();
				JsonGeoFeatureSet gfs = gson.fromJson(entry.getValue().getResponse(), JsonGeoFeatureSet.class);
				if(gfs != null){
					geoFeatSetMap.put(entry.getKey(), gfs );
				}
			}
		}
		return geoFeatSetMap;
	}

    public static final String GLOBAL_EXCEPTION_VIEW = "error/globalException";

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	logger.error("Global Exception Caught" , e);
        
    	//Need to see if anything is in the session
    	HttpSession session = req.getSession();
    	if(session != null){
    	    Object dq = session.getAttribute(SESSION_QUESTIONNAIRE);
    	    if(dq != null){
    	    	DisplayQuestionnaire dispQuestionnaire = (DisplayQuestionnaire) dq;
    	    	if(StringUtils.hasLength( dispQuestionnaire.getSubmissionTitle() )){
    	    		logger.error("Global Exception Caught - submission title " + dispQuestionnaire.getSubmissionTitle());
    	    	}
    	    	if( dispQuestionnaire.getSubmissionId() != null ){
    	    		logger.error("Global Exception Caught - submission id " + dispQuestionnaire.getSubmissionId());
    	    	}
    	    	if(dispQuestionnaire.getSavedSubmissionId() != null){
    	    		logger.error("Global Exception Caught - saved submission id " + dispQuestionnaire.getSavedSubmissionId());
    	    	}
    	    }else{
    	    	logger.error("Global Exception - no questionnaire in session");
    	    }    			
    	}		
    	sendAdminAlertEmail( e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(GLOBAL_EXCEPTION_VIEW);
        return mav;
    }
    
    private String getUserNameForSavedSubmission(DisplayQuestionnaire dq){
    	if(dq.getGroupAdminEdit() != null && dq.getGroupAdminEdit()){
    		if(dq.getSubmissionId() != null || dq.getSavedSubmissionId() != null){
    			Long submissionLookupId = (dq.getSubmissionId() != null ? dq.getSubmissionId() : dq.getSavedSubmissionId() );
    			Submission sub = submissionService.retrieveSubmissionById(submissionLookupId);
    			return sub.getSubmittingUsername();
    		}else{
    			return authenticationService.getLoggedInUsername();
    		}
    	}else{
    		return authenticationService.getLoggedInUsername();
    	}
    }
    
    private void sendAdminAlertEmail(Exception e){
    	adminNotificationService.notifyAdminByEmail("SHaRED Global Exception Caught", "Global Exception Caught", e);
    }
	
	public void setControlledVocabularyService(ControlledVocabularyService controlledVocabularyService) {
		this.controlledVocabularyService = controlledVocabularyService;
	}

	public void setSubmissionDataService(SubmissionDataService submissionDataService) {
		this.submissionDataService = submissionDataService;
	}
}
