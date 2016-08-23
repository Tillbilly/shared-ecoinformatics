package au.edu.aekos.shared.service.reports;

import static au.edu.aekos.shared.SubmissionAnswerBuilders.controlledVocabSuggestAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.date;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.dateAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multipleQuestionGroup;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.multiselectTextChildAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.questionSet;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textAnswer;
import static au.edu.aekos.shared.SubmissionAnswerBuilders.textBoxAnswer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorTest;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.reports.CertificateOfPublicationReportBean;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.security.SecurityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class ReportDataSourceServiceImplTest {

	private static final String SUBMISSION_NAME = "The submission name, not the dataset name";

	@Autowired
	private ReportDataSourceServiceImpl objectUnderTest;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private QuestionnaireConfigEntityDao entityDao;
	
	@Autowired
	private SecurityService authService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	private static final String QUESTIONNAIRE_NAME = "reportDataTestQuestionnaire.xml";
	private static final String GEO_FEATURE_SET_JSON = "{ \"questionId\" : \"1.2.4\", \"features\" : [ "
			+ "{\"id\":\"PLY_1\",\"geometry\":\"POLYGON((139.35009765625 -32.479614257813,139.57836914063 -33.496459960938,140.90649414063 -33.517211914063,141.44604492188 -32.147583007813,141.09326171875 -31.753295898438,140.61596679688 -32.749389648438,140.0556640625 -30.425170898438,138.12573242188 -31.109985351563,139.35009765625 -32.479614257813))\",\"description\":\"friendly\"},"
			+ "{\"id\":\"PLY_2\",\"geometry\":\"POLYGON((137.5654296875 -29.138549804688,137.482421875 -30.445922851563,135.92602539063 -29.180053710938,137.5654296875 -29.138549804688))\",\"description\":\"\"},"
			+ "{\"id\":\"PLY_3\",\"geometry\":\"POLYGON((141.84033203125 -29.553588867188,141.84033203125 -29.512084960938,141.86108398438 -29.512084960938,141.86108398438 -29.553588867188,141.84033203125 -29.553588867188))\",\"description\":\"\"},"
			+ "{\"id\":\"PLY_4\",\"geometry\":\"POLYGON((141.84033203125 -30.757202148438,141.84033203125 -29.595092773438,143.35522460938 -29.595092773438,143.35522460938 -30.757202148438,141.84033203125 -30.757202148438))\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_1\",\"geometry\":\"POINT(142.98168945313 -34.886840820313)\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_2\",\"geometry\":\"POINT(141.59130859375 -35.384887695313)\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_3\",\"geometry\":\"POINT(139.82739257813 -27.955688476563)\",\"description\":\"\"}] }";
	
	@AfterClass
	public static void afterClass() {
		MetaInfoExtractorTest.cleanCache();
	}
	
	/**
	 * Can we create a certificate report?
	 */
	@Test
	public void testGetDataForCertificateReport01() throws Exception {
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		List<CertificateOfPublicationReportBean> result = objectUnderTest.getDataForCertificateReport(sub.getId());
		assertThat(result.size(), is(1));
		CertificateOfPublicationReportBean resultBean = result.get(0);
		assertThat(resultBean.getSubmissionTitle(), is(SUBMISSION_NAME));
		assertThat(resultBean.getDatasetName(), is("Most awesome submission"));
		assertThat(resultBean.getDoi(), is("10.33327/05/537C5C6663F9E"));
		assertThat(resultBean.getLandingPageUrl(), is("http://aekos.org.au/dataset/" + sub.getId()));
		assertThat(resultBean.getLicenceType(), is("Creative Commons - Attribution 3.0 Australia"));
		assertThat(resultBean.getPublicationDateString(), is("12 December 2015"));
		assertThat(resultBean.getPublicationYear(), is("2015"));
		assertThat(resultBean.getSubmissionDateString(), is("10 December 2015"));
		assertThat(resultBean.getSubmissionId(), is(sub.getId()));
		assertThat(resultBean.getSubmitterNameString(), is("Dataset Contact Name"));
		assertThat(resultBean.getSubmitterOrganisationString(), is("Dataset Organisation Yall"));
	}

	private Submission prepareSubmission() throws Exception {
		QuestionnaireConfig qc = configService.readQuestionnaireConfig(QUESTIONNAIRE_NAME, true, false);
		SharedUser su = authService.getCreateDefaultUser("testReportUser");
		
		Submission result = new Submission();
		result.setSubmitter(su);
		result.setQuestionnaireConfig(entityDao.findById(qc.getSmsQuestionnaireId()));
		result.setTitle("report data test submission");
		result.setLastReviewDate(date("2015-12-12"));
		result.setSubmissionDate(date("2015-12-10"));
		result.setDoi("10.33327/05/537C5C6663F9E");
		
		result.getAnswers().add(textAnswer("1", "My Fantastic Dataset"));
		result.getAnswers().add(textAnswer("1.1", "Most awesome submission"));
		result.getAnswers().add(textAnswer("1.2", "1.2.wow"));
		result.getAnswers().add(textAnswer("1.3", SUBMISSION_NAME));
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
			multiselectTextChildAnswer("for - 1"),
			multiselectTextChildAnswer("for - 2")
		));
		result.getAnswers().add(multiselectTextAnswer("8",
			multiselectTextChildAnswer("seo - 1"),
			multiselectTextChildAnswer("seo - 2")
		));
		result.getAnswers().add(multiselectTextAnswer("9",
			multiselectTextChildAnswer("theme 1"),
			multiselectTextChildAnswer("theme 2")
		));
		result.getAnswers().add(textAnswer("10", "Dataset abstract all about my dataset"));
		result.getAnswers().add(dateAnswer("11", "21/11/2000"));
		result.getAnswers().add(dateAnswer("12", "13/10/2010"));
		result.getAnswers().add(textAnswer("13", "Study Area Description"));

		SubmissionAnswer answer15 = new SubmissionAnswer();
		answer15.setResponseType(ResponseType.GEO_FEATURE_SET);
		answer15.setResponse(GEO_FEATURE_SET_JSON);
		answer15.setQuestionId("15");
		result.getAnswers().add(answer15);
		
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
		return result;
	}
}
