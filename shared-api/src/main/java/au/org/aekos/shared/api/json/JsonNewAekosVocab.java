package au.org.aekos.shared.api.json;

import java.util.ArrayList;
import java.util.List;


public class JsonNewAekosVocab {
	
	private String vocabName;
	private List<JsonVocabValue> values = new ArrayList<JsonVocabValue>();
	
	public String getVocabName() {
		return vocabName;
	}
	public void setVocabName(String vocabName) {
		this.vocabName = vocabName;
	}
	public List<JsonVocabValue> getValues() {
		return values;
	}
	public void setValues(List<JsonVocabValue> values) {
		this.values = values;
	}
	
	
	
}
