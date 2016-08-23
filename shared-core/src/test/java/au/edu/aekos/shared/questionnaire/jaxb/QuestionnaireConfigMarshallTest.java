package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;

import org.junit.Test;

public class QuestionnaireConfigMarshallTest {
	
	@Test
	public void testJaxbMarshalling2() throws JAXBException, IOException{
		QuestionnaireConfig quest = buildTestQuestionnaireConfig2();
		writeDocument(quest);
	}
	
	@Test
	public void testJaxbMarshalling3() throws JAXBException, IOException{
		QuestionnaireConfig quest = buildTestQuestionnaireConfig3();
		writeDocument(quest);
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
		q1.setTraitName("Species");
		
		Question q2 = makeQuestion("2","Is the data awesome?", ResponseType.YES_NO );
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("1");
		cd.setResponseNotNull(true);
		cd.setResponseValue("Ecological Data");
		
		q2.setDisplayCondition(cd);
		
		Question q3 = makeQuestion("3","No really, what kind of data are you submitting?", ResponseType.CONTROLLED_VOCAB );
		DefaultVocabulary vocab = new DefaultVocabulary();
		vocab.getListEntries().add(new DefaultVocabularyTag("Animal"));
		vocab.getListEntries().add(new DefaultVocabularyTag("Vegetable", "A genus definition"));
		vocab.getListEntries().add(new DefaultVocabularyTag("Mineral", "A genus definition"));
		q3.setTraitName("genus-def");
		q3.setDefaultVocabulary(vocab);
		
		items.getEntryList().add(q1);
		items.getEntryList().add(q2);
		items.getEntryList().add(q3);
		
		quest.setItems(items);
		
		return quest;
		
	}
	
	@SuppressWarnings("unchecked")
	public static QuestionnaireConfig buildTestQuestionnaireConfig3(){
		QuestionnaireConfig quest = new QuestionnaireConfig();
		quest.setVersion("version X");
		quest.setTitle("SHaRED AEKOS Submission Tool - Example Config 3");
		quest.setSubtitle("Ecological Research Data Repository");
		quest.setIntroduction("Introduction to Shared - Question Group and Page Break Example");
		
		Items items = new Items();
		
		Question q1 = makeQuestion("1","What kind of data are you submitting?", ResponseType.CONTROLLED_VOCAB );
		q1.setTraitName("Species");
		
		Question q2 = makeQuestion("2","Is the data awesome?", ResponseType.YES_NO );
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("1");
		cd.setResponseNotNull(true);
		cd.setResponseValue("Ecological Data");
		
		q2.setDisplayCondition(cd);
		
		Question q3 = makeQuestion("3","No really, what kind of data are you submitting?", ResponseType.CONTROLLED_VOCAB );
		DefaultVocabulary vocab = new DefaultVocabulary();
		vocab.getListEntries().add(new DefaultVocabularyTag("Animal"));
		vocab.getListEntries().add(new DefaultVocabularyTag("Vegetable", "A genus definition"));
		vocab.getListEntries().add(new DefaultVocabularyTag("Mineral", "A genus definition"));
		q3.setTraitName("genus-def");
		q3.setDefaultVocabulary(vocab);
		
		items.getEntryList().add(q1);
		items.getEntryList().add(q2);
		items.getEntryList().add(q3);
		items.getEntryList().add(new PageBreak("Page 2")) ;
        
		QuestionGroup group1 = new QuestionGroup("100","Question Group 100", "Group Description");
		Question gq1 = makeQuestion("101", "Question number 101?", ResponseType.TEXT);
		Question gq2 = makeQuestion("102", "Question number 102?", ResponseType.DATE);
		Question gq3 = makeQuestion("103", "Question number 103?", ResponseType.COORDINATE);
		Question gq4 = makeQuestion("104", "Question number 104?", ResponseType.BBOX);
		
		ConditionalDisplay cond = new ConditionalDisplay();
		cond.setQuestionId("103");
		cond.setResponseNotNull(true);
		gq4.setDisplayCondition(cond);
		
		
		
		group1.getItems().getEntryList().add(gq1);
		group1.getItems().getEntryList().add(gq2);
		group1.getItems().getEntryList().add(gq3);
		group1.getItems().getEntryList().add(gq4);
		
		QuestionGroup group2 = new QuestionGroup("200","Question Group 200", "Description - Mineral Specific Questions");
		ConditionalDisplay condi = new ConditionalDisplay();
		condi.setQuestionId("3");
		condi.setResponseNotNull(true);
		condi.setResponseValue("Mineral");
		group2.setDisplayCondition(condi);
		
		QuestionGroup group3 = new QuestionGroup("2001","Question Sub Group 20000", " Sub Group Description");
		Question sgq1 = makeQuestion("20001", "Question number 20001?", ResponseType.TEXT);
		Question sgq2 = makeQuestion("20002", "Question number 20002?", ResponseType.DATE);
		group3.getItems().getEntryList().add(sgq1);
		group3.getItems().getEntryList().add(sgq2);
		group2.getItems().getEntryList().add(group3);
		
		items.getEntryList().add(group1);
		items.getEntryList().add(group2);
		
		quest.setItems(items);
		
		return quest;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
