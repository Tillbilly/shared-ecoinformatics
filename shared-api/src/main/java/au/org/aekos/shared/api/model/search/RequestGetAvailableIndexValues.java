package au.org.aekos.shared.api.model.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class RequestGetAvailableIndexValues {
	
	static final String SEARCH_FOR_ALL_VALUES = null;
	private static final Map<String, String> indexSuffixToFacetSuffixMapping = new HashMap<String, String>();
	private static final List<String> otherAllowableIndexFieldNames = new ArrayList<String>();
	
	private final String indexFieldName;
	private final String searchString;
	private final String resultOffset;
	private final String pageSize;

	/**
	 * Constructs a request that will return all values for the queried index field.
	 * 
	 * @param indexFieldName	The field to query
	 */
	public RequestGetAvailableIndexValues(String indexFieldName) {
		this(indexFieldName, SEARCH_FOR_ALL_VALUES, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a request that returns values matching <code>searchString</code> in 
	 * some way (implementation dependent) for the queried index field.
	 * 
	 * @param indexFieldName	field to query
	 * @param searchString		string to search for in the queried field
	 * @param resultOffset		number of results to offset by for response
	 * @param pageSize			number of items to returns in the response
	 */
	public RequestGetAvailableIndexValues(String indexFieldName, String searchString, int resultOffset, int pageSize) {
		this.indexFieldName = indexFieldName;
		this.searchString = searchString;
		this.resultOffset = String.valueOf(resultOffset);
		this.pageSize = String.valueOf(pageSize);
	}

	public String getIndexFieldName() {
		return indexFieldName;
	}

	public String getSearchString() {
		return searchString;
	}

	public boolean isSearchStringSupplied() {
		return searchString != null && searchString.trim().length() > 0;
	}

	public String getResultOffset() {
		return resultOffset;
	}

	public String getPageSize() {
		return pageSize;
	}

	public String getFacetFieldName() {
		for (Entry<String, String> currEntry : indexSuffixToFacetSuffixMapping.entrySet()) {
			if (!indexFieldName.endsWith(currEntry.getKey())) {
				continue;
			}
			StringBuilder result = new StringBuilder();
			result.append(indexFieldName.substring(0, indexFieldName.length() - currEntry.getKey().length()));
			result.append(currEntry.getValue());
			return result.toString();
		}
		throw new RuntimeException("ERROR: could not find a mapping for the index field name '" + indexFieldName + "'");
	}

	/**
	 * Tests if the supplied column name is considered valid in the SHaRED domain.
	 * 
	 * @param columnName	column name to test
	 * @return				<code>true</code> if the column name is valid for SHaRED, <code>false</code> otherwise.
	 */
	public static boolean isValidSharedColumnName(String columnName) {
		if (otherAllowableIndexFieldNames.contains(columnName)) {
			return true;
		}
		for (String currKey : indexSuffixToFacetSuffixMapping.keySet()) {
			if (columnName.endsWith(currKey)) {
				return true;
			}
		}
		return false;
	}
	
	static {
		indexSuffixToFacetSuffixMapping.put("_ft", "_facet_s");
		indexSuffixToFacetSuffixMapping.put("_stxt", "_facet_ss");
		indexSuffixToFacetSuffixMapping.put("_ftxt", "_facet_ss");
		
		InputStream propsStream = RequestGetAvailableIndexValues.class.getClassLoader().getResourceAsStream("shared-index-names.properties");
		Properties sharedIndexNameProperties = new Properties();
		try {
			sharedIndexNameProperties.load(propsStream);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load the properties file", e);
		}
		
		otherAllowableIndexFieldNames.add(sharedIndexNameProperties.getProperty("index-names.free-text"));
		otherAllowableIndexFieldNames.add(sharedIndexNameProperties.getProperty("index-names.spatial-location"));
	}
}
