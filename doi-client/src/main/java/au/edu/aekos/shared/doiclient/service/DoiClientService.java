package au.edu.aekos.shared.doiclient.service;

import au.edu.aekos.shared.doiclient.jaxb.Resource;

/**
 * This service talks to the TERN DOI minting service, which is a proxy to the ANDS Cite My Data Service.
 * 
 * We construct a Resource object which gets marshalled to XML and sent to the TERN service
 * using HttpClient.
 * 
 * The response will contain the DOI or an error message.
 * 
 * @author btill
 */
public interface DoiClientService {
	
	static final String UPDATE_SUCCESS_CODE = "MT002";
	
	void setDoiClientConfig(DoiClientConfig config);
	
	DoiClientConfig getDoiClientConfig();
	
	/**
	 * Mints a DOI.
	 * 
	 * @param resource		XML object describing the DOI details
	 * @param urlExtension 	URL suffix from the base URL for the landing page - e.g. for SHaRED submissions, will be "dataset/22"
	 * 
	 * @return 				DOI as a string
	 * @throws DoiClientServiceException when something goes wrong with the DOI service
	 */
	String mintDoi(Resource resource, String urlExtension) throws DoiClientServiceException;
	
	/**
	 * Updates the DOI record.
	 * @param resource		XML object describing the new DOI details
	 * @param doi			DOI to update
	 * @param urlExtension 	URL suffix from the base URL for the landing page - e.g. for SHaRED submissions, will be "dataset/22"
	 * 
	 * @return Response Update Code - a MT002 is success.
	 * @throws DoiClientServiceException when something goes wrong with the DOI service
	 */
	String updateDoiRecord(Resource resource, String doi, String urlExtension) throws DoiClientServiceException;
	
	void activateDoi(String doi);
	
	void deactivateDoi(String doi);
}
