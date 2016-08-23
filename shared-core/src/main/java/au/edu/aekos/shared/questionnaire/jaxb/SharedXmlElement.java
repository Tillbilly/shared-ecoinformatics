package au.edu.aekos.shared.questionnaire.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
@XmlType(namespace="http://shared.aekos.org.au/shared")
public interface SharedXmlElement {
	
	@XmlType(namespace="http://shared.aekos.org.au/shared")
	@XmlEnum
	public enum SharedXmlElementType {
		QUESTION,
		QUESTION_GROUP,
		MULTI_QUESTION_GROUP,
		ITEMS,
		QUESTIONNAIRE_CONFIG,
	}
	
	SharedXmlElement getParent();
	
	SharedXmlElementType getType();
	
	<T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz);
}
