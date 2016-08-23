package au.edu.aekos.shared;

import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answer;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answerGroup;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import au.edu.aekos.shared.QuestionnaireObjectBuilders.AnswerGroupPlaceholder;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;

public class QuestionnaireObjectBuildersTest {

	/**
	 * Can we create an answer?
	 */
	@Test
	public void testAnswer01() {
		Answer result = QuestionnaireObjectBuilders.answer("1.1", "wombat");
		assertThat(result.getQuestionId(), is("1.1"));
		assertThat(result.getResponse(), is("wombat"));
	}
	
	/**
	 * Can we create an answer group?
	 */
	@Test
	public void testAnswerGroup01() {
		AnswerGroupPlaceholder result = answerGroup(
			answer("1", "aaa"),
			answer("2", "bbb")
		);
		assertThat(result.getAnswers().length, is(2));
	}
	
	@Test
	public void testMultipleQuestionGroup01() {
		MultipleQuestionGroup questionGroup = new MultipleQuestionGroup("3", "", "");
		Answer result = QuestionnaireObjectBuilders.multipleAnswerGroup(questionGroup,
			answerGroup(
				answer("3.2", "Aaron"),
				answer("3.3", "Aaronson")
			),
			answerGroup(
				answer("3.2", "Bob"),
				answer("3.3", "Bobson")
			)
		);
		List<Map<String, Answer>> answerSetListForQ3 = result.getAnswerSetList();
		assertThat(answerSetListForQ3.size(), is(2));
		assertThat(answerSetListForQ3.get(0).get("3.2").getResponse(), is("Aaron"));
		assertThat(answerSetListForQ3.get(0).get("3.3").getResponse(), is("Aaronson"));
		assertThat(answerSetListForQ3.get(1).get("3.2").getResponse(), is("Bob"));
		assertThat(answerSetListForQ3.get(1).get("3.3").getResponse(), is("Bobson"));
	}
}
