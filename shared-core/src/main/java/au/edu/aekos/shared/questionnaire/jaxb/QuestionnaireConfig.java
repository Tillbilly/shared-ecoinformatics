package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="questionnaire",namespace="http://shared.aekos.org.au/shared")
@XmlType(name="questionnaire",namespace="http://shared.aekos.org.au/shared",propOrder={})
public class QuestionnaireConfig  implements Serializable, SharedXmlElement{
	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlElement(required=true, namespace="http://shared.aekos.org.au/shared")
	private String version;
	
	@XmlElement(required=true, namespace="http://shared.aekos.org.au/shared")
	private String title;
	
	@XmlElement(required=true, namespace="http://shared.aekos.org.au/shared")
	private String subtitle;
	
	@XmlElement(required=true,namespace="http://shared.aekos.org.au/shared")
	private String introduction;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String submissionTitleQuestionId;
	
	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private String firstPageTitle;

	@XmlElement(namespace="http://shared.aekos.org.au/shared")
	private Items items;
	
	@XmlTransient 
	private Long smsQuestionnaireId;

	@XmlTransient
	private Map<String,String > metatagToQuestionIdMap = null;
	
	@XmlTransient
	private Map<String,String > questionIdToMetatagMap = null;
	
	@XmlTransient
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	@XmlTransient
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@XmlTransient
	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	@XmlTransient
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	@XmlTransient
	public Items getItems() {
		return items;
	}

	public void setItems(Items items) {
		this.items = items;
	}
	@XmlTransient
	public String getSubmissionTitleQuestionId() {
		return submissionTitleQuestionId;
	}

	public void setSubmissionTitleQuestionId(String submissionTitleQuestionId) {
		this.submissionTitleQuestionId = submissionTitleQuestionId;
	}

	@XmlTransient
	public Long getSmsQuestionnaireId() {
		return smsQuestionnaireId;
	}

