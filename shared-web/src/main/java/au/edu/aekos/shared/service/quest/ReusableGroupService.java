package au.edu.aekos.shared.service.quest;

import java.util.List;

import au.edu.aekos.shared.data.entity.ReusableResponseGroup;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;


public interface ReusableGroupService {
	
	List<String> getListOfGroupNamesForGroup(Long questionnaireId, String userId, String groupId);
	
	ReusableResponseGroup getGroupByName(String groupName, Long questionnaireId, String userId, String groupId);
	
	void createReusableGroups(String userName, DisplayQuestionnaire quest, Submission persistedSubmission);
	
	void populateReusableGroupLists(DisplayQuestionnaire displayQuestionnaire, String userId);
	
	

}
