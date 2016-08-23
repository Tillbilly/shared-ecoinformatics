package au.org.ecoinformatics.rifcs.vocabs;

public enum NameTypeEnum {

    PRIMARY("primary","official name of the registry object"),
    ABBREVIATED("abbreviated","shortened form of, or acronym for, the official name"),
    ALTERNATIVE("alternative","any other form of name used now or in the past as a substitute or alternative for the official name");

	NameTypeEnum(String value, String description){
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
