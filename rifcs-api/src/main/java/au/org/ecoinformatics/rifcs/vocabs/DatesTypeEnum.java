package au.org.ecoinformatics.rifcs.vocabs;

public enum DatesTypeEnum {

    AVAILABLE("dc.available"," Date (often a range) that the resource became or will become available."),
    CREATED("dc.created"," Date of creation of the resource."),
    DATE_ACCEPTED("dc.dateAccepted"," Date of acceptance of the resource."),
    DATE_SUBMITTED("dc.dateSubmitted"," Date of submission of the resource."),
    ISSUED("dc.issued"," Date of formal issuance (e.g.publication) of the resource."),
    VALID("dc.valid"," Date (often a range) of validity of a resource.");

    DatesTypeEnum(String value, String description){
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
