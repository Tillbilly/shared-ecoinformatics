package au.edu.aekos.shared.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayCondition;
import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.web.controllers.QuestionnaireDisplaySupport;

public class QuestionnaireDisplaySupportTest {

	@Test
	public void testGenerateJQueryPageTriggerDisplayCondition(){
		Answer ans = new Answer();
		ans.setResponseType(ResponseType.TEXT);
		
		
		DisplayCondition dc = new DisplayCondition();
		dc.setConditionalAnswer(ans);
		dc.setQuestionId("123");
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("123");
		cd.setResponseNotNull(true);
		dc.setConditionalDisplay(cd);
		
		System.out.println(QuestionnaireDisplaySupport.generateJQueryPageTriggerDisplayCondition("222", dc, true) );
		assertTrue(true);
		cd.setResponseValue("trevor");
		ans.setResponseType(ResponseType.BBOX);
		System.out.println(QuestionnaireDisplaySupport.generateJQueryPageTriggerDisplayCondition("222", dc, true) );
		assertTrue(true);
	}
	
	@Test
	public void testDereferenceDotInSelectorId(){
		System.out.println(QuestionnaireDisplaySupport.dereferenceDotInSelectorId("321.234.21"));
		assertEquals("321\\\\.234\\\\.21", QuestionnaireDisplaySupport.dereferenceDotInSelectorId("321.234.21") );
		assertTrue(true);
		
	}
	
	@Test
	public void testForwardPopulateJqueryGeneration(){
		String str = QuestionnaireDisplaySupport.generateJQueryForwardPopulateString("1.1", "2.2.2", ResponseType.TEXT);
		assertNotNull(str);
		System.out.println(str);
	}
	
	
}
