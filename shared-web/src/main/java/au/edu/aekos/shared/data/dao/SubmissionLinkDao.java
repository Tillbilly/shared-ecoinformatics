package au.edu.aekos.shared.data.dao;

import java.util.List;
import java.util.Set;

import au.edu.aekos.shared.data.entity.SubmissionLink;

public interface SubmissionLinkDao extends HibernateDao<SubmissionLink, Long>{
	
	Set<Long> getLinkedSubmissionIds(Long submissionId);
	
	List<SubmissionLink> getLinksForSubmission(Long submissionId);
	
	/**
	 * Will return the link and inverse link, if they exist.
	 * @param submissionId1
	 * @param submissionId2
	 * @return
	 */
	List<SubmissionLink> getSubmissionLinksBetweenSubmissions(Long submissionId1, Long submissionId2); 
	

}
