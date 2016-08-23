package au.edu.aekos.shared.doiclient.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.doiclient.jaxb.Resource;
import au.edu.aekos.shared.doiclient.jaxb.response.Response;


@Service
public class DoiClientServiceImpl implements DoiClientService {

	private static final Logger logger = LoggerFactory.getLogger(DoiClientServiceImpl.class);
	public static final char UTF8_AE_CHAR = '\u00C6';
	public static final char UTF8_SINGLE_QUOTE = '\u0092'; // Not sure if this is a single quote, did Ben make a typo?
	
	@Autowired
	@Qualifier("doiClientConfig")
	private DoiClientConfig doiClientConfig;
	
	@Override
	public void setDoiClientConfig(DoiClientConfig config) {
		this.doiClientConfig = config;
	}

	@Override
	public String mintDoi(Resource resource, String urlExtension) throws DoiClientServiceException {
		logger.info("Attempting to mint DOI for " + resource.getTitles().getTitle().get(0).getValue() + " " + urlExtension);
		if(doiClientConfig == null){
			logger.error("DoiClientConfig not set");
			throw new DoiClientServiceException("DoiClientConfig not set");
		}
		String xmlStr = marshallResourceToXmlString(resource);
		URI endpoint = buildCreateEndpointURI(urlExtension);
		HttpPost httpPost = buildHttpPost(endpoint, xmlStr);
		HttpResponse response = executeHttpPost(httpPost);
		return validateResponseAndRetrieveDOI(response);
	}
	
	@Override
	public String updateDoiRecord(Resource resource, String doi,
			String urlExtension) throws DoiClientServiceException {
		logger.info("Attempting to update DOI record for " + resource.getTitles().getTitle().get(0).getValue() + " " + urlExtension + " " + doi);
		if(doiClientConfig == null){
			logger.error("DoiClientConfig not set");
			throw new DoiClientServiceException("DoiClientConfig not set");
		}
		String xmlStr = marshallResourceToXmlString(resource);
		URI endpoint = buildUpdateEndpointURI(doi, urlExtension);
		HttpPost httpPost = buildHttpPost(endpoint, xmlStr);
		HttpResponse response = executeHttpPost(httpPost);
		return validateResponseForUpdateDOI(response);
	}
	
	String validateResponseAndRetrieveDOI(HttpResponse httpResponse) throws DoiClientServiceException {
		logger.info(httpResponse.getStatusLine().toString());
		if( httpResponse.getStatusLine().getStatusCode() != 200 ){
			logger.error("Request failed with status line " + httpResponse.getStatusLine().toString());
			try{
				String responseXml = getResponseXml( httpResponse );
				logger.error(responseXml);
			}catch(Exception e){
				logger.error("Error retrieving response from 500",e);
			}
			throw new DoiClientServiceException("Request failed with status line " + httpResponse.getStatusLine().toString() );
		}
		String responseXml = getResponseXml( httpResponse );
		Response response = parseResponseXml(responseXml);
		String mintedDoi = response.getDoi();
		boolean isDoiMinted = ! StringUtils.hasLength(mintedDoi);
		if(isDoiMinted){
			String verboseMessage = StringUtils.hasLength(response.getVerbosemessage()) ? " (verbose message = " + response.getVerbosemessage() + ")" : "";
			throw new DoiClientServiceException("DOI not minted due to a response code of '" + response.getResponsecode() + 
					"' - " + response.getMessage() + verboseMessage, responseXml);
		}
		return mintedDoi;
	}
	
	private String validateResponseForUpdateDOI(HttpResponse httpResponse) throws DoiClientServiceException {
		logger.info(httpResponse.getStatusLine().toString());
		if( httpResponse.getStatusLine().getStatusCode() != 200 ){
			logger.error("Request failed with status line " + httpResponse.getStatusLine().toString());
			throw new DoiClientServiceException("Request failed with status line " + httpResponse.getStatusLine().toString() );
		}
		String responseXml = getResponseXml( httpResponse );
		Response response = parseResponseXml(responseXml);
	    return response.getResponsecode();
	}
	
