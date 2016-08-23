package au.org.aekos.shared.api.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class JsonNewAekosVocabResponse {
    
	private Date created = new Date();
	
	private Boolean success = Boolean.TRUE;
	
	private String message = null;
	
    private String errorMessage = null;	
    
    private List<JsonNewAekosVocab> newVocabEntriesList = new ArrayList<JsonNewAekosVocab>();
    
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<JsonNewAekosVocab> getNewVocabEntriesList() {
		return newVocabEntriesList;
	}
	public void setNewVocabEntriesList(List<JsonNewAekosVocab> newVocabEntriesList) {
		this.newVocabEntriesList = newVocabEntriesList;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getJsonString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
