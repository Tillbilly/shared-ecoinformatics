package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WorkflowHistoryModel {

	private Long submissionId;

	List<Object> submissionStatusHistory = new ArrayList<Object>();
	
	private Map<String, List<QuestionHistoryModel>> questionHistoryListMap = new HashMap<String, List<QuestionHistoryModel>>();
	
	private Map<Long, List<FileHistoryModel>> fileHistoryListMap = new HashMap<Long, List<FileHistoryModel>>();  //Show history of deleted files!!
	
	private List<ReviewHistoryModel> reviewHistoryList = new ArrayList<ReviewHistoryModel>();
	
	//Deleted files list!!
	
	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public List<Object> getSubmissionStatusHistory() {
		return submissionStatusHistory;
	}

	public void setSubmissionStatusHistory(List<Object> submissionStatusHistory) {
		this.submissionStatusHistory = submissionStatusHistory;
	}

	public Map<String, List<QuestionHistoryModel>> getQuestionHistoryListMap() {
		return questionHistoryListMap;
	}

	public void setQuestionHistoryListMap(
			Map<String, List<QuestionHistoryModel>> questionHistoryListMap) {
		this.questionHistoryListMap = questionHistoryListMap;
	}

	public Map<Long, List<FileHistoryModel>> getFileHistoryListMap() {
		return fileHistoryListMap;
	}

	public void setFileHistoryListMap(
			Map<Long, List<FileHistoryModel>> fileHistoryListMap) {
		this.fileHistoryListMap = fileHistoryListMap;
	}

	public List<ReviewHistoryModel> getReviewHistoryList() {
		return reviewHistoryList;
	}

	public void setReviewHistoryList(List<ReviewHistoryModel> reviewHistoryList) {
		this.reviewHistoryList = reviewHistoryList;
	}
	
	
	
}
