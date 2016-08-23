package au.edu.aekos.shared.data.entity.storage;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "STORAGE_LOCATION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="TYPE",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue(value="SL") //Never used as this class is never instantiated
public abstract class StorageLocation {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STORAGE_LOC_SEQ")
	@SequenceGenerator(name="STORAGE_LOC_SEQ")
	protected Long id;

	@Column(name="OBJ_NAME")
	protected String objectName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((objectName == null) ? 0 : objectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StorageLocation other = (StorageLocation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (objectName == null) {
			if (other.objectName != null)
				return false;
		} else if (!objectName.equals(other.objectName))
			return false;
		return true;
	}
	
	

}
