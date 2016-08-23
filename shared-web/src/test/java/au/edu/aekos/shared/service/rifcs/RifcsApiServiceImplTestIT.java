package au.edu.aekos.shared.service.rifcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.file.SiteFileService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.rifcs.RifcsApiServiceImplTest.RelatedInfoAnswersCallback;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.spatial.BBOX;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;
import au.edu.aekos.shared.web.json.JsonSpatialUtil;
import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
@Transactional
public class RifcsApiServiceImplTestIT {

	private static final Logger logger = LoggerFactory.getLogger(RifcsApiServiceImplTestIT.class);
	
	@Autowired
	private SiteFileService siteFileService;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private QuestionnaireConfigEntityDao entityDao;
	
	@Autowired
	private SecurityService authService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private RifcsService rifcsService;
	
	@Autowired
	private SharedRifcsMappingConfig config;
	
	private static final String QUESTIONNAIRE_NAME = "rifcsTestQuestionnaire.xml";
	private static final String GEO_FEATURE_SET_JSON = "{ \"questionId\" : \"1.2.4\", \"features\" : [ "
			+ "{\"id\":\"PLY_1\",\"geometry\":\"POLYGON(("
				+ "139.35009765625 -32.479614257813,139.57836914063 -33.496459960938,"
				+ "140.90649414063 -33.517211914063,141.44604492188 -32.147583007813,"
				+ "141.09326171875 -31.753295898438,140.61596679688 -32.749389648438,"
				+ "140.0556640625 -30.425170898438,138.12573242188 -31.109985351563,"
				+ "139.35009765625 -32.479614257813))\",\"description\":\"friendly\"},"
			+ "{\"id\":\"PLY_2\",\"geometry\":\"POLYGON(("
				+ "137.5654296875 -29.138549804688,137.482421875 -30.445922851563,"
				+ "135.92602539063 -29.180053710938,137.5654296875 -29.138549804688))\",\"description\":\"\"},"
			+ "{\"id\":\"PLY_3\",\"geometry\":\"POLYGON(("
				+ "141.84033203125 -29.553588867188,141.84033203125 -29.512084960938,"
				+ "141.86108398438 -29.512084960938,141.86108398438 -29.553588867188,"
				+ "141.84033203125 -29.553588867188))\",\"description\":\"\"},"
			+ "{\"id\":\"PLY_4\",\"geometry\":\"POLYGON(("
				+ "141.84033203125 -30.757202148438,141.84033203125 -29.595092773438,"
				+ "143.35522460938 -29.595092773438,143.35522460938 -30.757202148438,"
				+ "141.84033203125 -30.757202148438))\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_1\",\"geometry\":\"POINT(142.98168945313 -34.886840820313)\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_2\",\"geometry\":\"POINT(141.59130859375 -35.384887695313)\",\"description\":\"\"},"
			+ "{\"id\":\"PNT_3\",\"geometry\":\"POINT(139.82739257813 -27.955688476563)\",\"description\":\"\"}] }";
	private Gson gson = new Gson();
	
	/**
	 * Can we generate a bounding box from a site file?
	 */
	@Test
	public void testSpatialCoverageGeneration01() throws IOException {
        Resource rs = new ClassPathResource("sites-rifcs.csv");
		JsonUploadSiteFileResponse response = null;
		response = siteFileService.parseSiteFileToJson(rs.getFile(), "EPSG:4283", true);
		BBOX result = siteFileService.calculateBBOX(response);
		logger.info(result.getIso19139dcmiBox());
		assertEquals(
				"northlimit=-34.56789; southlimit=-38.33000; eastlimit=137.34000; westlimit=136.34000; projection=GDA94", 
				result.getIso19139dcmiBox());
	}
	
	/**
	 * Can we generate a bounding box from a response to the map picker question?
	 */
	@Test
	public void testSpatialCoverageGeneration02() {
		JsonGeoFeatureSet geoFeatureSet = gson.fromJson(GEO_FEATURE_SET_JSON, JsonGeoFeatureSet.class);
		BBOX result = JsonSpatialUtil.calculateBBOXFromJsonGeoFeatureSet(geoFeatureSet);
		assertNotNull(result);
		logger.info(result.getIso19139dcmiBox() );
		assertEquals(
				"northlimit=-27.95569; southlimit=-35.38489; eastlimit=143.35522; westlimit=135.92603; projection=GDA94", 
				result.getIso19139dcmiBox());
	}
	
	/**
	 * Can we SCP a RIF-CS file to the OAI server?
	 */
	@Test   //Takes a detailed knowledge of the questionnaire file above to make this work . . . 
	public void testGenerateRifcsDocumentForSharedSubmission() throws Exception{
		Submission sub = prepareSubmission();
		submissionDao.save(sub);
		assertNotNull(sub.getId());
		String xml = rifcsService.buildRifcsXmlFromSubmission(sub);
		logger.info(formatXml(xml));
		try {
			String xml2 = rifcsService.processRifcs(sub);
			assertEquals(xml, xml2);
		} finally {
			submissionDao.delete(sub);
		}
	}
	
	@Test
	public void testGetMetatagsFromConfigObject(){
		List<String> metatags = config.getRequiredMetatags();
		assertNotNull(metatags);
		for(String tag : metatags){
			logger.info(tag);
		}
	}

	private Submission prepareSubmission() throws Exception {
		QuestionnaireConfig qc = configService.readQuestionnaireConfig(QUESTIONNAIRE_NAME, true, false);
		SharedUser su = authService.getCreateDefaultUser("testRifcsITUser");
		QuestionnaireConfigEntity configId = entityDao.findById(qc.getSmsQuestionnaireId());
		return RifcsApiServiceImplTest.buildSubmissionWithRelatedInfoAnswer(su, configId, new RelatedInfoAnswersCallback() {
			@Override public void addAnswers(Submission sub) { /* do nothing */ }
		});
	}
	
	public static String formatXml(String unformattedXml){
		try {
	        Source xmlInput = new StreamSource(new StringReader(unformattedXml));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", 2);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}
}
