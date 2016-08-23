package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SameAsOptionList", namespace="http://shared.aekos.org.au/shared")
public class SameAsOptionList implements Serializable {

	private static final long serialVersionUID = -8791362926601094490L;
	@XmlElements({
		@XmlElement(name = "sameAsOption", type = SameAsOption.class, namespace="http://shared.aekos.org.au/shared")
	})
	private List<SameAsOption> optionList = new ArrayList<SameAsOption>();

	@XmlTransient
	public List<SameAsOption> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<SameAsOption> optionList) {
		this.optionList = optionList;
	}
	
}
