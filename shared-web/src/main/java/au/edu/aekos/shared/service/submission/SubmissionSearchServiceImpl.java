package au.edu.aekos.shared.service.submission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionLinkDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionLink;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;
import au.edu.aekos.shared.service.groupadmin.GroupAdminServiceException;

@Service
public class SubmissionSearchServiceImpl implements SubmissionSearchService {

	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionLinkDao submissionLinkDao;
	
	@Autowired
	private GroupAdminService groupAdminService;
	
	@Override
	@Transactional
	public List<Submission> getSubmissionsForUser(String userName) {
		return submissionDao.findSubmissionsByUsername(userName);
	}

	@Override
	@Transactional 
	//This will become more elaborate when User Group Access is enabled.
	//For now, will return submissions with a status of not DRAFT,INCOMPLETE or DELETED
	public List<Submission> getListOfLinkableSubmissionsForUser(String userName, Long linkOwnerSubmissionId) {
		List<Long> submissionIdsToExclude = new ArrayList<Long>();
		submissionIdsToExclude.add(linkOwnerSubmissionId);
		Set<Long> linkedSubIds = submissionLinkDao.getLinkedSubmissionIds(linkOwnerSubmissionId);
		if(linkedSubIds != null) {
			submissionIdsToExclude.addAll(linkedSubIds);
		}
		List<SubmissionStatus> subStatusExcludeList = new ArrayList<SubmissionStatus>();
		subStatusExcludeList.add(SubmissionStatus.SAVED);
		subStatusExcludeList.add(SubmissionStatus.DELETED);
		return submissionDao.getListOfSubmissionsForUserName(userName, null, subStatusExcludeList, submissionIdsToExclude);
	}

	@Override @Transactional
	public List<SubmissionLink> getListOfLinkedSubmissions(Long submissionId) {
		return submissionLinkDao.getLinksForSubmission(submissionId);
	}

	@Override @Transactional
	public List<Submission> getActiveSubmissionsForUser(String userName) {
		List<SubmissionStatus> excludeStatus = new ArrayList<SubmissionStatus>();
		excludeStatus.add(SubmissionStatus.DELETED);
		List<Submission> submissions = submissionDao.getListOfSubmissionsForUserName(userName, null, excludeStatus, null);
		return sortByDateDescendingAndSavedFirst(submissions);
	}
	
	@Override @Transactional
	public List<Submission> getAllSubmissions() {
		return submissionDao.getAll();
	}

	@Override @Transactional
	public List<Submission> getAllUndeletedSubmissions() {
		return submissionDao.getAllUndeletedSubmissions();
	}

	@Override @Transactional
	public Map<UserGroup, List<Submission>> getSubmissionsForGroupAdministrator(String adminUsername) {
		Map<UserGroup, List<Submission>> groupSubmissionMap = new HashMap<UserGroup, List<Submission>>();
		List<UserGroup> userGroups = groupAdminService.retrieveGroupsForAdminUser(adminUsername);
		if(userGroups != null){
			for(UserGroup ug : userGroups){
				List<Submission> userGroupSubmissions = new ArrayList<Submission>();
				if(ug.getMemberList() != null){
					for(SharedUser su : ug.getMemberList()){
						List<Submission> userSubmissions = getActiveSubmissionsForUser(su.getUsername());
						if(userSubmissions != null){
							userGroupSubmissions.addAll(userSubmissions);
						}
					}
				}
				groupSubmissionMap.put(ug, userGroupSubmissions );
			}
		}
		return groupSubmissionMap;
	}

	@Override @Transactional
	public List<Submission> getListOfLinkableSubmissionsForGroup(Long groupId,
			Long submissionId) throws GroupAdminServiceException {
		UserGroup userGroup = groupAdminService.retrieveUserGroup(groupId);
		List<String> users = userGroup.getListOfGroupUsernames();
		List<Long> submissionIdsToExclude = new ArrayList<Long>();
		submissionIdsToExclude.add(submissionId);
		Set<Long> linkedSubIds = submissionLinkDao.getLinkedSubmissionIds(submissionId);
		if(linkedSubIds != null) {
			submissionIdsToExclude.addAll(linkedSubIds);
		}
		List<SubmissionStatus> subStatusExcludeList = new ArrayList<SubmissionStatus>();
		subStatusExcludeList.add(SubmissionStatus.SAVED);
		subStatusExcludeList.add(SubmissionStatus.DELETED);
		return submissionDao.getListOfSubmissionsForUsers(users, null, subStatusExcludeList, submissionIdsToExclude);
		
	}
	
	public List<Submission> sortByDateDescendingAndSavedFirst(List<Submission> startList){
		if(startList == null || startList.size() == 0){
			return startList;
		}
		Collections.sort(startList, new SubmissionDateDescComparator());
		Map<Long,Submission> submissionMap = new HashMap<Long,Submission>();
		//Need to iterate the list twice unfortunately
		for(Submission sub : startList){
			submissionMap.put(sub.getId(), sub);
		}
		List<Submission> sortedList = new ArrayList<Submission>();
		for(Submission sub : startList){
			if(! submissionMap.containsKey(sub.getId())){
				continue;
			}
			sortedList.add(sub);
			if(sub.getDraftForSubmissionId() != null && submissionMap.containsKey(sub.getDraftForSubmissionId())){
				Submission original = submissionMap.get(sub.getDraftForSubmissionId());
				sortedList.add(original);
				submissionMap.remove(sub.getDraftForSubmissionId());
			}
		}
		return sortedList;
	}
	
}
