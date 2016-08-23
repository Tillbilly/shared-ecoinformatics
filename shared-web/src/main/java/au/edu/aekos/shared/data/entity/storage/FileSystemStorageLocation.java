package au.edu.aekos.shared.data.entity.storage;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("FILE_SYSTEM")
public class FileSystemStorageLocation  extends StorageLocation {

	public FileSystemStorageLocation() {
		super();
	}

	public FileSystemStorageLocation(File file, String path) {
		if( file.canRead() ){
		    fspath =  path;
		    objectName = file.getName();
		}
	}
	
	@Transient
	public static final StorageType storageType = StorageType.FILE_SYSTEM;
	
	@Column
	private String fspath;

	public String getFspath() {
		return fspath;
	}

	public void setFspath(String fspath) {
		this.fspath = fspath;
	}
	
	public String getFileName() {
		return objectName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fspath == null) ? 0 : fspath.hashCode());
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
		FileSystemStorageLocation other = (FileSystemStorageLocation) obj;
		if (fspath == null) {
			if (other.fspath != null)
				return false;
		} else if (!fspath.equals(other.fspath))
			return false;
		return true;
	}
	
	
	
}
