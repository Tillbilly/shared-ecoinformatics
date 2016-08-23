package au.edu.aekos.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class SubmissionAnswerBuilders {
	public static SubmissionAnswer multipleQuestionGroup(String id, QuestionSetEntity...questionSets) {
		SubmissionAnswer result = new SubmissionAnswer();
		result.setResponseType(ResponseType.MULTIPLE_QUESTION_GROUP);
		result.setQuestionId(id);
		for (QuestionSetEntity currQuestionSet : questionSets) {
			result.getQuestionSetList().add(currQuestionSet);
		}
		return result;
	}

	public static QuestionSetEntity questionSet(SubmissionAnswer...answers) {
		QuestionSetEntity result = new QuestionSetEntity();
		for (SubmissionAnswer currAnswer : answers) {
			result.getAnswerMap().put(currAnswer.getQuestionId(), currAnswer);
		}
		return result;
	}

	public static SubmissionAnswer dateAnswer(String id, String dateString) {
		return answer(id, dateString, ResponseType.DATE);
	}

	public static SubmissionAnswer textBoxAnswer(String id, String answerText) {
		return answer(id, answerText, ResponseType.TEXT_BOX);
	}

	public static SubmissionAnswer textAnswer(String id, String answerText) {
		return answer(id, answerText, ResponseType.TEXT);
	}

	private static SubmissionAnswer answer(String id, String answerText, ResponseType responseType) {
		SubmissionAnswer result = new SubmissionAnswer();
		result.setResponseType(responseType);
		result.setResponse(answerText);
		result.setQuestionId(id);
		return result;
	}
	
	public static Date date(String dateString) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
	}
	
	public static String accessedTodayDate() {
		return new SimpleDateFormat("dd MMM yyyy").format(new Date());
	}
	
	public static SubmissionAnswer multiselectTextAnswer(String id, SubmissionAnswer...childAnswers) {
		SubmissionAnswer result = new SubmissionAnswer();
		result.setResponseType(ResponseType.MULTISELECT_TEXT);
		result.setQuestionId(id);
		for (SubmissionAnswer currChild : childAnswers) {
			result.getMultiselectAnswerList().add(currChild);
		}
		return result;
	}

	public static SubmissionAnswer multiselectTextChildAnswer(String answerValue) {
		SubmissionAnswer result = new SubmissionAnswer();
		result.setResponse(answerValue);
		result.setResponseType(ResponseType.TEXT);
		return result;
	}
	
	public static SubmissionAnswer controlledVocabSuggestAnswer(String id, String responseValue) {
		SubmissionAnswer result = new SubmissionAnswer();
		result.setResponseType(ResponseType.CONTROLLED_VOCAB_SUGGEST);
		result.setResponse(responseValue);
		result.setQuestionId(id);
		return result;
	}
}
