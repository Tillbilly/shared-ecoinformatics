package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;
import au.edu.aekos.shared.web.model.SubmissionDataFileType;
import au.edu.aekos.shared.web.util.SharedFileUtils;

@Entity
@Table(name="SUBMISSION_DATA")
public class SubmissionData { 
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUBMISSION_DATA_SEQ")
	@SequenceGenerator(name="SUBMISSION_DATA_SEQ",allocationSize=1)
	private Long id;
	
	@Column 
	private String fileName;
	
	@Column(length=1000) 
	private String fileDescription;
	
	@Column
	private Date embargoDate;
	
	@Column
	private Long fileSizeBytes;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="DATA_ID", nullable=false)
	@ForeignKey(name="submission_data_fk")
	private Set<StorageLocation> storageLocations = new HashSet<StorageLocation>();
	
	@Enumerated(EnumType.STRING)
	private SubmissionDataType submissionDataType = SubmissionDataType.DATA;
	
	@Column(name="FORMAT")
	private String format;
	
	@Column(name="FORMAT_VERSION")
	private String formatVersion;
	
	@Column(name="SITEFILE_CS")
	private String siteFileCoordinateSystem;
	
	@Column(name="SITEFILE_CS_OTHER")
	private String siteFileCoordSysOther;
	
	//A file answer for a specific question
	@Column(name="QUESTION_ID")
	private String questionId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Submission submission;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public Set<StorageLocation> getStorageLocations() {
		return storageLocations;
	}

	public void setStorageLocations(Set<StorageLocation> storageLocations) {
		this.storageLocations = storageLocations;
	}

	public Date getEmbargoDate() {
		return embargoDate;
	}

	public void setEmbargoDate(Date embargoDate) {
		this.embargoDate = embargoDate;
	}

	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((embargoDate == null) ? 0 : embargoDate.hashCode());
		result = prime * result
				+ ((fileDescription == null) ? 0 : fileDescription.hashCode());
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result
				+ ((fileSizeBytes == null) ? 0 : fileSizeBytes.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result
				+ ((formatVersion == null) ? 0 : formatVersion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((questionId == null) ? 0 : questionId.hashCode());
		result = prime
				* result
				+ ((siteFileCoordSysOther == null) ? 0 : siteFileCoordSysOther
						.hashCode());
		result = prime
				* result
				+ ((siteFileCoordinateSystem == null) ? 0
						: siteFileCoordinateSystem.hashCode());
		result = prime
				* result
				+ ((storageLocations == null) ? 0 : storageLocations.hashCode());
		result = prime
				* result
				+ ((submissionDataType == null) ? 0 : submissionDataType
						.hashCode());
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
		SubmissionData other = (SubmissionData) obj;
		if (embargoDate == null) {
			if (other.embargoDate != null)
				return false;
		} else if (!embargoDate.equals(other.embargoDate))
			return false;
		if (fileDescription == null) {
			if (other.fileDescription != null)
				return false;
		} else if (!fileDescription.equals(other.fileDescription))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (fileSizeBytes == null) {
			if (other.fileSizeBytes != null)
				return false;
		} else if (!fileSizeBytes.equals(other.fileSizeBytes))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (formatVersion == null) {
			if (other.formatVersion != null)
				return false;
		} else if (!formatVersion.equals(other.formatVersion))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (siteFileCoordSysOther == null) {
			if (other.siteFileCoordSysOther != null)
				return false;
		} else if (!siteFileCoordSysOther.equals(other.siteFileCoordSysOther))
			return false;
		if (siteFileCoordinateSystem == null) {
			if (other.siteFileCoordinateSystem != null)
				return false;
		} else if (!siteFileCoordinateSystem
				.equals(other.siteFileCoordinateSystem))
			return false;
		if (storageLocations == null) {
			if (other.storageLocations != null)
				return false;
		} else if (!storageLocations.equals(other.storageLocations))
			return false;
		if (submissionDataType != other.submissionDataType)
			return false;
		return true;
	}

	public boolean equalsUpdateSubmission(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubmissionData other = (SubmissionData) obj;
		if (embargoDate == null) {
			if (other.embargoDate != null)
				return false;
		} else if (!embargoDate.equals(other.embargoDate))
			return false;
		if (fileDescription == null) {
			if (other.fileDescription != null)
				return false;
		} else if (!fileDescription.equals(other.fileDescription))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (fileSizeBytes == null) {
			if (other.fileSizeBytes != null)
				return false;
		} else if (!fileSizeBytes.equals(other.fileSizeBytes))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (formatVersion == null) {
			if (other.formatVersion != null)
				return false;
		} else if (!formatVersion.equals(other.formatVersion))
			return false;
		return true;
	}
	
	
	public boolean containsFilesystemStorageName(String storedFilename){
		if(this.getStorageLocations() == null || this.getStorageLocations().size() == 0){
			return false;
		}
		for(StorageLocation storageLocation : this.getStorageLocations() ){
			if(storageLocation instanceof au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation && storedFilename.equals(storageLocation.getObjectName()) ){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasObjectStoreLocation(){
		if(storageLocations != null && storageLocations.size() > 0){
			for(StorageLocation storageLocation : storageLocations ){
				if(storageLocation instanceof au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation){
					return true;
				}
			}
		}
		return false;
	}
	
	public FileSystemStorageLocation getFileSystemStorageLocation(){
		if(storageLocations != null && storageLocations.size() > 0){
			for(StorageLocation storageLocation : storageLocations ){
				if(storageLocation instanceof FileSystemStorageLocation){
					return (FileSystemStorageLocation) storageLocation;
				}
			}
		}
		return null;
	}
	
	public List<FileSystemStorageLocation> getFileSystemStorageLocations(){
		List<FileSystemStorageLocation> fileSystemStorageLocationList = new ArrayList<FileSystemStorageLocation>();
		if(storageLocations != null && storageLocations.size() > 0){
			for(StorageLocation storageLocation : storageLocations ){
				if(storageLocation instanceof FileSystemStorageLocation){
					fileSystemStorageLocationList.add((FileSystemStorageLocation) storageLocation);
				}
			}
		}
		return fileSystemStorageLocationList;
	}
	
	public List<ObjectStoreLocation> getObjectStoreLocations(){
		List<ObjectStoreLocation> objectStoreLocationList = new ArrayList<ObjectStoreLocation>();
		if(storageLocations != null && storageLocations.size() > 0){
			for(StorageLocation storageLocation : storageLocations ){
				if(storageLocation instanceof ObjectStoreLocation){
					objectStoreLocationList.add((ObjectStoreLocation) storageLocation);
				}
			}
		}
		return objectStoreLocationList;
	}

	public SubmissionDataType getSubmissionDataType() {
		return submissionDataType;
	}

	public void setSubmissionDataType(SubmissionDataType submissionDataType) {
		this.submissionDataType = submissionDataType;
	}

	public String getSiteFileCoordinateSystem() {
		return siteFileCoordinateSystem;
	}

	public void setSiteFileCoordinateSystem(String siteFileCoordinateSystem) {
		this.siteFileCoordinateSystem = siteFileCoordinateSystem;
	}

	public String getSiteFileCoordSysOther() {
		return siteFileCoordSysOther;
	}

	public void setSiteFileCoordSysOther(String siteFileCoordSysOther) {
		this.siteFileCoordSysOther = siteFileCoordSysOther;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormatVersion() {
		return formatVersion;
	}

	public void setFormatVersion(String formatVersion) {
		this.formatVersion = formatVersion;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public String getHumanReadableFileSize() {
		return SharedFileUtils.humanReadableByteCount(fileSizeBytes, true);
	}
	
	public String getFileTypeTitle() {
		return SubmissionDataFileType.getTitleForCode(submissionDataType.name());
	}

	public boolean isSpeciesFile() {
		return submissionDataType.isSpeciesFile();
	}
}
