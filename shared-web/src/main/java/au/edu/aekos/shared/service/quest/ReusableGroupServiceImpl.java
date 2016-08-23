package au.edu.aekos.shared.service.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.ReusableResponseGroupDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.ReusableAnswer;
import au.edu.aekos.shared.data.entity.ReusableResponseGroup;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Service
public class ReusableGroupServiceImpl implements ReusableGroupService {

	@Autowired
	private ReusableResponseGroupDao reusableResponseGroupDao;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Override
	@Transactional
	public List<String> getListOfGroupNamesForGroup(Long questionnaireId,
			String userId, String groupId) {
		List<String> namesList = new ArrayList<String>();
		List<ReusableResponseGroup> responseGroupList = reusableResponseGroupDao.retrieveResponseGroups(questionnaireId, userId, groupId);
		if(responseGroupList != null && responseGroupList.size() > 0){
			for(ReusableResponseGroup group : responseGroupList){
				namesList.add(group.getName());
			}
		}
		return namesList;
	}

	@Override
	@Transactional
	public ReusableResponseGroup getGroupByName(String groupName,
			Long questionnaireId, String userId, String groupId) {
		ReusableResponseGroup rrg = reusableResponseGroupDao.retrieveResponseGroupByName(groupName, questionnaireId, userId, groupId);
	    if(rrg != null){
	    	rrg.getAnswers().size(); //rehydrating due to lazy load exception
	    	for(ReusableAnswer ra : rrg.getAnswers() ){
	    		ra.getResponseType();
				if(ResponseType.getIsMultiselect(ra.getResponseType())){
	    			ra.getMultiselectAnswerList().size();
	    		}
	    	}
	    }
	
	    return rrg;
	}
	
	/**
	 * This is a bit fiddly, but the general plan is thus:
	 * 1) Identify reusable groups in the configuration
	 * 2) See if a reusable group has been used ( first by querying for name, then by comparison )
	 * 3) If it has`nt been used / or has been modified, create a new reusable group and persist it.
	 */
	@Transactional
	public void createReusableGroups(String userName, DisplayQuestionnaire quest, Submission persistedSubmission){
		if(quest.getReusableGroupIdOptionListMap().size() > 0 ){ //means there are reusable groups
			Map<String, QuestionGroup> questionGroupMap = quest.getConfig().getQuestionGroupMap();
			Map<String, SubmissionAnswer> answerMap = persistedSubmission.getAnswersMappedByQuestionId();
			
			for(String groupId : quest.getReusableGroupIdOptionListMap().keySet()){
				QuestionGroup groupConfig = questionGroupMap.get(groupId);
				
				String selectedGroupTitle = quest.getSelectedReusableGroupMap().get(groupId);
				if(StringUtils.hasLength(selectedGroupTitle) ){
					if( ! currentAnswersEqualReusableGroup(selectedGroupTitle , groupConfig, persistedSubmission ) ){
						createNewReusableGroup(incrementReusableGroupTitle( selectedGroupTitle ), groupConfig, persistedSubmission );
					}
				}else{
					String rgTitleQuestionId = groupConfig.getReusableGroupTitleQuestionId();
					SubmissionAnswer titleAnswer = answerMap.get(rgTitleQuestionId);
					if(titleAnswer != null && StringUtils.hasLength(titleAnswer.getResponse())){
						List<String> reusableGroupOptionList = quest.getReusableGroupIdOptionListMap().get(groupId);
						if(reusableGroupOptionList == null || reusableGroupOptionList.size() == 0 ||
							! reusableGroupOptionList.contains(titleAnswer.getResponse()) ){
							//create new reusable Group with the answers for the group.
							createNewReusableGroup(titleAnswer.getResponse(), groupConfig, persistedSubmission );
						}else{ //Actually now we should never get here - 
							
							   //Test with a dubug when ui is integrated.
							
							//TODO - the modification of a group - need to keep track of which group was selected for lookup,
							//Don`t just use the question title.
							//either the group has been reused and not changed, Needs some work
							//Or the content has been changed, and a new group needs creating with a modified title.
							if( ! currentAnswersEqualReusableGroup(titleAnswer.getResponse(), groupConfig, persistedSubmission ) ){
								//hmmmmmm this is`nt going to work correctly - might need to keep track of the used group name
								//not just the response question.
								createNewReusableGroup(incrementReusableGroupTitle( titleAnswer.getResponse() ), groupConfig, persistedSubmission );
							}
						}
					}
				}
			}
		}
	}
	
