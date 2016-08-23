package au.edu.aekos.shared.web.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.TraitValue;

/**
 * 
 * As of 17th Dec 2013, Manage this from the custom vocab 'coordinateReferenceSystem'
 * @author btill
 */
@Component
public class CoordinateSystemConfig {

	private Map<String, String> epsgToDcmiProjStringMap = new HashMap<String,String>();
	{
		epsgToDcmiProjStringMap.put("EPSG:4283", "GDA94");
		epsgToDcmiProjStringMap.put("EPSG:4326", "WGS84");
		epsgToDcmiProjStringMap.put("EPSG:28349", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28350", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28351", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28352", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28353", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28354", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28355", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28356", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28357", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:28358", "UTM zone 49 south");
		epsgToDcmiProjStringMap.put("EPSG:3112", "GDA94 Lambert");
		epsgToDcmiProjStringMap.put("EPSG:3577", "GDA94 Australian Albers");
	}
	
	private static final String COORD_REFERENCE_SYS_TRAIT_NAME = "coordinateReferenceSystem";
	
	@Autowired
	private ControlledVocabularyService controlledVocabService;
	
	private List<CoordSysDisplayPair> coordSysList = null;

	public List<CoordSysDisplayPair> getCoordSysList() {
		if(coordSysList == null || coordSysList.size() == 0){
			coordSysList = new ArrayList<CoordSysDisplayPair>();
			List<TraitValue> csList = controlledVocabService.getTraitValueList(COORD_REFERENCE_SYS_TRAIT_NAME, true, false);
			for(TraitValue tv : csList){
				CoordSysDisplayPair csdp = new CoordSysDisplayPair(tv.getTraitValue(), tv.getDisplayString());
				coordSysList.add(csdp);
			}
			coordSysList.add(new CoordSysDisplayPair("Other","Other"));
		}
		return coordSysList;
	}
	
	
	/**
	 * This returns the projection String used in the Iso19139dcmiBox definition for rifcs
	 * @param epsgCode
	 * @return
	 */
	public String getProjectionStringForEPSGCode(String epsgCode){
		if(epsgToDcmiProjStringMap.containsKey(epsgCode)){
			return epsgToDcmiProjStringMap.get(epsgCode);
		}
		return "";
	}
	
}
