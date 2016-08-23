package au.edu.aekos.shared.web.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;

public class ProgressIndicatorInfoTest {

	@Test
	public void testFactory01() {
		DisplayQuestionnaire source = new DisplayQuestionnaire();
		source.setCurrentPageNumber(1);
		source.setPages(Arrays.asList(new QuestionnairePage[] {new QuestionnairePage(), new QuestionnairePage(), new QuestionnairePage()}));
		ProgressIndicatorInfo result = ProgressIndicatorInfo.newInstance(source);
		assertEquals(1, result.getCurrentPageNum());
		assertEquals(3, result.getTotalPageCount());
	}
}
