package au.org.ecoinformatics.s3client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Test;

//import sun.net.www.protocol.http.HttpURLConnection;



public class S3RESTHttpClientTest {

	private static final String NECTAR_SWIFT_API_URL = "https://swift.rc.nectar.org.au:8888";
	private static final String BUCKET_NAME = "shared-dev";
	// You can get new values for these keys by getting your credentials as per http://support.rc.nectar.org.au/docs/authentication
	// Currently running with Tom Saleeba's keys
	private static final String API_KEY = "d2c2b99674774761bc36f42c3fe90e24";
	private static final String SECRET_KEY = "f9b70ee7cf3b437ab8c796ed9e3aac8b";
	
	/**
	 * Can we use the S3 client to upload and download a file?
	 */
	@Test
	public void testS3RESTHttpClient() throws Throwable {
		File f = new File( "src/test/resources/keezer.jpg" );
		String key = "myTestKey1";
		S3RESTHttpClient objectUnderTest = new S3RESTHttpClientImpl();
		objectUnderTest.setApiKey(API_KEY);
		objectUnderTest.setBucket(BUCKET_NAME);
		objectUnderTest.setS3ApiUrl(NECTAR_SWIFT_API_URL);
		objectUnderTest.setSecretKey(SECRET_KEY);
		//Check if key exists, delete it if it does
		if(objectUnderTest.objectExistInBucket(key)){
			objectUnderTest.deleteObject(key);
		}
		//Upload the file
		int response = objectUnderTest.putFile(f, key);
		assertEquals(200, response);
		//Now check the key exists in the bucket
		assertTrue(objectUnderTest.objectExistInBucket(key));
		//Now download the object
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int responseCode = objectUnderTest.getFileStreaming(key, os);
		//assertEquals(HttpURLConnection.HTTP_OK, responseCode);
		assertEquals((long) os.size(), f.length());
		//Delete the object
		objectUnderTest.deleteObject(key);
	}
}
