package au.org.ecoinformatics.rifcs.vocabs;

public enum CitationDateTypeEnum {

    PUBLICATION_DATE("publicationDate","the date when the data was or will be made publicly available"),
    AVAILABLE("available","the date the resource is made publicly available. May be a range, or indicate the end of an embargo period"),
    CREATED("created","the date the resource itself was put together; this could be a date range or a single date for a final component, e.g. the finalised file with all of the data"),
    DATE("date","any relevant date not otherwise specified"),
    DATE_ACCEPTED("dateAccepted","the date that the publisher accepted the resource into their system"),
    DATE_SUBMITTED("dateSubmitted","the date the creator submits the resource to the publisher. This could be different from dateAccepted if the publisher then applies a selection process"),
    END_PUBLICATION_DATE("endPublicationDate","use when publicationDate is a range"),
    ISSUED("issued","the date that the resource is published or distributed e.g. to a data centre"),
    MODIFIED("modified","the date of the last update to the resource, when the resource is being added to. (Equivalent to Datacite 'Updated')"),
    START_PUBLICATION_DATE("startPublicationDate","use when publicationDate is a range"),
    VALID("valid","the date or date range during which the dataset or resources are accurate");
	
	CitationDateTypeEnum(String value, String description){
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
