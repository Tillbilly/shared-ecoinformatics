package au.edu.aekos.shared.service.integration;

import au.org.aekos.shared.api.json.JsonNewAekosVocabResponse;
import au.org.aekos.shared.api.json.JsonSharedVocabResponse;

public interface VocabIntegrationService {
	JsonNewAekosVocabResponse getAllNewAekosIndexValues();
	JsonNewAekosVocabResponse getNewAekosIndexValues(String vocabName);
	JsonSharedVocabResponse getAllSharedVocabs();
	JsonSharedVocabResponse getSharedVocab(String vocabName);
}
