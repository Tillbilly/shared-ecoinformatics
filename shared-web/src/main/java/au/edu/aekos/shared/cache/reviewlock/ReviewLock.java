package au.edu.aekos.shared.cache.reviewlock;

import java.io.Serializable;
import java.util.Date;

/**
 * Use the submissionId as the key
 * @author btill
 */
public class ReviewLock implements Serializable{

	private static final long serialVersionUID = 4583626618988209600L;

	private String reviewerId;
	
	private Long submissionId;
	
	private Date lockDate = new Date();
	
	public ReviewLock(String reviewerId, Long submissionId) {
		this.reviewerId = reviewerId;
		this.submissionId = submissionId;
	}
	
	public String getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}
	public Long getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	public Date getLockDate() {
		return lockDate;
	}
	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}
	
}
