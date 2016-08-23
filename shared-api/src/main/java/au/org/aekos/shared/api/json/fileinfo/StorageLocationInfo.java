package au.org.aekos.shared.api.json.fileinfo;

/**
 * Contains objects store ID and store identifier 
 * ( related to container name in object store connection config )
 * @author btill
 */
public class StorageLocationInfo {

	/**
	 * The object ID in the s3 objectore
	 */
	private String objectId;
	
	/**
	 * SHaRED s3 configuration name representing the objectstore ( nectar container )
	 */
	private String objectStoreIdentifier;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getObjectStoreIdentifier() {
		return objectStoreIdentifier;
	}
	public void setObjectStoreIdentifier(String objectStoreIdentifier) {
		this.objectStoreIdentifier = objectStoreIdentifier;
	}
	
}
