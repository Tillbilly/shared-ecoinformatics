package au.org.ecoinformatics.s3client;

import java.io.File;
import java.io.OutputStream;

/**
 * This s3Client code originally resided in the shared-web project,  but has been 
 * extracted into its own library, so it can be used by the aekos-portal, and other future projects.
 * Fucntions enabled by this client have been written to suit the SHaRED use cases, as such not all 
 * of the possible operations are catered for.
 * Apache HttpClient is the http client library used to perform the RESTful HTTP calls.
 * @author btill
 */
public interface S3RESTHttpClient {
    /**
     * Does an object exist in the object store for the given key
     * @param key
     * @return true if the object exists, and the request returns a code 200, false otherwise
     * @throws Exception
     */
	boolean objectExistInBucket(String key) throws Exception;
	
	/**
	 * Deletes object from the object store with given key
	 * @param key
	 * @return The HTTP Response code ( 204 or 200 for success )
	 * @throws Exception
	 */
	int deleteObject(String key) throws Exception;
	
	/**
	 * Copies the file to the object store under the given key
	 * @param file
	 * @param key
	 * @return
	 * @throws Exception
	 */
	int putFile(File file, String key) throws Exception;
	//byte [] getFile(String key) throws Exception;
	
	/**
	 * Streams the file represented by the key into the given OutputStream
	 * @param key
	 * @param outputStream
	 * @return
	 * @throws Exception
	 */
	int getFileStreaming(String key, OutputStream outputStream) throws Exception;
	
	
	/****** These are setters for Spring xml context wiring. ****/
	/****** Might have multiple rest clients pointing to different object stores ****/
	void setS3ApiUrl(String s3ApiUrl);
	void setBucket(String bucket);
	void setApiKey(String apiKey);
	void setSecretKey(String secretKey);
	boolean areKeysSet();
	boolean isActive();
	void setActive(boolean active);
	String getObjectStoreName();
	void setObjectStoreName(String objectStoreName);
	
}
