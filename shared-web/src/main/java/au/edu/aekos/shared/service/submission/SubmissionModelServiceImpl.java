package au.edu.aekos.shared.service.submission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.SharedConstants;
import au.edu.aekos.shared.cache.config.QuestionnaireConfigCache;
import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.file.SiteFileService;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.quest.VocabularyDisplayConfig;
import au.edu.aekos.shared.web.model.GroupModel;
import au.edu.aekos.shared.web.model.MultiselectAnswerModel;
import au.edu.aekos.shared.web.model.QuestionModel;
import au.edu.aekos.shared.web.model.QuestionSetModel;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;
import au.edu.aekos.shared.web.model.SubmissionModel;

/**
 * The main job of this class is to create a submission model object from saved submissions, 
 * used for both submission view and review functions.
 * 
 * @author Ben Till
 */
@Component
public class SubmissionModelServiceImpl implements SubmissionModelService {

	Logger logger = LoggerFactory.getLogger(SubmissionModelServiceImpl.class);
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private SiteFileService siteFileService;
	
	@Autowired
	private QuestionnaireConfigCache questionnaireConfigCache;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private VocabularyDisplayConfig vocabDisplayConfig;
	
	//We can set the embargo date if it exists while processing the questions.
	@Value("${submission.embargo.question.metatag}")
	private String embargoMetatag = "SHD.embargo";
	
