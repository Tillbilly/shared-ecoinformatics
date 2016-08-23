package au.edu.aekos.shared.web.model;


import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;

public interface SubmissionItem {

	boolean isVisible();
	
	ConditionalDisplay getConditionalDisplay();
	
	ItemType getItemType();
	
	
}
