package au.edu.aekos.shared.service.reports;

import java.util.List;

import au.edu.aekos.shared.reports.CertificateOfPublicationReportBean;
import au.edu.aekos.shared.reports.DataCitationReportBean;
import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

public interface ReportDataSourceService {
	
	List<CertificateOfPublicationReportBean> getDataForCertificateReport(Long submissionId);
	
	List<SubmissionSummaryRow> getDataForSubmissionSummaryReport(Long submissionId, boolean checkPublished);
	
	List<DataCitationReportBean> getDataForCitationReport(Long submissionId);

}
