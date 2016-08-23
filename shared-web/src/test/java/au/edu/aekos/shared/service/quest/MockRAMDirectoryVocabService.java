package au.edu.aekos.shared.service.quest;

import java.io.IOException;
import java.util.List;

public class MockRAMDirectoryVocabService implements RAMDirectoryVocabService {

	@Override
	public List<TraitValue> performSearch(String term, String traitName,
			boolean termHighlight) throws IOException {
		return null;
	}

	@Override
	public List<TraitValue> performSearch(String term, String traitName,
			int numResults, boolean termHighlight) throws IOException {
		return null;
	}

	@Override
	public List<TraitValue> performSearchForSpeciesMatching(String term,
			String traitName, int listLength) throws IOException {
		return null;
	}

	@Override
	public TraitValue getBestFuzzyMatch(String term, String traitName)
			throws IOException {
		return null;
	}

	@Override
	public TraitValue performExactMatchSearchTraitValue(String term,
			String traitName) throws IOException {
		return null;
	}
}
