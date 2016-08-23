package au.edu.aekos.shared.web.controllers.integration;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsHtmlView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import au.edu.aekos.shared.reports.CertificateOfPublicationReportBean;
import au.edu.aekos.shared.reports.DataCitationReportBean;
import au.edu.aekos.shared.service.reports.ReportDataSourceService;
import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

@Controller
public class JasperReportsController {

	public static final String BEAN_COLLECTION_DATA_SOURCE_NAME = "beanCollectionDataSource";
	
	@Autowired @Qualifier("publicationCertificateReport")
	private JasperReportsPdfView publicationCertificatePdfView;
	
	@Autowired @Qualifier("submissionSummaryPdfReport")
	private JasperReportsPdfView submissionSummaryPdfView;
	
	@Autowired @Qualifier("submissionSummaryHtmlReport")
	private JasperReportsHtmlView submissionSummaryHtmlView;
	
	@Autowired @Qualifier("citationPdfReport")
	private JasperReportsPdfView citationPdfView;
	
	@Autowired
	private ReportDataSourceService reportDataSourceService;

	@RequestMapping(value="/reports/publicationCertificate", method = RequestMethod.GET )
	public JasperReportsPdfView getPublicationCertificatePdf(@RequestParam(required=true) Long submissionId , Model model, HttpServletResponse response){
		setContentDispositionFilenameToReportsView(publicationCertificatePdfView, "publicationCertificate-" + submissionId.toString() + ".pdf");
		List<CertificateOfPublicationReportBean> reportBeanList = reportDataSourceService.getDataForCertificateReport(submissionId);
		model.addAttribute(BEAN_COLLECTION_DATA_SOURCE_NAME, reportBeanList);
		return publicationCertificatePdfView;
	}
	
	@RequestMapping(value="/reports/submissionSummary", method = RequestMethod.GET )
	public JasperReportsPdfView getSubmissionSummaryPdf(@RequestParam(required=true) Long submissionId , Model model, HttpServletResponse response){
		setContentDispositionFilenameToReportsView(submissionSummaryPdfView, "submissionSummary-" + submissionId.toString() + ".pdf");
		List<SubmissionSummaryRow> reportBeanList = reportDataSourceService.getDataForSubmissionSummaryReport(submissionId, false);
		model.addAttribute(BEAN_COLLECTION_DATA_SOURCE_NAME, reportBeanList);
		return submissionSummaryPdfView;
	}
	
	@RequestMapping(value="/reports/published/submissionSummary", method = RequestMethod.GET )
	public JasperReportsPdfView getSubmissionSummaryPdfUnsecured(@RequestParam(required=true) Long submissionId , Model model, HttpServletResponse response){
		setContentDispositionFilenameToReportsView(submissionSummaryPdfView, "submissionSummary-" + submissionId.toString() + ".pdf");
		List<SubmissionSummaryRow> reportBeanList = reportDataSourceService.getDataForSubmissionSummaryReport(submissionId, true);
		model.addAttribute(BEAN_COLLECTION_DATA_SOURCE_NAME, reportBeanList);
		return submissionSummaryPdfView;
	}
	
	@RequestMapping(value="/reports/published/citation", method = RequestMethod.GET )
	public JasperReportsPdfView getCitationPdfUnsecured(@RequestParam(required=true) Long submissionId , Model model, HttpServletResponse response){
		setContentDispositionFilenameToReportsView(citationPdfView, "citation-" + submissionId.toString() + ".pdf");
		List<DataCitationReportBean> citationData = reportDataSourceService.getDataForCitationReport(submissionId);
		model.addAttribute(BEAN_COLLECTION_DATA_SOURCE_NAME, citationData);
		return citationPdfView;
	}
	
	@RequestMapping(value="/reports/published/submissionSummaryHtml", method = RequestMethod.GET )
	public JasperReportsHtmlView getSubmissionSummaryHtmlUnsecured(@RequestParam(required=true) Long submissionId , Model model, HttpServletResponse response){
		List<SubmissionSummaryRow> reportBeanList = reportDataSourceService.getDataForSubmissionSummaryReport(submissionId, true);
		model.addAttribute(BEAN_COLLECTION_DATA_SOURCE_NAME, reportBeanList);
		return submissionSummaryHtmlView;
	}
	
	private void setContentDispositionFilenameToReportsView(AbstractJasperReportsView view, String filename){
		Properties props = new Properties();
		props.put("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		view.setHeaders(props);
	}
	
}
