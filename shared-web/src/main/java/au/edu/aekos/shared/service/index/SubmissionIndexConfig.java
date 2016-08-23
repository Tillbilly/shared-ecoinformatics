package au.edu.aekos.shared.service.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionIndexConfig {
	
	private String coreName;
	private String solrUrl;
    private Boolean authRequired = Boolean.FALSE;
    private String username;
    private String password;
	private String embeddedIndexLocation;	
	private boolean embeddedServer = false;
	
	private List<SubmissionIndexField> fieldMappingList = new ArrayList<SubmissionIndexField>();
	
	private Map<String, ManualSubmissionValueWriter> manualFieldWriterMap = new HashMap<String, ManualSubmissionValueWriter>();
	

	public List<SubmissionIndexField> getFieldMappingList() {
		return fieldMappingList;
	}

	public void setFieldMappingList(List<SubmissionIndexField> fieldMappingList) {
		this.fieldMappingList = fieldMappingList;
	}

	public String getCoreName() {
		return coreName;
	}

	public void setCoreName(String coreName) {
		this.coreName = coreName;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

	public String getEmbeddedIndexLocation() {
		return embeddedIndexLocation;
	}

	public void setEmbeddedIndexLocation(String embeddedIndexLocation) {
		this.embeddedIndexLocation = embeddedIndexLocation;
	}

	public boolean isEmbeddedServer() {
		return embeddedServer;
	}

	public void setEmbeddedServer(boolean embeddedServer) {
		this.embeddedServer = embeddedServer;
	}

	public Map<String, ManualSubmissionValueWriter> getManualFieldWriterMap() {
		return manualFieldWriterMap;
	}

	public void setManualFieldWriterMap(
			Map<String, ManualSubmissionValueWriter> manualFieldWriterMap) {
		this.manualFieldWriterMap = manualFieldWriterMap;
	}

	public Boolean getAuthRequired() {
		return authRequired;
	}

	public void setAuthRequired(Boolean authRequired) {
		this.authRequired = authRequired;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<String> getRequiredMetatags(){
		List<String> metatagList = new ArrayList<String>();
		for(SubmissionIndexField field : fieldMappingList){
			String tag = field.getSharedTag();
			if(tag != null && tag.startsWith("SHD")){
			    metatagList.add(field.getSharedTag());
			}
		}
		//Check embedded tag config in the Writers
		for(ManualSubmissionValueWriter writer :  manualFieldWriterMap.values() ){
			if(writer instanceof CompositeMetatagWriter){
				CompositeMetatagWriter cWriter = (CompositeMetatagWriter) writer;
				List<String> embeddedMetatagList = cWriter.getRequiredMetatags();
				if(embeddedMetatagList != null){
					metatagList.addAll(embeddedMetatagList);
				}
			}
		}
		return metatagList;
	}

}
