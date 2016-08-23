package au.edu.aekos.shared.service.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;
import au.edu.aekos.shared.reports.CertificateOfPublicationReportBean;
import au.edu.aekos.shared.reports.DataCitationReportBean;
import au.edu.aekos.shared.service.citation.CitationService;
import au.edu.aekos.shared.service.citation.CitationService.CitationDisplayType;
import au.edu.aekos.shared.service.doi.DoiMintingService;
import au.edu.aekos.shared.service.integration.SubmissionInfoModelSummaryService;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

@Service
public class ReportDataSourceServiceImpl implements ReportDataSourceService {

	private static final String CERTIFICATE_REPORT_DATE_FORMAT = "dd MMMMM yyyy";
	private static final Logger logger = LoggerFactory.getLogger(ReportDataSourceServiceImpl.class);
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Autowired
	private CommonConceptMetatagConfig metatagConfig;
	
	@Autowired
	private DoiMintingService doiMintingService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private SubmissionInfoModelSummaryService submissionSummaryService;
	
	@Autowired
	private CitationService citationService;
	
	/**
	 * Will only return a single entry list
	 */
	@Transactional
	public List<CertificateOfPublicationReportBean> getDataForCertificateReport(Long submissionId){
		List<CertificateOfPublicationReportBean> dataList = new ArrayList<CertificateOfPublicationReportBean>();
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub != null){
			try {
				CertificateOfPublicationReportBean reportBean = mapSubmissionToCertificateOfPublicationReportBean(sub);
				dataList.add(reportBean);
			} catch (Exception e) {
				logger.error("Error occured mapping submission to certificateReportBean", e);
				dataList.add(new CertificateOfPublicationReportBean());
			}
		}else{
			dataList.add(new CertificateOfPublicationReportBean());  //empty data object
		}
		return dataList;
	}
	
	private CertificateOfPublicationReportBean mapSubmissionToCertificateOfPublicationReportBean(Submission sub) throws Exception {
		MetaInfoExtractor metaInfoExtractor;
		try {
			metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub, true); //Need to use display values
		} catch (Exception e) {
			logger.error("Error creating metaInfoExtractor", e);
			throw new MetaInfoExtractorException("Error creating metaInfoExtractor", e);
		}
		String submissionTitle = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSubmissionTitleMetatag());
		String datasetName = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetNameMetatag());
		String doi = sub.getDoi();
		Long submissionId = sub.getId();
		SimpleDateFormat sdf = new SimpleDateFormat(CERTIFICATE_REPORT_DATE_FORMAT);
		
		String submissionDate =  sub.getSubmissionDate() == null ? "" : sdf.format(sub.getSubmissionDate());
		
		String publishDate = sub.getLastReviewDate() == null ? "" : sdf.format(sub.getLastReviewDate() );
		String publishYear = "";
		if(sub.getLastReviewDate() != null){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(sub.getLastReviewDate());
			publishYear = Integer.toString(cal.get(Calendar.YEAR));
		}
		String licenceType = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSubmissionLicenceTypeMetatag());
		String submitterName = null;
		String organisation = "";
		//Submitter name
		submitterName = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getContactNameMetatag() );
		if(! StringUtils.hasLength(submitterName) && sub.getSubmitter() != null){
			submitterName = sub.getSubmitter().getFullName();
			if(!StringUtils.hasLength(submitterName)){
				submitterName = sub.getSubmitter().getUsername();
			}
		}
		//Organisation
		organisation = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getContactOrganisationMetatag() );
		if(! StringUtils.hasLength(organisation)){
			List<String> legalOrgList = metaInfoExtractor.getResponseListFromQuestionSet(metatagConfig.getLegalContactOrgMetatag());
			if(legalOrgList != null && legalOrgList.size() > 0){
				boolean notFirst = false; 
				StringBuilder sb = new StringBuilder();
			    for(String org : legalOrgList ){
			    	if(notFirst){
			    	    sb.append(", ");	
			    	}else{
			    		notFirst=true;
			    	}
			    	String displayString = controlledVocabularyService.getDisplayStringForTraitValue(ControlledVocabularyService.ORGANISATION_TRAIT, false, org);
			    	if(StringUtils.hasLength(displayString)){
			    		sb.append(displayString);
			    	}else{
			    		sb.append(org);
			    	}
			    }
			    organisation = sb.toString();
			}
		}
		if(! StringUtils.hasLength(organisation)){  //Last resort - we probably won't get here anymore
			organisation = sub.getSubmitter().getOrganisation();
			if(StringUtils.hasLength(organisation)){
				//Check to see if the controlled vocab organisation has a display string for the value
				String displayString = controlledVocabularyService.getDisplayStringForTraitValue(ControlledVocabularyService.ORGANISATION_TRAIT, false, organisation);
				if(displayString != null){
					organisation = displayString;
				}
			}else{
				organisation = "";
			}
		}
		String submissionUrl = doiMintingService.getDoiLandingPageBaseUrl() + submissionId.toString();
		return new CertificateOfPublicationReportBean(submissionTitle, datasetName, doi, submissionId, 
				                                      submissionDate, publishDate, submitterName, organisation, submissionUrl, publishYear, licenceType );
	}
	
	@Transactional 
	public List<SubmissionSummaryRow> getDataForSubmissionSummaryReport(Long submissionId, boolean checkPublished) {
		return submissionSummaryService.retrieveSubmissionSummaryRows(submissionId, checkPublished);
	}

	@Override
	public List<DataCitationReportBean> getDataForCitationReport(
			Long submissionId) {
		List<DataCitationReportBean> citationBeanList = new ArrayList<DataCitationReportBean>();
		MetaInfoExtractor mie = null;
		try {
			mie = metaInfoExtractorFactory.getInstance(submissionId, true);
		} catch (Exception e) {
			logger.error("Error trying to create MIE for citation report, subId:" + submissionId.toString(),e);
			return citationBeanList;
		}
		String accessStatement = citationService.buildAccessStatement(mie);
		String rightsStatement = citationService.buildRightsStatement(mie);
		String citation	= citationService.buildCitationString(mie, CitationDisplayType.PDF);
		String licenseString = citationService.retrieveLicenseString(mie);
		DataCitationReportBean citationBean = new DataCitationReportBean(citationService.retrieveDatasetName(mie), citation,
			licenseString, rightsStatement, accessStatement );
		citationBeanList.add(citationBean);
		return citationBeanList;
	}
}
