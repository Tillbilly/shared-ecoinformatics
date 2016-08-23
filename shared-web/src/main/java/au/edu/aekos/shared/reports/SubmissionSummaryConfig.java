package au.edu.aekos.shared.reports;

import java.util.ArrayList;
import java.util.List;

//Relates info model metatags to submission data concepts
//The report data service will know how to deal with each of the metatags
//when a derived metatag appears in the info model entry list.
//Dataset file info is treated as a group
public class SubmissionSummaryConfig {

	private String submissionIdMetatag;
	private String doiMetatag;
	private String submitterMetatag;
	private String publicationDateMetatag;
	private String dcmiBBOXMetatag;
	private String siteFileMetatag;
	private String siteFileSRSMetatag;
	private String pickedGeometryMetatag;
	
	//Only used for the dcmiBBOX calculation
	private String bboxMinXMetatag;
	private String bboxMinYMetatag;
	private String bboxMaxXMetatag;
	private String bboxMaxYMetatag;
	private String bboxSRSMetatag;
	
	//We are not going to print out the file contents in the summary report.
	private String floraFileEntryMetatag;
	private String floraCommonFileEntryMetatag;
    private String faunaFileEntryMetatag;
	private String faunaCommonFileEntryMetatag;

	//Construct a link for the file, if the metatag for the answer is in this list
	private List<String> fileLinkMetatags = new ArrayList<String>();
	
	private String datasetFileGroupName;
	private String filenameMetatag;
	private String filesizeMetatag;
	private String fileDescriptionMetatag;
	private String fileTypeMetatag;
	private String fileFormatMetatag;
	private String fileFormatVersionMetatag;
	private String fileIdMetatag;
	
	//Citation related derived metatags
	private String citationMetatag;
	private String rightsStatementMetatag;
	private String accessStatementMetatag;
	
	public String getSubmissionIdMetatag() {
		return submissionIdMetatag;
	}

	public void setSubmissionIdMetatag(String submissionIdMetatag) {
		this.submissionIdMetatag = submissionIdMetatag;
	}

	public String getDoiMetatag() {
		return doiMetatag;
	}

	public void setDoiMetatag(String doiMetatag) {
		this.doiMetatag = doiMetatag;
	}

	public String getPublicationDateMetatag() {
		return publicationDateMetatag;
	}

	public void setPublicationDateMetatag(String publicationDateMetatag) {
		this.publicationDateMetatag = publicationDateMetatag;
	}

	public String getDcmiBBOXMetatag() {
		return dcmiBBOXMetatag;
	}

	public void setDcmiBBOXMetatag(String dcmiBBOXMetatag) {
		this.dcmiBBOXMetatag = dcmiBBOXMetatag;
	}

	public String getFloraFileEntryMetatag() {
		return floraFileEntryMetatag;
	}

	public void setFloraFileEntryMetatag(String floraFileEntryMetatag) {
		this.floraFileEntryMetatag = floraFileEntryMetatag;
	}

	public String getFloraCommonFileEntryMetatag() {
		return floraCommonFileEntryMetatag;
	}

	public void setFloraCommonFileEntryMetatag(String floraCommonFileEntryMetatag) {
		this.floraCommonFileEntryMetatag = floraCommonFileEntryMetatag;
	}

	public String getFaunaFileEntryMetatag() {
		return faunaFileEntryMetatag;
	}

	public void setFaunaFileEntryMetatag(String faunaFileEntryMetatag) {
		this.faunaFileEntryMetatag = faunaFileEntryMetatag;
	}

	public String getFaunaCommonFileEntryMetatag() {
		return faunaCommonFileEntryMetatag;
	}

	public void setFaunaCommonFileEntryMetatag(String faunaCommonFileEntryMetatag) {
		this.faunaCommonFileEntryMetatag = faunaCommonFileEntryMetatag;
	}

	public String getDatasetFileGroupName() {
		return datasetFileGroupName;
	}

	public void setDatasetFileGroupName(String datasetFileGroupName) {
		this.datasetFileGroupName = datasetFileGroupName;
	}

	public String getFilenameMetatag() {
		return filenameMetatag;
	}

	public void setFilenameMetatag(String filenameMetatag) {
		this.filenameMetatag = filenameMetatag;
	}

	public String getFilesizeMetatag() {
		return filesizeMetatag;
	}

