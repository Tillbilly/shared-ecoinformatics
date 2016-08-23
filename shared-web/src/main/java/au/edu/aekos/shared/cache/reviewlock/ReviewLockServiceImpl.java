package au.edu.aekos.shared.cache.reviewlock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.stereotype.Service;

@Service
public class ReviewLockServiceImpl implements ReviewLockService {

	private static final String CACHE_NAME= "review_lock_cache";
	private Cache reviewLockCache = null;
	
	private Cache getCache(){
		if(reviewLockCache == null){
		    CacheManager cacheManager = CacheManager.getInstance();
		    reviewLockCache = cacheManager.getCache(CACHE_NAME);
		}
		return reviewLockCache;
	}

	public boolean submissionLockedForReview(Long submissionId){
		return getCache().isKeyInCache(submissionId);
	}
	
	public ReviewLock getReviewLock(Long submissionId){
		Element el = getCache().get(submissionId);
		if(el == null){
			return null;
		}
		return (ReviewLock) el.getObjectValue();
	}
	
	public void setReviewLock(Long submissionId, String username) throws ReviewLockException {
		ReviewLock lock = getReviewLock(submissionId);
		if(lock != null && ! lock.getReviewerId().equals(username) ){
			throw new ReviewLockException("Review locked by another user");
		}
		lock = new ReviewLock(username, submissionId);
		Element el = new Element(submissionId, lock);
		getCache().put(el);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getLockedSubmissionIds(){		
		return getCache().getKeys();
	}
	
    @SuppressWarnings("unchecked")
	public Set<Long> getLockedSubmissionIdsExcludingLocksByUser(String username){
    	Set<Long> lockedSubmissionIds = new HashSet<Long>();
    	List<Long> keys = getCache().getKeys();
    	if(keys == null || keys.size() == 0){
    		return lockedSubmissionIds;
    	}
    	for(Long key : keys){
    		ReviewLock lock = getReviewLock(key);
    		if(lock != null && ! lock.getReviewerId().equals(username)){
    			lockedSubmissionIds.add(key);
    		}
    	}
    	return lockedSubmissionIds;
	}

	@Override
	public void removeLock(Long submissionId) {
		getCache().remove(submissionId);
	}
	
	@Override
	public void removeLock(Long submissionId, String username) {
		ReviewLock lock = getReviewLock(submissionId);
		if(lock != null && username.equals(lock.getReviewerId())){
		    getCache().remove(submissionId);
		}
	}
}
