package au.edu.aekos.shared.service.citation;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;

public interface CitationService {
	
	public enum CitationDisplayType {
		AEKOS_PORTAL,
		PDF,
		RIFCS;
	}
	
	/**
	 * Builds a full citation string.
	 * 
	 * @param metaInfoExtractor	source of data for the string
	 * @param displayType		intended use of the string
	 * @return					full citation string
	 */
	String buildCitationString(MetaInfoExtractor metaInfoExtractor, CitationDisplayType displayType);
	
	String buildRightsStatement(MetaInfoExtractor metaInfoExtractor) ;
	
	String buildAccessStatement(MetaInfoExtractor metaInfoExtractor) ;
	
	String retrieveLicenseString(MetaInfoExtractor metaInfoExtractor);
	
	String retrieveDatasetName(MetaInfoExtractor metaInfoExtractor);

	String buildPublisherStringForRifcs(MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException;
}
