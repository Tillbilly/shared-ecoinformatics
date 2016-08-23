package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.SubmissionStatus;


/**
 * Used to display a stored submission
 * @author Ben Till
 *
 */
public class SubmissionModel {

	private Long submissionId;
	private Long draftForSubmissionId;
	private String status;
	private String submittedByUsername;
	private Date submissionDate;
	private Long submittedByUserId;
	private String submissionTitle;
	private String questionnaireVersion;
	private String configFileName;
	private Date configUploadDate;
	private Long questionnaireConfigId;
	private Date lastReviewDate;
	private String mintedDoi;
	private Date embargoDate;
	
	private List<SubmissionDataFileModel> fileList = new ArrayList<SubmissionDataFileModel>();
	
	private List<SubmissionItem> items = new ArrayList<SubmissionItem>();
	
	public Long getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public SubmissionStatus getStatusObj() {
		return SubmissionStatus.valueOf(status);
	}
	public String getSubmittedByUsername() {
		return submittedByUsername;
	}
	public void setSubmittedByUsername(String submittedByUsername) {
		this.submittedByUsername = submittedByUsername;
	}
	public Long getSubmittedByUserId() {
		return submittedByUserId;
	}
	public void setSubmittedByUserId(Long submittedByUserId) {
		this.submittedByUserId = submittedByUserId;
	}
	public String getSubmissionTitle() {
		return submissionTitle;
	}
	
	public String getSubmissionTitleForClone() {
		return submissionTitle.replace("_SAVED", "");
	}
	
	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}
	public String getQuestionnaireVersion() {
		return questionnaireVersion;
	}
	public void setQuestionnaireVersion(String questionnaireVersion) {
		this.questionnaireVersion = questionnaireVersion;
	}
	public Long getQuestionnaireConfigId() {
		return questionnaireConfigId;
	}
	public void setQuestionnaireConfigId(Long questionnaireConfigId) {
		this.questionnaireConfigId = questionnaireConfigId;
	}
	public List<SubmissionItem> getItems() {
		return items;
	}
	public void setItems(List<SubmissionItem> items) {
		this.items = items;
	}
	public String getConfigFileName() {
		return configFileName;
	}
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
	public Date getConfigUploadDate() {
		return configUploadDate;
	}
	public void setConfigUploadDate(Date configUploadDate) {
		this.configUploadDate = configUploadDate;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public List<SubmissionDataFileModel> getFileList() {
		return fileList;
	}
	public void setFileList(List<SubmissionDataFileModel> fileList) {
		this.fileList = fileList;
	}
	
	public List<QuestionModel> getAllQuestionModels(){
		List<QuestionModel> questionModelList = new ArrayList<QuestionModel>();
		for(SubmissionItem si : items){
			if(! si.getItemType().equals(ItemType.GROUP) ){
				questionModelList.add((QuestionModel) si );
			}else {
                GroupModel gm = (GroupModel) si;			
                questionModelList.addAll(gm.getAllQuestionModels());
			}
		}
		return questionModelList;
	}
	
	public Map<String, QuestionModel> getAllQuestionModelMap(){
		Map<String, QuestionModel> questionModelMap = new HashMap<String,QuestionModel>();
		for(QuestionModel q : getAllQuestionModels() ) {
			questionModelMap.put(q.getQuestionId(), q);
		}
		return questionModelMap;
	}
	
	public SubmissionDataFileModel getSubmissionDataFileModelById(Long fileId){
		for(SubmissionDataFileModel fileModel : fileList){
			if(fileId.equals(fileModel.getId())){
				return fileModel;
			}
		}
		return null;
	}
	
	/**
	 * @param otherUsername	The username to query ownership for
	 * @return				true is the owner's username is <code>otherUsername</code>, false otherwise
	 */
	public Boolean isOwnedBy(String otherUsername) {
		return submittedByUsername.equals(otherUsername);
	}
	
	public Long getDraftForSubmissionId() {
		return draftForSubmissionId;
	}
	public void setDraftForSubmissionId(Long draftForSubmissionId) {
		this.draftForSubmissionId = draftForSubmissionId;
	}
	public Date getLastReviewDate() {
		return lastReviewDate;
	}
	public void setLastReviewDate(Date lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}
	public String getMintedDoi() {
		return mintedDoi;
	}
	public void setMintedDoi(String mintedDoi) {
		this.mintedDoi = mintedDoi;
	}
	public Date getEmbargoDate() {
		return embargoDate;
	}
	public void setEmbargoDate(Date embargoDate) {
		this.embargoDate = embargoDate;
	}
}
