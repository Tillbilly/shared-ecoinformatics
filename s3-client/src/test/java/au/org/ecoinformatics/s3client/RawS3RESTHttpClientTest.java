package au.org.ecoinformatics.s3client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

//This test was used to develop S3RESTHttpClient  -  now all @Tests are removed
@Deprecated
public class RawS3RESTHttpClientTest {

    public static final String NECTAR_SWIFT_API_URL = "https://swift.rc.nectar.org.au:8888";
    //export EC2_ACCESS_KEY=cd0bd6b738744e179a19b42dd406a9bc
    //export EC2_SECRET_KEY=6ae0a3545a1341d4a38c8cb2e2478c96

    
	private static final String API_KEY = "cd0bd6b738744e179a19b42dd406a9bc";
	private static final String SECRET_KEY = "6ae0a3545a1341d4a38c8cb2e2478c96";
	
	//private static final String SECRET_KEY = "fOuVdHqEirUhzSozUX2z7KaeZ3M=" ;
	
	private static final String CONTAINER_NAME = "shared-dev";
	
	private static String getAuthorizationHeader(String authKey){
		return "AWS " + API_KEY + ":" + authKey ;
		
	}
	
	//org.jclouds.s3.filters.RequestAuthorizeSignature
	//GET\n\n\nFri, 14 Dec 2012 01:53:41 GMT\n/shared_submissions
	
	/*
	/GET /shared_submissions?max-keys=0 HTTP/1.1
			Date: Fri, 14 Dec 2012 01:20:16 GMT
			Authorization: AWS d9921bab82084cd4ac8beb43ec4da435:t/P9Rxv8Jz2kxXR+arpGH0nHTuc=
			User-Agent: jclouds/1.5.3 java/1.6.0_37
			Host: swift.rc.nectar.org.au:8888
	//		Accept: text/html, image/gif, image/jpeg, *; q=.2,  //*/ //*; q=.2
	//		Connection: keep-alive
	//*/
	
