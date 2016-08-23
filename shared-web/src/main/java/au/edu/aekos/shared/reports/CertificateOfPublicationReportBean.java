package au.edu.aekos.shared.reports;


public class CertificateOfPublicationReportBean {

	private String submissionTitle;
	private String datasetName;
	private String doi;
	private Long submissionId;
	private String submissionDateString;
	private String publicationDateString;
	private String submitterNameString;
	private String submitterOrganisationString;
	private String landingPageUrl;
	private String publicationYear;
	private String licenceType;
	
	public CertificateOfPublicationReportBean(String submissionTitle,
			String datasetName, String doi, Long submissionId,
			String submissionDateString, String publicationDateString,
			String submitterNameString, String submitterOrganisationString, String landingPageUrl,
			String publicationYear, String licenseType ) {
		super();
		this.submissionTitle = submissionTitle;
		this.datasetName = datasetName;
		this.doi = doi;
		this.submissionId = submissionId;
		this.submissionDateString = submissionDateString;
		this.publicationDateString = publicationDateString;
		this.submitterNameString = submitterNameString;
		this.submitterOrganisationString = submitterOrganisationString;
		this.landingPageUrl = landingPageUrl;
		this.publicationYear = publicationYear;
		this.licenceType = licenseType;
	}

	public CertificateOfPublicationReportBean() {
		super();
	}

	public CertificateOfPublicationReportBean(String submissionTitle,
			String doi, Long submissionId) {
		super();
		this.submissionTitle = submissionTitle;
		this.doi = doi;
		this.submissionId = submissionId;
	}

	public String getSubmissionTitle() {
		return submissionTitle;
	}

	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getSubmissionDateString() {
		return submissionDateString;
	}

	public void setSubmissionDateString(String submissionDateString) {
		this.submissionDateString = submissionDateString;
	}

	public String getPublicationDateString() {
		return publicationDateString;
	}

	public void setPublicationDateString(String publicationDateString) {
		this.publicationDateString = publicationDateString;
	}

	public String getSubmitterNameString() {
		return submitterNameString;
	}

	public void setSubmitterNameString(String submitterNameString) {
		this.submitterNameString = submitterNameString;
	}
	
	public String getSubmitterOrganisationString() {
		return submitterOrganisationString;
	}

	public void setSubmitterOrganisationString(String submitterOrganisationString) {
		this.submitterOrganisationString = submitterOrganisationString;
	}

	public String getLandingPageUrl() {
		return landingPageUrl;
	}

	public void setLandingPageUrl(String landingPageUrl) {
		this.landingPageUrl = landingPageUrl;
	}

	public String getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getLicenceType() {
		return licenceType;
	}

	public void setLicenceType(String licenceType) {
		this.licenceType = licenceType;
	}
}
