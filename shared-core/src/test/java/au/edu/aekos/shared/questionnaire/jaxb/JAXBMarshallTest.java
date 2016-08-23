package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

public class JAXBMarshallTest {

	@Test
	public void testJaxbMarshalling1() throws JAXBException, IOException{
		QuestionnaireConfig quest = buildTestQuestionnaireConfig();
		writeDocument(quest);
	}
	
	@Test
	public void testJaxbMarshalling2() throws JAXBException, IOException{
		QuestionnaireConfig quest = buildTestQuestionnaireConfig2();
		writeDocument(quest);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static QuestionnaireConfig buildTestQuestionnaireConfig(){
		QuestionnaireConfig quest = new QuestionnaireConfig();
		quest.setVersion("version X");
		quest.setTitle("SHaRED AEKOS Submission Tool");
		quest.setSubtitle("Ecological Research Data Repository");
		quest.setIntroduction("Introduction to Shared - this is an introduction spiel - perhaps in <![CDATA[  some funky business ]]>");
		
		Items items = new Items();
		
		Question q1 = makeQuestion("1","What kind of data are you submitting?", ResponseType.CONTROLLED_VOCAB );
		
		Question q2 = makeQuestion("2","Is the data awesome?", ResponseType.YES_NO );
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("1");
		cd.setResponseNotNull(true);
		cd.setResponseValue("TEST_SELECT");
		
		q1.setDisplayCondition(cd);
		items.getEntryList().add(q1);
		
		quest.setItems(items);
		
		return quest;
		
	}
	
	public static Question makeQuestion(String id, String question, ResponseType responseType ){
		return new Question(id,question, " a description for help or something",
				true, responseType, null );
	}
	
	void writeDocument( QuestionnaireConfig document)
		    throws JAXBException, IOException {
		    //Class<T> clazz = document.getValue().getClass();
		    
	    JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	    
	    m.marshal( document, System.out );
	}
	
	
	@SuppressWarnings("unchecked")
	public static QuestionnaireConfig buildTestQuestionnaireConfig2(){
		QuestionnaireConfig quest = new QuestionnaireConfig();
		quest.setVersion("version X");
		quest.setTitle("SHaRED AEKOS Submission Tool - Example Config");
		quest.setSubtitle("Ecological Research Data Repository");
		quest.setIntroduction("Introduction to Shared - this is an introduction spiel - perhaps in <![CDATA[  some funky business ]]>");
		
		Items items = new Items();
		
		Question q1 = makeQuestion("1","What kind of data are you submitting?", ResponseType.CONTROLLED_VOCAB );
		
		Question q2 = makeQuestion("2","Is the data awesome?", ResponseType.YES_NO );
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("1");
		cd.setResponseNotNull(true);
		cd.setResponseValue("Ecological Data");
		
		q2.setDisplayCondition(cd);
		items.getEntryList().add(q1);
		items.getEntryList().add(q2);
		
		quest.setItems(items);
		
		return quest;
		
	}
	
	
	
	
}
