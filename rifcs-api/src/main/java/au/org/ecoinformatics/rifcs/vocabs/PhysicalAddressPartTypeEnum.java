package au.org.ecoinformatics.rifcs.vocabs;

public enum PhysicalAddressPartTypeEnum {

    ADDRESS_LINE("addressLine","an address part that is a separate line of a structured address"),
    TEXT("text","a single address part that contains the whole address in unstructured form"),
    TELEPHONE_NUMBER("telephoneNumber","an address part that contains a telephone number including a mobile telephone number"),
    FAX_NUMBER("faxNumber","an address part that contains a fax (facsimile) number");

	PhysicalAddressPartTypeEnum(String value, String description){
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
