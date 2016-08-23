package au.edu.aekos.shared.valid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.validation.Errors;

import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.jaxb.Question;

public class PageAnswersValidatorTest {

	/**
	 * Can we validate dates?
	 */
	@Test
	public void testDateFormatValidation(){
		assertTrue(PageAnswersValidator.checkDateFormat("12/12/2005") );
		assertTrue(PageAnswersValidator.checkDateFormat("12/12/1900") );
		assertTrue(PageAnswersValidator.checkDateFormat("02/02/2022") );
		assertTrue(PageAnswersValidator.checkDateFormat("30/12/2005") );
		assertFalse(PageAnswersValidator.checkDateFormat("x2/12/2005") );
	}
	
	/**
	 * Can we survive an empty list
	 */
	@Test
	public void testGetFirstResponseMultiselectAnswerOrFirst01(){
		PageAnswersValidator objectUnderTest = new PageAnswersValidator();
		List<Answer> emptyList = Collections.emptyList();
		Answer result = objectUnderTest.getFirstResponseMultiselectAnswerOrFirst(emptyList);
		assertEquals(Answer.NULL_ANSWER, result);
	}
	
	/**
	 * Can we find the first answer with a response
	 */
	@Test
	public void testGetFirstResponseMultiselectAnswerOrFirst02(){
		PageAnswersValidator objectUnderTest = new PageAnswersValidator();
		Answer targetAnswer = new Answer();
		List<Answer> answerList = Arrays.asList(new Answer(), targetAnswer);
		targetAnswer.setResponse("some response");
		Answer result = objectUnderTest.getFirstResponseMultiselectAnswerOrFirst(answerList);
		assertEquals(targetAnswer, result);
	}
	
	/**
	 * Can we find survive a list of answers with no responses
	 */
	@Test
	public void testGetFirstResponseMultiselectAnswerOrFirst03(){
		PageAnswersValidator objectUnderTest = new PageAnswersValidator();
		List<Answer> answerList = Arrays.asList(new Answer(), new Answer());
		Answer result = objectUnderTest.getFirstResponseMultiselectAnswerOrFirst(answerList);
		assertEquals(Answer.NULL_ANSWER, result);
	}
	
	/**
	 * Can we survive an empty multiselect answer list
	 */
	@Test
	public void testValidateMultiselectQuestion01(){
		PageAnswersValidator objectUnderTest = new PageAnswersValidator();
		Answer answer = new Answer();
		List<Answer> multiselectAnswerList = Collections.emptyList();
		answer.setMultiselectAnswerList(multiselectAnswerList);
		objectUnderTest.validateMultiselectQuestion(new Question(), mock(Errors.class), answer);
	}
	
	/**
	 * Does a valid multiselect answer pass validation
	 */
	@Test
	public void testValidateMultiselectQuestion02(){
		PageAnswersValidator objectUnderTest = new PageAnswersValidator();
		Answer answer = new Answer();
		Answer firstMutliselectAnswer = new Answer();
		answer.setMultiselectAnswerList(Arrays.asList(firstMutliselectAnswer));
		firstMutliselectAnswer.setResponse("blah");
		Errors mockErrors = mock(Errors.class);
		objectUnderTest.validateMultiselectQuestion(new Question(), mockErrors, answer);
		verifyZeroInteractions(mockErrors);
	}
	
	/**
	 * Does a multiselect answer with a zero length response fail validation for the right reason 
	 */
	@Test
	public void testValidateMultiselectQuestion03(){
		PageAnswersValidator objectUnderTest = new PageAnswersValidator();
		Question question = new Question();
		question.setId("1.2.3");
		Answer answer = new Answer();
		Answer firstMutliselectAnswer = new Answer();
		answer.setMultiselectAnswerList(Arrays.asList(firstMutliselectAnswer));
		firstMutliselectAnswer.setResponse("");
		Errors mockErrors = mock(Errors.class);
		objectUnderTest.validateMultiselectQuestion(question, mockErrors, answer);
		verify(mockErrors).rejectValue("answers[1.2.3].multiselectAnswerList[0].response", "questionnaire.validation.mandatory");
	}
}
