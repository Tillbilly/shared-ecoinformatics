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
public class QuestionGroup  implements Serializable, DisplayConditionSubject, SharedXmlElement {
	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlAttribute(required=true)
	private String id;
	@XmlAttribute
	private Boolean showId = true;
	@XmlAttribute
    private Boolean reusableGroup = Boolean.FALSE;
	@XmlAttribute
	private Boolean clearButton = Boolean.FALSE;
	@XmlAttribute
    private String reusableGroupTitleQuestionId;

	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String groupTitle;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String groupDescription;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private Items items = new Items();
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private ConditionalDisplay displayCondition;

	@XmlTransient
	private SharedXmlElement parent;
	
	public QuestionGroup(String id, String groupTitle, String groupDescription) {
		super();
		this.id = id;
		this.groupTitle = groupTitle;
		this.groupDescription = groupDescription;
	}
	
	public QuestionGroup() {
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
	public String getGroupTitle() {
		return groupTitle;
	}
	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}
	@XmlTransient
	public String getGroupDescription() {
		return groupDescription;
	}
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	
	@XmlTransient
	public Boolean getReusableGroup() {
		return reusableGroup;
	}

	public void setReusableGroup(Boolean reusableGroup) {
		this.reusableGroup = reusableGroup;
	}

	@XmlTransient
	public String getReusableGroupTitleQuestionId() {
		return reusableGroupTitleQuestionId;
	}

	public void setReusableGroupTitleQuestionId(String reusableGroupTitleQuestionId) {
		this.reusableGroupTitleQuestionId = reusableGroupTitleQuestionId;
	}
	
	@XmlTransient
	public List<String> getAllGroupChildQuestionIds(){
		List<String> questionIdList = new ArrayList<String>();
		for(Object obj : getElements()){
			if(obj instanceof Question){
				Question q = (Question) obj;
				questionIdList.add(q.getId());
			}else if(obj instanceof QuestionGroup){
				QuestionGroup childGroup = (QuestionGroup) obj;
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
			}else if(obj instanceof QuestionGroup){
				QuestionGroup childGroup = (QuestionGroup) obj;
				Question q = childGroup.getChildQuestionById(questionId);
				if(q != null){
					return q;
				}
			}
		}
		return null;
	}

	@XmlTransient
	public Boolean getShowId() {
		return showId;
	}

	public void setShowId(Boolean showId) {
		this.showId = showId;
	}
    @XmlTransient
	public Boolean getClearButton() {
		return clearButton;
	}

	public void setClearButton(Boolean clearButton) {
		this.clearButton = clearButton;
	}

	/**
	 * @param id	The Question ID of the {@link MultipleQuestionGroup} to find.
	 * @return		The {@link MultipleQuestionGroup} if it exists, null otherwise.
	 */
	public MultipleQuestionGroup getChildMultipleQuestionGroupById(String id) {
		for (Object obj : getElements()) {
			if (!(obj instanceof MultipleQuestionGroup)) {
				continue;
			}
			MultipleQuestionGroup mqg = (MultipleQuestionGroup) obj;
			if (id.equalsIgnoreCase(mqg.getId())) {
				return mqg;
			}
		}
		return null;
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
		return SharedXmlElementType.QUESTION_GROUP;
	}

	@Override
	public <T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz) {
		return SharedXmlElementSupport.findAncestorOfType(clazz, parent);
	}
}
