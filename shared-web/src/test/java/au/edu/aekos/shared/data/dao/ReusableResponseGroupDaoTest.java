package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.ReusableAnswer;
import au.edu.aekos.shared.data.entity.ReusableResponseGroup;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class ReusableResponseGroupDaoTest {

	@Autowired
	private ReusableResponseGroupDao reusableResponseGroupDao;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private QuestionnaireConfigEntityDao configEntityDao;
	
	@Test
	public void testReusableResponseGroupConfig() {
		SharedUser su = createAUser("mrtest69");
		QuestionnaireConfig qc = getQuestionnaireConfig("test-reusableGroup.xml", "1");
		assertNotNull(qc.getSmsQuestionnaireId());
		
		//Now build a reusable response group
		ReusableResponseGroup rrg = buildBasicReusableResponseGroupWithAnswersForTest("group1");
		rrg.setSharedUser(su);
		QuestionnaireConfigEntity qce = configEntityDao.findById(qc.getSmsQuestionnaireId());
		rrg.setQuestionnaireConfig( qce  );
		reusableResponseGroupDao.save(rrg);
		assertTrue(true);
	}
	
	@Test
	public void testReusableResponseGroupDao() {
		SharedUser su = createAUser("mrtest69");
		QuestionnaireConfig qc = getQuestionnaireConfig("test-reusableGroup.xml", "1");
		assertNotNull(qc.getSmsQuestionnaireId());
		QuestionnaireConfigEntity qce = configEntityDao.findById(qc.getSmsQuestionnaireId());
		
		//Now build a reusable response group
		ReusableResponseGroup rrg = buildBasicReusableResponseGroupWithAnswersForTest("group1");
		rrg.setSharedUser(su);
		rrg.setQuestionnaireConfig( qce  );
		reusableResponseGroupDao.save(rrg);
		
		ReusableResponseGroup rrg2 = buildBasicReusableResponseGroupWithAnswersForTest("group2");
		rrg2.setSharedUser(su);
		rrg2.setQuestionnaireConfig( qce  );
		reusableResponseGroupDao.save(rrg2);
		
		ReusableResponseGroup rrg3 = buildBasicReusableResponseGroupWithAnswersForTest("group3");
		rrg3.setSharedUser(su);
		rrg3.setQuestionnaireConfig( qce  );
		reusableResponseGroupDao.save(rrg3);
		
		List<ReusableResponseGroup> lrrg = reusableResponseGroupDao.retrieveResponseGroups(qce.getId(), "mrtest69", "1");
		assertEquals(3, lrrg.size());
	}
	
/*
	<question id="1.1">
	    <text>Reusable Group Title Question Id</text>
	    <responseType>TEXT</responseType>
	    <description> a description for help or something</description>
	    <responseMandatory>true</responseMandatory>
	</question>
	<question id="1.2">
	     <text>Is the data awesome?</text>
	     <responseType>YES_NO</responseType>
	     <description> a description for help or something</description>
	     <responseMandatory>true</responseMandatory>
	</question>
*/
	private ReusableResponseGroup buildBasicReusableResponseGroupWithAnswersForTest(String name){
		ReusableResponseGroup rrg = new ReusableResponseGroup();
		rrg.setGroupId("1");
		rrg.setName(name);
		ReusableAnswer ra = new ReusableAnswer();
		ra.setQuestionId("1.1");
		ra.setResponse(name);
		ra.setResponseType(ResponseType.TEXT);
		rrg.getAnswers().add(ra);
		
		ReusableAnswer ra2 = new ReusableAnswer();
		ra2.setQuestionId("1.2");
		ra2.setResponse("Y");
		ra2.setResponseType(ResponseType.YES_NO );
		rrg.getAnswers().add(ra2);
		return rrg;
	}
	
	private SharedUser createAUser(String username){
		SharedUser su = new SharedUser();
		su.setEnabled(true);
		su.setUsername(username);
		sharedUserDao.save(su);
		return su;
	}
	
	private QuestionnaireConfig getQuestionnaireConfig(String filename, String version){
		QuestionnaireConfig qc = null;
		try {
			qc = questionnaireConfigService.getQuestionnaireConfig(filename, version, false);
			assertNotNull(qc.getSmsQuestionnaireId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return qc;
	}
}
