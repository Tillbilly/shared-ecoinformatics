package au.edu.aekos.shared.service.doi;

import static au.edu.aekos.shared.SubmissionAnswerBuilders.controlledVocabSuggestAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.date;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.dateAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multipleQuestionGroup;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextChildAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.questionSet;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textBoxAnswer;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorTest;
import au.edu.aekos.shared.doiclient.jaxb.Resource;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.security.SecurityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
@Transactional(propagation=Propagation.REQUIRED)
public class DoiMintingServiceTestIT {

	private static final String ID_OF_A_RECORD_THAT_EXISTS_IN_PROD = "28248";
	private static final String QUESTIONNAIRE_NAME = "rifcsTestQuestionnaire.xml";
	
	@Autowired
	private DoiMintingServiceImpl objectUnderTest;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService configService;

	@Autowired
	private QuestionnaireConfigEntityDao entityDao;

	@Autowired
	private SecurityService authService;
	
	@AfterClass
	public static void afterClass() {
		MetaInfoExtractorTest.cleanCache();
	}
	
	/**
	 * Can we mint a DOI against the test DOI service?
	 */
	@Test 
	public void testDoiMintingService() throws Throwable{
		Submission sub = createSubmission();
        submissionDao.save(sub);
		assertNotNull(sub.getId());
		Resource res = objectUnderTest.buildResourceForSubmission(sub);
		assertNotNull(res);
		StringWriter sw = stringifyResource(res);
	    System.out.println(sw.toString());
	    String urlThatWont404 = "/dataset/" + ID_OF_A_RECORD_THAT_EXISTS_IN_PROD + "&testHack=";
		objectUnderTest.setUrlSuffix(urlThatWont404); // we can't control the ID of the submission we create so have to hack the URL
		String doi = objectUnderTest.testMintDoiForSubmission(sub);
		System.out.println(doi);
	}

	private Submission createSubmission() throws Exception {
		QuestionnaireConfig qc = configService.readQuestionnaireConfig(QUESTIONNAIRE_NAME, true, false);
		SharedUser su = authService.getCreateDefaultUser("testDoiUser");
		Submission result = new Submission();
		result.setSubmitter(su);
		result.setQuestionnaireConfig(entityDao.findById(qc.getSmsQuestionnaireId()));
		result.setTitle("DoiMintingServiceTestIT");
		result.setLastReviewDate(date("2015-12-12"));
		result.setDoi("10.33327/05/537C5C5553F9E");
		
		result.getAnswers().add(textAnswer("1", "My Fantastic Dataset"));
		result.getAnswers().add(textAnswer("1.1", "Most awesome submission"));
		result.getAnswers().add(textAnswer("1.2", "1.2.wow"));
		result.getAnswers().add(textAnswer("2", "Dataset Contact Name"));
		result.getAnswers().add(textAnswer("3", "Dataset Organisation Yall"));
		result.getAnswers().add(
			multipleQuestionGroup("3.1",
				questionSet(
					textAnswer("3.1.1", "Org1")
				),
				questionSet(
					textAnswer("3.1.1", "Org2")
				)
			)
		);
		result.getAnswers().add(textBoxAnswer("4", "Dataset Contact Address my Friends"));
		result.getAnswers().add(textAnswer("5", "Ph: 123 456 666"));
		result.getAnswers().add(textAnswer("6", "ben@shared.com"));
		result.getAnswers().add(multiselectTextAnswer("7",
			multiselectTextChildAnswer("01"),
			multiselectTextChildAnswer("2"),
			multiselectTextChildAnswer("601"),
			multiselectTextChildAnswer("0602"),
			multiselectTextChildAnswer("60601"),
			multiselectTextChildAnswer("060608"),
			multiselectTextChildAnswer(" 20406")
		));
		result.getAnswers().add(multiselectTextAnswer("8",
			multiselectTextChildAnswer("04"),
			multiselectTextChildAnswer("5"),
			multiselectTextChildAnswer("201"),
			multiselectTextChildAnswer("0202"),
			multiselectTextChildAnswer("20601"),
			multiselectTextChildAnswer("020608")
		));
		result.getAnswers().add(multiselectTextAnswer("9",
			multiselectTextChildAnswer("theme 1"),
			multiselectTextChildAnswer("theme 2")
		));
		result.getAnswers().add(textAnswer("10", "Dataset abstract all about my dataset"));
		result.getAnswers().add(dateAnswer("11", "21/11/2000"));
		result.getAnswers().add(dateAnswer("12", "13/10/2010"));
		result.getAnswers().add(textAnswer("13", "Study Area Description"));

		result.getAnswers().add(
			multipleQuestionGroup("16",
				questionSet(
					textAnswer("16.1", "R.M."),
					textAnswer("16.2", "Davis"),
					textAnswer("16.3", "Lead Professor")
				),
				questionSet(
					textAnswer("16.1", "P.Q."),
					textAnswer("16.2", "McCracken"),
					textAnswer("16.3", "Big Boss")
				)
			)
		);
		result.getAnswers().add(textAnswer("17", "2010-2011"));
		result.getAnswers().add(textAnswer("18", "Creative Commons - Attribution 3.0 Australia"));
		result.getAnswers().add(controlledVocabSuggestAnswer("19", "My Organisation"));
		result.getAnswers().add(multiselectTextAnswer("20",
			multiselectTextChildAnswer("ecological theme uno"),
			multiselectTextChildAnswer("ecological theme dos")
		));
		result.getAnswers().add(multiselectTextAnswer("21",
			multiselectTextChildAnswer("something threatening"),
			multiselectTextChildAnswer("some pressure")
		));
		result.getAnswers().add(multiselectTextAnswer("22",
			multiselectTextChildAnswer("conserving"),
			multiselectTextChildAnswer("managing")
		));
		return result;
	}

	private StringWriter stringifyResource(Resource res) throws JAXBException, PropertyException {
		JAXBContext context = JAXBContext.newInstance(Resource.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-2.1 http://schema.datacite.org/meta/kernel-2.1/metadata.xsd");
	    StringWriter sw = new StringWriter();
	    m.marshal(res, sw);
		return sw;
	}
}