	@Override
	public SubmissionModel getSubmission(Long submissionId) throws Exception {
		
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub != null){
			questionnaireConfigCache.asyncInitialiseEntryInCache(submissionId);
			return mapSubmissionToSubmissionModel(sub);
		}
		return null;
	}
	
	public SubmissionModel mapSubmissionToSubmissionModel(Submission sub) throws Exception{
		SubmissionModel sm = new SubmissionModel();
		assignSubmissionDataToModel(sub, sm);
		QuestionnaireConfigEntity configEntity = configService.getQuestionnaireConfigEntity(sub.getQuestionnaireConfig().getId()) ;
		if( configEntity != null){
			assignConfigInfoToSubmissionModel(sm, configEntity);
			QuestionnaireConfig config = configService.parseConfigXML(configEntity);
			assignQandAToSubmissionModel(sub,  config, sm);
			List<SubmissionDataFileModel> dataFiles = mapSubmittedDataInfoToSubmissionModel(sub);
			sm.getFileList().addAll(dataFiles);
		}
		return sm;
	}
	
	private void assignSubmissionDataToModel(Submission sub, SubmissionModel sm){
		sm.setSubmissionId(sub.getId());
		sm.setDraftForSubmissionId(sub.getDraftForSubmissionId());
		sm.setSubmissionTitle(sub.getTitle());
		//sm.setSubmittedByUserId( sub.getSubmitter().getId() ) ;
		sm.setSubmittedByUsername( sub.getSubmitter().getUsername() );
		sm.setSubmissionDate(sub.getSubmissionDate());
		sm.setStatus(sub.getStatus().name());
		sm.setMintedDoi(sub.getDoi());
		sm.setLastReviewDate(sub.getLastReviewDate());
	}
	
	private void assignConfigInfoToSubmissionModel(SubmissionModel sm , QuestionnaireConfigEntity configEntity){
		if(configEntity != null){
		    sm.setQuestionnaireConfigId( configEntity.getId() ) ;
		    sm.setQuestionnaireVersion(configEntity.getVersion());
		    sm.setConfigUploadDate(configEntity.getImportDate());
		    sm.setConfigFileName( configEntity.getConfigFileName() );
		}
	}
	
	private void assignQandAToSubmissionModel(Submission sub, QuestionnaireConfig config, SubmissionModel submissionModel){
		Map<String, SubmissionAnswer> submissionAnswerMap = buildSubmissionAnswerMap(sub);
        String titleQuestionId = config.getSubmissionTitleQuestionId();
        for(Object obj : config.getItems().getEntryList()){
        	if(obj instanceof Question){
        		Question ques = (Question) obj;
        		QuestionModel q = buildQuestionModel(ques, submissionAnswerMap.get(ques.getId()), titleQuestionId, submissionModel.getSubmissionId(), submissionAnswerMap, submissionModel );
        		if(ResponseType.SITE_FILE.equals( q.getResponseType()) ){
        			if(sub.getSubmissionDataList() != null ){
	        			for(SubmissionData sd : sub.getSubmissionDataList() ){
	        				if(SubmissionDataType.SITE_FILE.equals( sd.getSubmissionDataType()) ){
	        					if(StringUtils.hasLength(sd.getSiteFileCoordSysOther()) ){
	        						q.setSiteFileCoordSystem(sd.getSiteFileCoordSysOther());
	        					}else {
	        						q.setSiteFileCoordSystem(siteFileService.getDescriptionForEpsgCode(sd.getSiteFileCoordinateSystem()));
	        					}
	        					q.setSiteFileDataId(sd.getId());
	        					q.setCanRenderSiteFile(siteFileService.isCoordSysSupported(q.getSiteFileCoordSystem()));
	        					break;
	        				}
	        			}
        			}
        		}
        		submissionModel.getItems().add(q);
        	}else if(obj instanceof QuestionGroup){
        		GroupModel gm = buildGroupModel( (QuestionGroup) obj, submissionAnswerMap, titleQuestionId, submissionModel.getSubmissionId(), submissionModel );
        		submissionModel.getItems().add(gm);
        	}else if(obj instanceof MultipleQuestionGroup){
        		QuestionModel q = buildQuestionSetModel((MultipleQuestionGroup) obj, submissionAnswerMap, titleQuestionId , submissionModel.getSubmissionId(), submissionModel );
        		submissionModel.getItems().add(q);
        	}
        }
	}
	
	private void addEmbargoToSubmissionModel(QuestionModel qm, SubmissionModel submissionModel ){
		if(StringUtils.hasLength(qm.getResponseText())){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date embargoDate = sdf.parse(qm.getResponseText());
				submissionModel.setEmbargoDate(embargoDate);
			} catch (ParseException e) {
				logger.error("Error parsing embargo date " + qm.getResponseText(), e);
			}
		}
	}
	
	private QuestionModel buildQuestionModel(Question question, SubmissionAnswer answer , String titleQuestionId, Long submissionId, Map<String, SubmissionAnswer> submissionAnswerMap, SubmissionModel submissionModel){
		QuestionModel qModel = new QuestionModel();
		qModel.setQuestionId(question.getId());
		qModel.setQuestionText(question.getText());
		qModel.setResponseType(question.getResponseType());
		qModel.setQuestionHelpText(question.getDescription());
		qModel.setControlledVocabTrait(question.getTraitName());
		qModel.setMetatag(question.getMetatag());
		qModel.setSubmissionId(submissionId);
		if(answer != null) {
			qModel.setSubmissionAnswerId( answer.getId() );
			if( ! ResponseType.getIsMultiselect(answer.getResponseType()) ){
				if(StringUtils.hasLength(question.getTraitName()) 
						&& ( ResponseType.CONTROLLED_VOCAB.equals( question.getResponseType() ) || ResponseType.CONTROLLED_VOCAB_SUGGEST.equals( question.getResponseType() ) ) ){
					String displayResponse = null;
					if(vocabDisplayConfig.getPopulateDisplayValuesForVocabList().contains(question.getTraitName())
							&& StringUtils.hasLength(answer.getResponse())){
						displayResponse = controlledVocabularyService.getTraitDisplayText(question.getTraitName(), answer.getResponse());
					}
					if( StringUtils.hasLength(displayResponse) ){
						qModel.setResponseText( displayResponse );
					}else if(StringUtils.hasLength(answer.getDisplayResponse())){
						qModel.setResponseText( answer.getDisplayResponse() );
					}else{
					    qModel.setResponseText(answer.getResponse());
					}
				}else{
					 qModel.setResponseText(answer.getResponse());
				}
				qModel.setSuggestionText(answer.getSuggestedResponse());
				if(ResponseType.IMAGE.equals( question.getResponseType()) &&
						answer.getAnswerImage() != null	){
					qModel.setImageObjectId(answer.getAnswerImage().getImageObjectId() );
					qModel.setImageThumbId(answer.getAnswerImage().getImageThumbnailId());
				}
				if(question.getId().equals(titleQuestionId)){
					qModel.setTitleQuestion(true);
				}
			}else{  //Do the multiselect thing
				if(answer.getMultiselectAnswerList() != null && answer.getMultiselectAnswerList().size() > 0 ){
					for(SubmissionAnswer manswer : answer.getMultiselectAnswerList() ){
						MultiselectAnswerModel msam = buildMultiselectAnswerModel(manswer, question.getTraitName());
						qModel.getMultiselectAnswerList().add(msam );
					}
				}
			}
		}
		//Add embargo to SubmissionModel
		if(embargoMetatag.equals(question.getMetatag())){
			addEmbargoToSubmissionModel(qModel, submissionModel);
		}
		
		
		//Check display condition to set visible flag ( AnswerMap will be null for a questionSet question )
		if(submissionAnswerMap != null && question.getDisplayCondition() != null &&  StringUtils.hasLength( question.getDisplayCondition().getQuestionId() )){
			qModel.setConditionalDisplay(question.getDisplayCondition());
			qModel.setVisible(checkIsVisible(question.getDisplayCondition(), submissionAnswerMap.get( question.getDisplayCondition().getQuestionId() ) ) );
		}
		return qModel;
	}
	
	private MultiselectAnswerModel buildMultiselectAnswerModel(SubmissionAnswer multiselectAnswer, String traitName){
		MultiselectAnswerModel answerModel = new MultiselectAnswerModel(multiselectAnswer);
		if(vocabDisplayConfig.getPopulateDisplayValuesForVocabList().contains(traitName)
				&& StringUtils.hasLength(multiselectAnswer.getResponse())){
			String displayResponse = controlledVocabularyService.getTraitDisplayText(traitName, multiselectAnswer.getResponse());
		    if( StringUtils.hasLength(displayResponse) ){
		    	answerModel.setResponseText( displayResponse );
		    }
		}
		return answerModel;
	}
	
	private GroupModel buildGroupModel( QuestionGroup questionGroup, Map<String, SubmissionAnswer> submissionAnswerMap , String titleQuestionId, Long submissionId, SubmissionModel submissionModel){
		GroupModel gModel = new GroupModel();
		if(questionGroup.getDisplayCondition() != null &&  StringUtils.hasLength( questionGroup.getDisplayCondition().getQuestionId() )){
			gModel.setConditionalDisplay(questionGroup.getDisplayCondition());
			gModel.setVisible(checkIsVisible(questionGroup.getDisplayCondition(), submissionAnswerMap.get( questionGroup.getDisplayCondition().getQuestionId() ) ) );
		}
		gModel.setGroupId(questionGroup.getId());
		gModel.setTitle(questionGroup.getGroupTitle());
		gModel.setDescription(questionGroup.getGroupDescription());
		for(Object obj : questionGroup.getElements() ){
			if(obj instanceof Question){
				Question ques = (Question) obj;
        		QuestionModel q = buildQuestionModel(ques, submissionAnswerMap.get(ques.getId()), titleQuestionId , submissionId, submissionAnswerMap, submissionModel);
        		gModel.getItems().add(q);
        	}else if(obj instanceof QuestionGroup){
        		GroupModel gm = buildGroupModel( (QuestionGroup) obj, submissionAnswerMap, titleQuestionId , submissionId, submissionModel );
        		gModel.getItems().add(gm);
        	}else if(obj instanceof MultipleQuestionGroup){
        		QuestionModel q = buildQuestionSetModel((MultipleQuestionGroup) obj, submissionAnswerMap, titleQuestionId , submissionId , submissionModel);
        		gModel.getItems().add(q);
        	}
		}
		return gModel;
	}
	
	private QuestionModel buildQuestionSetModel(MultipleQuestionGroup multipleQuestionGroup, Map<String, SubmissionAnswer>  submissionAnswerMap, String titleQuestionId , Long submissionId, SubmissionModel submissionModel ){
		QuestionModel questionModel = new QuestionModel();
		questionModel.setQuestionId(multipleQuestionGroup.getId());
		questionModel.setQuestionText(multipleQuestionGroup.getText());
		questionModel.setResponseType(ResponseType.MULTIPLE_QUESTION_GROUP );
		questionModel.setQuestionHelpText(multipleQuestionGroup.getDescription());
		questionModel.setSubmissionId(submissionId);

		SubmissionAnswer sa = submissionAnswerMap.get(multipleQuestionGroup.getId());
		if(sa != null){
			questionModel.setSubmissionAnswerId(sa.getId());
			if(sa.getQuestionSetList() != null && sa.getQuestionSetList().size() > 0){
				for(QuestionSetEntity questionSetEntity : sa.getQuestionSetList() ){
					QuestionSetModel qsm = new QuestionSetModel();
					qsm.setQuestionSetEntityId( questionSetEntity.getId() );
					for(Object obj : multipleQuestionGroup.getElements() ){
						if(obj instanceof Question){ //Should always be a question, but anyway . . . . 
							Question q = (Question) obj;
							QuestionModel setQuestionModel = buildQuestionModel(q, questionSetEntity.getAnswerMap().get(q.getId()) , titleQuestionId , submissionId,  null, submissionModel );
							qsm.getQuestionModelMap().put(q.getId(), setQuestionModel);
						}
					}
					questionModel.getQuestionSetModelList().add(qsm);
				}
			}
		}
		return questionModel;
	}
	
	private boolean checkIsVisible(ConditionalDisplay dc, SubmissionAnswer testSubmissionAnswer){
		if(dc != null && testSubmissionAnswer != null && dc.getQuestionId().equalsIgnoreCase(testSubmissionAnswer.getQuestionId() )){
			if(dc.getResponseNull() != null && dc.getResponseNull().booleanValue() && 
					 StringUtils.hasLength( testSubmissionAnswer.getResponse() ) ){
				return false;
			}
			if( dc.getResponseNotNull() != null 
					&& dc.getResponseNotNull().booleanValue() 
					&& ! StringUtils.hasLength( testSubmissionAnswer.getResponse() ) ){
				return false;
			}
			if( StringUtils.hasLength( dc.getResponseValue() ) &&  ! dc.getResponseValue().equals( testSubmissionAnswer.getResponse() ) ){
				return false;
			}
		}
		return true;
	}
	
	private Map<String, SubmissionAnswer> buildSubmissionAnswerMap(Submission sub){
		Map<String, SubmissionAnswer> submissionAnswerMap = new HashMap<String, SubmissionAnswer>();
		for(SubmissionAnswer answer: sub.getAnswers() ){
			submissionAnswerMap.put(answer.getQuestionId(), answer);
		}
		return submissionAnswerMap;
	}
	
	List<SubmissionDataFileModel> mapSubmittedDataInfoToSubmissionModel(Submission sub){
		if(!sub.hasDataFiles()){
			return Collections.emptyList();
		}
		return mapToSubmissionDataFileModels(sub.getSubmissionDataList());
	}

	private List<SubmissionDataFileModel> mapToSubmissionDataFileModels(List<SubmissionData> submissionDataList) {
		List<SubmissionDataFileModel> result = new ArrayList<SubmissionDataFileModel>();
		for(SubmissionData submissionData : submissionDataList) {
	    	SubmissionDataFileModel dataFile = new SubmissionDataFileModel(submissionData);
			result.add(dataFile);
			dataFile.setFormatTitle(controlledVocabularyService.getTraitDisplayText(SharedConstants.FILE_FORMAT_SHARED_VOCAB_NAME, dataFile.getFormat()));
	    }
	    return result;
	}
	
	@Override
	public List<SubmissionDataFileModel> mapSubmittedDataInfoToSubmissionModel(DisplayQuestionnaire dq) {
		if (!dq.hasSubmissionData()) {
			return Collections.emptyList();
		}
		return mapToSubmissionDataFileModels(dq.getSubmissionDataList());
	}

	public void setControlledVocabularyService(ControlledVocabularyService controlledVocabularyService) {
		this.controlledVocabularyService = controlledVocabularyService;
	}
}
