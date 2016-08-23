package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared", propOrder={})
public class MultipleQuestionGroup  implements Serializable, DisplayConditionSubject, SharedXmlElement {
	@XmlTransient
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required=true)
	private String id;
	
	@XmlAttribute
	private Boolean showId = true;
	
	@XmlAttribute(name="mandatory")
	private Boolean responseMandatory = Boolean.FALSE;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String text;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String textDetails;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String description;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private Items items = new Items();
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private ConditionalDisplay displayCondition;

	@XmlTransient
	private SharedXmlElement parent;
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.parent = (SharedXmlElement) parent;
	}
	
	public SharedXmlElement getParent() {
		return parent;
	}

	public MultipleQuestionGroup(String id, String text, String description) {
		super();
		this.id = id;
		this.text = text;
		this.description = description;
	}
	
	public MultipleQuestionGroup() {
		super();
	}
	
	@XmlTransient
	public Items getItems() {
		return items;
	}
	public void setItems(Items items) {
		this.items = items;
	}
	
	@SuppressWarnings("rawtypes")
	@XmlTransient
	public List getElements() {
		if(items != null && items.getEntryList() != null){
			return items.getEntryList();
		}
		return new ArrayList();
	}
	
	@XmlTransient
	public ConditionalDisplay getDisplayCondition() {
		return displayCondition;
	}

	public void setDisplayCondition(ConditionalDisplay displayCondition) {
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
	public List<String> getAllGroupChildQuestionIds(){
		List<String> questionIdList = new ArrayList<String>();
		for(Object obj : getElements()){
			if(obj instanceof Question){
				Question q = (Question) obj;
				questionIdList.add(q.getId());
			}else if(obj instanceof MultipleQuestionGroup){
				MultipleQuestionGroup childGroup = (MultipleQuestionGroup) obj;
				questionIdList.addAll(childGroup.getAllGroupChildQuestionIds());
			}
		}
		return questionIdList;
	}
	
	//@XmlTransient
	public Question getChildQuestionById(String questionId){
		for(Object obj : getElements()){
			if(obj instanceof Question){
				Question q = (Question) obj;
				if(q.getId().equals(questionId)){
					return q;
				}
			}else if(obj instanceof MultipleQuestionGroup){
				MultipleQuestionGroup childGroup = (MultipleQuestionGroup) obj;
				Question q = childGroup.getChildQuestionById(questionId);
				if(q != null){
					return q;
				}
			}
		}
		return null;
	}

	public boolean groupContainsMetatag(String metatag){
		for(Object obj : getElements()){
			if(obj instanceof Question){
				Question q = (Question) obj;
				if(metatag.equalsIgnoreCase(q.getMetatag())){
					return true;
				}
			}else if(obj instanceof MultipleQuestionGroup){
				MultipleQuestionGroup childGroup = (MultipleQuestionGroup) obj;
				if(childGroup.groupContainsMetatag(metatag)){
					return true;
				}
			}
		}
		return false;
	}
	
	@XmlTransient
	public Boolean getResponseMandatory() {
		return responseMandatory;
	}

	public void setResponseMandatory(Boolean responseMandatory) {
		this.responseMandatory = responseMandatory;
	}
    @XmlTransient
	public Boolean getShowId() {
		return showId;
	}

	public void setShowId(Boolean showId) {
		this.showId = showId;
	}

	@XmlTransient
	public String getTextDetails() {
		return textDetails;
	}

	public void setTextDetails(String textDetails) {
		this.textDetails = textDetails;
	}

	public int size() {
		if (items == null || items.getEntryList() == null) {
			return 0;
		}
		return items.getEntryList().size();
	}

	@Override
	public SharedXmlElementType getType() {
		return SharedXmlElementType.MULTI_QUESTION_GROUP;
	}

	@Override
	public <T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz) {
		return SharedXmlElementSupport.findAncestorOfType(clazz, parent);
	}
}
