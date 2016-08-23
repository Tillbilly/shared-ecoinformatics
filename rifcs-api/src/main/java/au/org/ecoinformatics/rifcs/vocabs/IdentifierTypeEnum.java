package au.org.ecoinformatics.rifcs.vocabs;

public enum IdentifierTypeEnum {
	
    ABN("abn","Australian Business Number"),
    ARC("arc","Australian Research Council identifier"),
    ARK("ark","ARK Persistent Identifier Scheme"),
    AU_ANL_PEAU("AU-ANL:PEAU","National Library of Australia identifier"),
    DOI("doi","Digital Object Identifier"),
    HANDLE("handle","HANDLE System Identifier"),
    INFOURI("infouri","'info' URI scheme"),
    ISIL("isil","International Standard Identifier for Libraries"),
    LOCAL("local","identifer unique within a local context"),
    NHMRC("nhmrc","National Health and Medical Research Council identifier"),
    ORCID("orcid","ORCID Identifier"),
    PURL("purl","Persistent Uniform Resource Locator"),
    URI("uri","Uniform Resource Identifier");

	IdentifierTypeEnum(String value, String description){
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