	public static String incrementReusableGroupTitle(String originalTitle){
		Pattern findNumberPattern = Pattern.compile("(.+)\\((\\d+)\\)");
		Matcher m = findNumberPattern.matcher(originalTitle);
		if(m.matches()){
			String title = m.group(1);
			String number = m.group(2);
			int x = Integer.parseInt(number) + 1;
			return title + "(" + x + ")";
		}
		return originalTitle + " (1)";
	}
	
	private void createNewReusableGroup(String title, QuestionGroup groupConfig, Submission persistedSubmission ){
	    ReusableResponseGroup rrg = new ReusableResponseGroup();
	    rrg.setName(title);
	    rrg.setGroupId(groupConfig.getId());
	    rrg.setQuestionnaireConfig(persistedSubmission.getQuestionnaireConfig());
	    rrg.setSharedUser( persistedSubmission.getSubmitter() );
	    
	    Map<String, SubmissionAnswer> submissionAnswerMap = persistedSubmission.getAnswersMappedByQuestionId();
	    for(String questionId : groupConfig.getAllGroupChildQuestionIds() ){
	    	SubmissionAnswer subAnswer = submissionAnswerMap.get(questionId);
	    	if(subAnswer != null){
	    	    rrg.getAnswers().add(new ReusableAnswer(subAnswer));
	    	}
	    }
	    reusableResponseGroupDao.save(rrg);
	}

	
	//Might need to also compare whether non mandatory responses are made ( if required )
	private boolean currentAnswersEqualReusableGroup(String reusableGroupName, QuestionGroup groupConfig, Submission persistedSubmission ){
		ReusableResponseGroup rrg = reusableResponseGroupDao.retrieveResponseGroupByName(reusableGroupName, persistedSubmission.getQuestionnaireConfig().getId(), 
				                                              persistedSubmission.getSubmitter().getUsername(), groupConfig.getId());
		if(rrg != null){
			Map<String, ReusableAnswer> reusableAnswerMap = rrg.getAnswersMappedByQuestionId();
			Map<String, SubmissionAnswer> subAnswerMap = persistedSubmission.getAnswersMappedByQuestionId();
			
			for(Entry<String, ReusableAnswer> reuseAnsEntry : reusableAnswerMap.entrySet() ){
				SubmissionAnswer subAnswer = subAnswerMap.get( reuseAnsEntry.getKey() );
				if(subAnswer == null){
					return false;
				}
				if( ! reusableAnswerEqualsSubmissionAnser( reuseAnsEntry.getValue() , subAnswer )){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	//TODO Finish this off later . . . . multiselects.
	private boolean reusableAnswerEqualsSubmissionAnser(ReusableAnswer reusableAnswer, SubmissionAnswer submissionAnswer){
		if(! reusableAnswer.getResponse().equals( submissionAnswer.getResponse() ) ){
			return false;
		}
		return true;
	}
	
	
	@Override
	@Transactional
	public void populateReusableGroupLists( DisplayQuestionnaire displayQuestionnaire, String userId) {
		if( displayQuestionnaire.getReusableGroupIdOptionListMap().size() > 0 ){
		    Long questionnaireId = displayQuestionnaire.getConfig().getSmsQuestionnaireId();
		    Set<String> reusableGroupIds = displayQuestionnaire.getReusableGroupIdOptionListMap().keySet();
		    List<ReusableResponseGroup> reusableResponseGroupList = reusableResponseGroupDao.retrieveResponseGroups(questionnaireId, userId, reusableGroupIds);
		    if(reusableResponseGroupList != null &&  reusableResponseGroupList.size() > 0){
		    	for(ReusableResponseGroup rrg : reusableResponseGroupList){
		    		displayQuestionnaire.getReusableGroupIdOptionListMap().get(rrg.getGroupId()).add(rrg.getName());
		    	}
		    }
		}
	}
	
	
	
	

}
