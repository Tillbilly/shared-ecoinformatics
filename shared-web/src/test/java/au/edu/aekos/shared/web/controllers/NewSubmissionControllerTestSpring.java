package au.edu.aekos.shared.web.controllers;

import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answer;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answerGroup;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.answerMap;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.dqWithConfigAndAnswers;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.getConfig;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.getMultipleQuestionGroupWithId;
import static au.edu.aekos.shared.QuestionnaireObjectBuilders.multipleAnswerGroup;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.quest.ControlledVocabularyServiceStub;
import au.edu.aekos.shared.service.submission.ExampleCitationDataProviderFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class NewSubmissionControllerTestSpring {

	@Autowired
	private NewSubmissionController objectUnderTest;
	
	/**
	 * Can we build an example citation string to be used on the submission summary?
	 */
	@Test
	public void testBuildExampleCitation01() {
		QuestionnaireConfig config = getConfig("au/edu/aekos/shared/NewSubmissionController_testBuildExampleCitation01-questionnaire.xml");
		MultipleQuestionGroup authorGroup = getMultipleQuestionGroupWithId(config, "16");
		MultipleQuestionGroup legalCustodianOrgGroup = getMultipleQuestionGroupWithId(config, "3.1");
		Map<String, Answer> answerMap = answerMap(
			answer("1.1", "A study of trees"),
			multipleAnswerGroup(legalCustodianOrgGroup,
				answerGroup(
					answer("3.1.1", ControlledVocabularyServiceStub.ORG_UNIVERSITY_OF_ADELAIDE),
					answer("3.1.2", "university")
				)
			),
			multipleAnswerGroup(authorGroup,
				answerGroup(
					answer("16.1", "A.K."),
					answer("16.2", "Smyth")
				),
				answerGroup(
					answer("16.1", "E."),
					answer("16.2", "Smee")
				)
			)
		);
		DisplayQuestionnaire questionnaire = dqWithConfigAndAnswers(config, answerMap);
		String result = objectUnderTest.buildExampleCitation(questionnaire);
		String thisYear = ExampleCitationDataProviderFactory.thisYear();
		assertThat(result, is("Smyth, A.K., Smee, E. ("+thisYear+"). A study of trees.<br />http://doi.org/[some DOI placeholder]."
				+ "<br />&AElig;KOS Data Portal, rights owned by University of Adelaide.<br />Accessed dd mmm yyyy, e.g., 01 Jan 2016."));
	}
}
