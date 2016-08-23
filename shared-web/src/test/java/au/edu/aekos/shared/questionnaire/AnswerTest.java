package au.edu.aekos.shared.questionnaire;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AnswerTest {

	/**
	 * Ensure we don't get a NPE
	 */
	@Test
	public void testToString01() {
		Answer objectUnderTest = new Answer();
		List<Map<String, Answer>> emptyAnswerSetList = Collections.emptyList();
		objectUnderTest.setAnswerSetList(emptyAnswerSetList);
		objectUnderTest.setDisplayResponse(null);
		List<Answer> emptyMultiselectAnswerList = Collections.emptyList();
		objectUnderTest.setMultiselectAnswerList(emptyMultiselectAnswerList);
		objectUnderTest.setQuestionId(null);
		objectUnderTest.setResponse("");
		objectUnderTest.setResponseType(null);
		objectUnderTest.setSuggestedResponse(null);
		objectUnderTest.toString();
	}
	
	/**
	 * Can we tell when an answer represents the user selecting "Other"?
	 */
	@Test
	public void testIsAnsweredAsOther01() {
		Answer objectUnderTest = new Answer();
		objectUnderTest.setResponse(Answer.OTHER);
		boolean result = objectUnderTest.isAnsweredAsOther();
		assertThat(result, is(true));
	}
	
	/**
	 * Can we tell when an answer represents a normal (non-Other) answer?
	 */
	@Test
	public void testIsAnsweredAsOther02() {
		Answer objectUnderTest = new Answer();
		objectUnderTest.setResponse("Blah blah");
		boolean result = objectUnderTest.isAnsweredAsOther();
		assertThat(result, is(false));
	}
}
