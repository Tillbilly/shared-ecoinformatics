package au.edu.aekos.shared.service.rifcs;

import java.io.File;
import java.io.IOException;

import org.ands.rifcs.base.RIFCSException;

import com.jcraft.jsch.JSchException;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;

public interface RifcsService {

	String buildRifcsXmlFromSubmission(Submission sub) throws Exception;
	
	/**
	 * This method creates the rifcs file and scp s it up to the server.
	 * @param submission
	 * @return
	 * @throws MetaInfoExtractorException
	 * @throws RIFCSException
	 * @throws IOException
	 * @throws JSchException
	 */
	String processRifcs(Submission submission) throws MetaInfoExtractorException, RifcsServiceException, IOException, JSchException;
    	
	/**
	 * This method creates the rifcs xml document and writes it to the returned file
	 * @param submission
	 * @return
	 * @throws RIFCSException 
	 * @throws MetaInfoExtractorException 
	 * @throws IOException 
	 */
	File generateRifcsFileForSubmission(Submission submission) throws MetaInfoExtractorException, RifcsServiceException, IOException;
	
	/**
	 * This is a post publish step to regenerate all of the RIFCS files should they be lost ( disaster recovery)
	 * Does NOT copy the files up to the joai server.
	 * @return number of records generated
	 */
	int regenerateRifcsForAllPublishedSubmissions();
	
}
