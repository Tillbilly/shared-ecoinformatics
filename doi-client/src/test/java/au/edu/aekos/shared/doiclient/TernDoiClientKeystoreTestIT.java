package au.edu.aekos.shared.doiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import org.junit.Test;

import au.edu.aekos.shared.doiclient.jaxb.ContributorType;
import au.edu.aekos.shared.doiclient.jaxb.Resource;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Creators.Creator;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Identifier;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Titles;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Titles.Title;
import au.edu.aekos.shared.doiclient.jaxb.response.Response;
import au.edu.aekos.shared.doiclient.service.DoiClientServiceImplTestIT;
import au.edu.aekos.shared.doiclient.util.ResourceBuilder;

//To debug SSL, add the following jvm parameter: -Djavax.net.debug=all

public class TernDoiClientKeystoreTestIT {

	private final String userId = "matt.schneider@adelaide.edu.au";
	private final String appId = "eb4ff2eaf145bc50b66bc3a85a0036c0";
	private final String topLevelURL = "http://aekos.org.au";
	private final String testServiceCreateUrl = "https://doi.tern.uq.edu.au/test/index.php?r=api/create";
//	private final String testServiceUpdateUrl = "https://doi.tern.uq.edu.au/test/index.php?r=api/update";
	
	private final String failureResponse = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<response type=\"failure\">" +
				"<responsecode>MT005</responsecode>" +
				"<message>The ANDS Cite My Data service is currently unavailable. Please try again at a later time. " +
					"If you continue to experience problems please contact services@ands.org.au.</message>	" +
				"<doi>10.5072/05/519AC7A621171</doi>	" +
				"<url>http://portal.aekos.org.au</url>	" +
				"<app_id>313ba574c47f1cdd8f626942dd8b6509441f23a9</app_id>	" +
				"<verbosemessage></verbosemessage>" +
			"</response>";
	
	/**
	 * Can we mint a DOI for a "simple" URL
	 */
	@Test
	public void testMintDoiFromTestService_simple() throws Throwable {
		Resource resource = createTestResource("My test title3");
		doMint(topLevelURL, resource);
	}
	
	/**
	 * Can we mint a DOI for a "custom" URL
	 */
	@Test
	public void testMintDoiFromTestService_customUrl() throws Throwable {
		Resource resource = createTestResource("My test title3");
		doMint(topLevelURL+ DoiClientServiceImplTestIT.URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS, resource); 
	}
	
	/**
	 * Can we parse a failure message?
	 */
	@Test
	public void testParseFailureResponseMessage() throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(Response.class);
	    Unmarshaller um = context.createUnmarshaller();
	    StringReader sr = new StringReader(failureResponse);
		Object o = um.unmarshal(sr);
		Response resp = (Response) o;
		assertFalse(resp.isSuccess());
		assertEquals("MT005", resp.getResponsecode());
	}
	
	/**
	 * Can we mint a DOI with a contributor?
	 */
	@Test 
	public void testContributor() throws Throwable {
		ResourceBuilder builder = new ResourceBuilder();
		builder.addCreator("Till, Benjamin").setTitle("Test").setPublisher("AEKOS").setPublicationYear("2013");
		builder.addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON );
		Resource r = builder.getResource();
		doMint(topLevelURL+ DoiClientServiceImplTestIT.URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS, r);
	}
	
	private void doMint(String url, Resource r) throws Throwable {
		DefaultHttpClient httpclient = new DefaultHttpClient(createClientConnectionManager());
		URI endpoint = new URI(testServiceCreateUrl + "&user_id=" + userId + "&app_id=" + appId + "&url="+url) ;
		System.out.println(endpoint.toASCIIString());
		HttpPost httpPost = new HttpPost(endpoint);
		JAXBContext context = JAXBContext.newInstance(Resource.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE );
	    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-2.1 http://schema.datacite.org/meta/kernel-2.1/metadata.xsd");
	    StringWriter sw = new StringWriter();
	    m.marshal(r, sw);
	    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("xml", sw.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
	    try{
	        HttpResponse response = httpclient.execute(httpPost);
	        HeaderIterator iter = response.headerIterator();
	        while(iter.hasNext()){
	        	Header h = (Header) iter.next();
	        	System.out.println(h.getName() + "   " + h.getValue() );
	        }
	        System.out.println(response.getStatusLine());
	        HttpEntity entity = response.getEntity();
	        System.out.println(entity.getContentType());
	        String responseXml = EntityUtils.toString(entity);
	        System.out.println(responseXml);
	        JAXBContext context2 = JAXBContext.newInstance(Response.class);
		    //Marshaller m = context.createUnMarshaller();
		    Unmarshaller um = context2.createUnmarshaller();
		    StringReader sr = new StringReader(responseXml);
			Object o = um.unmarshal(sr);
			Response resp = (Response) o;
	        assertNotNull(resp);
	        if(resp.isSuccess()){
	        	assertNotNull(resp.getDoi());
	        	System.out.println(resp.getDoi());
	        }else{
	        	assertEquals(Response.FAILURE_RESPONSE_TYPE, resp.getType());
	        }
	    }catch(javax.net.ssl.SSLPeerUnverifiedException e){
	    	e.printStackTrace();
	    	if (e.getCause() != null) {
	    		e.getCause().printStackTrace();
	    	}
	    	fail("SSL Verification error - is the certificate in the keystore still in date and is your user signed up to mint DOIs?");
	    }
	}
	
	private Resource createTestResource(String titleIn){
        Resource testResource = new Resource();
		
		Identifier emptyIdentifier = new Identifier();
		emptyIdentifier.setIdentifierType("DOI");
		testResource.setIdentifier(emptyIdentifier);
		
		Creator creator = new Creator();
		creator.setCreatorName("Till, Benjamin");  //Should this be the other way?
		
		Resource.Creators creators = new Resource.Creators();
		creators.getCreator().add(creator);
		testResource.setCreators(creators);
		
		Title title = new Title();
		title.setValue(titleIn);
		Titles titles = new Titles();
		titles.getTitle().add(title);
		testResource.setTitles(titles);
		
		testResource.setPublisher("SHaRED AEKOS");
		testResource.setPublicationYear("2013");
		return testResource;
	}
	
	private ClientConnectionManager createClientConnectionManager() throws KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        SchemeRegistry registry = new SchemeRegistry();
        //registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory() ));
        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        registry.register(new Scheme("https", 443, newSslSocketFactory() ));
        return new BasicClientConnectionManager(registry);
    }
	
	private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
        	Security.addProvider(new BouncyCastleProvider());
        	for(Provider prov : Security.getProviders()){
        		System.out.println(prov.getName());
        	}
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = TernDoiClientKeystoreTestIT.class.getClassLoader().getResourceAsStream("ternkeystore.bks");
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, "mysecret".toCharArray());
            } finally {
                in.close();
            }
            String alg=TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
            tmFact.init(trusted);
            
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
    	    ctx.init(null, tmFact.getTrustManagers(), null);
    	    SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            //SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            //sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return ssf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}

