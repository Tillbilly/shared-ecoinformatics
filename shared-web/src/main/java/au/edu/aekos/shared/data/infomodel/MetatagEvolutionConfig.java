package au.edu.aekos.shared.data.infomodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MetatagEvolutionConfig implements InitializingBean {

	@Autowired(required=false)  //These are defined in the evolution-context
	private List<ChangedMetatagHandler> metatagHandlerList = new ArrayList<ChangedMetatagHandler>();
	
	private Map<String, ChangedMetatagHandler> changeHandlerMap = new HashMap<String, ChangedMetatagHandler>();

	public Map<String, ChangedMetatagHandler> getChangeHandlerMap() {
		return changeHandlerMap;
	}

	public void setChangeHandlerMap(
			Map<String, ChangedMetatagHandler> changeHandlerMap) {
		this.changeHandlerMap = changeHandlerMap;
	}

	public boolean containsHandler(String metatag){
		return changeHandlerMap.containsKey(metatag);
	}
	
	public ChangedMetatagHandler getChangeHandler(String metatag){
		return changeHandlerMap.get(metatag);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(metatagHandlerList != null && metatagHandlerList.size() > 0){
			for(ChangedMetatagHandler handler : metatagHandlerList){
				if(StringUtils.hasLength(handler.getMetatag())){
					changeHandlerMap.put(handler.getMetatag(), handler);
				}
			}
		}
	}

	public void setMetatagHandlerList(List<ChangedMetatagHandler> metatagHandlerList) {
		this.metatagHandlerList = metatagHandlerList;
	}
	
	
}
