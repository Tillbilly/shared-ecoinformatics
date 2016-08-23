package au.edu.aekos.shared.service.citation;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;

@Service
public class CitationServiceImpl implements CitationService{
	
	private static final String CITATION_DATE_FORMAT = "dd MMM yyyy";
	public static final String DEFAULT_PUBLISHED_NL_SEPERATOR = " ";
    private static final Logger logger = LoggerFactory.getLogger(CitationServiceImpl.class);
	
	@Autowired
	private CommonConceptMetatagConfig metatagConfig;
	
	@Autowired
	private CitationStringService citationStringService;
	
	@Value(value="${aekos.enquiries.email}")
	private String accessStatementContactEmail;
	
	@Value(value="${citation.date-template.text}")
	private String citationDateTemplate;

	@Override
	public String buildRightsStatement(MetaInfoExtractor metaInfoExtractor) {
		String rightsStatement = null;
		try{
			String license = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSubmissionLicenceTypeMetatag());
			String publicationYear =  metaInfoExtractor.getDatasetPublicationYear();
			List<String> legalOrgList = getLegalContactOrgs(metaInfoExtractor);
			String legalOrg = concatenateListStrings(legalOrgList, ", ");
			//if(! StringUtils.hasLength(legalOrg)){ Actually, no legal orgs, no rights statement!!
			//	legalOrg = " "; //blank token, so rights statement is strill generated
			//}
			rightsStatement = citationStringService.buildRightsStatement(license, publicationYear, legalOrg);
		}catch(MetaInfoExtractorException e){
			logger.error("Error building rights statement for submission " + metaInfoExtractor.getSubmissionId().toString() + ", returning null", e);
		}
		return rightsStatement;
	}

	@Override
	public String buildAccessStatement(MetaInfoExtractor metaInfoExtractor) {
		String accessRights = null;
		try{
			String license = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSubmissionLicenceTypeMetatag());
			String datasetContactEmail = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getContactEmailMetatag());
			accessRights = citationStringService.buildAccessStatement(accessStatementContactEmail, datasetContactEmail, license);
		}catch(MetaInfoExtractorException e){
			logger.error("Error building access statement for submission " + metaInfoExtractor.getSubmissionId().toString() + ", returning null", e);
		}
		return accessRights;
	}

	@Override
	public String buildCitationString(MetaInfoExtractor metaInfoExtractor, CitationDisplayType displayType) {
		List<String> authorList = new ArrayList<String>();
		String datasetVersion = "";
		String datasetName = "";
		List<String> legalContactOrgList = new ArrayList<String>();
		try{
		    authorList = metaInfoExtractor.getCitationFormatAuthorNameList(metatagConfig.getAuthorGivenNameMetatag(), 
		    		metatagConfig.getAuthorSurnameMetatag());
		}catch(MetaInfoExtractorException e){
			logger.error("Tried to build author list for submission " + metaInfoExtractor.getSubmissionId().toString() 
					+ " from metatags " + metatagConfig.getAuthorGivenNameMetatag() + " : " + metatagConfig.getAuthorSurnameMetatag(), e);
		}
		String datasetPublicationYear = metaInfoExtractor.getDatasetPublicationYear();
		try{
		    datasetVersion = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetVersionMetatag());
		}catch(MetaInfoExtractorException e){
			logger.error("Tried to get dataset version for submission " + metaInfoExtractor.getSubmissionId().toString() 
					+" from metatag " + metatagConfig.getDatasetVersionMetatag(), e);
		}
		try{
		    datasetName = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetNameMetatag());
		}catch(MetaInfoExtractorException e){
			logger.error("Tried to get dataset name for submission " + metaInfoExtractor.getSubmissionId().toString() +" from metatag " + metatagConfig.getDatasetNameMetatag(), e);
		}
		try{
		    legalContactOrgList = getLegalContactOrgs(metaInfoExtractor);
		}catch(MetaInfoExtractorException e){
			logger.error("Tried to get legalContactOrgList for submission " + metaInfoExtractor.getSubmissionId().toString()
					+" from metatag " + metatagConfig.getLegalContactOrgMetatag(), e);
		}
		String accessDateString;
		switch (displayType) {
		case AEKOS_PORTAL:
		case PDF:
			accessDateString = todayInCitationDateFormat();
			break;
		case RIFCS:
			accessDateString = citationDateTemplate;
			break;
		default:
			throw new IllegalStateException("Programmer error: unhandled display type - " + displayType);
		}
		String doi = metaInfoExtractor.getSubmission().getDoi();
		String nlFragment = DEFAULT_PUBLISHED_NL_SEPERATOR;
		CitationDataProvider dataProvider = new CitationDataProvider(authorList, datasetPublicationYear, datasetVersion,
				datasetName, legalContactOrgList, doi, accessDateString, nlFragment);
		switch (displayType) {
		case AEKOS_PORTAL:
			String portalCitationResult = citationStringService.assembleCitationString(dataProvider);
			logResult(portalCitationResult, displayType, metaInfoExtractor.getSubmissionId().toString());
			return portalCitationResult;
		case PDF:
			String pdfCitationResult = citationStringService.assembleCitationStringForPrint(dataProvider);
			logResult(pdfCitationResult, displayType, metaInfoExtractor.getSubmissionId().toString());
			return pdfCitationResult;
		case RIFCS:
			String rifcsCitationResult = citationStringService.assembleCitationStringForRifcs(dataProvider);
			logResult(rifcsCitationResult, displayType, metaInfoExtractor.getSubmissionId().toString());
			return rifcsCitationResult;
		default:
			throw new IllegalStateException("Programmer error: unhandled display type - " + displayType);
		}
	}

	@Override
	public String retrieveLicenseString(MetaInfoExtractor metaInfoExtractor) {
		String license = "license";
		try{
		    license = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSubmissionLicenceTypeMetatag() );
		}catch(MetaInfoExtractorException e){
			logger.error("Tried to get license for submission " + metaInfoExtractor.getSubmissionId().toString()
					+" from metatag " + metatagConfig.getSubmissionLicenceTypeMetatag(), e);
		}
		return license;
	}

	@Override
	public String retrieveDatasetName(MetaInfoExtractor metaInfoExtractor) {
		String datasetName = "ERROR - meta info failed";
		try{
		    datasetName = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetNameMetatag());
		}catch(MetaInfoExtractorException e){
			logger.error("Tried to get dataset name for submission " + metaInfoExtractor.getSubmissionId().toString()
					+" from metatag " + metatagConfig.getDatasetNameMetatag(), e);
		}
		return datasetName;
	}

	@Override
	public String buildPublisherStringForRifcs(MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException {
		List<String> legalContactOrgList = getLegalContactOrgs(metaInfoExtractor);
		return citationStringService.buildPublisherStringForRifcs(legalContactOrgList);
	}
	
	private List<String> getLegalContactOrgs(MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException {
		List<String> legalContactOrgList = metaInfoExtractor.getResponseListFromQuestionSet(metatagConfig.getLegalContactOrgMetatag());
		if((legalContactOrgList == null || legalContactOrgList.size() == 0 ) && StringUtils.hasLength(metatagConfig.getContactOrganisationMetatag())){
			String org = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getContactOrganisationMetatag());
			if(StringUtils.hasLength(org)){
				List<String> orgList = new ArrayList<String>();
				orgList.add(org);
				return orgList;
			}
		}
        return legalContactOrgList;		
	}
	
	private String concatenateListStrings(List<String> strings, String sep){
		StringBuilder sb = new StringBuilder();
		if(strings != null && strings.size() > 0){
			for(String str : strings){
				if(sb.length() > 0){
					sb.append(sep);
				}
				sb.append(str);
			}
		}
		return sb.toString();
	}
	
	public static String todayInCitationDateFormat() {
		Date accessDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(CITATION_DATE_FORMAT);
		String accessDateString = sdf.format(accessDate);
		return accessDateString;
	}
	
	private void logResult(String citationString, CitationDisplayType displayType, String submissionId) {
		logger.info("Created " + displayType + " typed citation for submission ID " + submissionId + ":" + citationString);
	}
}
