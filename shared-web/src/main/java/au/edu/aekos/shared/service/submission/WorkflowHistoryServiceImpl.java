package au.edu.aekos.shared.service.submission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionHistoryDao;
import au.edu.aekos.shared.data.entity.AnswerReview;
import au.edu.aekos.shared.data.entity.AnswerReviewOutcome;
import au.edu.aekos.shared.data.entity.FileReview;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.data.entity.SubmissionHistory;
import au.edu.aekos.shared.data.entity.SubmissionReview;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.web.model.AnswerReviewModel;
import au.edu.aekos.shared.web.model.FileHistoryModel;
import au.edu.aekos.shared.web.model.QuestionHistoryModel;
import au.edu.aekos.shared.web.model.ResubmitAnswerIndicator;
import au.edu.aekos.shared.web.model.ResubmitDataIndicator;
import au.edu.aekos.shared.web.model.ReviewHistoryModel;
import au.edu.aekos.shared.web.model.SubmissionReviewModel;
import au.edu.aekos.shared.web.model.SubmittedFileReviewModel;
import au.edu.aekos.shared.web.model.WorkflowHistoryModel;

@Service
public class WorkflowHistoryServiceImpl implements WorkflowHistoryService {

	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionHistoryDao submissionHistoryDao;
	
	@Autowired
	private SubmissionReviewService submissionReviewService;
	
