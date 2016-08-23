package au.edu.aekos.shared.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.quest.TraitValue;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.valid.AddEntryVocabAnalysisValidator;
import au.edu.aekos.shared.web.model.AnalysisResponseAction;
import au.edu.aekos.shared.web.model.AnalysisResponseModel;
import au.edu.aekos.shared.web.model.AnswerReviewModel;
import au.edu.aekos.shared.web.model.QuestionModel;
import au.edu.aekos.shared.web.model.QuestionSetModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;
import au.edu.aekos.shared.web.model.VocabAnalysisModel;

@Controller
public class AnalyseVocabSuggestionController {


	private static final Logger logger = LoggerFactory.getLogger(AnalyseVocabSuggestionController.class);
	
	@Autowired
	private ReviewSubmissionController reviewSubmissionController;

	@Autowired
	private SubmissionModelService submissionManager;

	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private ControlledVocabularyService vocabService;
	
	@Autowired
	private AddEntryVocabAnalysisValidator addEntryValidator;
	
	@Autowired
	private SubmissionService submissionService;
	
	@RequestMapping(value="/analyseVocabSuggestion/{questionId:.+}", method = RequestMethod.POST)
	public String analyseVocabSuggestion(@PathVariable String questionId, @ModelAttribute("review") SubmissionReviewModel submissionReview,  Model model, HttpSession session ) throws Exception{
		session.setAttribute(ReviewSubmissionController.CURRENT_REVIEW, submissionReview);
		return prepareModelForVocabSuggestionAnalysisView(submissionReview.getSubmissionId(), questionId, model, session);
	}
	
	@RequestMapping(value="/analyseVocabSuggestion/{questionId}/{groupIndex}/{groupQuestionId:.+}", method = RequestMethod.POST)
	public String analyseVocabSuggestionQuestionSet(@PathVariable String questionId, 
			                                        @PathVariable Integer groupIndex, 
			                                        @PathVariable String groupQuestionId,
			                                        @ModelAttribute("review") SubmissionReviewModel submissionReview,  
			                                        Model model, 
			                                        HttpSession session ) throws Exception{
		session.setAttribute(ReviewSubmissionController.CURRENT_REVIEW, submissionReview);
		return prepareModelForVocabSuggestionAnalysisViewQS(submissionReview.getSubmissionId(), questionId, groupIndex, groupQuestionId, model, session);
	}
	
	@RequestMapping(value="/cancelAddVocabValues", method = RequestMethod.POST)
	public String cancelAddVocabValues( @ModelAttribute VocabAnalysisModel analysisModel , Model model, HttpSession session ) throws Exception{
		SubmissionReviewModel submissionReview = (SubmissionReviewModel) session.getAttribute(ReviewSubmissionController.CURRENT_REVIEW);
		if(submissionReview == null){
			return "sessionExpired";
		}
		return prepareModelForVocabSuggestionAnalysisView(submissionReview.getSubmissionId(), analysisModel.getQuestionId(), model, session);
	}
	
	private String prepareModelForVocabSuggestionAnalysisView(Long submissionId, String questionId, Model model, HttpSession session) throws Exception{
		SubmissionModel submission = submissionManager.getSubmission( submissionId );
		Map<String, QuestionModel> qmMap = submission.getAllQuestionModelMap();
		setVocabModelAttributesForQuestion( qmMap.get(questionId), submission, model, qmMap );
		return "vocabSuggestionAnalysis";
	}
	
	private String prepareModelForVocabSuggestionAnalysisViewQS(Long submissionId, String questionId, Integer groupIndex, String qsQId, Model model, HttpSession session) throws Exception{
		SubmissionModel submission = submissionManager.getSubmission( submissionId );
		Map<String, QuestionModel> qmMap = submission.getAllQuestionModelMap();
		QuestionModel parentQuestionGroupModel = qmMap.get(questionId);
		setVocabModelAttributesForQuestionSetResponse(questionId, parentQuestionGroupModel, groupIndex, qsQId, submission, model );
		return "vocabSuggestionAnalysis";
	}
	
