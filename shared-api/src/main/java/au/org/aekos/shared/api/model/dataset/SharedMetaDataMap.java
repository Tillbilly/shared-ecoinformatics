package au.org.aekos.shared.api.model.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedMetaDataMap {
	/*
	 *	FIXME might have to change SharedResponse to support repeating groups 
	 */
	private Map<String, List<SharedResponse>> metaDataMap = new HashMap<String, List<SharedResponse>>();

	public void put(SharedResponse sharedResponse) {
		String key = sharedResponse.getMetaTag();
		List<SharedResponse> currentValue = metaDataMap.get(key);
		if (currentValue == null) {
			currentValue = new ArrayList<SharedResponse>();
			metaDataMap.put(key, currentValue);
		}
		currentValue.add(sharedResponse);
	}

	public SharedResponse getFirstEntry(String metaTag) {
		List<SharedResponse> result = metaDataMap.get(metaTag);
		if (result == null || result.size() < 1) {
			return null;
		}
		return result.iterator().next();
	}

	public List<SharedResponse> getEntries(String metaTag) {
		List<SharedResponse> result = metaDataMap.get(metaTag);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}

	public int getDistinctFieldCount() {
		return metaDataMap.size();
	}
}
