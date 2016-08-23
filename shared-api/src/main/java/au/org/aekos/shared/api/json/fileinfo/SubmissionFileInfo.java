package au.org.aekos.shared.api.json.fileinfo;

import java.util.ArrayList;
import java.util.List;

public class SubmissionFileInfo {

	private Long id;
	private String name;
	private String size;
	private String sizeBytes;
	private String description;
	private String format;
	private String formatVersion;
	private String type;
	private String siteFileCS;
	private List<StorageLocationInfo> s3Locations = new ArrayList<StorageLocationInfo>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getSizeBytes() {
		return sizeBytes;
	}
	public void setSizeBytes(String sizeBytes) {
		this.sizeBytes = sizeBytes;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSiteFileCS() {
		return siteFileCS;
	}
	public void setSiteFileCS(String siteFileCS) {
		this.siteFileCS = siteFileCS;
	}
	public List<StorageLocationInfo> getS3Locations() {
		return s3Locations;
	}
	public void setS3Locations(List<StorageLocationInfo> s3Locations) {
		this.s3Locations = s3Locations;
	}
	
}
