package au.org.aekos.shared.questionnaire;

import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.jaxb.DefaultVocabulary;
import au.edu.aekos.shared.questionnaire.jaxb.DefaultVocabularyTag;
/**
 * Header to test git
 */
public class QuestionnaireUtils {

	public static void cleanseCharactersFromDefaultVocabulary(DefaultVocabulary vocab){
		if(vocab != null && vocab.getListEntries() != null && vocab.getListEntries().size() > 0){
			for(DefaultVocabularyTag tag : vocab.getListEntries()){
				cleanseCharactersFromDefaultVocabularyTag(tag);
			}
		}
	}
	
	public static void cleanseCharactersFromDefaultVocabularyTag(DefaultVocabularyTag tag){
		if(StringUtils.hasLength( tag.getValue() ) ){
			tag.setValue(cleanseCharactersFromString(tag.getValue()) ) ;
		}
	}
	
	public static String cleanseCharactersFromString(String in){
		in = in.replaceAll("[\\t\\r]", "");
		return in.trim();
	}
	
}
