package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Items", namespace="http://shared.aekos.org.au/shared")
public class Items  implements Serializable, SharedXmlElement {
	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlElements({
		@XmlElement(name = "question", type = Question.class, namespace="http://shared.aekos.org.au/shared"),
		@XmlElement(name = "questionGroup", type = QuestionGroup.class,namespace="http://shared.aekos.org.au/shared"),
		@XmlElement(name = "pageBreak", type = PageBreak.class,namespace="http://shared.aekos.org.au/shared" ),
		@XmlElement(name = "multipleQuestionGroup", type = MultipleQuestionGroup.class,namespace="http://shared.aekos.org.au/shared" )
	})
	protected List entryList = new ArrayList();
	
	@XmlTransient
	public List getEntryList(){
		return entryList;
	}

	@XmlTransient
	private SharedXmlElement parent;
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.parent = (SharedXmlElement) parent;
	}
	
	public SharedXmlElement getParent() {
		return parent;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entryList == null) ? 0 : entryList.hashCode());
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
		Items other = (Items) obj;
		if (entryList == null) {
			if (other.entryList != null)
				return false;
		} else if (!entryList.equals(other.entryList))
			return false;
		return true;
	}

	@Override
	public SharedXmlElementType getType() {
		return SharedXmlElementType.ITEMS;
	}

	@Override
	public <T extends SharedXmlElement> T findAncestorOfType(Class<T> clazz) {
		return SharedXmlElementSupport.findAncestorOfType(clazz, parent);
	}
}