	/*Need to do the following:
	  
	  * Determine what trait ( vocab ) the suggested response is for
	    Then get all of the vocab values in a list ( or table ).
	    
	  * If the response is for an answer with metatag '*Suggest', need to
	    find the question with the actual metatag, determine what vocab from the parent question,
	    and get all the answers for the parent question  
	
	*/
	private void setVocabModelAttributesForQuestion(
			QuestionModel questionModel, SubmissionModel submission, Model model, Map<String, QuestionModel> qmMap ) throws Exception {
		QuestionModel parentQuestion = null;
		String vocabName = questionModel.getControlledVocabTrait();
        if(questionModel.getMetatag() != null && questionModel.getMetatag().endsWith("Suggest")){ //dynatree 'other' suggest  or multi select cv suggest post question
           String parentQmetatag = questionModel.getMetatag().replaceAll("Suggest", "");
           Map<String, String> metatagToQuestionIdMap = questionnaireConfigService.getMetatagToQuestionIdMap(submission.getQuestionnaireConfigId());
           if(! metatagToQuestionIdMap.containsKey(parentQmetatag)){
        	   logger.error("");
        	   //HANDLE THIS - should be validated on questionnaire loading
           }
           parentQuestion = qmMap.get(metatagToQuestionIdMap.get(parentQmetatag));
           vocabName = parentQuestion.getControlledVocabTrait();
        }
        model.addAttribute("vocabName", vocabName);
        model.addAttribute("vocabList", vocabService.getTraitValueList(vocabName, null, true) );
        model.addAttribute("question", questionModel);
        model.addAttribute("parentQuestion", parentQuestion);
        model.addAttribute("submission",submission);
	}
	
	private void setVocabModelAttributesForQuestionSetResponse( String questionId, 
            QuestionModel containingQuestionSetModel, 
            Integer groupIndex, 
            String qsQId, 
            SubmissionModel submission, 
            Model model ){
	    model.addAttribute("containingQuestion", containingQuestionSetModel);
		model.addAttribute("groupIndex", groupIndex);
		model.addAttribute("groupQID", qsQId);
		QuestionSetModel qsm = containingQuestionSetModel.getQuestionSetModelList().get(groupIndex);
		QuestionModel questionModel =  qsm.getQuestionModelMap().get(qsQId);
		String vocabName = questionModel.getControlledVocabTrait();
		model.addAttribute("questionSet", qsm);
		model.addAttribute("question", questionModel);
		model.addAttribute("vocabName", vocabName);
	    model.addAttribute("vocabList", vocabService.getTraitValueList(vocabName, null, true) );
	    model.addAttribute("submission",submission);
	}
	
	
	private static final String VOCAB_ANALYSIS_MODEL = "VOCAB_ANALYSIS_MODEL";
	
	@RequestMapping(value="/processVocabAnalysis", method = RequestMethod.POST)
	public String processVocabAnalysis( @ModelAttribute VocabAnalysisModel vaModel , Model model, HttpSession session ) throws Exception{
		SubmissionReviewModel reviewModel = (SubmissionReviewModel) session.getAttribute(ReviewSubmissionController.CURRENT_REVIEW);
		if(reviewModel == null){
			return "sessionExpired";
		}
		//If there are adds,  need to fire up the add to vocab page, and validate the addition before moving on . . . 
		if(responsesContainAdds(vaModel)){
			session.setAttribute(VOCAB_ANALYSIS_MODEL, vaModel);
			vaModel.getNewVocabEntryMap().clear();
			for(AnalysisResponseModel arm : vaModel.getResponseList()){
				if(AnalysisResponseAction.ADD.equals(arm.getAction()) ){
					TraitValue tv = new TraitValue();
					tv.setTraitValue(arm.getOriginalValue());
					vaModel.getNewVocabEntryMap().put(arm.getOriginalValue(), tv);
				}
			}
			Boolean isCustom = vocabService.getListOfAvailableCustomTraits().contains(vaModel.getVocabName());
			model.addAttribute("isCustom", isCustom);
			model.addAttribute("analysisModel", vaModel);
			return "vocabAnalysisAddVocab";
		}
		Map<String, TraitValue> responseTraitModificationMap = new HashMap<String, TraitValue>();
		Map<String, TraitValue> chosenTraitsMap = prepareChosenVocabTraits(vaModel);
		if(chosenTraitsMap.size() > 0){
			responseTraitModificationMap.putAll(chosenTraitsMap);
		}
		
		updateSubmissionResponsesForAddedOrChosenVocabs(vaModel , responseTraitModificationMap );
		
		updateSubmissionResponsesForUseParent(vaModel);
	    updateSessionReviewModel(vaModel, responseTraitModificationMap, reviewModel);
		return reviewSubmissionController.modifyReview( vaModel.getSubmissionId() , model, session  ) ;
	}
	
