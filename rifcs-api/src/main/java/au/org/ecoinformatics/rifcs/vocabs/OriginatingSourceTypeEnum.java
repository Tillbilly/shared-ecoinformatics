package au.org.ecoinformatics.rifcs.vocabs;

public enum OriginatingSourceTypeEnum {
	
	AUTHORITATIVE("authoritative", " the source holds the authoritative version of the metadata about the registry object");

	OriginatingSourceTypeEnum(String value, String description){
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