	//@Test
	public void testCanonicaliseRequestHeaders() throws SignatureException{
		String descString = S3AuthUtils.canonicaliseRequestHeaders("GET", "Fri, 14 Dec 2012 01:20:16 GMT","/shared_submissions" );
		String authKey = S3AuthUtils.calculateRFC2104HMAC(descString, SECRET_KEY);
		assertEquals("t/P9Rxv8Jz2kxXR+arpGH0nHTuc=", authKey);
	}
	
	
	//@Test
	public void testGETContainer() throws URISyntaxException, ParseException, SignatureException{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		URI endpoint = new URI(NECTAR_SWIFT_API_URL + "/" + CONTAINER_NAME ) ;
		HttpGet httpGet = new HttpGet(endpoint);
		
		Date authDate = new Date();
		String dateString = S3AuthUtils.constructDateString(authDate);
		httpGet.addHeader("Date", dateString);
		String descString = S3AuthUtils.canonicaliseRequestHeaders("GET", dateString,"/" + CONTAINER_NAME );
		String authKey = S3AuthUtils.calculateRFC2104HMAC(descString, SECRET_KEY);
		httpGet.addHeader("Authorization", getAuthorizationHeader(authKey) );
		System.out.println(httpGet.toString() );
		try {
			HttpResponse response1 = httpclient.execute(httpGet);
			String jsonStr = EntityUtils.toString(response1.getEntity());
			System.out.println(jsonStr);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	//@Test
	public void testDELETEThenPUTFile() throws URISyntaxException, ParseException, SignatureException{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		File fileToUpload = new File("/Users/a1042238/Downloads/tickets.pdf");  
		String fileName = "tickets.pdf";
		long contentLength = fileToUpload.length();
		
		
		System.out.println("Working with " + fileName + "   length: " + contentLength);
		
		URI endpoint = new URI(NECTAR_SWIFT_API_URL + "/" + CONTAINER_NAME ) ;
		HttpDelete httpDel = new HttpDelete(endpoint + "/" +"tickets.pdf");
		
		Date authDate = new Date();
		String dateString = S3AuthUtils.constructDateString(authDate);
		httpDel.addHeader("Date", dateString);
		String descString = S3AuthUtils.canonicaliseRequestHeaders("DELETE", dateString,"/" + CONTAINER_NAME + "/" + fileName);
		String authKey = S3AuthUtils.calculateRFC2104HMAC(descString, SECRET_KEY);
		httpDel.addHeader("Authorization", getAuthorizationHeader(authKey) );
		try {
			HttpResponse response1 = httpclient.execute(httpDel);
			System.out.println(response1.getStatusLine());
			EntityUtils.consumeQuietly(response1.getEntity());
			//String jsonStr = EntityUtils.toString(response1.getEntity());
			//System.out.println(jsonStr);
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		HttpPut httpPut = new HttpPut(endpoint + "/" +"tickets.pdf");
		authDate = new Date();
		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.MINUTE, 30);
		dateString = S3AuthUtils.constructDateString(c.getTime());
		System.out.println(dateString);
		httpPut.addHeader("Date", dateString);
	    descString = S3AuthUtils.canonicaliseRequestHeaders("PUT", dateString,"/" + CONTAINER_NAME + "/" + fileName);
		authKey = S3AuthUtils.calculateRFC2104HMAC(descString, SECRET_KEY);
		httpPut.addHeader("Authorization", getAuthorizationHeader(authKey) );
		//httpPut.addHeader("Content-Length", Long.toString(contentLength) );
		
		FileEntity fileEntity = new FileEntity(fileToUpload);
	    httpPut.setEntity(fileEntity);   
	    System.out.println(httpPut.toString());
	    long currentTime = System.currentTimeMillis();
	    try {
			HttpResponse response1 = httpclient.execute(httpPut);
			//String jsonStr = EntityUtils.toString(response1.getEntity());
			//System.out.println(jsonStr);
			System.out.println(response1.getStatusLine());
			if(response1.getEntity() != null){
				String jsonStr = EntityUtils.toString(response1.getEntity());
				System.out.println(jsonStr);
			}else{
			    EntityUtils.consumeQuietly(response1.getEntity());
			}
		}catch(Exception e){
			long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
			System.out.println( "Exception Time took: " + timeTaken + " seconds");
			e.printStackTrace();
			fail();
		}
	    long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
		System.out.println( "Time taken: " + timeTaken + " seconds");
		
		HttpGet httpGet = new HttpGet(endpoint + "/" +"tickets.pdf");
		
		authDate = new Date();
		c = GregorianCalendar.getInstance();
		c.add(Calendar.MINUTE, 30);
		dateString = S3AuthUtils.constructDateString(c.getTime());
		System.out.println(dateString);
		httpGet.addHeader("Date", dateString);
	    descString = S3AuthUtils.canonicaliseRequestHeaders("GET", dateString,"/" + CONTAINER_NAME + "/" + fileName);
		authKey = S3AuthUtils.calculateRFC2104HMAC(descString, SECRET_KEY);
		httpGet.addHeader("Authorization", getAuthorizationHeader(authKey) );
		//httpPut.addHeader("Content-Length", Long.toString(contentLength) );
		
		//FileEntity fileEntity = new FileEntity(fileToUpload);
	    //httpPut.setEntity(fileEntity);   
	    currentTime = System.currentTimeMillis();
	    File fileToDownload = new File("/Users/a1042238/temp/" + "tickets.pdf");
	    try {
			HttpResponse response1 = httpclient.execute(httpGet);
			System.out.println(response1.getStatusLine());
			InputStream is = response1.getEntity().getContent();
			FileOutputStream fos = new FileOutputStream(fileToDownload);
			IOUtils.copy(is, fos);
			is.close();
			fos.close();
			System.out.println(response1.getStatusLine());
			//EntityUtils.consumeQuietly(response1.getEntity());
		}catch(Exception e){
			timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
			System.out.println( "Exception Time took: " + timeTaken + " seconds");
			e.printStackTrace();
			fail();
		}
	    timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
		System.out.println( "Time taken: " + timeTaken + " seconds");
		assertEquals( fileToUpload.length() , fileToDownload.length() );
	}
	
	//@Test
	public void testUseHEADToSeeIfObjectExistsInBucket() throws ParseException, SignatureException{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String fileName = "johnbx.tgz";
		
		HttpHead httpHead = new HttpHead(NECTAR_SWIFT_API_URL + "/" + CONTAINER_NAME + "/" +fileName);
		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.MINUTE, 30);
		String dateString = S3AuthUtils.constructDateString(c.getTime());
		System.out.println(dateString);
		httpHead.addHeader("Date", dateString);
	    String descString = S3AuthUtils.canonicaliseRequestHeaders("HEAD", dateString,"/" + CONTAINER_NAME + "/" + fileName);
		String authKey = S3AuthUtils.calculateRFC2104HMAC(descString, SECRET_KEY);
		httpHead.addHeader("Authorization", getAuthorizationHeader(authKey) );
		//httpPut.addHeader("Content-Length", Long.toString(contentLength) );
		
		
	    long currentTime = System.currentTimeMillis();
	    try {
			HttpResponse response1 = httpclient.execute(httpHead);
			//String jsonStr = EntityUtils.toString(response1.getEntity());
			//System.out.println(jsonStr);
			System.out.println(response1.getStatusLine());
			if(response1.getEntity() != null){
				String jsonStr = EntityUtils.toString(response1.getEntity());
				System.out.println(jsonStr);
			}else{
			    EntityUtils.consumeQuietly(response1.getEntity());
			}
			assertEquals(200, response1.getStatusLine().getStatusCode());
		}catch(Exception e){
			long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
			System.out.println( "Exception Time took: " + timeTaken + " seconds");
			e.printStackTrace();
			fail();
		}
	    long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
		System.out.println( "Time taken: " + timeTaken + " seconds");
	}
	
}