	@RequestMapping(value="/processAddVocabValues", method = RequestMethod.POST)
	public String processAddVocabValues( @ModelAttribute VocabAnalysisModel analysisModel, BindingResult result, Model model, HttpSession session ) throws Exception{
		//First check to see whether the Review is still active
		SubmissionReviewModel reviewModel = (SubmissionReviewModel) session.getAttribute(ReviewSubmissionController.CURRENT_REVIEW);
		if(reviewModel == null){
			return "sessionExpired";
		}
		addEntryValidator.validate(analysisModel, result);
		if(result.hasErrors()){
			model.addAttribute("analysisModel", analysisModel);
			return "vocabAnalysisAddVocab";
		}
	    String vocabName = analysisModel.getVocabName();
	    List<TraitValue> newTraits = new ArrayList<TraitValue>(); 
	    newTraits.addAll(analysisModel.getNewVocabEntryMap().values());
		vocabService.addSuggestedValuesToVocab(vocabName, newTraits);
		Map<String, TraitValue> responseTraitModificationMap = new HashMap<String, TraitValue>();
		responseTraitModificationMap.putAll(analysisModel.getNewVocabEntryMap());
		
		Map<String, TraitValue> chosenTraitsMap = prepareChosenVocabTraits(analysisModel);
		if(chosenTraitsMap.size() > 0){
			responseTraitModificationMap.putAll(chosenTraitsMap);
		}
		if(responseTraitModificationMap.size() > 0){
		    updateSubmissionResponsesForAddedOrChosenVocabs(analysisModel , responseTraitModificationMap );
		}
	    updateSessionReviewModel(analysisModel, responseTraitModificationMap,  reviewModel);
	    return reviewSubmissionController.modifyReview( analysisModel.getSubmissionId() , model, session  ) ;
	}
	
	
	private void updateSessionReviewModel(VocabAnalysisModel analysisModel, 
			                              Map<String, TraitValue> responseTraitModificationMap,
			                              SubmissionReviewModel sessionReviewModel){
		if(StringUtils.hasLength(analysisModel.getParentQuestionId())){
			updateSessionReviewModelForParentQuestion( analysisModel,responseTraitModificationMap, sessionReviewModel);
			return;
		}
		if(StringUtils.hasLength(analysisModel.getGroupQuestionId())){
			updateSessionReviewModelForQuestionGroup(analysisModel,responseTraitModificationMap, sessionReviewModel);
			return;
		}
		
		//We are adding to the reviewModel comments and selecting reject for anything but 'Add'
		AnswerReviewModel arm = sessionReviewModel.getAnswerReviews().get(analysisModel.getQuestionId());
		if(arm == null){
			arm  = new AnswerReviewModel();
			sessionReviewModel.getAnswerReviews().put(analysisModel.getQuestionId(), arm);
		}
		arm.setComment(null);
		List<AnalysisResponseAction> handledActionList = new ArrayList<AnalysisResponseAction>();
		for(AnalysisResponseModel anRespModel : analysisModel.getResponseList()){
			if(anRespModel.getAction() == null){
				continue;
			}
			switch(anRespModel.getAction()){
			case ADD:
				if(responseTraitModificationMap.containsKey( anRespModel.getOriginalValue() ) ){ //The trait has been added
					String tvAdded = responseTraitModificationMap.get(anRespModel.getOriginalValue()).getTraitValue();
					String com = "New trait " + tvAdded + " added to vocab " + analysisModel.getVocabName();
					arm.appendComment(com, "\n");
					if(handledActionList.size() == 0){
						arm.setOutcome(AnswerReviewOutcome.PASS);
						handledActionList.add(AnalysisResponseAction.ADD);
					}
				}
				break;
			case CHOOSE:
				String com = "Value " + anRespModel.getOriginalValue() + " changed to " +anRespModel.getChosenValue();
				arm.appendComment(com, "\n");
				if(handledActionList.size() == 0){
					arm.setOutcome(AnswerReviewOutcome.PASS);
					handledActionList.add(AnalysisResponseAction.CHOOSE);
				}
				break;
			case SUGGEST:
				arm.setOutcome(AnswerReviewOutcome.REJECT);
				String comm = "The vocabulary value " + anRespModel.getChosenValue() + " is suggested, for answer " + anRespModel.getOriginalValue();
				arm.appendComment(comm, "\n");
				handledActionList.add(AnalysisResponseAction.REJECT);
				break;
			case REJECT:
				String commen = "The vocabulary value " + anRespModel.getOriginalValue() + " has been rejected";
				arm.appendComment(commen, "\n");
				arm.setOutcome(AnswerReviewOutcome.REJECT);
				handledActionList.add(AnalysisResponseAction.REJECT);
				break;
			}
		}
	}
	
