package au.org.ecoinformatics.rifcs.vocabs;

public enum DescriptionTypeEnum {

    BRIEF("brief"," short account for selection purposes"),
    FULL("full"," full account"),
    LOGO("logo"," symbol used as an identifying mark"),
    NOTE("note"," a brief informational message, not object metadata, to notify the record consumer of some important aspect regarding the object or its metadata"),
    DELIVERY_METHOD("deliverymethod"," (services only) information about how the service is delivered. Should be one of webservice, software, offline, workflow"),
    SIGNIFICANCE_STATEMENT("significanceStatement"," (collections only) a statement describing the significance of a collection within its domain or context"),
    RESEARCH_AREAS("researchAreas"," Text describing a contributor organisation's distinctive research portfolio and research strengths."),
    RESEARCH_DATA_PROFILE("researchDataProfile"," Text describing and highlighting the research data (and related parties, projects and services) whose description the organisation has contributed to Research Data Australia."),
    RESEARCH_SUPPORT("researchSupport"," Text describing specific data-related support services offered by the contributor organisation such as archives, repositories, data centres, metadata stores, high performance computing facilities, data-intensive instruments, e-research support centres, data management support services, etc."),
    LINEAGE("lineage"," Text describing the collection lineage");
	
	DescriptionTypeEnum(String value, String description){
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
