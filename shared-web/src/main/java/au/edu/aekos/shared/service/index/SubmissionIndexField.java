package au.edu.aekos.shared.service.index;

public class SubmissionIndexField {

	private String sharedTag;
	private String indexFieldName;
	//When indexing a species common name, we also need to write a corresponding
	//scientific and genus/species type entries to the scientific species index field.
	private String speciesIndexFieldName;
	
	//For species indexing, may wish to add the autocomplete search style fields
	//in addition to the indexFieldName specified. Only used by writers that implement
	//the Marker interface SearchFieldWriter, species writers usually
	//private Boolean addSearchIndexFields = Boolean.FALSE; 
	//Actually - might implement this on the portal side.
	
	
	@SuppressWarnings("rawtypes")
	private Class convertValueToClass;
	
	public String getSharedTag() {
		return sharedTag;
	}
	public void setSharedTag(String sharedTag) {
		this.sharedTag = sharedTag;
	}
	public String getIndexFieldName() {
		return indexFieldName;
	}
	public void setIndexFieldName(String indexFieldName) {
		this.indexFieldName = indexFieldName;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getConvertValueToClass() {
		return convertValueToClass;
	}
	public void setConvertValueToClass(@SuppressWarnings("rawtypes") Class convertValueToClass) {
		this.convertValueToClass = convertValueToClass;
	}
	public String getSpeciesIndexFieldName() {
		return speciesIndexFieldName;
	}
	public void setSpeciesIndexFieldName(String speciesIndexFieldName) {
		this.speciesIndexFieldName = speciesIndexFieldName;
	}
	@Override
	public String toString() {
		return "SubmissionIndexField [sharedTag=" + sharedTag
				+ ", indexFieldName=" + indexFieldName
				+ ", speciesIndexFieldName=" + speciesIndexFieldName
				+ ", convertValueToClass=" + convertValueToClass + "]";
	}
	
	
	
}
