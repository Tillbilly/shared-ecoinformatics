package au.edu.aekos.shared.valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.data.infomodel.MetatagEvolutionConfig;
import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.doi.DoiDataConfig;
import au.edu.aekos.shared.service.index.SubmissionIndexConfig;
import au.edu.aekos.shared.service.rifcs.SharedRifcsMappingConfig;

@Component
public class QuestionnaireMetamodelValidator implements Validator {

	@Autowired
	private SharedRifcsMappingConfig rifcsConfig;
	
	@Autowired
	private DoiDataConfig doiConfig;
	
	@Autowired
	private SubmissionIndexConfig indexConfig;
	
	@Autowired
	private CommonConceptMetatagConfig commonMetatagConfig;

	@Autowired
	private MetatagEvolutionConfig evolutionConfig;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
	}
	
	public void validateQuestionnaireContainsPublishMetatags(QuestionnaireConfig qc, Errors errors){
		Map<String, String> metatagToQIdMap = qc.getMetatagToQuestionIdMap();
		
		//Common concept metatags
		List<String> ccMetatags = commonMetatagConfig.getRequiredMetatags();
		for(String ccTag : ccMetatags){
			if(! metatagToQIdMap.containsKey(ccTag) && ! evolutionConfig.containsHandler(ccTag)){
				errors.reject("", "Metatag config requires metatag '" + ccTag +"', not present in questionnaire config " + qc.getVersion());
			}
		}
		
		//Doi tags
		List<String> doiMetatags = doiConfig.getRequiredMetatags();
		for(String doiTag : doiMetatags){
			if(! metatagToQIdMap.containsKey(doiTag) && ! evolutionConfig.containsHandler(doiTag)){
				errors.reject("", "DOI minting config requires metatag '" + doiTag +"', not present in questionnaire config " + qc.getVersion());
			}
		}
		
		//Rifcs tags
		List<String> rifcsMetatags = rifcsConfig.getRequiredMetatags();
		for(String rifcsTag : rifcsMetatags){
			if(! metatagToQIdMap.containsKey(rifcsTag) && ! evolutionConfig.containsHandler(rifcsTag)){
				errors.reject("", "Rifcs config requires metatag '" + rifcsTag +"', not present in questionnaire config " + qc.getVersion());
			}
		}
		
		//Publication Index
		List<String> indexMetatags = indexConfig.getRequiredMetatags();
		for(String indexTag : indexMetatags){
			if(! metatagToQIdMap.containsKey(indexTag) && ! evolutionConfig.containsHandler(indexTag)){
				errors.reject("", "Index config requires metatag '" + indexTag +"', not present in questionnaire config " + qc.getVersion());
			}
		}
		
	}

}