	public void setSmsQuestionnaireId(Long smsQuestionnaireId) {
		this.smsQuestionnaireId = smsQuestionnaireId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((introduction == null) ? 0 : introduction.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime
				* result
				+ ((smsQuestionnaireId == null) ? 0 : smsQuestionnaireId
						.hashCode());
		result = prime
				* result
				+ ((submissionTitleQuestionId == null) ? 0
						: submissionTitleQuestionId.hashCode());
		result = prime * result
				+ ((subtitle == null) ? 0 : subtitle.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuestionnaireConfig other = (QuestionnaireConfig) obj;
		if (introduction == null) {
			if (other.introduction != null)
				return false;
		} else if (!introduction.equals(other.introduction))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (smsQuestionnaireId == null) {
			if (other.smsQuestionnaireId != null)
				return false;
		} else if (!smsQuestionnaireId.equals(other.smsQuestionnaireId))
			return false;
		if (submissionTitleQuestionId == null) {
			if (other.submissionTitleQuestionId != null)
				return false;
		} else if (!submissionTitleQuestionId
				.equals(other.submissionTitleQuestionId))
			return false;
		if (subtitle == null) {
			if (other.subtitle != null)
				return false;
		} else if (!subtitle.equals(other.subtitle))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	@XmlTransient
	public Map<String, QuestionGroup> getQuestionGroupMap(){
		Map<String, QuestionGroup> questionGroupMap = new HashMap<String,QuestionGroup>();
		for(Object obj : items.getEntryList() ){
			if(obj instanceof QuestionGroup ){
				QuestionGroup group = (QuestionGroup) obj;
				questionGroupMap.put(group.getId(), group);
				buildSubQuestionGroupMap(group, questionGroupMap);
			}
		}
		return questionGroupMap;
	}
	
	private void buildSubQuestionGroupMap(QuestionGroup group, Map<String, QuestionGroup> questionGroupMap){
		for(Object obj : group.getItems().getEntryList() ){
			if(obj instanceof QuestionGroup ){
				QuestionGroup subgroup = (QuestionGroup) obj;
				questionGroupMap.put(subgroup.getId(), subgroup);
				buildSubQuestionGroupMap(subgroup, questionGroupMap);
			}
		}
	}
	//This only works when multipleQuestionGroups are'nt contained in a question group
	@Deprecated
	protected MultipleQuestionGroup getMultipleQuestionGroupById(String id){
		for(Object obj : items.getEntryList() ){
			if(obj instanceof QuestionGroup ){
				
			}else if(obj instanceof MultipleQuestionGroup){
				MultipleQuestionGroup mqg = ( MultipleQuestionGroup) obj;
				if(id.equals(mqg.getId())){
					return mqg;
				}
			}
		}
		return null;
	}
	/**
	 * 
	 * @param includeMultiples Whether to include questions from multipleQuestionGroups
	 * @return
	 */  
	public List<Question> getAllQuestions(boolean includeMultiples){
		List<Question> questionList = new ArrayList<Question>();
		for(Object obj : items.getEntryList() ){
           handleItem(obj, questionList, includeMultiples);
		}
		return questionList;
	}
	
	private List<Question> getQuestionsFromMultipleQuestionGroup(MultipleQuestionGroup mqg){
		List<Question> questionList = new ArrayList<Question>();
		for(Object o : mqg.getItems().getEntryList()){
			if(o instanceof Question){
				questionList.add((Question) o);
			}
		}
		return questionList;
	}
	
	private List<Question> getQuestionsFromQuestionGroup(QuestionGroup qg, boolean includeMultiples){
		List<Question> questionList = new ArrayList<Question>();
		for(Object o : qg.getItems().getEntryList()){
			handleItem(o,questionList,includeMultiples);
		}
		return questionList;
	}
	
	private void handleItem(Object item, List<Question> questionList, boolean includeMultiples){
		if(item instanceof Question){
        	questionList.add((Question) item );
        }else if(item instanceof QuestionGroup){
        	questionList.addAll( getQuestionsFromQuestionGroup((QuestionGroup) item, includeMultiples));
        }else if( item instanceof MultipleQuestionGroup && includeMultiples ){
        	questionList.addAll( getQuestionsFromMultipleQuestionGroup((MultipleQuestionGroup) item ));
        }
	}
	
	@XmlTransient
	public Map<String,String > getMetatagToQuestionIdMap(){
		if(metatagToQuestionIdMap != null && metatagToQuestionIdMap.size() > 0){
			return metatagToQuestionIdMap;
		}
		metatagToQuestionIdMap = new HashMap<String, String>();
		for(Question q : getAllQuestions(true) ){
			if( q.getMetatag() != null && ! q.getMetatag().isEmpty() ){
				metatagToQuestionIdMap.put(q.getMetatag(), q.getId());
			}
		}
		return metatagToQuestionIdMap;
	}

	@XmlTransient
	public Map<String,String > getQuestionIdToMetatagMap(){
		if(questionIdToMetatagMap != null && questionIdToMetatagMap.size() > 0){
			return questionIdToMetatagMap;
		}
		questionIdToMetatagMap = new HashMap<String, String>();
		for(Question q : getAllQuestions(true) ){
			if( q.getMetatag() != null && ! q.getMetatag().isEmpty() ){
				questionIdToMetatagMap.put(q.getId(), q.getMetatag());
			}
		}
		return questionIdToMetatagMap;
	}
	
	//MQG might be inside ordinary question groups, need to iterate recursively over all items inside QuestionGroups
	@XmlTransient  
	public List<MultipleQuestionGroup> getAllMultipleQuestionGroups(){
		List<MultipleQuestionGroup> multipleQuestionGroupList = new ArrayList<MultipleQuestionGroup>();
		for(Object item : items.getEntryList()){
			if(item instanceof QuestionGroup){
				retrieveMultipleQuestionGroupsFromQuestionGroupItems((QuestionGroup) item, multipleQuestionGroupList);
			}else if(item instanceof MultipleQuestionGroup){
				multipleQuestionGroupList.add((MultipleQuestionGroup) item);
			}
		}
		return multipleQuestionGroupList;
	}
	
	private void retrieveMultipleQuestionGroupsFromQuestionGroupItems(QuestionGroup qg, List<MultipleQuestionGroup> mqgList){
		for(Object item : qg.getItems().getEntryList()){
			if(item instanceof QuestionGroup){
				retrieveMultipleQuestionGroupsFromQuestionGroupItems((QuestionGroup) item, mqgList);
			}else if(item instanceof MultipleQuestionGroup){
				mqgList.add((MultipleQuestionGroup) item);
			}
		}
	}
	
	
	@Override
	public SharedXmlElement getParent() {
		return null;
	}

	@Override
	public SharedXmlElementType getType() {
		return SharedXmlElementType.QUESTIONNAIRE_CONFIG;
	}

	@Override
	public <T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz) {
		return null;
	}

	/**
	 * Finds a question that has the supplied ID
	 * 
	 * @param id	ID of the question to search for
	 * @return		The found question, otherwise null
	 */
	public Question getQuestionById(String id) {
		List<Question> questions = getAllQuestions(true);
		for (Question question : questions) {
			if (id.equalsIgnoreCase(question.getId())) {
				return question;
			}
		}
		return null;
	}

	@XmlTransient
	public String getFirstPageTitle() {
		return firstPageTitle;
	}

	public void setFirstPageTitle(String firstPageTitle) {
		this.firstPageTitle = firstPageTitle;
	}

	/**
	 * Finds the closest ancestor of type <code>ancestorQuestionClass</code>
	 * 
	 * @param descendantQuestionId		ID of the question to start searching from
	 * @param ancestorQuestionClass		Type of the ancestor to find
	 * @return							The ancestor if found, null otherwise
	 */
	public <T extends SharedXmlElement> T getQuestionAncestor(String descendantQuestionId, Class<T> ancestorQuestionClass) {
		if (descendantQuestionId == null) {
			return null;
		}
		Question descendantQuestion = getQuestionById(descendantQuestionId);
		if (descendantQuestion == null) {
			return null;
		}
		return descendantQuestion.findAncestorOfType(ancestorQuestionClass);
	}

	public String getMetatagByQuestionId(String questionId) {
		return getQuestionIdToMetatagMap().get(questionId);
	}
}
