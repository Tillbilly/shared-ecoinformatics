package au.edu.aekos.shared.questionnaire.jaxb;

import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared")
class SharedXmlElementSupport {
	
	/**
	 * Finds an ancestor of the specified type. Designed as a delegate for implementors of {@link SharedXmlElement}
	 * 
	 * @param clazz		The type of ancestor to find
	 * @param parent	The parent to start searching from
	 * @return			The found ancestor, otherwise null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz, SharedXmlElement parent) {
		SharedXmlElement currentParent = parent;
		while (currentParent != null) {
			if (currentParent.getClass().isAssignableFrom(clazz)) {
				return (T) currentParent;
			}
			currentParent = currentParent.getParent();
		}
		return null;
	}
}
