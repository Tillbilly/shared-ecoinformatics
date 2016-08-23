package au.edu.aekos.shared.web.model;

import au.edu.aekos.shared.data.entity.FileReview;
import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;

/**
 * The reviewer should inspect any files included with the submission.
 * @author a1042238
 *
 */
public class SubmittedFileReviewModel {
	
	private Long submissionDataId;
	private SubmissionFileReviewOutcome reviewOutcome;
	private String comments;

	public SubmittedFileReviewModel() {}

	public SubmittedFileReviewModel(FileReview fr) {
		this.comments = fr.getComments();
		this.reviewOutcome = fr.getReviewOutcome();
		this.submissionDataId = fr.getSubmissionData().getId();
	}

	public SubmissionFileReviewOutcome getReviewOutcome() {
		return reviewOutcome;
	}

	public void setReviewOutcome(SubmissionFileReviewOutcome reviewOutcome) {
		this.reviewOutcome = reviewOutcome;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getSubmissionDataId() {
		return submissionDataId;
	}

	public void setSubmissionDataId(Long submissionDataId) {
		this.submissionDataId = submissionDataId;
	}
	

}
