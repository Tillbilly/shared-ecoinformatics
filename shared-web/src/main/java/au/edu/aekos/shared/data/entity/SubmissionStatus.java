package au.edu.aekos.shared.data.entity;

import java.util.Arrays;
import java.util.List;

public enum SubmissionStatus {
	SAVED,				// User saved submission - only 1 saved version allowed per real submission
	SUBMITTED, 			// User Submitted Submissions but review not started yet
	APPROVED, 			// After the submission has been published by the reviewer
	PUBLISHED, 			// Published state - solr indexed, available in aekos portal
	PEER_REVIEWED,      // For group submissions requiring peer review.
	REMOVED,            // Post published - removed state, deleted from solr index.
	REJECTED, 			// State when Reviewer rejects the review
	REJECTED_SAVED,     //A partial save post review rejection
	DISCARDED, 			// State when Reviewer Discards the review
	DELETED, 			// State when user deletes the Submission ?
	RESUBMITTED, 		// State when User corrects the rejected submissions and resubmits it.
	UPDATE_SUBMITTED,	//
	INCOMPLETE,          //Deprecated   Included so as not to break old data
	REJECTED_INCOMPLETE,  //Deprecated
	DRAFT                 //
	
;

	public boolean isDeletable() {
		final List<SubmissionStatus> deletableStatuses = Arrays.asList(
			SubmissionStatus.SAVED,
			SubmissionStatus.DISCARDED,
			SubmissionStatus.REJECTED,
			SubmissionStatus.REJECTED_SAVED,
			SubmissionStatus.SUBMITTED,
			SubmissionStatus.RESUBMITTED
		);
		return deletableStatuses.contains(this);
	}

	public boolean isEditable() {
		final List<SubmissionStatus> editableStatuses = Arrays.asList(
			SubmissionStatus.SAVED,
			SubmissionStatus.RESUBMITTED,
			SubmissionStatus.SUBMITTED
		);
		return editableStatuses.contains(this);
	}

	public boolean isModifiable() {
		final List<SubmissionStatus> modifiableStatuses = Arrays.asList(
			SubmissionStatus.REJECTED,
			SubmissionStatus.REJECTED_SAVED
		);
		return modifiableStatuses.contains(this);
	}

	public boolean isApproved() {
		final List<SubmissionStatus> modifiableStatuses = Arrays.asList(
				SubmissionStatus.APPROVED,
				SubmissionStatus.PUBLISHED
			);
		return modifiableStatuses.contains(this);
	}
	
	public boolean isPublicationCertificateAvailable() {
		return this.equals(PUBLISHED);
	}

	public static List<SubmissionStatus> getDeprecatedStatuses(){
		return Arrays.asList(
				SubmissionStatus.INCOMPLETE,
				SubmissionStatus.REJECTED_INCOMPLETE,
				SubmissionStatus.DRAFT
			);
	}
	
	public boolean isDeleted() {
		return DELETED.equals(this);
	}
	
	public boolean isPeerReviewable() {
		final List<SubmissionStatus> peerReviewableStatuses = Arrays.asList(
				SubmissionStatus.SUBMITTED,
				SubmissionStatus.RESUBMITTED
			);
		return peerReviewableStatuses.contains(this);
	}
	

	public boolean isLinkable() {
		final List<SubmissionStatus> notLinkableStatuses = Arrays.asList(
			SubmissionStatus.SAVED,
			SubmissionStatus.REJECTED_SAVED,
			SubmissionStatus.DELETED
		);
		return !notLinkableStatuses.contains(this);
	}
}
