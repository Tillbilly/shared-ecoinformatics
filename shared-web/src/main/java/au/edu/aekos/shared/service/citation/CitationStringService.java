package au.edu.aekos.shared.service.citation;

import java.util.List;



public interface CitationStringService {
	
	String buildAccessStatement(String ecoinfContactEmail, String datasetContactEmail, String license);
	
	String buildRightsStatement(String license, String publicationYear, String legalOrg);
	
	/**
	 * Creates a citation string that is appropriate for a HTML document.
	 * 
	 * @param dataProvider	object to provide information for the citation template
	 * @return				full citation string
	 */
	String assembleCitationString(CitationDataProvider dataProvider);

	/**
	 * By default, the citation string generates the citation string with the \u00C6 (&AElig;) character in its web encoding
	 * for HTML page display. This method simply replaces the resultant String with \u00C6 
	 * 
	 * @param dataProvider	object to provide information for the citation template
	 * @return				full citation string
	 */
	String assembleCitationStringForPrint(CitationDataProvider dataProvider);
	
	/**
	 * Creates a citation string that is appropriate for a RIF-CS document.
	 * 
	 * @param dataProvider	object to provide information for the citation template
	 * @return				full citation string
	 */
	String assembleCitationStringForRifcs(CitationDataProvider dataProvider);

	String buildPublisherStringForRifcs(List<String> legalContactOrgList);
}
