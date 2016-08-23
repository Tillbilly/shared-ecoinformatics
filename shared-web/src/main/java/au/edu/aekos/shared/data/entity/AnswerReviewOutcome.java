package au.edu.aekos.shared.data.entity;

public enum AnswerReviewOutcome {

	PASS("Pass"),
	PASS_WITH_MODS("Modify"),
	REJECT("Reject");
	
	AnswerReviewOutcome(String description){
    	this.description= description;
    }
	
    private final String description;
    
    public String getDescription() {
		return description;
	}
}
