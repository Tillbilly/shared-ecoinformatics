package au.edu.aekos.shared.questionnaire.jaxb;

import static org.junit.Assert.*;

import org.junit.Test;

public class SharedXmlElementSupportTest {

	/**
	 * Is the parent found when it's the first thing checked
	 */
	@Test
	public void testFindParentOfType01() {
		SharedXmlElement parent = new QuestionGroup();
		Class targetClass = parent.getClass();
		SharedXmlElement result = SharedXmlElementSupport.findAncestorOfType(targetClass, parent);
		assertEquals(parent, result);
	}

	/**
	 * Is the parent found when it's the second thing checked
	 */
	@Test
	public void testFindParentOfType02() {
		SharedXmlElement grandparent = new MultipleQuestionGroup();
		Class targetClass = grandparent.getClass();
		QuestionGroup parent = new QuestionGroup();
		parent.afterUnmarshal(null, grandparent);
		SharedXmlElement result = SharedXmlElementSupport.findAncestorOfType(targetClass, parent);
		assertEquals(grandparent, result);
	}
}
