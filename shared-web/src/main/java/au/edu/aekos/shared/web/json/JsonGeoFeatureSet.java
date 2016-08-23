package au.edu.aekos.shared.web.json;

import java.util.ArrayList;
import java.util.List;

public class JsonGeoFeatureSet {

	private String srs = "EPSG:4283"; //Our back end uses EPSG:4283 ( GDA94 lon lats ) as a default.
	private String questionId;
	private List<JsonFeature> features = new ArrayList<JsonFeature>();
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public List<JsonFeature> getFeatures() {
		return features;
	}
	public void setFeatures(List<JsonFeature> features) {
		this.features = features;
	}
	public String getSrs() {
		return srs;
	}
	public void setSrs(String srs) {
		this.srs = srs;
	}
}
