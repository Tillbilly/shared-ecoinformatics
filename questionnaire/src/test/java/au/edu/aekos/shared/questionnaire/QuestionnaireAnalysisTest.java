package au.edu.aekos.shared.questionnaire;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;



public class QuestionnaireAnalysisTest {

	QuestionnaireConfig readQuestionnaireConfig() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    return (QuestionnaireConfig) un.unmarshal( new File( "sharedQuestionnaire.xml" ) );
	}
	
	private class QuestionMeta{
		public String meta;
		public String quest;
		public String traitName;
		public String respType;
		
		QuestionMeta(Question q){
			this.meta = q.getMetatag();
			this.quest = q.getText();
			this.respType = q.getResponseType().name();
			this.traitName = q.getTraitName();
		}
		
		public String toString(){
			String met = ( meta == null ) ? " - " : meta ;
			String trait = ( traitName == null ) ? " - " : " \""+traitName+"\"";
			return met + "  " + respType + "  " + quest + "  " + trait ;
			
		}
		
		public String toStringShort(){
			String met = ( meta == null ) ? " - " : meta ;
			return met + "  " + respType ;
		}
	}
	
	
	@Test
	public void testPrintMetaModel() throws JAXBException{
		QuestionnaireConfig qc = readQuestionnaireConfig();
		List<Question> questionList = qc.getAllQuestions(true);
		System.out.println("There are " + questionList.size() + " questions in the questionnaire.");
		List<QuestionMeta> questionListNoMeta = new ArrayList<QuestionMeta>();
		List<QuestionMeta> questionListMeta = new ArrayList<QuestionMeta>();
		List<Question> questionsWithTraits = new ArrayList<Question>();
		for(Question q : questionList){
			if(q.getTraitName() != null){
				questionsWithTraits.add(q);
			}
			QuestionMeta qm = new QuestionMeta(q);
			if(q.getMetatag() != null ){
				questionListMeta.add(qm);
			}else{
			    questionListNoMeta.add(qm);	
			}
		}
		
		if(questionListNoMeta.size() > 0 ) {
			System.out.println("The following questions have no meta tags :");
			for(QuestionMeta qm : questionListNoMeta){
				System.out.println(qm.toString());
			}
		}else{
			System.out.println("All questions have meta tags");
		}
		
		for(QuestionMeta qm : questionListMeta){
			System.out.println(qm.toString());
		}
		for(QuestionMeta qm : questionListMeta){
			System.out.println(qm.toStringShort());
		}
		for(QuestionMeta qm : questionListMeta){
			System.out.println(qm.meta);
		}
		Set<String> customTraits = new LinkedHashSet<String>();
		Set<String> aekosTraits = new LinkedHashSet<String>();
		if(questionsWithTraits.size() > 0){
			System.out.println("\nControlled vocabularies used are :");
			for(Question q : questionsWithTraits){
				System.out.println(q.getTraitName() + "  " + q.getIsCustom() + "   " + q.getMetatag() );
			    if(q.getIsCustom()){
			    	customTraits.add(q.getTraitName());
			    }else{
			    	aekosTraits.add(q.getTraitName());
			    }
			}
			System.out.println("\nAekos traits referenced :");
			for(String trait : aekosTraits){
				System.out.println(trait);
			}
			
			System.out.println("\nCustom traits referenced :");
			for(String trait : customTraits){
				System.out.println(trait);
			}
		}
	}
	
	@Test
	public void testPrintQuestionnaireStats(){
		
	}
	
	@Test
	public void testSuggestMetatagsHaveAssociatedParentQuestions() throws JAXBException{
		QuestionnaireConfig qc = readQuestionnaireConfig();
		List<Question> questionList = qc.getAllQuestions(true);
		Set<String> metatags = new HashSet<String>();
		boolean fail = false;
		for(Question q : questionList){
			if(StringUtils.hasLength( q.getMetatag() ) ){
				if(metatags.contains(q.getMetatag())){
					System.out.println("Metatag " + q.getMetatag() + " is used more than once. See question " + q.getId() );
					fail = true;
				}else{
					metatags.add(q.getMetatag());
				}
			}
		}

		//Now check all 'SHD.metatagSuggest' metatags have an associated parent 'SHD.metatag'
		for(Question q : questionList){
			if(StringUtils.hasLength(q.getMetatag()) && 
					q.getMetatag().endsWith("Suggest") && 
					! metatags.contains(q.getMetatag().replaceAll("Suggest", "")) ){
				fail = true;
				System.out.println("Suggest metatag " + q.getMetatag() + " does not have parent question with metatag " + q.getMetatag().replaceAll("Suggest", "") + " for Q " + q.getId());
			}
		}
		if(fail){
			Assert.fail();
		}
			
		
	}
	
	
	
	
}
