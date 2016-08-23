package au.edu.aekos.shared.service.submission;



/**
 * An enum representing the state of a current submission answer compared with a 
 * prior submission answer.
 * For 'RESUBMITTED' status submissions - used by the submission review screen.
 * 
 * @author btill
 */
public enum ReviewAnswerHistoryIndicator {
	WAS_REJECTED,
	ANSWER_CHANGED,
	NO_CHANGE
}
