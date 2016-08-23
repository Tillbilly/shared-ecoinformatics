package au.edu.aekos.shared.web.json;

import com.google.gson.Gson;

public class JsonIsSessionActiveResponse {

	private final boolean sessionActive;
	
	public JsonIsSessionActiveResponse(boolean sessionActive) {
		this.sessionActive = sessionActive;
	}

	public boolean isSessionActive() {
		return sessionActive;
	}

	public String getJsonString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
