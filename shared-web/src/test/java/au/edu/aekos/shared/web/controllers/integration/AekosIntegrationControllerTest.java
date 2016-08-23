package au.edu.aekos.shared.web.controllers.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-data-context-hssql.xml", "classpath:test-servlet-context.xml" })
@Transactional
public class AekosIntegrationControllerTest {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup (this.wac).build();
	}
	
	/**
	 * Can we successfully request submission details for a PUBLISHED submission?
	 */
	@Test
	public void testGetSubmissionDetails01() throws Exception {
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dq = new DisplayQuestionnaire(qc);
		assertNotNull(dq);
		final long submissionId = submissionService.createNewSubmission("test-tgsd01", dq, SubmissionStatus.PUBLISHED);
		ResultHandler assertResponseIsSuccessAndHasCorrectId = new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				assertEquals("application/json", result.getResponse().getHeader("content-type"));
				Gson gson = new Gson();
				ResponseGetDatasetSummary resp = gson.fromJson(result.getResponse().getContentAsString(), ResponseGetDatasetSummary.class);
				assertTrue(resp.isSuccess());
				assertThat(resp.getPayload().getDatasetId(), is(String.valueOf(submissionId)));
				assertFalse(resp.isPendingPublish());
			}
		};
		mockMvc.perform(get("/integration/datasetSummary/"+submissionId))
            .andExpect(status().isOk())
            .andDo(assertResponseIsSuccessAndHasCorrectId);
		submissionDao.updateSubmissionStatus(submissionId,SubmissionStatus.DELETED); //FIXME do we need to actually delete the row or is this enough?
	}

	/**
	 * Can we handle a request for a non-existent submission ID
	 */
	@Test
	public void testGetSubmissionDetails02() throws Exception {
		ResultHandler assertResponseIndicatesNothingFound = new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				assertEquals("application/json", result.getResponse().getHeader("content-type"));
				Gson gson = new Gson();
				ResponseGetDatasetSummary resp = gson.fromJson(result.getResponse().getContentAsString(), ResponseGetDatasetSummary.class);
				assertFalse(resp.isSuccess());
				assertTrue(resp.getErrorMessage().length() > 0);
				assertFalse(resp.isPendingPublish());
			}
		};
		final Long nonExistentId = 666L;
		mockMvc.perform(get("/integration/datasetSummary/"+nonExistentId))
            .andExpect(status().isOk())
            .andDo(assertResponseIndicatesNothingFound);
	}
	
	/**
	 * Can we successfully request submission details for an APPROVED submission?
	 */
	@Test
	public void testGetSubmissionDetails03() throws Exception {
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dq = new DisplayQuestionnaire(qc);
		assertNotNull(dq);
		final long submissionId = submissionService.createNewSubmission("test-tgsd03", dq, SubmissionStatus.APPROVED);
		ResultHandler assertResponseIsSuccessAndHasCorrectId = new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				assertEquals("application/json", result.getResponse().getHeader("content-type"));
				Gson gson = new Gson();
				ResponseGetDatasetSummary resp = gson.fromJson(result.getResponse().getContentAsString(), ResponseGetDatasetSummary.class);
				assertTrue(resp.isSuccess());
				assertThat(resp.getPayload().getDatasetId(), is(String.valueOf(submissionId)));
				assertTrue(resp.isPendingPublish());
			}
		};
		mockMvc.perform(get("/integration/datasetSummary/"+submissionId))
            .andExpect(status().isOk())
            .andDo(assertResponseIsSuccessAndHasCorrectId);
		submissionDao.updateSubmissionStatus(submissionId,SubmissionStatus.DELETED); //FIXME do we need to actually delete the row or is this enough?
	}
	
	/**
	 * Can we request submission details for a submission that's not publicly available?
	 */
	@Test
	public void testGetSubmissionDetails04() throws Exception {
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		DisplayQuestionnaire dq = new DisplayQuestionnaire(qc);
		assertNotNull(dq);
		final long submissionId = submissionService.createNewSubmission("test-tgsd04", dq, SubmissionStatus.SUBMITTED);
		ResultHandler assertResponseIsFailure = new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				assertEquals("application/json", result.getResponse().getHeader("content-type"));
				Gson gson = new Gson();
				ResponseGetDatasetSummary resp = gson.fromJson(result.getResponse().getContentAsString(), ResponseGetDatasetSummary.class);
				assertFalse(resp.isSuccess());
				assertNull(resp.getPayload());
				assertFalse(resp.isPendingPublish());
			}
		};
		mockMvc.perform(get("/integration/datasetSummary/"+submissionId))
            .andExpect(status().isOk())
            .andDo(assertResponseIsFailure);
		submissionDao.updateSubmissionStatus(submissionId,SubmissionStatus.DELETED); //FIXME do we need to actually delete the row or is this enough?
	}
}
