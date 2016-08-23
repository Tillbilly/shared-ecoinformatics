package au.edu.aekos.shared.web.model;


public class AnalysisResponseModel {

	private String originalValue = null;
	private String chosenValue = null;
	private AnalysisResponseAction action = null;
	
	public AnalysisResponseModel() {
		super();
	}
	public String getOriginalValue() {
		return originalValue;
	}
	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}
	public String getChosenValue() {
		return chosenValue;
	}
	public void setChosenValue(String chosenValue) {
		this.chosenValue = chosenValue;
	}
	public AnalysisResponseAction getAction() {
		return action;
	}
	public void setAction(AnalysisResponseAction action) {
		this.action = action;
	}
	
}
