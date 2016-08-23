package au.edu.aekos.shared.service.doi;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.doiclient.jaxb.Resource;

public interface DoiMintingService {
	
	/**
	 * Mints a doi from the configs defined in the doi-context.
	 * Will save the minted doi to the submission.
	 * @param sub
	 * @return The minted DOI
	 * @throws DoiMintingException
	 */
	String mintDoiForSubmission(Submission sub) throws DoiMintingException;
	
	/**
	 * Call this to not trigger a new transaction on the mintDoi method,
	 * the requires new propagation hangs junit
	 * @param sub
	 * @return
	 * @throws DoiMintingException
	 */
	String testMintDoiForSubmission(Submission sub) throws DoiMintingException;

	Resource buildResourceForSubmission(Submission sub) throws MetaInfoExtractorException;
	
	String getDoiLandingPageBaseUrl();
	

}
