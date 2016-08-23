package au.org.ecoinformatics.rifcs.vocabs;

public enum DateTypeEnum {
    DATE_FROM("dateFrom","start date for a temporal coverage period"),
    DATE_TO("dateTo","end date for a temporal coverage period");
	
	DateTypeEnum(String value, String description){
		this.value = value;
		this.description = description;
	}

	public final String value;
	public final String description;
	
	public String getValue(){
		return value;
	}
	public String getDescription(){
		return description;
	}

}
