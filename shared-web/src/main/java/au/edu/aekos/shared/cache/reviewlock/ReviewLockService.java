package au.edu.aekos.shared.cache.reviewlock;

import java.util.List;
import java.util.Set;

public interface ReviewLockService {

	boolean submissionLockedForReview(Long submissionId);

	ReviewLock getReviewLock(Long submissionId);

	void setReviewLock(Long submissionId, String username) throws ReviewLockException;
	
	Set<Long> getLockedSubmissionIdsExcludingLocksByUser(String username);
	
	List<Long> getLockedSubmissionIds();
	
	/**
	 * Clears the lock without any checks. If you're needing some safety so you only
	 * clear locks for the current user then see {@link ReviewLockService#removeLock(Long, String)}.
	 * 
	 * @param submissionId	The ID of the submission that is locked (and should be cleared)
	 */
	void removeLock(Long submissionId);
	
	/**
	 * Call this method when the functionality will be exposed externally (i.e. through a URL)
	 * so a user can't hack the call to clear to clear someone else's lock.
	 * 
	 * @param submissionId	The ID of the submission that is locked (and should be cleared)
	 * @param username		The username of the person requesting the lock
	 */
	void removeLock(Long submissionId, String username) ;
}