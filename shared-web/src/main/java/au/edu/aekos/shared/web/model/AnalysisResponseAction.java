package au.edu.aekos.shared.web.model;

public enum AnalysisResponseAction {
	ADD("Add to vocab"),
	REJECT("Reject suggestion"),
	CHOOSE("Choose Existing Value"),
	SUGGEST("Suggest Existing Value"),
	PARENT("Remove and use already selected Parent Value");
	
	AnalysisResponseAction(String desc){
		this.description = desc;
	}
	
	public String description;
}
