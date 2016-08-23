package au.edu.aekos.shared.valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.TraitValue;
import au.edu.aekos.shared.web.model.VocabAnalysisModel;



/**
 * I guess its just a convention to keep using the Validator interface
 * @author btill
 */
@Component
public class AddEntryVocabAnalysisValidator implements Validator {
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return VocabAnalysisModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		VocabAnalysisModel vam = (VocabAnalysisModel) target;
		if(vam.getNewVocabEntryMap() == null || vam.getNewVocabEntryMap().size() == 0){
			return;
		}
		
		boolean isCustom = controlledVocabularyService.getListOfAvailableCustomTraits().contains( vam.getVocabName() );
		
		List<String> valuesToAdd = new ArrayList<String>(); //Used to ensure the same vocab value is not entered twice
		
		for(Map.Entry<String, TraitValue> entry : vam.getNewVocabEntryMap().entrySet()){
			if(controlledVocabularyService.traitListContainsValue(vam.getVocabName(),  isCustom, entry.getValue().getTraitValue() ) ){
				Object [] errorArgs = { entry.getValue().getTraitValue() , vam.getVocabName() };
				errors.rejectValue("newVocabEntryMap["+entry.getKey() + "].traitValue", "vocab.analysis.addValueAlreadyExists", errorArgs, "vocab entry already exists");
			}else if(valuesToAdd.contains(entry.getValue().getTraitValue())){
				errors.rejectValue("newVocabEntryMap["+entry.getKey() + "].traitValue", "vocab.analysis.addValueAlreadyToBeCreated", "vocab.analysis.addValueAlreadyToBeCreated");
			}
			valuesToAdd.add(entry.getValue().getTraitValue());
		}
	}

}
