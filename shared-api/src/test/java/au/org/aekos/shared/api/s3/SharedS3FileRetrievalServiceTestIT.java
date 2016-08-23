package au.org.aekos.shared.api.s3;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import au.org.aekos.shared.api.json.fileinfo.StorageLocationInfo;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfo;
import au.org.ecoinformatics.s3client.S3RESTHttpClient;
import au.org.ecoinformatics.s3client.S3RESTHttpClientImpl;


/**
 * This uses s3 api keys from my own project, because the keys are the same for any container in a project,
 * meaning I can't use qa because the keys are the same as prod.
 * 
 * In any case, the keys, url etc just need to be an active s3 location,
 * in case my project gets killed ( its not a matter of if, but when )
 * 
 * <bean id="s3RestClientDev" class="au.org.ecoinformatics.s3client.S3RESTHttpClientImpl">
            <property name="active" value="true" />
            <property name="objectStoreName" value="nectar_shared_dev" />
            <property name="S3ApiUrl" value="https://swift.rc.nectar.org.au:8888" />
            <property name="bucket" value="shared_submissions" />
            <property name="apiKey" value="d9921bab82084cd4ac8beb43ec4da435" />
            <property name="secretKey" value="4b834e1a23d84be2a09a19cc6e32ad37" />
        </bean>
 * 
 * @author btill
 */
public class SharedS3FileRetrievalServiceTestIT {
	
	private static final String PRIMARY_OBJECT_STORE_NAME = "dev";
	private static final String PRIMARY_CONTAINER_NAME = "shared_submissions";
	private static final String BU_OBJECT_STORE_NAME = "dev_bu";
	private static final String BU_CONTAINER_NAME = "shared_submissions_backup";
	private static final String S3_API_URL = "https://swift.rc.nectar.org.au:8888";
	private static final String API_KEY = "d9921bab82084cd4ac8beb43ec4da435";
	private static final String SECRET_KEY = "4b834e1a23d84be2a09a19cc6e32ad37";
	
	private SharedS3FileRetrievalService initialiseService(){
		SharedS3FileRetrievalServiceImpl service = new SharedS3FileRetrievalServiceImpl();
		service.setPrimaryObjectStoreClient(initS3RestClient(PRIMARY_OBJECT_STORE_NAME, PRIMARY_CONTAINER_NAME));
		service.setBackupObjectStoreClient(initS3RestClient(BU_OBJECT_STORE_NAME, BU_CONTAINER_NAME));
		return service;
	}

	private S3RESTHttpClient initS3RestClient(String objStoreName, String containerName){
		S3RESTHttpClientImpl s3Client = new S3RESTHttpClientImpl();
		s3Client.setActive(true);
		s3Client.setApiKey(API_KEY);
		s3Client.setBucket(containerName);
		s3Client.setObjectStoreName(objStoreName);
		s3Client.setS3ApiUrl(S3_API_URL);
		s3Client.setSecretKey(SECRET_KEY);
		return s3Client;
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testSharedSharedS3FileRetrievalServiceHappyDays() throws Exception{
		File f =  new File( "src/test/resources/test-s3.txt" ) ;
		File outputFile = new File("src/test/resources/test-s3-download.txt");
		String key = "123";
		
		if(outputFile.exists()){
			outputFile.delete();
		}
		
		S3RESTHttpClient primaryS3 = initS3RestClient(PRIMARY_OBJECT_STORE_NAME, PRIMARY_CONTAINER_NAME);
		S3RESTHttpClient secondaryS3 = initS3RestClient(BU_OBJECT_STORE_NAME, BU_CONTAINER_NAME);
		Assert.assertEquals(200, primaryS3.putFile(f, key) );
		Assert.assertEquals(200, secondaryS3.putFile(f, key) );
		
		SharedS3FileRetrievalService fileRetrievalService = initialiseService();
		
		SubmissionFileInfo fileInfo = new SubmissionFileInfo();
		fileInfo.setName("test-s3.txt");
		fileInfo.setId(new Long(69l));
		
		StorageLocationInfo sli = new StorageLocationInfo();
		sli.setObjectStoreIdentifier(PRIMARY_OBJECT_STORE_NAME);
		sli.setObjectId(key);
		fileInfo.getS3Locations().add(sli);
		
		StorageLocationInfo sli2 = new StorageLocationInfo();
		sli2.setObjectStoreIdentifier(BU_OBJECT_STORE_NAME);
		sli2.setObjectId(key);
		
		fileInfo.getS3Locations().add(sli2);
		
		fileRetrievalService.retrieveObjectToLocal(fileInfo, outputFile);
		Assert.assertTrue(outputFile.exists());
		Assert.assertEquals(f.length(), outputFile.length() );
		
		//Now delete from the primary store, see if its picked up from the backup
		int respcodeX = primaryS3.deleteObject(key);
		System.out.println(respcodeX);
		outputFile.delete();
		System.out.println("Waiting for 5sec to let s3 catch up to the delete");
		Thread.currentThread().sleep(25000l);  //Needs to wait for the delete object to register
		fileRetrievalService.retrieveObjectToLocal(fileInfo, outputFile);
		Assert.assertTrue(outputFile.exists());
		Assert.assertEquals(f.length(), outputFile.length() );
		
		//Now delete from the backup store - we should get an exception
		int respcode = secondaryS3.deleteObject(key);
		System.out.println(respcode);
		outputFile.delete();
		System.out.println("Waiting for 5sec to let s3 catch up to the delete");
		Thread.currentThread().sleep(5000l);
		try{
			fileRetrievalService.retrieveObjectToLocal(fileInfo, outputFile);
			Assert.fail();
		}catch(SharedS3FileRetrievalException ex){
			// success!
			System.out.println(ex.getMessage());
			return;
		}
		Assert.fail(); 
	}
}