	private void updateSessionReviewModelForParentQuestion(VocabAnalysisModel analysisModel, 
			                                               Map<String, TraitValue> responseTraitModificationMap, 
			                                               SubmissionReviewModel sessionReviewModel){
		//We are adding to the reviewModel comments and selecting reject for anything but 'Add'
		AnswerReviewModel arm = sessionReviewModel.getAnswerReviews().get(analysisModel.getQuestionId());
		if(arm == null){
			arm  = new AnswerReviewModel();
			sessionReviewModel.getAnswerReviews().put(analysisModel.getQuestionId(), arm);
		}
		arm.setComment(null);
		AnswerReviewModel parm = sessionReviewModel.getAnswerReviews().get(analysisModel.getParentQuestionId());
		if(parm == null){
			parm  = new AnswerReviewModel();
			sessionReviewModel.getAnswerReviews().put(analysisModel.getParentQuestionId(), parm);
		}
		List<AnalysisResponseAction> handledActionList = new ArrayList<AnalysisResponseAction>();
		for(AnalysisResponseModel anRespModel : analysisModel.getResponseList()){
			if(anRespModel.getAction() == null){
				continue;
			}
			switch(anRespModel.getAction()){
			case ADD:
				if(responseTraitModificationMap.containsKey( anRespModel.getOriginalValue() ) ){ //The trait has been added
					String tvAdded = responseTraitModificationMap.get(anRespModel.getOriginalValue()).getTraitValue();
					String com = "New trait " + tvAdded + " added to vocab " + analysisModel.getVocabName();
					parm.appendComment(com, "\n");
					com = "trait " + tvAdded + " moved to parent question";
					arm.appendComment(com, "\n");
					if(handledActionList.size() == 0){
						//arm.setOutcome(AnswerReviewOutcome.PASS);
						handledActionList.add(AnalysisResponseAction.ADD);
					}
				}
				break;
			case CHOOSE:
				String com = "Value " + anRespModel.getOriginalValue() + " changed to " +anRespModel.getChosenValue();
				parm.appendComment(com, "\n");
				com = "Value " + anRespModel.getOriginalValue() + " moved to parent question";
				arm.appendComment(com, "\n");
				if(handledActionList.size() == 0){
					//parm.setOutcome(AnswerReviewOutcome.PASS);
					handledActionList.add(AnalysisResponseAction.CHOOSE);
				}
				break;
			case SUGGEST:
				arm.setOutcome(AnswerReviewOutcome.REJECT);
				String comm = "The vocabulary value " + anRespModel.getChosenValue() + " is suggested, for answer " + anRespModel.getOriginalValue();
				arm.appendComment(comm, "\n");
				handledActionList.add(AnalysisResponseAction.REJECT);
				break;
			case REJECT:
				String commen = "The vocabulary value " + anRespModel.getOriginalValue() + " has been rejected";
				arm.appendComment(commen, "\n");
				arm.setOutcome(AnswerReviewOutcome.REJECT);
				handledActionList.add(AnalysisResponseAction.REJECT);
				break;
			case PARENT:
				com = "The response " + anRespModel.getOriginalValue() + " is already chosen in parent question";
				arm.appendComment(com, "\n");
				//arm.setOutcome(AnswerReviewOutcome.REJECT);
				if(handledActionList.size() == 0){
					arm.setOutcome(AnswerReviewOutcome.PASS);
				}
				handledActionList.add(AnalysisResponseAction.PARENT);
				break;
			}
		}
	}
	
	
	private void updateSessionReviewModelForQuestionGroup(VocabAnalysisModel analysisModel, 
            Map<String, TraitValue> responseTraitModificationMap, 
            SubmissionReviewModel sessionReviewModel){
		
		// We are adding to the reviewModel comments and selecting reject for
		// anything but 'Add'
		AnswerReviewModel arm = sessionReviewModel.getAnswerReviews().get(analysisModel.getGroupQuestionId());
		if (arm == null) {
			arm = new AnswerReviewModel();
			sessionReviewModel.getAnswerReviews().put(analysisModel.getGroupQuestionId(), arm);
		}
		
		//List<AnalysisResponseAction> handledActionList = new ArrayList<AnalysisResponseAction>();
		for (AnalysisResponseModel anRespModel : analysisModel.getResponseList()) {
			if (anRespModel.getAction() == null) {
				continue;
			}
			switch (anRespModel.getAction()) {
			case ADD:
				if (responseTraitModificationMap.containsKey(anRespModel.getOriginalValue())) { // The trait has been added
					String tvAdded = responseTraitModificationMap.get(anRespModel.getOriginalValue()).getTraitValue();
					String com = "New trait " + tvAdded + " added to vocab "+ analysisModel.getVocabName() +"for sub qID " + analysisModel.getQuestionId() + " index " + analysisModel.getGroupIndex().toString();
					arm.appendComment(com, "\n");
				}
				break;
			case CHOOSE:
				String com = "Value " + anRespModel.getOriginalValue()
						+ " changed to " + anRespModel.getChosenValue();
				arm.appendComment(com, "\n");
				break;
			case SUGGEST:
				arm.setOutcome(AnswerReviewOutcome.REJECT);
				String comm = "The vocabulary value "
						+ anRespModel.getChosenValue()
						+ " is suggested, for answer "
						+ anRespModel.getOriginalValue();
				arm.appendComment(comm, "\n");
				break;
			case REJECT:
				arm.setOutcome(AnswerReviewOutcome.REJECT);  
				String commen = "The vocabulary value "
						+ anRespModel.getOriginalValue() + " has been rejected";
				arm.appendComment(commen, "\n");
				break;
			case PARENT:  //Should'nt happen in a question group
				com = "The response " + anRespModel.getOriginalValue()
						+ " is already chosen in parent question";
				arm.appendComment(com, "\n");
				break;
			}
		}
	}
	
	
	
