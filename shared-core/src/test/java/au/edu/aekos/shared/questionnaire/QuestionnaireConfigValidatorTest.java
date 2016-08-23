package au.edu.aekos.shared.questionnaire;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidator;

public class QuestionnaireConfigValidatorTest {

	@Test
	public void testValidateDisplayConditions_questionPresentAndInOrder(){
		
		QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
		
		//Test display condition for question that does not exist.
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("TEST");
		
		Map<String, Question> questionMap = new HashMap<String,Question>();
		QuestionnaireConfig config = new QuestionnaireConfig();
		BindingResult br = new BeanPropertyBindingResult(config, "config"); 
		configValidator.validateDisplayConditions(cd, "ID", true, questionMap, br);
		Assert.assertTrue(br.hasErrors());
		ObjectError er = br.getGlobalError();
		System.out.println(er.getDefaultMessage() );
		br = new BeanPropertyBindingResult(config, "config"); 
		configValidator.validateDisplayConditions(cd, "ID", false, questionMap, br);
		Assert.assertTrue(br.hasErrors());
		er = br.getGlobalError();
		System.out.println(er.getDefaultMessage() );
		
		//Test does not fail when question is present.
		Question q = new Question("TEST","you like?","desc",false, ResponseType.TEXT, null);
		questionMap.put(q.getId(), q);
		br = new BeanPropertyBindingResult(config, "config"); 
		configValidator.validateDisplayConditions(cd, "ID", true, questionMap, br);
		Assert.assertFalse(br.hasErrors());
	}
	
	@Test
	public void testValidateDisplayConditions_questionPrerequisiteQuestionOfCorrectType(){
		
		QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
		QuestionnaireConfig config = new QuestionnaireConfig();
		//Test display condition for question that does not exist.
		ConditionalDisplay cd = new ConditionalDisplay();
		cd.setQuestionId("TEST");
		cd.setResponseNotNull(true);
		cd.setResponseValue("carnivores");
		
		Map<String, Question> questionMap = new HashMap<String,Question>();
		
		Question q = new Question("TEST","you like?","desc",false, ResponseType.TEXT, null);
		questionMap.put(q.getId(), q);
		BindingResult br = new BeanPropertyBindingResult(config, "config"); 
		configValidator.validateDisplayConditions(cd, "ID", true, questionMap, br);
		Assert.assertTrue(br.hasErrors());
		
		q = new Question("TEST","you like?","desc",false, ResponseType.CONTROLLED_VOCAB, null);
		questionMap.put(q.getId(), q);
		br = new BeanPropertyBindingResult(config, "config"); 
		configValidator.validateDisplayConditions(cd, "ID", true, questionMap, br);
		Assert.assertFalse(br.hasErrors());
		
	}
	
	@Test
	public void testSubmissionTitleConfig() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/questionnaire_titleval1.xml" ) );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
        BindingResult br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        
        o = un.unmarshal( new File( "src/test/resources/questionnaire_titleval2.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
		
	}
	
	@Test
	public void testReusableGroup() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/questionnaire_reuseGroup.xml" ) );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
        BindingResult br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertFalse(br.hasErrors());
        
        o = un.unmarshal( new File( "src/test/resources/questionnaire_reuseGroup_fail1.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        for(ObjectError err : br.getAllErrors() ){
        	System.out.println(err.getDefaultMessage());
        }
        
        o = un.unmarshal( new File( "src/test/resources/questionnaire_reuseGroup_fail2.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        for(ObjectError err : br.getAllErrors() ){
        	System.out.println(err.getDefaultMessage());
        }
        
        o = un.unmarshal( new File( "src/test/resources/questionnaire_reuseGroup_falsePosFix.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertFalse(br.hasErrors());
        for(ObjectError err : br.getAllErrors() ){
        	System.out.println(err.getDefaultMessage());
        }
	}
	
	@Test
	public void testControlledVocabSameAsOption() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/questionnaire_extendedcv.xml" ) );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
        BindingResult br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertFalse(br.hasErrors());
		
        o = un.unmarshal( new File( "src/test/resources/questionnaire_extendedcv_fail1.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        for(ObjectError err : br.getAllErrors()){
        	System.out.println(err.toString());
        }
        
        o = un.unmarshal( new File( "src/test/resources/questionnaire_extendedcv_fail2.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        for(ObjectError err : br.getAllErrors()){
        	System.out.println(err.toString());
        }
        
        o = un.unmarshal( new File( "src/test/resources/questionnaire_extendedcv_fail3.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        for(ObjectError err : br.getAllErrors()){
        	System.out.println(err.toString());
        }
	}
	
	@Test
	public void testPrepopulate() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/questionnaire_prepop.xml" ) );
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
        BindingResult br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertFalse(br.hasErrors());
        o = un.unmarshal( new File( "src/test/resources/questionnaire_prepop_fail.xml" ) );
        config = (QuestionnaireConfig) o;
        br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        Assert.assertTrue(br.hasErrors());
        Assert.assertEquals(3, br.getAllErrors().size() );
        for(ObjectError err : br.getAllErrors()){
        	System.out.println(err.toString());
        }
		
		
	}
	
	
	
}
