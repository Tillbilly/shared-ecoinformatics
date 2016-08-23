package au.org.ecoinformatics.rifcs.vocabs;

public enum PhysicalAddressTypeEnum {
	
    STREET_ADDRESS("streetAddress","address where an entity is physically located"),
    POSTAL_ADDRESS("postalAddress","address where mail for an entity should be sent");

	PhysicalAddressTypeEnum(String value, String description){
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
