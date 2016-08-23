package au.org.ecoinformatics.rifcs.vocabs;

public enum CollectionRelationTypeEnum {

    DESCRIBES("describes","is a catalogue for, or index of, of items in the related collection"),
    HAS_PART("hasPart","contains the related collection"),
    HAS_ASSOCIATION_WITH("hasAssociationWith","has an unspecified relationship with the related registry object"),
    HAS_COLLECTOR("hasCollector","has been aggregated by the related party"),
    HAS_PRINCIPAL_INVESTIGATOR("hasPrincipalInvestigator","is researched by the related party"),
    IS_DESCRIBED_BY("isDescribedBy","is catalogued or indexed by the related collection"),
    IS_LOCATED_IN("isLocatedIn","is held in the related repository"),
    IS_LOCATION_FOR("isLocationFor","is the repository where the related collection is held"),
    IS_MANAGED_BY("isManagedBy","is maintained and made accessible by the related party"),
    IS_OUTPUT_OF("isOutputOf","is a product of the related activity"),
    IS_OWNED_BY("isOwnedBy","legally belongs to the related party"),
    IS_PART_OF("isPartOf","is contained within the related collection"),
    SUPPORTS("supports","can be contributed to, accessed or used through the related service"),
    IS_ENRICHED_BY("isEnrichedBy","additional value provided to a collection by a party"),
    IS_DERIVED_FROM("isDerivedFrom","collection is derived from the related collection, e.g. through analysis"),
    HAS_DERIVED_COLLECTION("hasDerivedCollection","the related collection is derived from the collection, e.g. through analysis"),
    IS_AVAILABLE_THROUGH("isAvailableThrough","specialisation of isSupportBy type - for Harvest, Search and Syndicate"),
    IS_PRODUCED_BY("isProducedBy","specialisation of isSupportBy type - for Create, Generate and Assemble"),
    IS_PRESENTED_BY("isPresentedBy","specialisation of isSupportBy type - for Report"),
    HAS_VALUE_ADDED_BY("hasValueAddedBy","specialisation of supports type - for Annotate"),
    IS_OPERATED_ON_BY("isOperatedOnBy","specialisation of isSupportBy type - for Transform");

	CollectionRelationTypeEnum(String value, String description){
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
	
	public static CollectionRelationTypeEnum getCollectionRelationTypeEnumFromValue(String value){
		for(CollectionRelationTypeEnum relationTypeEnum : CollectionRelationTypeEnum.values() ){
			if(value.equalsIgnoreCase(relationTypeEnum.getValue())){
				return relationTypeEnum;
			}
		}
		return null;
	}

}
