package au.edu.aekos.shared.service.submission;

import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.service.file.FileSystemDataIntegrityException;
import au.edu.aekos.shared.service.quest.TraitValue;

/**
 * Technically I should`nt put presentation objects in the service interface,
 * but for the purposes of prototyping . . .  DisplayQuestionnaire
 * 
 * @author a1042238
 */
public interface SubmissionService {
	
	public static final String SAVED_SUB_TITLE_SFX = "_SAVED";

	Long createNewSubmission(String userName, DisplayQuestionnaire quest, SubmissionStatus submissionStatus) throws Exception;
	
	void updateSubmission(String userName, DisplayQuestionnaire quest, SubmissionStatus submissionStatus) throws Exception;
	
	/**
	 * Creates a 'Saved' copy of a submission. 
	 * This is based on the current session 'version' of the submission.
	 * @param userName
	 * @param quest
	 * @return
	 * @throws FileSystemDataIntegrityException 
	 */
	Long createSavedSubmission(String userName, DisplayQuestionnaire quest) throws FileSystemDataIntegrityException;
	
	/**
	 * @param submissionId		The ID of the submission to retrieve
	 * @return					The requested submission if it exists, null otherwise
	 */
	Submission retrieveSubmissionById(Long submissionId);
	
	/**
	 * When a submission title is not specified - need a title - 
	 * @param submissionId
	 * @return
	 */ 
	String getDefaultSubmissionTitle(String userName);

	DisplayQuestionnaire populateDisplayQuestionnaireFromSubmissionEntity(
			Long submissionId) throws Exception;
	
	DisplayQuestionnaire populateDisplayQuestionnaireForCloneFromSubmissionEntity(
			Long submissionId) throws Exception;
	
	Submission retrieveSubmissionByTitleAndUsername(String submissionTitle, String username);
	
	void updateSubmissionStatus(Long submissionId, SubmissionStatus submissionStatus);

	void updateSubmissionAnswerWithNewVocabValue(Long submissionId,
			String questionId, Map<String, TraitValue> addedTraitsMap);
	
	void updateParentSubmissionAnswerWithNewVocabValue(Long submissionId, String questionId, String parentQuestionId, Map<String, TraitValue> addedTraitsMap) throws Exception;
	
	void updateQuestionSetSubmissionAnswerWithNewVocabValue(Long submissionId, String groupQuestionId, Integer setIndex, String questionId, Map<String, TraitValue> addedTraitsMap) throws Exception;
	
	void removeResponsesFromMultiselectAnswer( List<String> responsesToRemove, Long submissionId, String questionId);
	
	void deleteSubmissionIfCurrentUserAuthorised(Long submissionId);
	
	/**
	 * Used for the reindexAllSubmissions functionality
	 * @return
	 */
	List<Long> getPublishedSubmissionIdList();
	
	/**
	 * If a submission has REMOVED status it will be removed from the solr index ( aekos portal )
	 * @return
	 */
	List<Long> getRemovedSubmissionIdList();
	
	void changeSubmissionOwner(Submission sub, SharedUser newOwner);

}
