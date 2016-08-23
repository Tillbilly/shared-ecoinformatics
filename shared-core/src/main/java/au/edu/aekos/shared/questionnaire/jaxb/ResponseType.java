package au.edu.aekos.shared.questionnaire.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared")
@XmlEnum
public enum ResponseType {
	
    TEXT(""),
    TEXT_BOX(""),
    CONTROLLED_VOCAB(""),
    CONTROLLED_VOCAB_SUGGEST(""),
    YES_NO(""),
    DATE(""),
    COORDINATE("Deprecated"),
    GEO_FEATURE_SET(""),
    IMAGE(""),
    BBOX(""),
    MULTISELECT_TEXT(""),
    MULTISELECT_TEXT_BOX(""),
    MULTISELECT_CONTROLLED_VOCAB(""),
    MULTISELECT_CONTROLLED_VOCAB_SUGGEST("Deprecated"), //deprecated
    MULTISELECT_IMAGE(""),
    TREE_SELECT(""),
    SITE_FILE(""),
    SPECIES_LIST(""),
    LICENSE_CONDITIONS(""),
    DOCUMENT(""),
    MULTIPLE_DOCUMENT(""),
    MULTIPLE_QUESTION_GROUP("Not used in xml config, but used to indicate multiple question groups in the domain model");
    
    ResponseType(String description){
    	this.description= description;
    }
    private final String description;
    
    public static ResponseType getRawType(ResponseType rt){
    	if(rt.equals(ResponseType.MULTISELECT_CONTROLLED_VOCAB) || rt.equals(ResponseType.TREE_SELECT)){
    		return ResponseType.CONTROLLED_VOCAB;
    	}else if(rt.equals(ResponseType.MULTISELECT_CONTROLLED_VOCAB_SUGGEST)){
    		return ResponseType.CONTROLLED_VOCAB_SUGGEST;
    	}else if( rt.equals(ResponseType.MULTISELECT_TEXT ) ){
    		return ResponseType.TEXT;
    	}else if( rt.equals(ResponseType.MULTISELECT_IMAGE ) ){
    		return ResponseType.IMAGE;
    	}else if( rt.equals(ResponseType.MULTISELECT_TEXT_BOX ) ){
    		return ResponseType.TEXT_BOX;
    	}else if( rt.equals(ResponseType.MULTIPLE_DOCUMENT ) ){
    		return ResponseType.DOCUMENT;
    	}
    	return rt;
    }
    
    public static boolean getIsMultiselect(ResponseType responseType){
    	if(ResponseType.MULTISELECT_TEXT.equals(responseType) || 
    			ResponseType.MULTISELECT_CONTROLLED_VOCAB.equals(responseType) || 
    			ResponseType.MULTISELECT_CONTROLLED_VOCAB_SUGGEST.equals(responseType) || 
    			ResponseType.MULTISELECT_IMAGE.equals(responseType) ||
    			ResponseType.MULTISELECT_TEXT_BOX.equals(responseType) ||
    			ResponseType.TREE_SELECT.equals(responseType) ||
    			ResponseType.MULTIPLE_DOCUMENT.equals(responseType) ){
    		return true;
    	}
    	return false;
    }

	public String getDescription() {
		return description;
	}
}
