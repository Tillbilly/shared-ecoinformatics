package au.edu.aekos.shared.web.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class JsonUploadSiteFileResponse implements Serializable{

	private static final long serialVersionUID = -5120511160093201320L;

	private String message;
	
	private String error;
	
	private String fileName;
	
	private String description;
	
	private String coordSys;
	
	private String coordSysOther;
	
	private List<JsonSite> sites = new ArrayList<JsonSite>();

	public List<JsonSite> getSites() {
		return sites;
	}

	public void setSites(List<JsonSite> sites) {
		this.sites = sites;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getJsonString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoordSys() {
		return coordSys;
	}

	public void setCoordSys(String coordSys) {
		this.coordSys = coordSys;
	}

	public String getCoordSysOther() {
		return coordSysOther;
	}

	public void setCoordSysOther(String coordSysOther) {
		this.coordSysOther = coordSysOther;
	}
	
}
