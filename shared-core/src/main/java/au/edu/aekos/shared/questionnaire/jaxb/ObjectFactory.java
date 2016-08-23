package au.edu.aekos.shared.questionnaire.jaxb;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	/*
	 * Most objects require nothing but a simple create method. But whenever an element has 
	 * to be represented as a JAXBElement<?>, an additional factory method for wrapping the
	 *  "pure" Java object of some class Foo into an element of class JAXBElement<Foo> must 
	 *  be provided. This method is then annotated with XmlElementDecl, providing the components 
	 *  of the element's tag name through the attributes namespace and name. 
	 *  
	 *  This is a snippet from some object factory where an element of TreeType is wrapped 
	 *  into a JAXBElement<TreeType>: 
	*/
	/*@XmlElementDecl(namespace = "", name = "tree")
	public JAXBElement<TreeType> createTree( TreeType value) {
	    return new JAXBElement<TreeType>(_Tree_QNAME, TreeType.class, null, value);
	}*/
	
	
	
}
