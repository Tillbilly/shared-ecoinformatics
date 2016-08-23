package au.org.ecoinformatics.rifcs.vocabs;

public enum RelatedInformationTypeEnum {
    ACTIVITY("activity","An undertaking or process related to the creation, update, or maintenance of a collection."),
    COLLECTION("collection","An aggregation of physical and/or digital resources which has meaning in a research context."),
    DATA_QUALITY_INFORMATION("dataQualityInformation","data quality statements or summaries of data quality issues affecting the data."),
    METADATA("metadata","An alternative metadata format for the Object. This is most likely to be a discipline or system-specific format. E.g. NetCDF or ANZLIC."),
    PARTY("party","A person, group or role related to the creation, update, or maintenance of a collection, to an activity, or to the provision of a service."),
    PUBLICATION("publication","any formally published document, whether available in digital or online form or not."),
    REUSE_INFORMATION("reuseInformation","information that supports reuse of data, such as data definitions, instrument calibration or settings, units of measurement, sample descriptions, experimental parameters, methodology, data analysis techniques, or data derivation rules."),
    SERVICE("service","A system (analogue or digital) that provides one or more functions of value to an end user."),
    WEBSITE("website","any publicly accessible web location containing information related to the collection, activity, party or service.");
	RelatedInformationTypeEnum(String value, String description){
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
