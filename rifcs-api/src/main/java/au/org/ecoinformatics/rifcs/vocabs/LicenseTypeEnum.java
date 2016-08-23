package au.org.ecoinformatics.rifcs.vocabs;

public enum LicenseTypeEnum {

    CC_BY("CC-BY","Attribution"),
    CC_BY_SA("CC-BY-SA","Attribution-SharedAlike"),
    CC_BY_ND("CC-BY-ND","Attribution-NoDervis"),
    CC_BY_NC("CC-BY-NC","Attribution-NonCommercial"),
    CC_BY_NC_SA("CC-BY-NC-SA","Attribution-NonCommercial-SharedAlike"),
    CC_BY_NC_ND("CC-BY-NC-ND","Attribution-NonCommercial-NoDervis"),
    GPL("GPL","General Public License"),
    AUS_GOAL_RESTRICTIVE("AusGoalRestrictive","AusGoal Restrictive License"),
    NO_LICENSE("NoLicence","No licence"),
    UNKNOWN_OTHER("Unknown/Other","Unknown license or record provider defined license");
	
	LicenseTypeEnum(String value, String description){
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
