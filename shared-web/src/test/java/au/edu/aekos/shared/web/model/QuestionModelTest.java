package au.edu.aekos.shared.web.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class QuestionModelTest {

	/**
	 * Can we survive a null metatag?
	 */
	@Test
	public void testGetHasSuggestedVocabEntry01() {
		QuestionModel objectUnderTest = new QuestionModel();
		objectUnderTest.setMetatag(null);
		boolean result = objectUnderTest.getHasSuggestedVocabEntry();
		assertFalse(result);
	}

}
