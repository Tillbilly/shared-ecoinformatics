package au.edu.aekos.shared.data.entity;

public enum ReviewOutcome {
	
	REJECTED("Submission has been rejected"),
	MOD_REQUESTED("Request for submission modification"),
	PUBLISH("Submission passed review, OK to publish"),
	REVIEW_SAVED("Review incomplete, saved");
	
	private ReviewOutcome(String description) {
		this.description = description;
	}

	private final String description;
	
	public String getDescription(){
		return description;
	}
	
	
}
