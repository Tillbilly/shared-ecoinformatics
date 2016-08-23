package au.edu.aekos.shared.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.context.WebApplicationContext;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-data-context-hssql.xml", "classpath:test-servlet-context.xml" })
public class UploadFileControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup (this.wac).build();
	}

	/**
	 * Can we handle a success path?
	 */
	@Test
	public void testQuestionFileUpload01() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(NewSubmissionController.SESSION_QUESTIONNAIRE, new DisplayQuestionnaire());
		mockMvc.perform(fileUpload("/questionFileUpload")
					.file(file).session(session)
					.param("description", "blah")
					.param("questionId", "10.6")
					.param("responseType", "DOCUMENT")
				)
	            .andExpect(status().isOk())
	            .andDo(new ResultHandler() {

	            	@Override
					public void handle(MvcResult result) throws Exception {
						Gson gson = new Gson();
						UploadFileJsonResponse resp = gson.fromJson(result.getResponse().getContentAsString(), UploadFileJsonResponse.class);
						assertTrue(resp.isSuccess());
						assertEquals("blah", resp.getDescription());
						assertEquals("orig", resp.getOrigFilename());
						assertEquals("stub.file", resp.getStoredFilename());
					}
				});
	}
	
	/**
	 * Can we fail gracefully when we can't find the questionnaire on the session (due to expired session most likely)
	 */
	@Test
	public void testQuestionFileUpload02() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
		mockMvc.perform(fileUpload("/questionFileUpload")
					.file(file)
					.param("description", "blah")
					.param("questionId", "10.6")
					.param("responseType", "DOCUMENT")
				)
	            .andExpect(status().isOk())
	            .andDo(new ResultHandler() {

	            	@Override
					public void handle(MvcResult result) throws Exception {
						Gson gson = new Gson();
						UploadFileJsonResponse resp = gson.fromJson(result.getResponse().getContentAsString(), UploadFileJsonResponse.class);
						assertFalse(resp.isSuccess());
					}
				});
	}
}
