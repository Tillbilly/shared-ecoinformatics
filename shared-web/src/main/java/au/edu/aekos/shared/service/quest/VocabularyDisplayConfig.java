package au.edu.aekos.shared.service.quest;

import java.util.ArrayList;
import java.util.List;

/**
 * If a vocab is specified in this list, then display values will be used
 * instead of vocab values for viewing and reviewing submissions on the
 * SHaRED side.
 * @author btill
 */
public class VocabularyDisplayConfig {
	
	private List<String> populateDisplayValuesForVocabList = new ArrayList<String>();

	public List<String> getPopulateDisplayValuesForVocabList() {
		return populateDisplayValuesForVocabList;
	}

	public void setPopulateDisplayValuesForVocabList(
			List<String> populateDisplayValuesForVocabList) {
		this.populateDisplayValuesForVocabList = populateDisplayValuesForVocabList;
	}
}
