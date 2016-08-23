package au.edu.aekos.shared.service.submission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.GroupSubmissionTransferDao;
import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionAnswerDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.dao.SubmissionHistoryDao;
import au.edu.aekos.shared.data.dao.SubmissionReviewDao;
import au.edu.aekos.shared.data.entity.AnswerImage;
import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionReview;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.ImageAnswer;
import au.edu.aekos.shared.questionnaire.ImageSeriesAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.file.FileSystemDataIntegrityException;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.quest.ReusableGroupService;
import au.edu.aekos.shared.service.quest.TraitValue;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.upload.SharedImageFileSupport;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;

@Service
public class SubmissionServiceImpl implements SubmissionService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubmissionServiceImpl.class);

	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Autowired
	private SubmissionAnswerDao submissionAnswerDao;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private SubmissionDataService submissionDataService;
	
	@Autowired
	private QuestionnaireConfigEntityDao configEntityDao;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private SubmissionHistoryDao submissionHistoryDao;
	
	@Autowired
	private SubmissionReviewDao submissionReviewDao;
	
	@Autowired
	private SecurityService authService;
	
	@Autowired
	private ReusableGroupService reusableGroupService;
	
	@Autowired
	private SubmissionAuthorisationService submissionAuthorisationService;
	
	@Autowired
	private SubmissionReviewService submissionReviewService;
	
	@Autowired
	private GroupSubmissionTransferDao transferDao;
	
	@Value("${shared.upload.tempimage.path}")
	private String tempImagePath;
	
	@Transactional
	public Long createNewSubmission(String userName, DisplayQuestionnaire quest, SubmissionStatus submissionStatus)
			throws Exception {
		
		Submission submission = new Submission();
		QuestionnaireConfigEntity configEntity = configEntityDao.findById(quest.getQuestionnaireConfigEntityId()) ;
		submission.setQuestionnaireConfig(configEntity);
		mapAnswersToNewSubmissionEntity(quest.getAllAnswers(), quest.getImageAnswerMap(), quest.getImageSeriesAnswerMap(), submission);
		
		submission.setSubmissionDate(new Date());
		submission.setStatus(submissionStatus);
		setSubmissionTitle(submission, quest.getAllAnswers(), quest.getConfig().getSubmissionTitleQuestionId(), userName);
		setNewSubmissionUser(userName, quest, submission);
		mapUploadedDataToNewSubmission(quest, submission);
		submissionDao.save(submission);
		submissionDataService.writeSubmissionDataToObjectStore(submission);
		reusableGroupService.createReusableGroups(userName, quest, submission);
		if(quest.getSavedSubmissionId() != null){
			hardDeleteSavedSubmission(quest.getSavedSubmissionId());
		}
		return submission.getId();
	}
	
	private void setNewSubmissionUser(String userName, DisplayQuestionnaire quest, Submission submission){
		if(Boolean.TRUE.equals(quest.getGroupAdminEdit())){
			Long subId = quest.getSavedSubmissionId();
			Submission savedSubmission = submissionDao.findById(subId);
			submission.setSubmitter(savedSubmission.getSubmitter());
		}else{
			SharedUser user = authService.getCurrentUser();
			if(user == null){
				 user = authService.getCreateDefaultUser(userName);
			}
			if(user != null ){
			    submission.setSubmitter(user);
			}
		}
	}
	
	@Transactional
	public void updateSubmission(String userName,
			DisplayQuestionnaire quest, SubmissionStatus submissionStatus)
			throws Exception {
		if(quest.getSubmissionId() == null ){
			logger.error("Cannot update partially saved submission without providing ID");
			throw new Exception("Cannot update partially saved submission without providing ID");
		}
		Submission savedSubmission = retrieveSubmissionById( quest.getSubmissionId() ) ;
		if(savedSubmission == null){
			logger.error("No submission exists with ID:" + quest.getSubmissionId());
			throw new Exception("No submission exists with ID:" + quest.getSubmissionId());
		}
		
		
		//Create a history record
		
		//Use case : if the history status is either INCOMPLETE OR Draft , if Yes , no need to update the status again for them
		//Simplified : if the savedSubmissionStatus is same as new SubmissionStatus , then no need to update.
		SubmissionStatus savedStatus = savedSubmission.getStatus();
		if( SubmissionStatus.RESUBMITTED.equals( submissionStatus ) ){
			SubmissionReview lastReview = submissionReviewDao.getLastReviewForSubmissionId(quest.getSubmissionId());
			submissionHistoryDao.createSubmissionHistory(savedSubmission, lastReview);
		}else if (!savedStatus.equals(submissionStatus)){
		    submissionHistoryDao.createSubmissionHistory(savedSubmission);
		}
		savedSubmission.setStatus(submissionStatus);
		mapAnswersToExistingSubmissionEntity(quest.getAllAnswers(), quest.getImageAnswerMap(),  savedSubmission);
		//update any data changes
		List<SubmissionData> deletedSubmissionDataList = updateDataChangesOnExistingSubmission( quest.getSubmissionDataList(), quest.getQuestionUploadSubmissionDataMap().values(), savedSubmission );
		//If this is a group admin edit - Don't change the username
		if( quest.getGroupAdminEdit() == null || ! quest.getGroupAdminEdit() ){
		    savedSubmission.setSubmitter(sharedUserDao.findUserByUsername(userName));
		}
		
		updateSubmissionMetadata( savedSubmission, quest.getSubmissionTitle() );
		submissionDao.update(savedSubmission);
		submissionDao.flush();
		submissionDataService.deleteSubmissionData(deletedSubmissionDataList);
		deleteSavedVersionsOfSubmission(savedSubmission.getId());
		submissionDataService.writeSubmissionDataToObjectStore(savedSubmission);
	}
	
	private void setSubmissionTitle(Submission submission, Map<String, Answer> answers, String titleQuestionId, String userName){
		if(StringUtils.hasLength(titleQuestionId) && answers.containsKey(titleQuestionId)){
			submission.setTitle( answers.get(titleQuestionId).getResponse() );
		}else{
			submission.setTitle(getDefaultSubmissionTitle(userName));
		}
	}
	
	private void mapAnswersToNewSubmissionEntity(Map<String, Answer> questionnaireAnswerMap, Map<String,ImageAnswer> imageAnswerMap, Map<String, ImageSeriesAnswer> imageSeriesAnswerMap, Submission submission){
		if(questionnaireAnswerMap == null || questionnaireAnswerMap.size() == 0){
			return;
		}
		for(String questionId : questionnaireAnswerMap.keySet()){
			Answer answer = questionnaireAnswerMap.get(questionId);
			if( ! answer.hasResponse()){
				continue;
			}
			SubmissionAnswer subAnswer = createNewSubmissionAnswer(answer, imageAnswerMap.get(questionId), imageSeriesAnswerMap.get(questionId) );
			submission.getAnswers().add(subAnswer);
		}
	}
	
	private AnswerImage createNewAnswerImageFromImageAnswer(ImageAnswer imageAnswer){
		AnswerImage ai = new AnswerImage();
		ai.setDate( imageAnswer.getImageDate() );
		ai.setDescription(imageAnswer.getImageDescription());
		ai.setImageName(imageAnswer.getImageFileName());
		ai.setImageType(SharedImageFileSupport.getFileSuffix(imageAnswer.getImageFileName()));
		try{
			ai.setImageThumbnailId(imageAnswer.getImageThumbnailName());
		    ai.setImageThumbnail(SharedImageFileSupport.readImageFileFromDiskIntoByteArray(imageAnswer.getImageThumbnailName(), tempImagePath) );
		    ai.setImageObjectId(imageAnswer.getImageObjectName());
		    ai.setImage(SharedImageFileSupport.readImageFileFromDiskIntoByteArray(imageAnswer.getImageObjectName(), tempImagePath));
		}catch(IOException ex){
			logger.error("Error occured whilst trying to process " + imageAnswer.getImageFileName() );
			logger.error(ex.getMessage(), ex);
			return null;
		}
		return ai;
	}
	
	void mapAnswerToNewSubmissionAnswer(Answer answer, SubmissionAnswer submissionAnswer){
		submissionAnswer.setQuestionId( answer.getQuestionId() );
		submissionAnswer.setResponse(answer.getResponse());
		submissionAnswer.setDisplayResponse(answer.getDisplayResponse());
		submissionAnswer.setResponseType(answer.getResponseType());
		submissionAnswer.setSuggestedResponse(answer.getSuggestedResponse());
	}
	
	private void mapUploadedDataToNewSubmission(DisplayQuestionnaire quest, Submission submission) throws FileSystemDataIntegrityException{
		if(quest.getSubmissionDataList() != null && quest.getSubmissionDataList().size() > 0){
		    for(SubmissionData sd : quest.getSubmissionDataList()){
		    	if(sd.getSubmission() != null && sd.getSubmission().getId() != null && sd.getSubmission().getId().equals(quest.getSavedSubmissionId()) ){
		    		submission.getSubmissionDataList().add(cloneSavedSubmissionDataAndSetNewSubmission(sd, submission));
		    	}else{
		    	    submission.getSubmissionDataList().add(reattachSubmissionDataAndSetNewSubmission(sd, submission));
		    	}
		    }
		}
		if( quest.getQuestionUploadSubmissionDataMap().size() > 0 ){
			//If the submission data's have an ID they need to be reattached to the session, 
			//won't hurt to reset the submission either.
			for(SubmissionData sd : quest.getQuestionUploadSubmissionDataMap().values()){
                if(sd.getSubmission() != null && sd.getSubmission().getId() != null && sd.getSubmission().getId().equals(quest.getSavedSubmissionId()) ){
                	submission.getSubmissionDataList().add(cloneSavedSubmissionDataAndSetNewSubmission(sd, submission));
		    	}else{
				    submission.getSubmissionDataList().add(reattachSubmissionDataAndSetNewSubmission(sd, submission));
		    	}
			}
		}
	}
	
	private SubmissionData reattachSubmissionDataAndSetNewSubmission(SubmissionData sd, Submission submission){
		if(sd.getId() != null ){
    		SubmissionData seshSubData = submissionDataDao.findById(sd.getId());
    		if(seshSubData != null ){
    		    seshSubData.setSubmission(submission);
    		    return seshSubData;
    		}else{
    			sd.setId(null);
    			sd.setSubmission(submission);
    			return sd;
    		}
    	}else{
    		sd.setSubmission(submission);
    		return sd;
    	}
	}
	
	private SubmissionData cloneSavedSubmissionDataAndSetNewSubmission(SubmissionData sd, Submission submission) throws FileSystemDataIntegrityException{
		if(sd.getId() != null ){
			SubmissionData clonedSD = submissionDataService.cloneToNonPersistedFileSystemData(sd);
			clonedSD.setSubmission(submission);
			return clonedSD;
    	}
		sd.setSubmission(submission);
		return sd;
	}
	
	private void mapAnswersToExistingSubmissionEntity(Map<String, Answer> questionnaireAnswerMap, Map<String,ImageAnswer> imageAnswerMap, Submission existingSubmission){
		if(questionnaireAnswerMap == null || questionnaireAnswerMap.size() == 0){
			return;
		}
		Map<String, SubmissionAnswer> questionIdToSubmissionAnswerMap = buildQuestionIdToSubmissionAnswerMap(existingSubmission);
		Set<Long> submissionAnswerIdsToRemove = new HashSet<Long>();
		List<SubmissionAnswer> answersToAdd = new ArrayList<SubmissionAnswer>();
		for(String questionId : questionnaireAnswerMap.keySet()){
			Answer answer = questionnaireAnswerMap.get(questionId);
			SubmissionAnswer subAnswer = null;
			if(  questionIdToSubmissionAnswerMap.containsKey(questionId ) ){
				subAnswer = questionIdToSubmissionAnswerMap.get(questionId);
			}
			if(! answer.hasResponse() ){
				if(subAnswer != null ){
					submissionAnswerIdsToRemove.add(subAnswer.getId());
			    }
				continue;
			}
			if(subAnswer != null && ! hasAnswerChanged( answer, subAnswer ) ){
				continue;
			}
			ImageAnswer imageAnswer = imageAnswerMap.get(questionId);
			SubmissionAnswer newAnswer = createNewSubmissionAnswer(answer, imageAnswer, null);
			if(subAnswer != null){
			    submissionAnswerIdsToRemove.add(subAnswer.getId());
			}
			//existingSubmission.getAnswers().remove(subAnswer);
			//existingSubmission.getAnswers().add(newAnswer);
			answersToAdd.add(newAnswer);
		}
		if(submissionAnswerIdsToRemove.size() > 0){  //Possible N+1 issue?
			Iterator<SubmissionAnswer> iter = existingSubmission.getAnswers().iterator();
			while(iter.hasNext()){
				SubmissionAnswer toBeRemoved = iter.next();
				if(submissionAnswerIdsToRemove.contains(toBeRemoved.getId()) ){
					iter.remove();
				}
			}
		}
		if(answersToAdd.size() > 0){
			existingSubmission.getAnswers().addAll(answersToAdd);
		}
	}
	
	private SubmissionAnswer createNewSubmissionAnswer(Answer answer, ImageAnswer imageAnswer, ImageSeriesAnswer imageSeriesAnswer){
		if(answer.getIsMultiSelect()){
			return createNewMultiselectSubmissionAnswer(answer, imageSeriesAnswer);
		}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(answer.getResponseType()) ){
			return createNewQuestionSetAnswer(answer);
		}
		SubmissionAnswer subAnswer = new SubmissionAnswer();
		mapAnswerToNewSubmissionAnswer(answer, subAnswer);
		if(subAnswer.getResponseType().equals(ResponseType.IMAGE) && StringUtils.hasLength( answer.getResponse()) ){
			if(imageAnswer != null){
				subAnswer.setAnswerImage(createNewAnswerImageFromImageAnswer(imageAnswer));
			}
		}
		return subAnswer;
	}
	
	private SubmissionAnswer createNewMultiselectSubmissionAnswer(Answer answer, ImageSeriesAnswer imageSeriesAnswer){
		SubmissionAnswer subAnswer = new SubmissionAnswer();
		mapAnswerToNewSubmissionAnswer(answer, subAnswer);
		//ResponseType rawRT = ResponseType.getRawType(answer.getResponseType());
		int imageSeriesAnswerIndex = 0;
		for( Answer manswer:  answer.getMultiselectAnswerList()   ){
			SubmissionAnswer multiAnswer = new SubmissionAnswer();
			mapAnswerToNewSubmissionAnswer(manswer, multiAnswer);
            if(ResponseType.MULTISELECT_IMAGE.equals(answer.getResponseType() ) ){
            	if(imageSeriesAnswer != null && imageSeriesAnswer.getImageAnswerList().size() >  imageSeriesAnswerIndex 
            			&& imageSeriesAnswer.getImageAnswerList().get(imageSeriesAnswerIndex) != null){
            	    multiAnswer.setAnswerImage(createNewAnswerImageFromImageAnswer(imageSeriesAnswer.getImageAnswerList().get(imageSeriesAnswerIndex)) );
            	}
            }
			subAnswer.getMultiselectAnswerList().add(multiAnswer);
			imageSeriesAnswerIndex++;
		}
		return subAnswer;
	}
	
	private SubmissionAnswer createNewQuestionSetAnswer(Answer answer){
		SubmissionAnswer subAnswer = new SubmissionAnswer();
		mapAnswerToNewSubmissionAnswer(answer, subAnswer);
		for(Map<String,Answer> answerSet : answer.getAnswerSetList()){
			if(answerSetHasResponse(answerSet)){
				QuestionSetEntity questionSetEntity = mapAnswerSetToQuestionSetEntity(answerSet);
				subAnswer.getQuestionSetList().add(questionSetEntity);
			}
		}
		return subAnswer;
	}
	
	private QuestionSetEntity mapAnswerSetToQuestionSetEntity(Map<String,Answer> answerSet){
		QuestionSetEntity questionSetEntity = new QuestionSetEntity();
		for(Map.Entry<String, Answer> entry : answerSet.entrySet()) {
			SubmissionAnswer sa = new SubmissionAnswer();
			Answer ans = entry.getValue();
			sa.setResponseType(ans.getResponseType());
			sa.setResponse(ans.getResponse());
			sa.setDisplayResponse(ans.getDisplayResponse());
			sa.setSuggestedResponse(ans.getSuggestedResponse());
			sa.setQuestionId(entry.getKey());
			questionSetEntity.getAnswerMap().put(entry.getKey(), sa);
		}
		
		return questionSetEntity;
	}
	
	private boolean answerSetHasResponse(Map<String,Answer> answerSet){
		for(Answer ans : answerSet.values()){
			if(ans.hasResponse()){
				return true;
			}
		}
		return false;
	}
	
	//build a map of question id to index - for the existing submission
	private Map<String, SubmissionAnswer> buildQuestionIdToSubmissionAnswerMap(Submission submission){
		Map<String, SubmissionAnswer> questionIdToExistingAnswerIndexMap = new HashMap<String, SubmissionAnswer>();
		if( submission.getAnswers().size() > 0  ){
			for(SubmissionAnswer sa : submission.getAnswers()){
				questionIdToExistingAnswerIndexMap.put(sa.getQuestionId(), sa);
			}
		}
		return questionIdToExistingAnswerIndexMap;
	}
	
	private boolean hasAnswerChanged(Answer submittedAnswer, SubmissionAnswer answerEntity){
		if( ! answerEntity.hasResponse() && ! submittedAnswer.hasResponse() ){
			return false;
		}
		if( ! answerEntity.hasResponse() || ! submittedAnswer.hasResponse() ){
			return true;
		}
		if(submittedAnswer.getIsMultiSelect() ){
			return hasChangedMultiselectAnswerLists( submittedAnswer, answerEntity  );
		}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(submittedAnswer.getResponseType() ) ){
			return hasQuestionSetAnswerChanged(submittedAnswer, answerEntity );
		}
		else if( ! submittedAnswer.getResponse().equals( answerEntity.getResponse() ) ){
			return true;
		}
		return false;
	}
	
	public boolean hasChangedMultiselectAnswerLists( Answer submittedAnswer, SubmissionAnswer answerEntity ){
		if(submittedAnswer.getMultiselectAnswerList().size() != answerEntity.getMultiselectAnswerList().size()){
			return true;
		}
		for(int x =0; x < submittedAnswer.getMultiselectAnswerList().size(); x++){
			if( submittedAnswer.getMultiselectAnswerList().get(x).hasResponse() &&    
					! submittedAnswer.getMultiselectAnswerList().get(x).getResponse().equals(answerEntity.getMultiselectAnswerList().get(x).getResponse()) ){
				return true;
			}else if( answerEntity.getMultiselectAnswerList().get(x).hasResponse() &&    
					! answerEntity.getMultiselectAnswerList().get(x).getResponse().equals( submittedAnswer.getMultiselectAnswerList().get(x).getResponse()) ){
				return true;
			}
		}
		return false;
	}

	public boolean hasQuestionSetAnswerChanged(Answer submittedAnswer, SubmissionAnswer answerEntity ){
		if( submittedAnswer.getAnswerSetList().size() != answerEntity.getQuestionSetList().size() ){
			return true;
		}
		if(submittedAnswer.getAnswerSetList().size() == 0){
			return false;
		}
		for(int x = 0; x < submittedAnswer.getAnswerSetList().size(); x++){
			Map<String,Answer> answerSet = submittedAnswer.getAnswerSetList().get(x);
			QuestionSetEntity qse = answerEntity.getQuestionSetList().get(x);
			Map<String, SubmissionAnswer> submissionAnswerSet = qse.getAnswerMap();
			if(answerSet.size() != submissionAnswerSet.size()){
				return true; //This should never happen
			}
			if(answerSet.size() == 0){
				return false; //This should never happen too.
			}
			for(Map.Entry<String,Answer> entry :  answerSet.entrySet() ){
				Answer a = entry.getValue();
				SubmissionAnswer sa = submissionAnswerSet.get(entry.getKey());
				if(sa == null){
					return true;
				}
				if(!StringUtils.hasLength( a.getResponse() ) && ! StringUtils.hasLength( sa.getResponse() ) ){
					continue;
				}
				if( (StringUtils.hasLength( a.getResponse() ) && ! StringUtils.hasLength( sa.getResponse() ) )   
						|| (! StringUtils.hasLength( a.getResponse() ) && StringUtils.hasLength( sa.getResponse() )) ){
					return true;
				}
				if( ! a.getResponse().equals( sa.getResponse()) ){
					return true;
				}
			}
			//Now check all the keys in the persisted SubmissionAnswer are in the new Answer
			for(String key : submissionAnswerSet.keySet() ){
				if( ! answerSet.containsKey(key)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	@Transactional
	public Submission retrieveSubmissionById(Long submissionId) {
		Submission submission = submissionDao.findSubmissionByIdEagerAnswer(submissionId);
		if (submission == null) {
			return null;
		}
		submission.getQuestionnaireConfig();
		//Attempt to hydrate the submission data
		submission.getSubmissionDataList().size();
		//Populate any multiselect and questionSet answers too.
		for(SubmissionAnswer submissionAnswer : submission.getAnswers() ){
			if(ResponseType.getIsMultiselect( submissionAnswer.getResponseType() )){
				submissionAnswer.getMultiselectAnswerList().size();
			}else if(ResponseType.MULTIPLE_QUESTION_GROUP.equals(submissionAnswer.getResponseType()) 
					&& submissionAnswer.getQuestionSetList().size() > 0){
				for(QuestionSetEntity qse : submissionAnswer.getQuestionSetList() ){
					qse.getAnswerMap().size();
				}
			}
		}
		return submission;
	}

	@Override
	@Transactional
	public String getDefaultSubmissionTitle(String userName) {
		List<Submission> subList = submissionDao.findSubmissionsByUsername(userName);
		int number = 1;
		if(subList != null && subList.size() > 0){
			number += subList.size(); 
		}
		return "Submission " + Integer.toString(number);
	}
	
	private void updateSubmissionMetadata( Submission savedSubmission, String submissionTitle ){
		if(StringUtils.hasLength(submissionTitle) ){
			savedSubmission.setTitle(submissionTitle);
		}
		savedSubmission.setSubmissionDate(new Date());
	}
  
	//Need to compare each submitted entry with what has been saved.
	//Add any new ones,  remove what has changed ( or has not been submitted again )
	private List<SubmissionData> updateDataChangesOnExistingSubmission(List<SubmissionData> submittedDataList, Collection<SubmissionData> singleDataCollection, Submission savedSubmission){
		List<SubmissionData> subDataListWithSF = new ArrayList<SubmissionData>();
		subDataListWithSF.addAll(submittedDataList);
		subDataListWithSF.addAll(singleDataCollection);
		
		//There may be some N+1 business going on in production re - sheila's submission.
		
		List<SubmissionData> savedSubmissionDataList = savedSubmission.getSubmissionDataList();
		for(SubmissionData submittedData : subDataListWithSF ){
		    if(submittedData != null && ! listContainsSubmittedData(submittedData ,  savedSubmissionDataList  ) ){
		    	savedSubmissionDataList.add(submittedData);
		    }
		}
		
		List<SubmissionData> deletedSubmissionData = new ArrayList<SubmissionData>();
		for (Iterator<SubmissionData> iterator = savedSubmissionDataList.iterator(); iterator.hasNext();) {
			SubmissionData submissionData = iterator.next();
			if(! listContainsSubmittedData(submissionData ,  subDataListWithSF  ) ){
				deletedSubmissionData.add(submissionData);
				iterator.remove();
			}
		}
		return deletedSubmissionData;
	}
	
	private boolean listContainsSubmittedData(SubmissionData submittedData ,  List<SubmissionData> submissionDataList  ){
		if( submissionDataList.size() == 0   ){
			return false;
		}
		for( SubmissionData submissionData : submissionDataList ){
			if( submittedData.equalsUpdateSubmission(submissionData)  ){
				return true;
			}
		}
		return false;
	}
	
	@Transactional
	public DisplayQuestionnaire populateDisplayQuestionnaireFromSubmissionEntity(
			Long submissionId) throws Exception{
		Submission sub = retrieveSubmissionById(submissionId);
		QuestionnaireConfigEntity configEntity = sub.getQuestionnaireConfig();
		QuestionnaireConfig questionnaireConfig = questionnaireConfigService.parseConfigXML(configEntity);
		DisplayQuestionnaire displayQuestionnaire = new DisplayQuestionnaire(questionnaireConfig, sub);
		//Need to populate any images
		populateDisplayQuestionnaireImagesFromExistingSubmission(displayQuestionnaire, sub);
		//And details of data files
		populateDisplayQuestionnaireDataFromExistingSubmission(displayQuestionnaire, sub);
		//Need to check if the submission has been reviewed before, if so, create the reviewModel
		SubmissionReviewModel reviewModel = submissionReviewService.getLastReviewForSubmission(displayQuestionnaire.getSubmissionId());
		displayQuestionnaire.setLastReview(reviewModel);
		return displayQuestionnaire;
	}
	
	@Override @Transactional
	public DisplayQuestionnaire populateDisplayQuestionnaireForCloneFromSubmissionEntity(
			Long submissionId) throws Exception {
		Submission sub = retrieveSubmissionById(submissionId);
		QuestionnaireConfigEntity configEntity = sub.getQuestionnaireConfig();
		QuestionnaireConfig questionnaireConfig = questionnaireConfigService.parseConfigXML(configEntity);
		DisplayQuestionnaire displayQuestionnaire = new DisplayQuestionnaire(questionnaireConfig, configEntity.getId(), sub);
		return displayQuestionnaire;
	}
	
	
	
	private void populateDisplayQuestionnaireImagesFromExistingSubmission(DisplayQuestionnaire displayQuestionnaire, Submission sub){
		for(SubmissionAnswer subAnswer : sub.getAnswers() ){
			if(ResponseType.IMAGE.equals( subAnswer.getResponseType()) && subAnswer.getAnswerImage() != null ){
				AnswerImage image = subAnswer.getAnswerImage();
				ImageAnswer imageAnswer = new ImageAnswer( subAnswer.getQuestionId(),
						                                   image.getDescription(),
						                                   image.getImageName(),
						                                   image.getImageObjectId(),
						                                   image.getImageThumbnailId() );
				imageAnswer.setAnswerId(image.getId());
				displayQuestionnaire.getImageAnswerMap().put(subAnswer.getQuestionId(), imageAnswer);
			}
		}
	}
	
    private void populateDisplayQuestionnaireDataFromExistingSubmission(DisplayQuestionnaire displayQuestionnaire, Submission sub){
		if( sub.getSubmissionDataList() != null && sub.getSubmissionDataList().size() > 0 ){
			displayQuestionnaire.getSubmissionDataList().clear();
			for( SubmissionData sd : sub.getSubmissionDataList() ){
			    sd.getStorageLocations().size();
			    if(SubmissionDataType.DATA.equals( sd.getSubmissionDataType() ) || SubmissionDataType.RELATED_DOC.equals( sd.getSubmissionDataType() ) ){
			        displayQuestionnaire.getSubmissionDataList().add(sd); 
			    }else{
			    	displayQuestionnaire.getQuestionUploadSubmissionDataMap().put(sd.getQuestionId() , sd);
			    }
			}
		}
	}
    
    @Transactional
	@Override
	public Submission retrieveSubmissionByTitleAndUsername(
			String submissionTitle, String username) {
		return submissionDao.retrieveSubmissionByTitleAndUsername(submissionTitle, username);
	}

	@Override
	@Transactional
	public void updateSubmissionStatus(Long submissionId, SubmissionStatus submissionStatus) {
		submissionDao.updateSubmissionStatus(submissionId, submissionStatus);
	}

	//TODO History record PRE-REVIEW - or wrap up all changes in the
	@Override @Transactional
	public void updateSubmissionAnswerWithNewVocabValue(Long submissionId,
			String questionId, Map<String, TraitValue> addedTraitsMap) {
		SubmissionAnswer sa = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, questionId);
		if(sa == null){
			//I don't think this ever gets called 
			Submission sub = submissionDao.findById(submissionId);
			Map<String, List<SubmissionAnswer>> qsSubAnswerMap = sub.getQuestionIdToQuestionSetAnswerMap();
			if(! qsSubAnswerMap.containsKey(questionId)){
				return;
			}
			for(SubmissionAnswer qsSa : qsSubAnswerMap.get(questionId)){
				processUpdateTraitsOnSubmissionAnswer(qsSa, addedTraitsMap);
			}
			submissionDao.saveOrUpdate(sub);
		}else{
			processUpdateTraitsOnSubmissionAnswer(sa, addedTraitsMap);
			submissionAnswerDao.saveOrUpdate(sa);
		}
	}
	
	@Override @Transactional //No fancy question set work on this one ( unless required) 
	public void updateParentSubmissionAnswerWithNewVocabValue(
			Long submissionId, String questionId, String parentQuestionId,
			Map<String, TraitValue> addedTraitsMap) throws Exception {
		if(addedTraitsMap == null || addedTraitsMap.size() == 0){
			return;
		}
		SubmissionAnswer sa = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, questionId);
		SubmissionAnswer parentSubmissionAnswer = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, parentQuestionId);
		if(parentSubmissionAnswer == null){
			parentSubmissionAnswer = createParentSubmissionAnswerForSuggest(submissionId, parentQuestionId );
			
		}
		
		if(ResponseType.TEXT.equals(sa.getResponseType())){
			//Hav'nt come across this case yet . . . 
			
		}else if(ResponseType.MULTISELECT_TEXT.equals(sa.getResponseType())){
			//We need an iterator on the child SA's multiselect answers so they can be removed, at the same time an answer
			//being added to the parent question
			if(sa.hasResponse()){
				for(Map.Entry<String, TraitValue> entry : addedTraitsMap.entrySet()){
					Iterator<SubmissionAnswer> iter = sa.getMultiselectAnswerList().iterator();
					while(iter.hasNext()){
						SubmissionAnswer msa = iter.next();
						if(entry.getKey().equals(msa.getResponse()) ){
							//Need to add the answer to the parentQuestion multiselect answer list, 
							//then remove it via the iterator.
							SubmissionAnswer newParentSA = new SubmissionAnswer();
							newParentSA.setResponse(entry.getValue().getTraitValue());
							newParentSA.setDisplayResponse(entry.getValue().getDisplayString());
							newParentSA.setSuggestedResponse(null);
							newParentSA.setResponseType(msa.getResponseType());
							parentSubmissionAnswer.getMultiselectAnswerList().add(newParentSA);
							iter.remove();
						}
					}
				}
				submissionAnswerDao.saveOrUpdate(sa);
				submissionAnswerDao.saveOrUpdate(parentSubmissionAnswer);
			}
		}
	}
	
	private SubmissionAnswer createParentSubmissionAnswerForSuggest(Long submissionId, String parentQuestionId ) throws Exception{
		SubmissionAnswer existingSA = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, parentQuestionId);
		if(existingSA != null){
			return existingSA;
		}
		
		Submission sub = retrieveSubmissionById(submissionId);
		QuestionnaireConfig qc = questionnaireConfigService.getQuestionnaireConfig(sub);
		Question q = qc.getQuestionById(parentQuestionId);
		SubmissionAnswer newSubmissionAnswer = new SubmissionAnswer();
		newSubmissionAnswer.setMetaTag(q.getMetatag());
		newSubmissionAnswer.setResponseType(q.getResponseType());
		newSubmissionAnswer.setQuestionId(parentQuestionId);
		newSubmissionAnswer.setSubmission(sub);
		sub.getAnswers().add(newSubmissionAnswer);
	    submissionDao.saveOrUpdate(sub);
	    return newSubmissionAnswer;
	}
	
	@Override @Transactional
	public void updateQuestionSetSubmissionAnswerWithNewVocabValue(Long submissionId, String groupQuestionId, Integer setIndex, String questionId, Map<String, TraitValue> addedTraitsMap)  {
		SubmissionAnswer sa = submissionAnswerDao.retrieveQuestionSetSubmissionAnswer(submissionId, groupQuestionId, setIndex, questionId);
		if(sa != null){
		    processUpdateTraitsOnSubmissionAnswer(sa, addedTraitsMap);
		    submissionAnswerDao.saveOrUpdate(sa);
		}
	}
	
	@Transactional
	public void removeResponsesFromMultiselectAnswer(List<String> responsesToRemove, Long submissionId, String questionId){
		SubmissionAnswer sa = submissionAnswerDao.retrieveSubmissionAnswerBySubmissionIdAndQuestionId(submissionId, questionId);
		if(sa == null || ! ResponseType.getIsMultiselect(sa.getResponseType())){
			logger.error("Can't remove answers from SubmissionAnswer " + questionId + " subId:" + submissionId.toString() +". Answer doesnt exist or is'nt multiselect.  Non fatal.");
			return;
		}
		Iterator<SubmissionAnswer> iter = sa.getMultiselectAnswerList().iterator();
		while(iter.hasNext()){
			SubmissionAnswer msA = iter.next();
			if(responsesToRemove.contains(msA.getResponse())){
				iter.remove();
			}
		}
		submissionAnswerDao.saveOrUpdate(sa);
	}
	
	private void processUpdateTraitsOnSubmissionAnswer(SubmissionAnswer sa, Map<String, TraitValue> addedTraitsMap){
		if(ResponseType.getIsMultiselect(sa.getResponseType())){
			for(SubmissionAnswer msa : sa.getMultiselectAnswerList()){
				updateMatchingSubmissionAnswerResponse(msa, addedTraitsMap);
			}
		}else{
			updateMatchingSubmissionAnswerResponse(sa, addedTraitsMap);
		}
	}
	
	private void updateMatchingSubmissionAnswerResponse(SubmissionAnswer sa, Map<String, TraitValue> addedTraitsMap){
		for(Map.Entry<String, TraitValue> entry : addedTraitsMap.entrySet()){
			if(entry.getKey().equals(sa.getResponse()) || entry.getKey().equals(sa.getSuggestedResponse())){
				sa.setResponse(entry.getValue().getTraitValue());
				sa.setDisplayResponse(entry.getValue().getDisplayString());
				sa.setSuggestedResponse(null);
				return;
			}
		}
	}

	@Override @Transactional
	public void deleteSubmissionIfCurrentUserAuthorised(Long submissionId) {
		String loggedInUsername = authService.getLoggedInUsername();
		if(submissionAuthorisationService.userCanWrite(loggedInUsername, submissionId) ){
			logger.info("Submission id " + submissionId.toString() + " status changed to 'DELETED' by user " + loggedInUsername);
			updateSubmissionStatus(submissionId,SubmissionStatus.DELETED);
		}
	}

	@Override @Transactional
	public Long createSavedSubmission(String userName, DisplayQuestionnaire quest) throws FileSystemDataIntegrityException {
		Long realSubmissionId = quest.getSubmissionId();
		SubmissionStatus status = SubmissionStatus.SAVED;
		if(realSubmissionId != null){
			Submission realSubmission = submissionDao.findById(realSubmissionId);
			if(realSubmission != null && SubmissionStatus.REJECTED.equals(realSubmission.getStatus())){
				status = SubmissionStatus.REJECTED_SAVED;
			}
		}
		Long savedSubmissionId = quest.getSavedSubmissionId();
		if(savedSubmissionId == null && realSubmissionId != null){
			//We may not have launched the submission from a saved copy,
			//in this case, need to search for a draftForSubId of the realSubmissionID
			List<Long> savedSubIdList = submissionDao.findExistingSavedSubmissionIdForSubmission(realSubmissionId);
			if(savedSubIdList != null && savedSubIdList.size() > 0){
				savedSubmissionId = savedSubIdList.get(0);
			}
		}
		
		Long newSavedSubmissionId = createNewSavedSubmission(userName, quest, status);
		
		if(savedSubmissionId != null ){
			transferDao.migrateTansferRecordsToSubmissionId(savedSubmissionId, newSavedSubmissionId);
			//Completely deletes the old saved submission from the DB
			hardDeleteSavedSubmission(savedSubmissionId);
		}
		return newSavedSubmissionId;
	}
	
	private Long createNewSavedSubmission(String userName, DisplayQuestionnaire quest, SubmissionStatus submissionStatus) throws FileSystemDataIntegrityException{
		Submission submission = new Submission();
		QuestionnaireConfigEntity configEntity = configEntityDao.findById(quest.getQuestionnaireConfigEntityId()) ;
		submission.setQuestionnaireConfig(configEntity);
		mapAnswersToNewSubmissionEntity(quest.getAllAnswers(), quest.getImageAnswerMap(), quest.getImageSeriesAnswerMap(), submission);
		submission.setSubmissionDate(new Date());
		submission.setStatus(submissionStatus);
		setSavedSubmissionTitle(submission, quest.getAllAnswers(), quest.getConfig().getSubmissionTitleQuestionId(), userName);
		
		SharedUser user = sharedUserDao.findUserByUsername(userName);
		if(user == null){  //SharedUser might be null when this code is called via a unit test.
			 user = authService.getCreateDefaultUser(userName);
		}
		if(user != null){
		    submission.setSubmitter(user);
		}
		
		mapDataToNewSavedSubmission(quest, submission);
		submission.setDraftForSubmissionId(quest.getSubmissionId());
		submissionDao.save(submission);
		return submission.getId();
	}
	
	private void mapDataToNewSavedSubmission(DisplayQuestionnaire quest, Submission submission) throws FileSystemDataIntegrityException{
		//This data is the data saved at the end of a submission
		if(quest.getSubmissionDataList() != null && quest.getSubmissionDataList().size() > 0){
		    for(SubmissionData sd : quest.getSubmissionDataList()){
		    	SubmissionData clonedSubmissionData = submissionDataService.cloneToNonPersistedFileSystemData(sd);
		    	if(clonedSubmissionData != null){
		    		submission.getSubmissionDataList().add(clonedSubmissionData);
		    	}
		    	else if(sd.getQuestionId() != null){
		    		//If the data is null, but represents a SubmissionAnswer data file, remove the answer from the submission
		    		//need to iterate over the answers list so we can remove the answer with a matching question ID
		    		Iterator<SubmissionAnswer> iter =  submission.getAnswers().iterator();
		    		while(iter.hasNext()){
		    			SubmissionAnswer sa = iter.next();
		    			if(sd.getQuestionId().equals(sa.getQuestionId())){
		    				iter.remove();
		    				break;
		    			}
		    		}
		    	}
		    }
		}
		if( quest.getQuestionUploadSubmissionDataMap().size() > 0 ){
			for(SubmissionData sd : quest.getQuestionUploadSubmissionDataMap().values()){
		    	SubmissionData clonedSubmissionData = submissionDataService.cloneToNonPersistedFileSystemData(sd);
		    	if(clonedSubmissionData != null){
		    		submission.getSubmissionDataList().add(clonedSubmissionData);
		    	}
		    	else if(sd.getQuestionId() != null){
		    		//If the data is null, but represents a SubmissionAnswer data file, remove the answer from the submission
		    		//need to iterate over the answers list so we can remove the answer with a matching question ID
		    		Iterator<SubmissionAnswer> iter =  submission.getAnswers().iterator();
		    		while(iter.hasNext()){
		    			SubmissionAnswer sa = iter.next();
		    			if(sd.getQuestionId().equals(sa.getQuestionId())){
		    				iter.remove();
		    				break;
		    			}
		    		}
		    	}
		    }
		}
	}
	
	private void deleteSavedVersionsOfSubmission(Long submissionId){
		List<Long> savedSubIdList = submissionDao.findExistingSavedSubmissionIdForSubmission(submissionId);
		if(savedSubIdList != null){
			for(Long savedSubId : savedSubIdList){
				hardDeleteSavedSubmission(savedSubId);
			}
		}
	}
	
	private void hardDeleteSavedSubmission(Long submissionId){
		Submission sub = submissionDao.findById(submissionId);
		if(sub != null && ( SubmissionStatus.SAVED.equals(sub.getStatus()) || SubmissionStatus.REJECTED_SAVED.equals(sub.getStatus())) || SubmissionStatus.DELETED.equals(sub.getStatus())){
			//TODO Until saving is worked out, DO NOT delete any data, especially when saving within a session
			logger.info("HARD DELETE submission physical files is SWITCHED OFF!!!");
			submissionDataService.deleteSubmissionData(sub.getSubmissionDataList());
			for(SubmissionAnswer sa : sub.getAnswers()){
				submissionAnswerDao.delete(sa);
			}
			//Delete any submission transfer records associated with the submission
			transferDao.deleteTransferRecordsForSubmission(submissionId);
			submissionDao.delete(sub);
		}
	}
	
	private void setSavedSubmissionTitle(Submission submission, Map<String, Answer> answers, String titleQuestionId, String userName){
		if(StringUtils.hasLength(titleQuestionId) && answers.containsKey(titleQuestionId)){
			submission.setTitle( answers.get(titleQuestionId).getResponse()+ SubmissionService.SAVED_SUB_TITLE_SFX );
		}else{
			submission.setTitle(getDefaultSubmissionTitle(userName) + SubmissionService.SAVED_SUB_TITLE_SFX);
		}
	}

	@Transactional @Override
	public List<Long> getPublishedSubmissionIdList() {
		return submissionDao.getListOfSubmissionIds(SubmissionStatus.PUBLISHED);
	}
	
	@Transactional @Override
	public List<Long> getRemovedSubmissionIdList() {
		return submissionDao.getListOfSubmissionIds(SubmissionStatus.REMOVED);
	}

	@Override @Transactional
	public void changeSubmissionOwner(Submission sub, SharedUser newOwner) {
		Submission submission = submissionDao.findById(sub.getId());
		submission.setSubmitter(newOwner);
		submissionDao.saveOrUpdate(submission);
		
	}
	
}
