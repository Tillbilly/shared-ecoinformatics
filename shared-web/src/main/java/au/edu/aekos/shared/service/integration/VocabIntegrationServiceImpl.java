package au.edu.aekos.shared.service.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.CustomVocabularyDao;
import au.edu.aekos.shared.data.dao.NewAekosVocabEntryDao;
import au.edu.aekos.shared.data.entity.CustomVocabulary;
import au.edu.aekos.shared.data.entity.NewAekosVocabEntry;
import au.org.aekos.shared.api.json.JsonNewAekosVocab;
import au.org.aekos.shared.api.json.JsonNewAekosVocabResponse;
import au.org.aekos.shared.api.json.JsonSharedVocab;
import au.org.aekos.shared.api.json.JsonSharedVocabResponse;
import au.org.aekos.shared.api.json.JsonVocabValue;

@Service
public class VocabIntegrationServiceImpl implements VocabIntegrationService {

	@Autowired
	private NewAekosVocabEntryDao newVocabDao;
	
	@Autowired
	private CustomVocabularyDao customVocabDao;
	
	@Override @Transactional
	public JsonNewAekosVocabResponse getAllNewAekosIndexValues() {
		JsonNewAekosVocabResponse response = new JsonNewAekosVocabResponse();
		List<NewAekosVocabEntry> allNewEntryList = newVocabDao.retrieveNewEntries();
		if(allNewEntryList == null || allNewEntryList.size() == 0){
			response.setMessage("No new vocab entries");
			return response;
		}
		Map<String, List<JsonVocabValue>> vocabToVocabValueMap = new HashMap<String, List<JsonVocabValue>>();
		for(NewAekosVocabEntry newEntry : allNewEntryList){
			JsonVocabValue jsonVal = convertNewAekosVocabEntryToJsonVocabValue( newEntry);
			if( ! vocabToVocabValueMap.containsKey( newEntry.getVocabularyName() ) ){
				vocabToVocabValueMap.put(newEntry.getVocabularyName(), new ArrayList<JsonVocabValue>());
			}
			vocabToVocabValueMap.get(newEntry.getVocabularyName()).add(jsonVal);
		}
		for(Map.Entry<String, List<JsonVocabValue>> entry : vocabToVocabValueMap.entrySet()){
			JsonNewAekosVocab aekosVocab = new JsonNewAekosVocab();
			aekosVocab.setVocabName(entry.getKey());
			aekosVocab.setValues(entry.getValue());
			response.getNewVocabEntriesList().add(aekosVocab);
		}
		
		return response;
	}

	@Override @Transactional
	public JsonNewAekosVocabResponse getNewAekosIndexValues(String vocabName) {
		JsonNewAekosVocabResponse response = new JsonNewAekosVocabResponse();
		List<NewAekosVocabEntry> newEntryList = newVocabDao.retrieveNewEntriesForVocabulary(vocabName);
		if(newEntryList == null || newEntryList.size() == 0){
			response.setMessage("No new vocab entries");
			return response;
		}
		JsonNewAekosVocab aekosVocab = new JsonNewAekosVocab();
		aekosVocab.setVocabName(vocabName);
		for(NewAekosVocabEntry vocabEntry : newEntryList){
			JsonVocabValue val = convertNewAekosVocabEntryToJsonVocabValue(vocabEntry);
			aekosVocab.getValues().add(val);
		}
		response.getNewVocabEntriesList().add(aekosVocab);
		return response;
	}

	private JsonVocabValue convertNewAekosVocabEntryToJsonVocabValue(NewAekosVocabEntry newEntry){
		JsonVocabValue vocabValue = new JsonVocabValue();
		vocabValue.setValue(newEntry.getValue());
		return vocabValue;
	}

	@Override @Transactional
	public JsonSharedVocabResponse getAllSharedVocabs() {
		JsonSharedVocabResponse response = new JsonSharedVocabResponse();
		List<CustomVocabulary> customVocabularyList = customVocabDao.getAllForExtract();
		if(customVocabularyList == null || customVocabularyList.size() == 0){
			response.setMessage("No new vocab entries");
			return response;
		}
		Map<String,List<JsonVocabValue>> vocabNameToValuesMap = new HashMap<String,List<JsonVocabValue>>();
		for(CustomVocabulary cv : customVocabularyList ){
			JsonVocabValue jvv = convertCustomVocabToJsonVocabValue(cv);
			if( ! vocabNameToValuesMap.containsKey(cv.getVocabularyName())){
				vocabNameToValuesMap.put(cv.getVocabularyName(), new ArrayList<JsonVocabValue>());
			}
			vocabNameToValuesMap.get(cv.getVocabularyName()).add(jvv);
		}
		for(Map.Entry<String, List<JsonVocabValue>> entry : vocabNameToValuesMap.entrySet()){
			JsonSharedVocab jsv = new JsonSharedVocab();
			jsv.setVocabName(entry.getKey());
			jsv.setValues(entry.getValue());
			response.getSharedVocabList().add(jsv);
		}
		return response;
	}

	@Override @Transactional
	public JsonSharedVocabResponse getSharedVocab(String vocabName) {
		JsonSharedVocabResponse response = new JsonSharedVocabResponse();
		List<CustomVocabulary> customVocabularyList = customVocabDao.retrieveCustomVocabularyByName(vocabName);
		if(customVocabularyList == null || customVocabularyList.size() == 0){
			response.setMessage("No vocab entries for " + vocabName);
			return response;
		}
		JsonSharedVocab jsv = new JsonSharedVocab();
		jsv.setVocabName(vocabName);
		for(CustomVocabulary cv : customVocabularyList){
			jsv.getValues().add(convertCustomVocabToJsonVocabValue(cv) );
		}
		response.getSharedVocabList().add(jsv);
		return response;
	}
	
	private JsonVocabValue convertCustomVocabToJsonVocabValue(CustomVocabulary customVocab){
		JsonVocabValue vocabValue = new JsonVocabValue();
		vocabValue.setValue(customVocab.getValue());
		vocabValue.setDisplayValue(customVocab.getDisplayValue());
		vocabValue.setParent(customVocab.getParentValue());
		return vocabValue;
	}
	
	
}
