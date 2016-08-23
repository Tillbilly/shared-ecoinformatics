package au.edu.aekos.shared.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigServiceImpl;

public class QuestionnaireConfigServiceTest {

	/**
	 * Can we load a questionnaire config from a file?
	 */
	@Test
	public void testReadQuestionnaireConfig01() throws Exception{
		QuestionnaireConfigService configService = new QuestionnaireConfigServiceImpl();
		QuestionnaireConfig qc = configService.readQuestionnaireConfig("questionnaire3.xml", false, false);
		assertThat(qc.getTitle(), is("SHaRED AEKOS Submission Tool - Example Config 3"));
		assertThat(qc.getVersion(), is("version 3"));
	}
}