	Response parseResponseXml(String responseXml) throws DoiClientServiceException {
		try {
			JAXBContext context = JAXBContext.newInstance(Response.class);
			Unmarshaller um = context.createUnmarshaller();
		    StringReader sr = new StringReader(responseXml);
			Object o = um.unmarshal(sr);
			return (Response) o;
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(), e);
		}
	}
	
	private String getResponseXml(HttpResponse response) throws DoiClientServiceException{
		HttpEntity entity = response.getEntity();
        try {
			String responseXml = EntityUtils.toString(entity);
			logger.debug(responseXml);
			return responseXml;
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(), e);
		}
	}
	
	private String marshallResourceToXmlString(Resource resource) throws DoiClientServiceException {
		Marshaller m = getResourceMarshaller();
		StringWriter sw = new StringWriter();
		try {
			m.marshal(resource, sw);
		} catch (JAXBException e) {
			logger.error("Error occured trying to marshall resource", e);
			throw new DoiClientServiceException("Error occured trying to marshall resource", e );
		}
		String xml = sw.toString();
		logger.debug(xml);
		if(doiClientConfig.getAsciiCleanse() != null && Boolean.TRUE.equals(doiClientConfig.getAsciiCleanse())){
			xml = asciiCleanse(xml);
			logger.debug("ASCII Cleansed XML :" + xml);
		}
		return xml;
	}
	
	private HttpResponse executeHttpPost(HttpPost httpPost ) throws DoiClientServiceException{
		DefaultHttpClient httpClient = getDefaultHttpClient();
		try {
			return httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			logger.error("Error executing httpPost :" + e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(),e);
		} catch (IOException e) {
			logger.error("Error executing httpPost :" + e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(),e);
		}
	}
	
	private HttpPost buildHttpPost(URI endpoint, String xmlPayload){
		HttpPost httpPost = new HttpPost( endpoint );
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    nvps.add(new BasicNameValuePair("xml", xmlPayload));
	    httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
	    return httpPost;
	}
	
	URI buildCreateEndpointURI(String urlExtension) throws DoiClientServiceException{
		StringBuilder sb = new StringBuilder();
		sb.append(doiClientConfig.getDoiMintingServiceUrl().trim() )
		  .append("?r=api/create&user_id=")
		  .append(doiClientConfig.getUserId().trim())
		  .append("&app_id=")
		  .append(doiClientConfig.getAppId().trim())
		  .append("&url=").append( doiClientConfig.getTopLevelUrl().trim() ).append(urlExtension);
		try{
			String urlString = sb.toString();
			logger.info("Request URL :" + urlString);
		    return new URI(urlString);
		}catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(), e);
		}
	}
	
	URI buildUpdateEndpointURI(String doi, String urlExtension) throws DoiClientServiceException{
		StringBuilder sb = new StringBuilder();
		sb.append(doiClientConfig.getDoiMintingServiceUrl() )
		  .append("?r=api/update&user_id=")
		  .append(doiClientConfig.getUserId())
		  .append("&app_id=")
		  .append(doiClientConfig.getAppId())
		  .append("&url=").append( doiClientConfig.getTopLevelUrl() ).append(urlExtension)
		  .append("&doi=").append(doi);
		try{
			String urlString = sb.toString();
			logger.info("Request URL :" + urlString);
		    return new URI(urlString);
		}catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
			throw new DoiClientServiceException(e.getMessage(), e);
		}
	}
	
	private DefaultHttpClient getDefaultHttpClient() throws DoiClientServiceException{
		return getSslEnabledDefaultHttpClient(locateKeystoreFile() , doiClientConfig.getKeystorePassword());
	}
	
	private DefaultHttpClient getSslEnabledDefaultHttpClient(File keyStore, String keystorePassword) throws DoiClientServiceException {
		SSLSocketFactory sslSocketFactory =  getSslSocketFactory(keyStore, keystorePassword);
		ClientConnectionManager clientConnectionManager = createClientConnectionManager(sslSocketFactory);
		DefaultHttpClient httpclient = new DefaultHttpClient(clientConnectionManager);
		return httpclient;
	}
	
	private ClientConnectionManager createClientConnectionManager(SSLSocketFactory sslSocketFactory) {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", 443, sslSocketFactory ));
        return new BasicClientConnectionManager(registry);
    }
	
	private SSLSocketFactory getSslSocketFactory(File keyStore, String keystorePassword) throws DoiClientServiceException {
        try {
        	Security.addProvider(new BouncyCastleProvider());  //Keystore created with bouncy castle provider jar
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = new FileInputStream (keyStore );
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, keystorePassword.toCharArray());
            } finally {
                in.close();
            }
            String alg=TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
            tmFact.init(trusted);
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
    	    ctx.init(null, tmFact.getTrustManagers(), null);
    	    SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return ssf;
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            throw new DoiClientServiceException("Socket Factory Creation error", e);
        }
    }
	
	private File locateKeystoreFile() throws DoiClientServiceException{
		String keystoreFilePath = doiClientConfig.getKeystoreFilePath();
		if(!StringUtils.hasLength(keystoreFilePath)){
			logger.error("No keystore has been specified in the configuration");
			throw new DoiClientServiceException("No keystore has been specified in the configuration");
		}
		int indx = keystoreFilePath.indexOf("classpath:");
		File returnFile = null;
		if(indx == -1){
			//Try read the file as an absolute file system path
			returnFile = new File(keystoreFilePath);
			if(! returnFile.exists() ){
				logger.error("keystore " + keystoreFilePath +" does not exist.");
				throw new DoiClientServiceException("keystore " + keystoreFilePath +" does not exist.");
			}
		}else{
			//Attempt to get the data from the classpath
			String classpathResourceName = keystoreFilePath.substring("classpath:".length());
			ClassPathResource cpResource = new ClassPathResource(classpathResourceName);
			if( ! cpResource.exists() ){
				logger.error("Keystore " + keystoreFilePath +" does not exist on the classpath");
				throw new DoiClientServiceException("Keystore " + keystoreFilePath +" does not exist on the classpath");
			}
			try{
			    returnFile = cpResource.getFile();
			}catch(IOException ioEx){
				logger.error("Problem reading classpath file "+ keystoreFilePath , ioEx);
				throw new DoiClientServiceException("Problem reading classpath file "+ keystoreFilePath , ioEx);
			}
		}
		return returnFile;
	}
	
	private Marshaller getResourceMarshaller() throws DoiClientServiceException {
		try{
			JAXBContext context = JAXBContext.newInstance(Resource.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE );
		    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-2.1 http://schema.datacite.org/meta/kernel-2.1/metadata.xsd");
		    return m;
		}catch(JAXBException e){
			logger.error("Error creating Resource marshaller", e);
			throw new DoiClientServiceException("Error creating Resource marshaller", e);
		}
	}

	@Override
	public void activateDoi(String doi) {
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public void deactivateDoi(String doi) {
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public DoiClientConfig getDoiClientConfig() {
		return doiClientConfig;
	}
	
	/**
	 * This is a quick hack put in because the TERN DOI Client was not liking unicode characters
	 * for windows weird apostrophes, and the AEKOS AE ligature thingy.
	 * 
	 */
	private static final Map<Character,String> charSubMap = new HashMap<Character, String>();
	
	{
		charSubMap.put(new Character(UTF8_AE_CHAR), "AE");
		charSubMap.put(new Character(UTF8_SINGLE_QUOTE), "'");
	}

	private static final String UNKNOWN_ASCII_CHAR = "_?";

    private static String asciiCleanse(String inputString ){
		CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
		StringBuilder output = new StringBuilder();
		for(int x = 0; x < inputString.length(); x++){
			char ch = inputString.charAt(x);
			if(! asciiEncoder.canEncode(ch)){
				Character lookupChar = new Character(ch);
				if( charSubMap.containsKey(lookupChar)){
					output.append(charSubMap.get(lookupChar));
				}else{
					output.append(UNKNOWN_ASCII_CHAR);
				}
			}else{
				output.append(ch);
			}
		}
		return output.toString();
	}
	
}
