package au.org.ecoinformatics.rifcs.vocabs;

public enum RelatedInformationIdentifierTypeEnum {

    ABN("abn","Australian Business Number"),
    ARC("arc","Australian Research Council identifier"),
    ARK("ark","ARK Persistent Identifier Scheme"),
    AU_ANL_PEAU("AU-ANL:PEAU","National Library of Australia identifier"),
    DOI("doi","Digital Object Identifier"),
    EAN13("ean13","International Article Number"),
    EISSN("eissn","electronic International Standard Serial Number"),
    HANDLE("handle","HANDLE System Identifier"),
    INFOURI("infouri","'info' URI scheme"),
    ISBN("isbn","International Standard Book Number"),
    ISIL("isil","International Standard Identifier for Libraries"),
    ISSN("issn","International Standard Serial Number"),
    ISTC("istc","International Standard Text Code.http://www.istc-international.org/html"),
    LISSN("lissn",""),
    LOCAL("local","identifer unique within a local context"),
    MEDIA_TYPE("mediaType","The Media Type (MIME type) of the information. Values should be taken from IANA Media Type assignment listing. You may choose to use application/x-{name} if it is well known within the relevant discipline."),
    NHMRC("nhmrc","National Health and Medical Research Council identifier"),
    ORCID("orcid","ORCID Identifier"),
    PURL("purl","Persistent Uniform Resource Locator"),
    RESEARCHER_ID("researcherID","Thomson Reuters ResearcherID"),
    UPC("upc","Universal Product Code"),
    URI("uri","Uniform Resource Identifier"),
    URN("urn","Uniform Resource Name");

	RelatedInformationIdentifierTypeEnum(String value, String description){
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
