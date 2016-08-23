package au.org.ecoinformatics.rifcs.vocabs;

public enum CitationStyleEnum {

    HARVARD("Harvard",""),
    APA("APA","American Psychological Association"),
    MLA("MLA","Modern Language Association of America"),
    VANCOUVER("Vancouver",""),
    IEEE("IEEE","Institute of Electrical and Electronic Engineers"),
    CSE("CSE","Council of Science Editors"),
    CHICAGO("Chicago","Chicago Manual of Style"),
    AMA("AMA","American Medical Association"),
    AGPS_AGIMO("AGPS-AGIMO","Australian Style Manual"),
    AGLC("AGLC","Australian Guide to Legal Citation"),
    ACS("ACS","American Chemical Society"),
    DATACITE("Datacite","International Data Citation");
	
	CitationStyleEnum(String value, String description){
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
