package au.edu.aekos.shared.service.submission;

import static org.junit.Assert.*;

import org.junit.Test;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class SubmissionServiceImplTest {

	@Test
	public void testMapAnswerToNewSubmissionAnswer() {
		SubmissionServiceImpl objectUnderTest = new SubmissionServiceImpl();
		Answer source = new Answer();
		source.setQuestionId("1.1");
		source.setDisplayResponse("resp");
		source.setResponseType(ResponseType.TEXT);
		source.setSuggestedResponse("sugg");
		SubmissionAnswer result = new SubmissionAnswer();
		objectUnderTest.mapAnswerToNewSubmissionAnswer(source, result);
		assertEquals("1.1", result.getQuestionId());
		assertEquals("resp", result.getDisplayResponse());
		assertEquals(ResponseType.TEXT, result.getResponseType());
		assertEquals("sugg", result.getSuggestedResponse());
	}
}
