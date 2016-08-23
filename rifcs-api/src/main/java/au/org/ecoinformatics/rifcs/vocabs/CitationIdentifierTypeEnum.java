package au.org.ecoinformatics.rifcs.vocabs;

public enum CitationIdentifierTypeEnum {
	
    ARK("ark","ARK Persistent Identifier Scheme"),
    DOI("doi","Digital Object Identifier"),
    EAN13("ean13","International Article Number"),
    EISSN("eissn","electronic International Standard Serial Number"),
    HANDLE("handle","HANDLE System Identifier"),
    INFOURI("infouri","'info' URI scheme"),
    ISBN("isbn","International Standard Book Number"),
    ISSN("issn","International Standard Serial Number"),
    ISTC("istc","International Standard Text Code. http://www.istc-international.org/html"),
    LISSN("lissn",""),
    LOCAL("local","identifer unique within a local context"),
    PURL("purl","Persistent Uniform Resource Locator"),
    UPC("upc","Universal Product Code"),
    URI("uri","Uniform Resource Identifier"),
    URN("urn","Uniform Resource Name");
	
	CitationIdentifierTypeEnum(String value, String description){
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
