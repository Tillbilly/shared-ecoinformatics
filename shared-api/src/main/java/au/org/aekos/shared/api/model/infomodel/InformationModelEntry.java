package au.org.aekos.shared.api.model.infomodel;

public class InformationModelEntry {

	private String metatag;
	private String displayText;
	private String description;
	private Boolean questionnaireAnswer = null;  //Answer or Derived
	private Boolean mandatory = null;
	private Boolean multipleValues = null;
	private String vocabulary;
	private String relatedMetatag;
	private String section;
	private String subsection;
	private String group;
	
	public String getMetatag() {
		return metatag;
	}
	public void setMetatag(String metatag) {
		this.metatag = metatag;
	}
	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getQuestionnaireAnswer() {
		return questionnaireAnswer;
	}
	public void setQuestionnaireAnswer(Boolean questionnaireAnswer) {
		this.questionnaireAnswer = questionnaireAnswer;
	}
	public Boolean getMandatory() {
		return mandatory;
	}
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
	public Boolean getMultipleValues() {
		return multipleValues;
	}
	public void setMultipleValues(Boolean multipleValues) {
		this.multipleValues = multipleValues;
	}
	public String getVocabulary() {
		return vocabulary;
	}
	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}
	public String getRelatedMetatag() {
		return relatedMetatag;
	}
	public void setRelatedMetatag(String relatedMetatag) {
		this.relatedMetatag = relatedMetatag;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getSubsection() {
		return subsection;
	}
	public void setSubsection(String subsection) {
		this.subsection = subsection;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	public InformationModelEntry clone(){
		InformationModelEntry clonedEntry = new InformationModelEntry();
		clonedEntry.setDescription(description);
		clonedEntry.setDisplayText(displayText);
		clonedEntry.setGroup(group);
		clonedEntry.setMandatory(mandatory.booleanValue());
		clonedEntry.setMetatag(metatag);
		clonedEntry.setMultipleValues(multipleValues.booleanValue());
		clonedEntry.setQuestionnaireAnswer(questionnaireAnswer.booleanValue());
		clonedEntry.setRelatedMetatag(relatedMetatag);
		clonedEntry.setSection(section);
		clonedEntry.setSection(section);
		clonedEntry.setSubsection(subsection);
		clonedEntry.setSubsection(subsection);
		clonedEntry.setVocabulary(vocabulary);
		return clonedEntry;
	}
	
	@Override
	public String toString() {
		return String.format("%s [questionnaireAnswer=%s, section=%s, subsection=%s, group=%s]",
				displayText, questionnaireAnswer, section, subsection, group);
	}
	
}
