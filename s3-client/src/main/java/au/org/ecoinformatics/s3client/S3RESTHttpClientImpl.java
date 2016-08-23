package au.org.ecoinformatics.s3client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class S3RESTHttpClientImpl implements S3RESTHttpClient {

	private static final Logger logger = LoggerFactory.getLogger(S3RESTHttpClientImpl.class);
	
	private DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());
	
	private String S3ApiUrl;
	
	private String bucket;
	
	private String apiKey;
	
	private String secretKey;
	
	private String objectStoreName;
	
	private boolean active;
	
	private String buildContainerEndpoint(){
		return S3ApiUrl + "/" + bucket;
	}
	
	private String buildObjectEndpoint(String objectKey){
		return buildContainerEndpoint() + "/" + objectKey;
	}
	
	private String buildContentStringForAuthDescription(String key){
		String contentStr =  "/" + bucket + "/" + key;
		return contentStr;
	}
	
	@Override
	public boolean objectExistInBucket(String key) throws SignatureException, ParseException, ClientProtocolException, IOException {
		HttpHead httpHead = new HttpHead(buildObjectEndpoint(key));
		addAuthorizationAndDateRequestHeaders(httpHead, key);
		String requstLineString = httpHead.getRequestLine().toString();
		logger.info(requstLineString);
		HttpResponse response = httpClient.execute(httpHead);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			logger.warn("Possible problem - received non-OK response: " + 
					response.getStatusLine().toString() + " for the request: " + requstLineString);
		} else {
			logger.info(response.getStatusLine().toString());
		}
		return ( HttpStatus.SC_OK == response.getStatusLine().getStatusCode() );
	}
	
	@Override
	public int putFile(File file, String key) throws Exception {
		HttpPut httpPut = new HttpPut(buildObjectEndpoint(key));
		addAuthorizationAndDateRequestHeaders(httpPut, key);
		//HttpClient adds the content length header automagically
		//httpPut.addHeader("Content-Length", Long.toString(contentLength) );
		FileEntity fileEntity = new FileEntity(file);
	    httpPut.setEntity(fileEntity);   
	    long currentTime = System.currentTimeMillis();
	    int responseCode = -1;
	    try {
	    	logger.info( file.getName() + " to " + httpPut.getRequestLine());
			HttpResponse response = httpClient.execute(httpPut);
			logger.info(response.getStatusLine().toString());
			responseCode = response.getStatusLine().getStatusCode();
			if(response.getEntity() != null && response.getEntity().getContentLength() > 0){
				logger.info( EntityUtils.toString(response.getEntity()) );
			}else{
			    EntityUtils.consumeQuietly(response.getEntity());
			}
		}catch(Exception e){
			long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
			logger.error( "Exception Occurred Time took: " + timeTaken + " seconds");
			logger.error(e.getMessage());
			throw e;
		}
	    long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
		logger.info( "Time taken: " + timeTaken + " seconds");
		return responseCode;
	}
	
	@Override
	public int getFileStreaming(String key, OutputStream outputStream) throws Exception {
		HttpGet httpGet = new HttpGet(buildObjectEndpoint(key));
		addAuthorizationAndDateRequestHeaders(httpGet, key);
		long currentTime = System.currentTimeMillis();
	    int responseCode = -1;
	    try {
	    	logger.info("S3 GET " +  httpGet.getRequestLine());
			HttpResponse response = httpClient.execute(httpGet);
			logger.info(response.getStatusLine().toString());
			responseCode = response.getStatusLine().getStatusCode();
			if(responseCode == 200){
				InputStream is = response.getEntity().getContent();
				IOUtils.copy(is, outputStream);
				is.close();
				outputStream.close();
			}else{
				EntityUtils.consumeQuietly(response.getEntity());
				logger.info(key + " not found." + httpGet.getRequestLine() + " " + response.getStatusLine().toString());
			}
			//EntityUtils.consumeQuietly(response1.getEntity());
		}catch(Exception e){
			long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
			logger.error( "Exception Time took: " + timeTaken + " seconds");
			logger.error(e.getMessage(), e);
			throw e;
		}
	    long timeTaken =  ( System.currentTimeMillis() - currentTime ) / 1000 ;
		logger.info( "Time taken: " + timeTaken + " seconds");
		return responseCode;
	}
	
	@Override
	public int deleteObject(String key) throws Exception {
		HttpDelete httpDelete = new HttpDelete(buildObjectEndpoint(key));
		addAuthorizationAndDateRequestHeaders(httpDelete, key);
		int responseCode = -1;
	    try {
	    	logger.info("S3 DELETE " +  httpDelete.getRequestLine());
			HttpResponse response = httpClient.execute(httpDelete);
			logger.info(response.getStatusLine().toString());
			responseCode = response.getStatusLine().getStatusCode();
			if(responseCode != 204 && responseCode != 200){ //204 is the accepted code, but may be 200
				logger.error("Deleting " + key + " failed." + httpDelete.getRequestLine() + " " + response.getStatusLine().toString());
				if(response.getEntity() != null ){
					logger.error("Entity response from delete:" + response.getEntity().toString() );
				}else{
					EntityUtils.consumeQuietly(response.getEntity());
				}
			}else{
				EntityUtils.consumeQuietly(response.getEntity());
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return responseCode;
	}
	
	private void addAuthorizationAndDateRequestHeaders(HttpRequestBase httpRequest, String key) throws ParseException, SignatureException {
		String dateString = S3AuthUtils.constructDateString(new Date());
		httpRequest.addHeader("Date", dateString);
	    String descString = S3AuthUtils.canonicaliseRequestHeaders(httpRequest.getMethod(), dateString, buildContentStringForAuthDescription(key));
		String authKey = S3AuthUtils.calculateRFC2104HMAC(descString, secretKey);
		httpRequest.addHeader("Authorization", getAuthorizationHeader(authKey) );
	}
	
	private String getAuthorizationHeader(String authKey){
		String authHeaderStr =  "AWS " + apiKey + ":" + authKey ;
		return authHeaderStr;
	}

	public String getS3ApiUrl() {
		return S3ApiUrl;
	}

	public void setS3ApiUrl(String s3ApiUrl) {
		S3ApiUrl = s3ApiUrl;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getObjectStoreName() {
		return objectStoreName;
	}

	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override //Check to see if keys are set, if not, they'll be set in the ObjectStore service on startup from a properties file.
	public boolean areKeysSet() {
		if(!StringUtils.hasLength(getApiKey()) || !StringUtils.hasLength(getSecretKey()) ){
			return false;
		}
		return true;
	}
	
}