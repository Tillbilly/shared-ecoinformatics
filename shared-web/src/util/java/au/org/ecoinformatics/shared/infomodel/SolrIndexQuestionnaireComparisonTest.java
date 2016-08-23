package au.org.ecoinformatics.shared.infomodel;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.index.SubmissionIndexConfig;



public class SolrIndexQuestionnaireComparisonTest {

	@Test
	public void testSolrConfigCoverage() throws JAXBException {
		File questionnaireXml = new File("src/main/resources/sharedQuestionnaire.xml");
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal(questionnaireXml );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        List<Question> questionList = config.getAllQuestions(true);
        
        ApplicationContext springContext = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/spring/appServlet/pubindex-context.xml" );
        Assert.assertNotNull(springContext);
        
        if(!springContext.containsBean("pubindexConfig")){
        	Assert.fail("No bean named 'pubindexConfig' in runtime pubindex-context");
        }
        
        SubmissionIndexConfig indexConfig = (SubmissionIndexConfig ) springContext.getBean("pubindexConfig");
        List<String> metatagList = indexConfig.getRequiredMetatags();
        
        for(Question q : questionList){
        	if(StringUtils.hasLength( q.getMetatag() ) ){
        		if(! metatagList.contains(q.getMetatag()) ){
        			System.out.println("Q " + q.getId() + " meta " + q.getMetatag() + " is not defined in pubindex-context");
        		}
        	}
        }
	}
	
	
}
