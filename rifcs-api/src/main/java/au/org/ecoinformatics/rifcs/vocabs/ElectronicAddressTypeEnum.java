package au.org.ecoinformatics.rifcs.vocabs;

public enum ElectronicAddressTypeEnum {

    EMAIL("email","string used to receive messages by means of a computer network"),
    OTHER("other","other electronic address"),
    URL("url","Uniform Resource Locator"),
    WSDL("wsdl","(service only) Web Service Definition Language");

	ElectronicAddressTypeEnum(String value, String description){
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
