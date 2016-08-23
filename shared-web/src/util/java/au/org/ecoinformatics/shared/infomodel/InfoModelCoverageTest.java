package au.org.ecoinformatics.shared.infomodel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.org.aekos.shared.api.informationModel.InformationModelFactory;
import au.org.aekos.shared.api.model.infomodel.InformationModelEntry;
import au.org.aekos.shared.api.model.infomodel.SharedInformationModel;


//Checks the information model against the latest questionnaire
//to check the info model covers all defined metatags
public class InfoModelCoverageTest {

	@Test
	public void checkInfoModelCoverageOfLatestQuestionnaire() throws JAXBException{
		SharedInformationModel sharedInfoModel = InformationModelFactory.getSharedInformationModel();
		Map<String,InformationModelEntry> metatagToInfoModelEntryMap = sharedInfoModel.getMetatagToInfoModelEntryMap();
		
		File questionnaireXml = new File("src/main/resources/sharedQuestionnaire.xml");
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal(questionnaireXml );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        List<Question> questionList = config.getAllQuestions(true);
        Map<String, Question> metatagToQuestionMap = new HashMap<String, Question>();
        for(Question q : questionList){
        	if(StringUtils.hasLength(q.getMetatag())){
        	    if(!metatagToInfoModelEntryMap.containsKey(q.getMetatag())){
        	    	System.out.println("Q " + q.getId() + " meta " + q.getMetatag() + " not contained in information model" );
        	    }
        	    metatagToQuestionMap.put(q.getMetatag(), q);
        	}
        }
        
        for(InformationModelEntry ime : sharedInfoModel.getEntryList()){
        	if( Boolean.TRUE.equals(ime.getQuestionnaireAnswer() ) ){
        		if(! metatagToQuestionMap.containsKey( ime.getMetatag() ) ){
        			System.out.println("Questionnaire does not contain question for metatag " + ime.getMetatag());
        		}
        	}
        }
	}
	
	
}
