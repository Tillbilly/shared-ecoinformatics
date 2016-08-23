package au.org.ecoinformatics.rifcs.vocabs;

public enum DateFormatEnum {
	
	W3CDTF("W3CDTF","W3C Date Time Format");
	
	DateFormatEnum(String value, String description){
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
