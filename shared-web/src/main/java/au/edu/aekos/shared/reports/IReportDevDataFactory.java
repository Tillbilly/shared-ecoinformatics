package au.edu.aekos.shared.reports;

import java.util.ArrayList;
import java.util.List;

import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

public class IReportDevDataFactory {
	
	private static final String TEMPLATE = "#AUTHORS# (#PUB_YEAR#). #NAME##VERSION#.#NL#"
			+ "#DOI#.#NL#"
			+ "Obtained from Advanced Ecological Knowledge and Observation System Data Portal (&AElig;KOS, http://www.aekos.org.au/)"
			+ ", made available by #LEGAL_CONTACT_ORGS#.#NL#"
			+ "Accessed #ACCESS_DATE#.";
	
    private static final String RIGHTS_STATEMENT_TEMPLATE = "(C)_publicationYear_ _legalOrganisation_. Rights owned by _legalOrganisation_. Rights licensed subject to _licenseStr_.";
	
	private static final String ACCESS_TEMPLATE = "These data can be freely downloaded via the Advanced Ecological Knowledge and Observation System (AEKOS) Data Portal and used subject to the _licenseStr_." 
	                                             +" Attribution and citation is required as described under License and Citation." 
			                                     + " We ask you to send citations of publications arising from work that use these data to TERN Ecoinformatics and citation and copies of publication to _datasetContactEmail_";
	
	
	/**
	 * This method is used by ireports to develop against the SubmissionSummaryRow object as a BeanCollectionDataSource
	 */
	public static List<SubmissionSummaryRow> createTestBeanCollection_Section(){
		List<SubmissionSummaryRow> reportList = new ArrayList<SubmissionSummaryRow>();
		reportList.add(new SubmissionSummaryRow("Submission Details", "Subsection", "Title", "My Title" ));
		reportList.add(new SubmissionSummaryRow("Submission Details", "Subsection","Submission ID", "243567"));
		reportList.add(new SubmissionSummaryRow("Spatial", "Subsection1","Location Description", "Well, heres a story from a long time ago to see if we can implicitly wrap text and make the box stretch vertically to fill the space, ti would be amazing if we could" , "TEXT_BOX", "metatag" ));
		reportList.add(new SubmissionSummaryRow("Spatial", "Subsection1","Loc2", "some facts"));
		reportList.add(new SubmissionSummaryRow("Spatial", "Subsection2","Loc3", "some more facts"));
		reportList.add(new SubmissionSummaryRow("Another Section" , "Subsection3","title","description"));
		reportList.add(new SubmissionSummaryRow("Another Section" ,"Subsection3","title","description"));
		return reportList;
	}
	/**
	 * This method is used by ireports to develop against the SubmissionSummaryRow object as a BeanCollectionDataSource
	 */
	public static List<SubmissionSummaryRow> createTestBeanCollection_SubSection(){
		List<SubmissionSummaryRow> reportList = new ArrayList<SubmissionSummaryRow>();
		reportList.add(new SubmissionSummaryRow("Submission Details", "Subsection","Title", "My Title" ));
		reportList.add(new SubmissionSummaryRow("Submission Details", "Subsection","Submission ID", "243567"));
		reportList.add(new SubmissionSummaryRow("Spatial", "Subsection","Location Description", "Well, heres a story from a long time ago to see if we can implicitly wrap text and make the box stretch vertically to fill the space, ti would be amazing if we could" , "TEXT_BOX","metatag" ));
		reportList.add(new SubmissionSummaryRow("Spatial", "Subsection", "Loc2", "some facts"));
		reportList.add(new SubmissionSummaryRow("Spatial", "Subsection", "Loc3", "some more facts"));
		reportList.add(SubmissionSummaryRow.createGroupInstance("AUTHOR", "Subsection","Author",1, "Name", "AUTHOR NAME", "TEXT","mt"));
		reportList.add(SubmissionSummaryRow.createGroupInstance("AUTHOR", "Subsection","Author",1, "NameX", "AUTHOR NAMEX", "TEXT","mt"));
		reportList.add(SubmissionSummaryRow.createGroupInstance("AUTHOR", "Subsection","Author",2, "Name", "AUTHOR NAME2", "TEXT","mt"));
		reportList.add(SubmissionSummaryRow.createGroupInstance("AUTHOR", "Subsection","Author",2, "NameX", "AUTHOR NAMEX2", "TEXT","mt"));
		reportList.add(new SubmissionSummaryRow("Another Section" ,"Subsection","title","description"));
		reportList.add(new SubmissionSummaryRow("Another Section" ,"Subsection","title","description"));
		return reportList;
	}
	
	//These methods are used to provide development data in ireports
	public static List<CertificateOfPublicationReportBean> createBeanCollection(){
		List<CertificateOfPublicationReportBean> certificateReportData = new ArrayList<CertificateOfPublicationReportBean>();
		certificateReportData.add(new CertificateOfPublicationReportBean("Submission Title","DOI1.233456/345", 1234l) );
		return certificateReportData;
	}
	
	public static List<CertificateOfPublicationReportBean> createDetailedBeanCollection(){
		List<CertificateOfPublicationReportBean> certificateReportData = new ArrayList<CertificateOfPublicationReportBean>();
		certificateReportData.add(new CertificateOfPublicationReportBean("submissionTitle",
				"datasetName", "doi", 12345l,
				"submissionDateString", "publicationDateString",
				"submitterNameString",  "submitterOrganisationString", "http://www.1800HowsMyDriving.com","1984","cc-by"));
		return certificateReportData;
	}
	
	public static List<DataCitationReportBean> createDataCitationTestBean(){
		List<DataCitationReportBean> beanList = new ArrayList<DataCitationReportBean>();
		DataCitationReportBean bean = new DataCitationReportBean();
		bean.setDatasetTitle("Dataset Title");
		bean.setCitation(TEMPLATE);
		bean.setAccessStatement(ACCESS_TEMPLATE);
		bean.setLicense("TERN-CY CC some license statement");
		bean.setRightsStatement(RIGHTS_STATEMENT_TEMPLATE);
		beanList.add(bean);
		return beanList;
	}
}