	@Override
	public void getSubmissionWorkflowHistoryForQuestion(Long submissionId,
			String questionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public WorkflowHistoryModel getSubmissionWorkflowHistory(Long submissionId) {
		WorkflowHistoryModel whm = new WorkflowHistoryModel();
		whm.setSubmissionId(submissionId);
		List<SubmissionHistory> submissionHistoryList = submissionHistoryDao.retrieveSubmissionHistoryForSubmission(submissionId);
		for(SubmissionHistory subHist : submissionHistoryList){
			
			SubmissionReviewModel review = null;
			if(subHist.getSubmissionReview() != null ){
				review = submissionReviewService.buildSubmissionReviewModel(subHist.getSubmissionReview());
			}
			
			for(SubmissionAnswer subAnswer : subHist.getAnswers() ){
				addHistoricalSubmissionAnswerToWorkflowHistoryModel(whm, subAnswer, review, subHist.getSubmitter().getUsername() , subHist);
			}
			
			for(SubmissionData submissionData : subHist.getSubmissionDataList() ){
				addHistoricalSubmissionDataToWorkflowHistoryModel(whm, submissionData, review, subHist.getSubmitter().getUsername() , subHist);
			}
			
			if(review != null){
				addHistoricalReviewToWorkflowHistoryModel(whm, review);
			}
			
		}
		
		return whm;
	}
	
	private void addHistoricalSubmissionAnswerToWorkflowHistoryModel(WorkflowHistoryModel whm, 
			                                                         SubmissionAnswer subAnswer, 
			                                                         SubmissionReviewModel review, 
			                                                         String submitterUsername ,
			                                                         SubmissionHistory submissionHistory){
		QuestionHistoryModel qhm = buildQuestionHistoryModel(subAnswer, review, submitterUsername, submissionHistory );
		String questionId = subAnswer.getQuestionId();
		if( whm.getQuestionHistoryListMap().containsKey(questionId )  ){
			whm.getQuestionHistoryListMap().get(questionId).add(qhm);
		}else{
			List<QuestionHistoryModel> questionHistoryList = new ArrayList<QuestionHistoryModel>();
			questionHistoryList.add(qhm);
			whm.getQuestionHistoryListMap().put(questionId, questionHistoryList);
		}
	}
	
	private void addHistoricalSubmissionDataToWorkflowHistoryModel(WorkflowHistoryModel whm, 
            SubmissionData subData, 
            SubmissionReviewModel review, 
            String submitterUsername ,
            SubmissionHistory submissionHistory){
		
		FileHistoryModel fhm = buildFileHistoryModel(subData, review, submitterUsername, submissionHistory );
		Long submissionDataId = subData.getId();
		if( whm.getFileHistoryListMap().containsKey(submissionDataId )  ){
			whm.getFileHistoryListMap().get(submissionDataId).add(fhm);
		}else{
			List<FileHistoryModel> fileHistoryList = new ArrayList<FileHistoryModel>();
			fileHistoryList.add(fhm);
			whm.getFileHistoryListMap().put(submissionDataId, fileHistoryList);
		}
	}
	
	private QuestionHistoryModel buildQuestionHistoryModel( 
            SubmissionAnswer subAnswer, 
            SubmissionReviewModel review, 
            String submitterUsername ,
            SubmissionHistory submissionHistory){
		QuestionHistoryModel qhm = new QuestionHistoryModel();
		String questionId = subAnswer.getQuestionId();
		qhm.setQuestionId(questionId);
		qhm.setAnswerId(subAnswer.getId());
		qhm.setResponse(subAnswer.getResponse());
		qhm.setSubmittedBy(submitterUsername);
		qhm.setSubmissionDate( submissionHistory.getSubmissionDate() );
		qhm.setSubmissionStatus(submissionHistory.getStatus());
		if( review != null ){
			qhm.setReviewDate( review.getReviewDate() );
			AnswerReviewModel answerReview = review.getAnswerReviews().get(questionId);
			qhm.setAnswerReviewOutcome(answerReview.getOutcome());
			qhm.setReviewComments(answerReview.getComment());
		}
		return qhm;
    }
	
	private FileHistoryModel buildFileHistoryModel(SubmissionData subData, SubmissionReviewModel review, String submitterUsername, SubmissionHistory submissionHistory ){
		FileHistoryModel fhm = new FileHistoryModel();
		fhm.setFileId( subData.getId() );
		fhm.setFileName(subData.getFileName());
		fhm.setDescription(subData.getFileDescription() );
		fhm.setSubmitter(submitterUsername);
		fhm.setSubmittedDate(submissionHistory.getSubmissionDate() );
		fhm.setSubmissionStatus(submissionHistory.getStatus());
		//Check if deleted 
		boolean deleted = true;
		for(SubmissionData currentSd : submissionHistory.getSubmission().getSubmissionDataList() ){
			if(currentSd.getId().equals(subData.getId()) ){
				deleted = false;
				break;
			}
		}
		fhm.setIsDeleted(deleted);
		if(review != null && review.getFileReviews().containsKey( subData.getId() )){
			SubmittedFileReviewModel dataReview = review.getFileReviews().get(subData.getId());
			fhm.setReviewDate(review.getReviewDate()) ;
			fhm.setReviewOutcome(dataReview.getReviewOutcome());
			fhm.setReviewComments( dataReview.getComments() );
		}
		
		return fhm;
	}
	
	private void addHistoricalReviewToWorkflowHistoryModel(WorkflowHistoryModel whm, SubmissionReviewModel review){
		ReviewHistoryModel reviewHistory = new ReviewHistoryModel();
		reviewHistory.setReviewId(review.getSubmissionReviewId());
		reviewHistory.setNotes(review.getNotes());
		reviewHistory.setReviewDate( review.getReviewDate() );
		reviewHistory.setReviewer(review.getReviewer());
		reviewHistory.setReviewOutcome(review.getReviewOutcome() );
		whm.getReviewHistoryList().add(reviewHistory);
	}

	@Transactional
	public void buildIndicatorMaps( Long submissionId,  Map<String, ResubmitAnswerIndicator> answerIndicatorMap, Map<Long, ResubmitDataIndicator> dataIndicatorMap) {
		Submission currentSubmission = submissionDao.findById(submissionId); 
		List<SubmissionHistory> submissionHistoryList = submissionHistoryDao.retrieveSubmissionHistoryForSubmission(submissionId);
		SubmissionReview lastReviewForSubmission = null;
		SubmissionHistory submissionPreLastReview = null;
		
		if(submissionHistoryList != null && submissionHistoryList.size() > 0 ){
			for(SubmissionHistory subHistory : submissionHistoryList ){
				if(subHistory.getSubmissionReview() != null){
					lastReviewForSubmission = subHistory.getSubmissionReview();
				}
				if(SubmissionStatus.SUBMITTED.equals( subHistory.getStatus() ) ){
					submissionPreLastReview = subHistory;
				}
				if(lastReviewForSubmission != null && submissionPreLastReview != null ){
					break;
				}
			}
		}
		//Need to compare the answers from the current state of the submission to the answers from submissionPreLastReview
		Map<String, SubmissionAnswer> oldSubAnswerMap = submissionPreLastReview.getAnswersMappedByQuestionId();
		for(SubmissionAnswer subAnswer : currentSubmission.getAnswers()){
			ResubmitAnswerIndicator rsa = new ResubmitAnswerIndicator();
			rsa.setQuestionId(subAnswer.getQuestionId());
			SubmissionAnswer oldAnsForComparison = oldSubAnswerMap.get(subAnswer.getQuestionId());
			if( oldAnsForComparison == null || ! oldAnsForComparison.getId().equals(subAnswer.getId() )) {
				rsa.setHasAnswerChanged(Boolean.TRUE);
			}
			answerIndicatorMap.put(subAnswer.getQuestionId(), rsa);
		}
		//Now add the review indication
		if(lastReviewForSubmission != null){
			for(AnswerReview answerReview : lastReviewForSubmission.getAnswerReviews()){
				if(answerReview.getAnswerReviewOutcome().equals(AnswerReviewOutcome.REJECT) ){
					answerIndicatorMap.get(answerReview.getQuestionId()).setWasRejected(Boolean.TRUE);
				}
			}
		}
		
		//Do I need to know if files have been deleted??  
		//Probably - need to see the history of what has happened.
		
		//And now similar processing to get the file indicator map populated.
		//Retrieve a map of the old submissionData records
		Map<Long, SubmissionData> oldSubDataMap = submissionPreLastReview.getSubmissionDataMappedById();
		Set<Long> dataIdsInNewSubmission = new HashSet<Long>(); 
		for(SubmissionData subData : currentSubmission.getSubmissionDataList()){
			ResubmitDataIndicator rsd = new ResubmitDataIndicator();
			rsd.setDataId(subData.getId());
			dataIdsInNewSubmission.add(subData.getId());
			if(! oldSubDataMap.containsKey(subData.getId())){
				rsd.setNewFile(Boolean.TRUE);
			}
			dataIndicatorMap.put(subData.getId(), rsd);
		}
		
		//Now look at the old data list - see if any files have been deleted ( add them )
		for(SubmissionData subData : submissionPreLastReview.getSubmissionDataList() ){
			if( ! dataIdsInNewSubmission.contains(subData.getId() ) ){
				ResubmitDataIndicator rsd = new ResubmitDataIndicator();
				rsd.setDataId(subData.getId());
				rsd.setHasBeenDeleted(Boolean.TRUE);
				dataIndicatorMap.put(subData.getId(), rsd);
			}
		}
		
		//Now check file reviews of any of the files have been rejected in a previous review
		if(lastReviewForSubmission != null){
			for(FileReview fileReview : lastReviewForSubmission.getFileReviews() ){
				if(fileReview.getReviewOutcome().equals(SubmissionFileReviewOutcome.REJECT) && dataIndicatorMap.containsKey(fileReview.getSubmissionData().getId() ) ){
					dataIndicatorMap.get(fileReview.getSubmissionData().getId()).setWasRejected(Boolean.TRUE);
				}
			}
		}
	}
}
