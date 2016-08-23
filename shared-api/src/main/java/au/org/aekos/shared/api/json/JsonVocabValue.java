package au.org.aekos.shared.api.json;

public class JsonVocabValue {

	private String value;
	private String displayValue; //Only used for SHaRED values
	private String parent; //Only used for shared values
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDisplayValue() {
		return displayValue;
	}
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
}
