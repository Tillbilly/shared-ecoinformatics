package au.edu.aekos.shared.reports;

public class DataCitationReportBean {
    private String datasetTitle;
	private String citation;
	private String license;
	private String rightsStatement;
	private String accessStatement;
	
	public DataCitationReportBean() {
		super();
	}
	public DataCitationReportBean(String datasetTitle, String citation,
			String license, String rightsStatement, String accessStatement) {
		super();
		this.datasetTitle = datasetTitle;
		this.citation = citation;
		this.license = license;
		this.rightsStatement = rightsStatement;
		this.accessStatement = accessStatement;
	}
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getRightsStatement() {
		return rightsStatement;
	}
	public void setRightsStatement(String rightsStatement) {
		this.rightsStatement = rightsStatement;
	}
	public String getAccessStatement() {
		return accessStatement;
	}
	public void setAccessStatement(String accessStatement) {
		this.accessStatement = accessStatement;
	}
	public String getDatasetTitle() {
		return datasetTitle;
	}
	public void setDatasetTitle(String datasetTitle) {
		this.datasetTitle = datasetTitle;
	}
	
	
}