	public void setFilesizeMetatag(String filesizeMetatag) {
		this.filesizeMetatag = filesizeMetatag;
	}

	public String getFileDescriptionMetatag() {
		return fileDescriptionMetatag;
	}

	public void setFileDescriptionMetatag(String fileDescriptionMetatag) {
		this.fileDescriptionMetatag = fileDescriptionMetatag;
	}

	public String getFileTypeMetatag() {
		return fileTypeMetatag;
	}

	public void setFileTypeMetatag(String fileTypeMetatag) {
		this.fileTypeMetatag = fileTypeMetatag;
	}

	public String getFileFormatMetatag() {
		return fileFormatMetatag;
	}

	public void setFileFormatMetatag(String fileFormatMetatag) {
		this.fileFormatMetatag = fileFormatMetatag;
	}

	public String getFileFormatVersionMetatag() {
		return fileFormatVersionMetatag;
	}

	public void setFileFormatVersionMetatag(String fileFormatVersionMetatag) {
		this.fileFormatVersionMetatag = fileFormatVersionMetatag;
	}

	public String getFileIdMetatag() {
		return fileIdMetatag;
	}

	public void setFileIdMetatag(String fileIdMetatag) {
		this.fileIdMetatag = fileIdMetatag;
	}

	public String getSiteFileMetatag() {
		return siteFileMetatag;
	}

	public void setSiteFileMetatag(String siteFileMetatag) {
		this.siteFileMetatag = siteFileMetatag;
	}

	public String getPickedGeometryMetatag() {
		return pickedGeometryMetatag;
	}

	public void setPickedGeometryMetatag(String pickedGeometryMetatag) {
		this.pickedGeometryMetatag = pickedGeometryMetatag;
	}

	public String getSiteFileSRSMetatag() {
		return siteFileSRSMetatag;
	}

	public void setSiteFileSRSMetatag(String siteFileSRSMetatag) {
		this.siteFileSRSMetatag = siteFileSRSMetatag;
	}

	public String getBboxMinXMetatag() {
		return bboxMinXMetatag;
	}

	public void setBboxMinXMetatag(String bboxMinXMetatag) {
		this.bboxMinXMetatag = bboxMinXMetatag;
	}

	public String getBboxMinYMetatag() {
		return bboxMinYMetatag;
	}

	public void setBboxMinYMetatag(String bboxMinYMetatag) {
		this.bboxMinYMetatag = bboxMinYMetatag;
	}

	public String getBboxMaxXMetatag() {
		return bboxMaxXMetatag;
	}

	public void setBboxMaxXMetatag(String bboxMaxXMetatag) {
		this.bboxMaxXMetatag = bboxMaxXMetatag;
	}

	public String getBboxMaxYMetatag() {
		return bboxMaxYMetatag;
	}

	public void setBboxMaxYMetatag(String bboxMaxYMetatag) {
		this.bboxMaxYMetatag = bboxMaxYMetatag;
	}

	public String getBboxSRSMetatag() {
		return bboxSRSMetatag;
	}

	public void setBboxSRSMetatag(String bboxSRSMetatag) {
		this.bboxSRSMetatag = bboxSRSMetatag;
	}

	public String getSubmitterMetatag() {
		return submitterMetatag;
	}

	public void setSubmitterMetatag(String submitterMetatag) {
		this.submitterMetatag = submitterMetatag;
	}

	public List<String> getFileLinkMetatags() {
		return fileLinkMetatags;
	}

	public void setFileLinkForMetatags(List<String> fileLinkMetatags) {
		this.fileLinkMetatags = fileLinkMetatags;
	}

	public void setFileLinkMetatags(List<String> fileLinkMetatags) {
		this.fileLinkMetatags = fileLinkMetatags;
	}

	public String getCitationMetatag() {
		return citationMetatag;
	}

	public void setCitationMetatag(String citationMetatag) {
		this.citationMetatag = citationMetatag;
	}

	public String getRightsStatementMetatag() {
		return rightsStatementMetatag;
	}

	public void setRightsStatementMetatag(String rightsStatementMetatag) {
		this.rightsStatementMetatag = rightsStatementMetatag;
	}

	public String getAccessStatementMetatag() {
		return accessStatementMetatag;
	}

	public void setAccessStatementMetatag(String accessStatementMetatag) {
		this.accessStatementMetatag = accessStatementMetatag;
	}
	
	
}