	private Map<String, TraitValue> prepareChosenVocabTraits(VocabAnalysisModel analysisModel){
		Map<String, TraitValue> responseToChooseTraitMap = new HashMap<String, TraitValue>();
		for(AnalysisResponseModel arm : analysisModel.getResponseList()){
			if(AnalysisResponseAction.CHOOSE.equals( arm.getAction() ) && StringUtils.hasLength(arm.getChosenValue()) ){
				TraitValue tv = vocabService.getTraitValue(analysisModel.getVocabName(), arm.getChosenValue());
				responseToChooseTraitMap.put(arm.getOriginalValue(), tv);
			}
		}
		return responseToChooseTraitMap;
	}
	
	private void updateSubmissionResponsesForAddedOrChosenVocabs(VocabAnalysisModel analysisModel , 
			                                                      Map<String, TraitValue> traitsMap) throws Exception{
		
		
		if(StringUtils.hasLength( analysisModel.getParentQuestionId() ) ){
			submissionService.updateParentSubmissionAnswerWithNewVocabValue(analysisModel.getSubmissionId(), analysisModel.getQuestionId(), analysisModel.getParentQuestionId(), traitsMap );
		}else if(StringUtils.hasLength(analysisModel.getGroupQuestionId()) ){
			
		}else{
		    submissionService.updateSubmissionAnswerWithNewVocabValue(analysisModel.getSubmissionId(), analysisModel.getQuestionId(), traitsMap );
		}
	}
	
	private void updateSubmissionResponsesForUseParent(VocabAnalysisModel vaModel){
		List<String> responsesToRemove = new ArrayList<String>();
		for(AnalysisResponseModel arm : vaModel.getResponseList() ){
			if(AnalysisResponseAction.PARENT.equals(arm.getAction()) ){
				responsesToRemove.add(arm.getOriginalValue());
			}
		}
		if(responsesToRemove.size() > 0){
		    submissionService.removeResponsesFromMultiselectAnswer( responsesToRemove, vaModel.getSubmissionId(), vaModel.getQuestionId());
		}
	}
	
	
	private boolean responsesContainAdds(VocabAnalysisModel vaModel){
		if( vaModel.getResponseList() != null && vaModel.getResponseList().size() > 0 ){
			for(AnalysisResponseModel arm :  vaModel.getResponseList() ){
				if(AnalysisResponseAction.ADD.equals( arm.getAction() ) ){
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	
	
	private QuestionModel findContainingQuestion(String questionId , Map<String, QuestionModel> qmMap ){
		return null;
	}
	
	
}
