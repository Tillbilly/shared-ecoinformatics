package au.edu.aekos.shared.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.web.json.JsonIsSessionActiveResponse;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-data-context-hssql.xml", "classpath:test-servlet-context.xml" })
@Transactional
public class LoginControllerTest {
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup (this.wac).build();
	}
	
	/**
	 * Can we tell when the session IS still active?
	 */
	@Test
	public void testIsSessionActive01() throws Exception {
		ResultHandler assertResponseIsSuccessAndTrue = new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				assertEquals("text/html", result.getResponse().getHeader("content-type"));
				Gson gson = new Gson();
				JsonIsSessionActiveResponse resp = gson.fromJson(result.getResponse().getContentAsString(), JsonIsSessionActiveResponse.class);
				assertTrue(resp.isSessionActive());
			}
		};
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE, new DisplayQuestionnaire());
		mockMvc.perform(get("/isSessionActive").session(session))
            .andExpect(status().isOk())
            .andDo(assertResponseIsSuccessAndTrue);
	}
	
	/**
	 * Can we tell when the session IS NOT still active?
	 */
	@Test
	public void testIsSessionActive02() throws Exception {
		ResultHandler assertResponseIsSuccessAndTrue = new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				assertEquals("text/html", result.getResponse().getHeader("content-type"));
				Gson gson = new Gson();
				JsonIsSessionActiveResponse resp = gson.fromJson(result.getResponse().getContentAsString(), JsonIsSessionActiveResponse.class);
				assertFalse(resp.isSessionActive());
			}
		};
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE, null);
		mockMvc.perform(get("/isSessionActive").session(session))
            .andExpect(status().isOk())
            .andDo(assertResponseIsSuccessAndTrue);
	}
}
