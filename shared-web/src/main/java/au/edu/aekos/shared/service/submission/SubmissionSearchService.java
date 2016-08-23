package au.edu.aekos.shared.service.submission;

import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionLink;
import au.edu.aekos.shared.data.entity.groupadmin.UserGroup;
import au.edu.aekos.shared.service.groupadmin.GroupAdminServiceException;

public interface SubmissionSearchService {

	List<Submission> getSubmissionsForUser(String userName);
	
	List<Submission> getActiveSubmissionsForUser(String userName);
	
	List<Submission> getListOfLinkableSubmissionsForUser(String userName, Long linkOwnerSubmissionId);
	
	List<Submission> getListOfLinkableSubmissionsForGroup(Long groupId, Long submissionId) throws GroupAdminServiceException;
	
	List<SubmissionLink> getListOfLinkedSubmissions(Long submissionId);

	List<Submission> getAllSubmissions();
	
	List<Submission> getAllUndeletedSubmissions();
	
	/**
	 * Initial group admin function, retrieve all submissions ( excluding deleted) for users in a user group,
	 * where adminUsername is named as a group administrator.
	 * @param adminUsername
	 * @return
	 */
	Map<UserGroup, List<Submission>> getSubmissionsForGroupAdministrator(String adminUsername);
	
}
