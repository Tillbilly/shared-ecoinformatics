package au.edu.aekos.shared.solr.index;

public class SharedSolrField {
	
	//If its not a dynamic field, its an implicit field - i.e. doi or id ( submission id )
	//Otherwise need to extrapolate the suffix for dynamic field based on the object type
	//and or other settings.
	private boolean dynamic = false;
	private boolean multipleValues = false;
	private boolean index = true;
	private boolean store = true;
	private boolean catchAllCopy = true;
	
	private String fieldName;
	private Object fieldValue;
	
	
	
}
