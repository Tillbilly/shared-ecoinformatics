package au.edu.aekos.shared.service.s3;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface ObjectStoreDataManifestService {

	void writeManifestToObjectStore(String objectStoreIdentifier) throws FileNotFoundException, IOException, Exception;
	
}
