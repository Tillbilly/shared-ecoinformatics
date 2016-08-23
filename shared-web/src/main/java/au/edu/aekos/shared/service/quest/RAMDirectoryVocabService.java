package au.edu.aekos.shared.service.quest;

import java.io.IOException;
import java.util.List;



public interface RAMDirectoryVocabService {
	
	List<TraitValue> performSearch(String term, String traitName, boolean termHighlight) throws IOException;
	
	List<TraitValue> performSearch(String term, String traitName, int numResults, boolean termHighlight) throws IOException;
	
	List<TraitValue> performSearchForSpeciesMatching(String term, String traitName, int listLength) throws IOException;

	TraitValue getBestFuzzyMatch(String term, String traitName) throws IOException;
	
	TraitValue performExactMatchSearchTraitValue(String term, String traitName) throws IOException;
	
}
