package au.edu.aekos.shared.service.citation;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CitationStringServiceImpl implements CitationStringService {

	private static final Logger logger = LoggerFactory.getLogger(CitationStringServiceImpl.class);
	
	public static final String DOI_URL_PREFIX = "http://doi.org/"; // TODO turn into a config property but need to convert the static stuff first
	private static final String ECOINF_CONTACT_EMAIL = "datacited@aekos.org.au";
	private static final String PRINT_AE_CHAR = "\u00C6";
	private static final String WEB_AE_CHAR = "&AElig;";
	private static final String XML_AE_CHAR = "Æ";
	private static final String PUBLISHER_TEMPLATE = WEB_AE_CHAR + "KOS Data Portal, rights owned by #LEGAL_CONTACT_ORGS#";
	private static final String CITATION_TEMPLATE = "#AUTHORS# (#PUB_YEAR#). #NAME##VERSION#.#NL#"
		+ DOI_URL_PREFIX + "#DOI#.#NL#"
		+ PUBLISHER_TEMPLATE + ".#NL#"
		+ "Accessed #ACCESS_DATE#.";

    private static final String RIGHTS_STATEMENT_TEMPLATE = "(C)_publicationYear_ _legalOrganisation_. Rights owned by _legalOrganisation_. "
    		+ "Rights licensed subject to _licenseStr_.";
	
	private static final String ACCESS_TEMPLATE = "These data can be freely downloaded via the Advanced Ecological Knowledge and Observation "
			+ "System (" + PRINT_AE_CHAR + "KOS) Data Portal and used subject to the _licenseStr_. Attribution and citation is required as "
			+ "described under License and Citation. We ask you to send citations of publications arising from work that use these data "
			+ "to TERN Eco-informatics at "+ECOINF_CONTACT_EMAIL+" and citation and copies of publications to _datasetContactEmail_";
	
	private static final LegalContactOrgsCitationTemplateFiller LEGAL_CONTACT_ORGS_TEMPLATE_FILLER = new LegalContactOrgsCitationTemplateFiller();
	private static final List<CitationTemplateFiller> CITATION_TEMPLATE_FILLERS = new LinkedList<CitationTemplateFiller>();
	static {
		CITATION_TEMPLATE_FILLERS.add(new AuthorsCitationTemplateFiller());
		CITATION_TEMPLATE_FILLERS.add(new PublicationYearCitationTemplateFiller());
		CITATION_TEMPLATE_FILLERS.add(new DatasetNameCitationTemplateFiller());
		CITATION_TEMPLATE_FILLERS.add(new DatasetVersionCitationTemplateFiller());
		CITATION_TEMPLATE_FILLERS.add(LEGAL_CONTACT_ORGS_TEMPLATE_FILLER);
		CITATION_TEMPLATE_FILLERS.add(new DOICitationTemplateFiller());
		CITATION_TEMPLATE_FILLERS.add(new AccessDateCitationTemplateFiller());
		CITATION_TEMPLATE_FILLERS.add(new NewLineCitationTemplateFiller());
	}
	
	@Override	
	public String assembleCitationString(CitationDataProvider dataProvider) {
		String result = CITATION_TEMPLATE;
		for (CitationTemplateFiller currFiller : CITATION_TEMPLATE_FILLERS) {
			result = currFiller.doReplace(dataProvider, result);
		}
		return result;
	}
	
	@Override	
	public String assembleCitationStringForPrint(CitationDataProvider dataProvider) {
		String result = assembleCitationString(dataProvider);
		return result.replace(WEB_AE_CHAR, PRINT_AE_CHAR);
	}

	@Override	
	public String assembleCitationStringForRifcs(CitationDataProvider dataProvider) {
		String result = assembleCitationString(dataProvider);
		return result.replace(WEB_AE_CHAR, XML_AE_CHAR);
	}
	
	@Override
	public String buildPublisherStringForRifcs(List<String> legalContactOrgList) {
		String filledTemplate = LEGAL_CONTACT_ORGS_TEMPLATE_FILLER.doReplaceHelper(PUBLISHER_TEMPLATE, legalContactOrgList);
		return filledTemplate.replace(WEB_AE_CHAR, XML_AE_CHAR);
	}
	
	private interface CitationTemplateFiller {
		String doReplace(CitationDataProvider dataProvider, String template);
	}
	
	private static class AuthorsCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#AUTHORS#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			if (!dataProvider.hasAuthors()) {
				template = template.replace(PLACEHOLDER, "AUTHORS");
			}
			String athorsFragment = concatenate(dataProvider.getAuthors(), ", ");
			return template.replace(PLACEHOLDER, athorsFragment);
		}
	}
	
	private static class PublicationYearCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#PUB_YEAR#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			if (dataProvider.hasDatasetPublicationYear()) {
				return template.replace(PLACEHOLDER, dataProvider.getDatasetPublicationYear());
			}
			return template.replace(PLACEHOLDER, "PUBLICATION_YEAR");
		}
	}
	
	private static class DatasetNameCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#NAME#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			if (!dataProvider.hasDatasetName())
				return template.replace(PLACEHOLDER, "DATASET_NAME");
			return template.replace(PLACEHOLDER, dataProvider.getDatasetName());
		}
	}
	
	private static class DatasetVersionCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#VERSION#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			if (!dataProvider.hasDatasetVersion()) {
				return template.replace(PLACEHOLDER, "");
			}
			String versionFragment = String.format(", Version %s", dataProvider.getDatasetVersion());
			return template.replace(PLACEHOLDER, versionFragment);
		}
	}
	
	private static class LegalContactOrgsCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#LEGAL_CONTACT_ORGS#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			if (!dataProvider.hasLegalContactOrgs()) {
				return template.replace(PLACEHOLDER, "LEGAL_CONTACT_ORGANISATIONS");
			}
			return doReplaceHelper(template, dataProvider.getLegalContactOrgs());
		}

		private String doReplaceHelper(String template, List<String> legalContactOrgs) {
			String legalContactOrgsFragment = concatenate(legalContactOrgs, ", ");
			return template.replace(PLACEHOLDER, legalContactOrgsFragment);
		}
	}
	
	private static class DOICitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#DOI#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
		    String doi = dataProvider.getDoi();
		    if(!StringUtils.hasLength(doi)){
		    	doi = "DEFAULT_DOI";
		    }
			return template.replace(PLACEHOLDER, doi);
		}
	}
	
	private static class AccessDateCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#ACCESS_DATE#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			return template.replace(PLACEHOLDER, dataProvider.getAccessDate());
		}
	}
	
	private static class NewLineCitationTemplateFiller implements CitationTemplateFiller {
		private static final String PLACEHOLDER = "#NL#";
		
		@Override
		public String doReplace(CitationDataProvider dataProvider, String template) {
			return template.replace(PLACEHOLDER, dataProvider.getNewLineFragment());
		}
	}
	
	//Hmmm I think legal org needs some work.  As it is currently non mandatory . . . .
	public String buildRightsStatement(String license, String publicationYear, String legalOrg){
		if(StringUtils.hasLength(license) && StringUtils.hasLength(publicationYear) && StringUtils.hasLength(legalOrg)){
			return RIGHTS_STATEMENT_TEMPLATE
					.replace("_publicationYear_", publicationYear)
					.replaceAll("_legalOrganisation_", legalOrg)
					.replace("_licenseStr_", license);
		}
		logger.info("Rights statement not written due to ");
		return null;
	}
	
	public String buildAccessStatement(String ecoinfContactEmail, String datasetContactEmail, String license){
		if(StringUtils.hasLength(datasetContactEmail) && StringUtils.hasLength(license)){
			return ACCESS_TEMPLATE
					.replace("_licenseStr_", license)
					.replace("_datasetContactEmail_", datasetContactEmail);
		}
		return null;
	}
	
	/**
	 * Concatenates all the strings in <code>sources</code> using the <code>separator</code>
	 * 
	 * @param sources		Strings to cat
	 * @param separator		Separator to use
	 * @return				All the sources concatenated using the supplied separator
	 */
	static String concatenate(List<String> sources, String separator) {
		StringBuilder result = new StringBuilder();
		boolean notFirst = false;
		for(String currSource : sources){
			if(notFirst){
				result.append(separator);
			}
			notFirst = true;
			result.append(currSource);
		}
		return result.toString();
	}
}
