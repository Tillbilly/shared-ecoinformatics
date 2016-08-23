package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://shared.aekos.org.au/shared")
public class PageBreak  implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	private String pageTitle;

	public PageBreak() {
		super();
	}
	
	public PageBreak(String pageTitle) {
		super();
		this.pageTitle = pageTitle;
	}

	@XmlTransient
	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
}
