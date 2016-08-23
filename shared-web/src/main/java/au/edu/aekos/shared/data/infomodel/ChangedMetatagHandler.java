package au.edu.aekos.shared.data.infomodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Metatags may change their name, or represent different concepts as the questionnaire evolves.
 * This class handles the substitution and handling of a metatag not present in older submissions
 * @author btill
 */
public class ChangedMetatagHandler {
	enum DefaultDateStrategy {
		CURRENT,
		DEFAULT_VALUE
	}
    //The current metatag
	private String metatag;
	//List of previous metatags that can be used in place of the current metatag for indexing/rifcs/reporting
	private List<String> alternateMetatagList = new ArrayList<String>();
	//If ignore is true, validation will pass if the metatag is not present.
	private Boolean ignore = Boolean.FALSE;
	//If an alternateMetatag is not present, indicates whether to use a default value
	private Boolean useDefaultValue = Boolean.FALSE;
	//The strategy used to handle a Date default value ( see inner enum for options )
	private DefaultDateStrategy defaultDateStrategy = null;
	
	private String defaultDateString;
	
	private String defaultDateFormatString = "dd/MM/yyyy";
	
	private String defaultTextValue = "Not Collected";
	
	public String getMetatag() {
		return metatag;
	}
	public void setMetatag(String metatag) {
		this.metatag = metatag;
	}
	public List<String> getAlternateMetatagList() {
		return alternateMetatagList;
	}
	public void setAlternateMetatagList(List<String> alternateMetatagList) {
		this.alternateMetatagList = alternateMetatagList;
	}
	public Boolean getIgnore() {
		return ignore;
	}
	public void setIgnore(Boolean ignore) {
		this.ignore = ignore;
	}
	
	public Boolean getUseDefaultValue(){
		return useDefaultValue;
	}
	
	public void setUseDefaultValue(Boolean useDefaultValue) {
		this.useDefaultValue = useDefaultValue;
	}
	
	public DefaultDateStrategy getDefaultDateStrategy() {
		return defaultDateStrategy;
	}
	public void setDefaultDateStrategy(DefaultDateStrategy defaultDateStrategy) {
		this.defaultDateStrategy = defaultDateStrategy;
	}
	public String getDefaultDateString() {
		return defaultDateString;
	}
	public void setDefaultDateString(String defaultDateString) {
		this.defaultDateString = defaultDateString;
	}
	public String getDefaultDateFormatString() {
		return defaultDateFormatString;
	}
	public void setDefaultDateFormatString(String defaultDateFormatString) {
		this.defaultDateFormatString = defaultDateFormatString;
	}
	public String getDefaultTextValue() {
		return defaultTextValue;
	}
	public void setDefaultTextValue(String defaultTextValue) {
		this.defaultTextValue = defaultTextValue;
	}
	
	public String getDefaultValue(){
		if(defaultDateStrategy != null){ //Return a Date String
			SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormatString);
			if(DefaultDateStrategy.CURRENT.equals(defaultDateStrategy)){
				return sdf.format(new Date());
			}else{
				return defaultDateString;
			}
		}else{
			return defaultTextValue;
		}
		
	}
	
}
