package au.edu.aekos.shared.data.entity.storage;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("OBJECT_STORE")
public class ObjectStoreLocation extends StorageLocation{

	@Transient
	public static final StorageType storageType = StorageType.OBJECT_STORE;
	
	@Column
	private String objectId;
	
	@Column
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((objectId == null) ? 0 : objectId.hashCode());
		result = prime
				* result
				+ ((objectStoreIdentifier == null) ? 0 : objectStoreIdentifier
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectStoreLocation other = (ObjectStoreLocation) obj;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		if (objectStoreIdentifier == null) {
			if (other.objectStoreIdentifier != null)
				return false;
		} else if (!objectStoreIdentifier.equals(other.objectStoreIdentifier))
			return false;
		return true;
	}
	
	
	
}
