package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared", propOrder={})
public class Question implements Serializable, DisplayConditionSubject, SharedXmlElement {

	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlAttribute(required=true)
	private String id;
	@XmlAttribute
	private Boolean showId = true;
	@XmlAttribute(name="type", required=true)
	private ResponseType responseType;
	@XmlAttribute(name="mandatory")
	private Boolean responseMandatory = false;
	@XmlAttribute(name="trait")
	private String traitName;
	@XmlAttribute(name="isCustom" )
	private Boolean isCustom = false;
	@XmlAttribute(name="auto" )
	private Boolean autocomplete = false;
	@XmlAttribute(name="customVal" ) //I.e. for integer validation on a text response customVal="int"
	private String customValidation;
	@XmlAttribute(name="meta" )
	private String metatag = null;
	@XmlAttribute(name="prepop" )
	private String prepopulateQuestionId = null;
	@XmlAttribute(name="parentSelect" ) //Used for TREE_SELECT response type
	private Boolean parentSelect = Boolean.TRUE;
	@XmlAttribute(name="maxLength" ) 
	private String maxLength = null;
	@XmlAttribute(name="alphaSort" ) 
	private Boolean alphaSort = true;  //Defines whether a custom controlled vocab is alphabetically sorted. True by default. Tree selects and aekos vocabs ignore this value
	@XmlElement(required=true,namespace="http://shared.aekos.org.au/shared")
	private String text;
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String description;
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private Boolean multiselect = false;
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private ConditionalDisplay displayCondition;
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private DefaultVocabulary defaultVocabulary;
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String responseInputClass;
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private SameAsOptionList sameAsOptionList = new SameAsOptionList();
	@XmlTransient
	private SharedXmlElement parent;
	
	public Question(){
		super();
	};
	
	public Question(String id, String text, String description,
			Boolean responseMandatory, ResponseType responseType,
			ConditionalDisplay displayCondition) {
		super();
		this.id = id;
		this.text = text;
		this.description = description;
		this.responseMandatory = responseMandatory;
		this.responseType = responseType;
		this.displayCondition = displayCondition;
	}
	

	@XmlTransient
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlTransient
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@XmlTransient
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlTransient
	public ResponseType getResponseType() {
		return responseType;
	}
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	@XmlTransient
	public Boolean getResponseMandatory() {
		return responseMandatory;
	}
	public void setResponseMandatory(Boolean responseMandatory) {
		this.responseMandatory = responseMandatory;
	}
	@XmlTransient
	public ConditionalDisplay getDisplayCondition() {
		return displayCondition;
	}
	public void setDisplayCondition(ConditionalDisplay displayCondition) {
		this.displayCondition = displayCondition;
	}

	@XmlTransient
	public DefaultVocabulary getDefaultVocabulary() {
		return defaultVocabulary;
	}

	public void setDefaultVocabulary(DefaultVocabulary defaultVocabulary) {
		this.defaultVocabulary = defaultVocabulary;
	}

	@XmlTransient
	public String getTraitName() {
		return traitName;
	}

	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}

	@XmlTransient
	public Boolean getMultiselect() {
		return multiselect;
	}

	public void setMultiselect(Boolean multiselect) {
		this.multiselect = multiselect;
	}
	@XmlTransient
	public String getResponseInputClass() {
		return responseInputClass;
	}

	public void setResponseInputClass(String responseInputClass) {
		this.responseInputClass = responseInputClass;
	}

	@XmlTransient
	public Boolean getIsCustom() {
		return isCustom;
	}

	public void setIsCustom(Boolean isCustom) {
		this.isCustom = isCustom;
	}
    @XmlTransient
	public String getMetatag() {
		return metatag;
	}

	public void setMetatag(String metatag) {
		this.metatag = metatag;
	}
 
	@XmlTransient
	public SameAsOptionList getSameAsOptionList() {
		return sameAsOptionList;
	}

	public void setSameAsOptionList(SameAsOptionList sameAsOptionList) {
		this.sameAsOptionList = sameAsOptionList;
	}

	@XmlTransient
	public String getPrepopulateQuestionId() {
		return prepopulateQuestionId;
	}

	public void setPrepopulateQuestionId(String prepopulateQuestionId) {
		this.prepopulateQuestionId = prepopulateQuestionId;
	}
	@XmlTransient
	public Boolean getShowId() {
		return showId;
	}
	public void setShowId(Boolean showId) {
		this.showId = showId;
	}
	@XmlTransient
	public Boolean getAutocomplete() {
		return autocomplete;
	}

	public void setAutocomplete(Boolean autocomplete) {
		this.autocomplete = autocomplete;
	}
	
	@XmlTransient
	public String getUrlTraitName(){
		if(traitName != null && traitName.length() > 0){
			return traitName.replaceAll(" ", "_");
		}
		return null;
	}

	@XmlTransient
	public String getCustomValidation() {
		return customValidation;
	}

	public void setCustomValidation(String customValidation) {
		this.customValidation = customValidation;
	}

	@XmlTransient
	public Boolean getParentSelect() {
		return parentSelect;
	}

	public void setParentSelect(Boolean parentSelect) {
		this.parentSelect = parentSelect;
	}

        public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.parent = (SharedXmlElement) parent;
	}
	
	@Override
	public SharedXmlElement getParent() {
		return parent;
	}

	@Override
	public SharedXmlElementType getType() {
		return SharedXmlElementType.QUESTION;
	}

	@Override
	public <T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz) {
		return SharedXmlElementSupport.findAncestorOfType(clazz, parent);
	}
	
	@XmlTransient
	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
    @XmlTransient
	public Boolean getAlphaSort() {
		return alphaSort;
	}

	public void setAlphaSort(Boolean alphaSort) {
		this.alphaSort = alphaSort;
	}

}
