package au.edu.aekos.shared.questionnaire;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;
import au.edu.aekos.shared.web.model.SubmissionDataFileType;
import au.edu.aekos.shared.web.util.SharedFileUtils;

public class SubmissionFileModel {

	private static final Logger logger = LoggerFactory.getLogger(SubmissionFileModel.class);
	
	private String filename;
	private String storedFilename;
	private Long fileSizeBytes;
	private Boolean deleted = Boolean.FALSE;
    private String filetype;
	private String description;
    private Long id;
    private String fileFormatVersion;
	private String fileFormat;
	private String fileFormatTitle;
	
	/**
	 * Constructor so new objects can be constructed and populated when entries are added on a web page
	 */
	public SubmissionFileModel() {}

	public SubmissionFileModel(SubmissionData dataEntity) {
		this.filename = dataEntity.getFileName();
		this.fileSizeBytes = dataEntity.getFileSizeBytes();
		this.filetype = dataEntity.getSubmissionDataType().name();
		this.description = dataEntity.getFileDescription();
		this.id = dataEntity.getId();
		this.storedFilename = getStoredFileName(dataEntity);
		this.fileFormatVersion = dataEntity.getFormatVersion();
		this.fileFormat = dataEntity.getFormat();
	}

	/**
	 * Attempts to find a storage location of the correct type. If there are
	 * more than one of the correct type, the found location will depend
	 * on ordering.
	 * 
	 * @param dataEntity	The entity to search for storage locations
	 * @return				The found storage location, null otherwise
	 */
	static String getStoredFileName(SubmissionData dataEntity) {
		if(dataEntity.getStorageLocations().size() == 0) {
			return null;
		}
		Set<StorageLocation> sortedStorageLocations = new TreeSet<StorageLocation>(new StorageLocationComparator());
		sortedStorageLocations.addAll(dataEntity.getStorageLocations());
        for(StorageLocation location : sortedStorageLocations) {
        	if(!(location instanceof FileSystemStorageLocation)) {
        		continue;
        	}
    		FileSystemStorageLocation fssl = (FileSystemStorageLocation) location;
    		return fssl.getObjectName();
        }
		return null;
	}
	
	/**
	 * A crude comparator so we have some chance of testing based on order. It
	 * will order lexicographically based on a StorageLocation's objectName.
	 */
	private static class StorageLocationComparator implements Comparator<StorageLocation> {

		@Override
		public int compare(StorageLocation o1, StorageLocation o2) {
			try {
				return o1.getObjectName().compareTo(o2.getObjectName());
			} catch (NullPointerException e) {
				return 1;
			}
		}
	}
	
	public String getHumanReadableFileSize() {
		if (fileSizeBytes == null) {
			logger.warn(String.format("The file %s (type:%s, format:%s) has a null file size.", filename, filetype, fileFormat));
			return SharedFileUtils.humanReadableByteCount(0, true);
		}
		return SharedFileUtils.humanReadableByteCount(fileSizeBytes, true);
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getStoredFilename() {
		return storedFilename;
	}

	public void setStoredFilename(String storedFilename) {
		this.storedFilename = storedFilename;
	}

	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileFormatVersion() {
		return fileFormatVersion;
	}

	public void setFileFormatVersion(String fileFormatVersion) {
		this.fileFormatVersion = fileFormatVersion;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getFileTypeTitle() {
		return SubmissionDataFileType.getTitleForCode(filetype);
	}

	public String getFileFormatTitle() {
		return fileFormatTitle;
	}

	public void setFileFormatTitle(String fileFormatTitle) {
		this.fileFormatTitle = fileFormatTitle;
	}
}
