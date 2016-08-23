package au.edu.aekos.shared.questionnaire;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidator;

public class QuestionnaireValidationTest {
    
	@Test
	public void testQuestionnaire() {
		Object o = null;
		try{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    o = un.unmarshal( new File( "sharedQuestionnaire.xml" ) );
		} catch(JAXBException e){
			e.printStackTrace();
			Assert.fail();
		}
	    Assert.assertTrue(true);	
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        Assert.assertTrue(true);
        QuestionnaireConfigValidator validator = new QuestionnaireConfigValidator();
        BindingResult br = new BeanPropertyBindingResult(config, "config"); 
        validator.validate(config, br);
        if(br.hasErrors()){
        	for(ObjectError er : br.getAllErrors() ){
        		System.out.println(er.toString() );
        	}
        	Assert.fail("questionnaire fails validation!");
        }
        
        
        
        
	}
	
}
