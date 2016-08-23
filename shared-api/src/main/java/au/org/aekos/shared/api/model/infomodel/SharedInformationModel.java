package au.org.aekos.shared.api.model.infomodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public class SharedInformationModel {

	private List<InformationModelEntry> entryList = new ArrayList<InformationModelEntry>();
	
	private transient Map<String, InformationModelEntry> metatagToInfoModelEntryMap = new HashMap<String, InformationModelEntry>();
	
	public List<InformationModelEntry> getEntriesForGroupTitle(String groupTitle){
		List<InformationModelEntry> groupEntryList = new ArrayList<InformationModelEntry>();
		if(StringUtils.hasLength(groupTitle)){
			for(InformationModelEntry entry : entryList){
				if(groupTitle.equalsIgnoreCase(entry.getGroup())){
					groupEntryList.add(entry);
				}
			}
		}
		return groupEntryList;
	}
	
	public Map<String, InformationModelEntry> getMetatagToInfoModelEntryMap(){
		if(metatagToInfoModelEntryMap != null && metatagToInfoModelEntryMap.size() > 0){
			return metatagToInfoModelEntryMap;
		}
		metatagToInfoModelEntryMap = new HashMap<String, InformationModelEntry>();
		for(InformationModelEntry entry : entryList){
			if(StringUtils.hasLength(entry.getMetatag())){
				metatagToInfoModelEntryMap.put(entry.getMetatag(), entry);
			}
		}
		return metatagToInfoModelEntryMap;
	}
	
	public List<InformationModelEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<InformationModelEntry> entryList) {
		this.entryList = entryList;
	}
	
	public SharedInformationModel clone(){
		SharedInformationModel clonedModel = new SharedInformationModel();
		for(InformationModelEntry imEntry : entryList){
			clonedModel.getEntryList().add(imEntry.clone());
		}
		return clonedModel;
	}
	
	
}
