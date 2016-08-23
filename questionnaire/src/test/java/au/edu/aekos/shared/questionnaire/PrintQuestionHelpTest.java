package au.edu.aekos.shared.questionnaire;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;

import org.junit.Test;

import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;

public class PrintQuestionHelpTest {
    
	QuestionnaireConfig readQuestionnaireConfig() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    return (QuestionnaireConfig) un.unmarshal( new File( "sharedQuestionnaire.xml" ) );
	}
	
	public static int NUMBER_COL_WIDTH = 10;
	public static int INDENT_SPACES = 2;
	
	
	@Test
	public void printQuestionnaireStructure() throws JAXBException{
		QuestionnaireConfig q = readQuestionnaireConfig();
		Assert.assertNotNull(q);
		for(Object item : q.getItems().getEntryList() ){
			if(item instanceof Question){
				printQuestion((Question) item, 0);
			}else if(item instanceof QuestionGroup){
				printQuestionGroup((QuestionGroup) item, 0);
			}else if(item instanceof MultipleQuestionGroup){
				printMultipleQuestionGroup((MultipleQuestionGroup) item, 0);
			}
		}
	}
	
	private void printQuestion(Question q, int indent){
		String paddedQid = padNumberColString(q.getId());
		System.out.println(indentStr(indent) + paddedQid +" " + q.getText() + "\n" + indentStr(indent) + q.getDescription() );
	}
	
    private void printQuestionGroup(QuestionGroup g, int indent){
    	String paddedQid = padNumberColString(g.getId());
    	System.out.println("\n" + indentStr(indent) + paddedQid +" " + g.getGroupTitle() + "\n" + indentStr(indent) + g.getGroupDescription());
    	
    	for(Object item : g.getItems().getEntryList() ){
			if(item instanceof Question){
				printQuestion((Question) item, indent + INDENT_SPACES);
			}else if(item instanceof QuestionGroup){
				printQuestionGroup((QuestionGroup) item,indent + INDENT_SPACES);
			}else if(item instanceof MultipleQuestionGroup){
				printMultipleQuestionGroup((MultipleQuestionGroup) item, indent + INDENT_SPACES);
			}
		}
	}
    
    private void printMultipleQuestionGroup(MultipleQuestionGroup m, int indent){
    	String paddedQid = padNumberColString(m.getId());
    	System.out.println(indentStr(indent) + paddedQid +" *" + m.getText() + "*" );
    	for(Object obj : m.getElements() ){
    		printQuestion((Question) obj, indent + INDENT_SPACES);
    	}
    }
    
    private String padNumberColString(String questionId){
    	int padRequired = NUMBER_COL_WIDTH - questionId.length(); 
    	for(int x = 0; x < padRequired; x++ ){
    		questionId = questionId + " ";
    	}
    	return questionId;
    }
    
    private String indentStr(int indent){
    	String indentStr = "";
    	if(indent == 0){
    		return indentStr;
    	}
    	for(int x = 0; x < indent; x++){
    		indentStr += " ";
    	}
    	return indentStr;
    }
    
    
	
	
}
