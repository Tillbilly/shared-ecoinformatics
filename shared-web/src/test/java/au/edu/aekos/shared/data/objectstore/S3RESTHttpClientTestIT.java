package au.edu.aekos.shared.data.objectstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import au.org.ecoinformatics.s3client.*;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Test;



public class S3RESTHttpClientTestIT {

	private static final String NECTAR_SWIFT_API_URL = "https://swift.rc.nectar.org.au:8888";
	//These keys are for a container in the SHaRED project.
	private static final String API_KEY = "d2c2b99674774761bc36f42c3fe90e24"; // Generated from Tom's account, if he leaves, this will probably die
	private static final String SECRET_KEY = "f9b70ee7cf3b437ab8c796ed9e3aac8b";
	private static final String BUCKET_NAME = "shared-dev";
	
	@Test
	public void testS3RESTHttpClient(){
		File f =  new File( "src/test/resources/keezer.jpg" ) ;
		String key = "myTestKey";
		
		S3RESTHttpClient s3client = new S3RESTHttpClientImpl();
		s3client.setApiKey(API_KEY);
		s3client.setBucket(BUCKET_NAME);
		s3client.setS3ApiUrl(NECTAR_SWIFT_API_URL);
		s3client.setSecretKey(SECRET_KEY);
		boolean keyExists = false;
		
		//Check if key exists
		try{
			if( s3client.objectExistInBucket(key) ){
				keyExists = true;
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
			fail();
		}
		//if the key exists,  delete it.
		try{
			if(keyExists){
				s3client.deleteObject(key);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		//Upload the file
		try{
		    int response = s3client.putFile(f, key);
		    assertEquals(200, response);
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		//Now check the key exists in the bucket
		//Check if key exists
		try{
			assertTrue( s3client.objectExistInBucket(key) );
		}catch(Exception e){
			System.out.println(e.getMessage());
			fail();
		}
		//Now download the object
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try{
		    int responseCode = s3client.getFileStreaming(key, os);
		    assertEquals(200, responseCode);
		    assertEquals(new Long(os.size()), new Long(f.length() ) );
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		//Delete the object
		try{
			s3client.deleteObject(key);
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
}
