package au.edu.aekos.shared.doiclient.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import au.edu.aekos.shared.doiclient.jaxb.response.Response;

public class DoiClientServiceImplTestNoSpring {
	
	/**
	 * Can we parse a failure message with a nested verbose message (hint: we can't currently)?
	 */
	@Test
	public void testParseResponseXml01() throws DoiClientServiceException{
		String xml = "<?xml version=\"1.0\"?>" + // This is a real response from the test service
				"<response type=\"failure\">" +
				"<responsecode>errUrlNotResolvable</responsecode>" +
				"<message>Failed to mint DOI</message>" +
				"<verbosemessage><response type=\"failure\"> " +
				"                <responsecode>errUrlNotResolvable</responsecode>" +
				"                <message>Failed to mint DOI</message>" +
				"                <verbosemessage>[TERN-DOI] The url does not exist.</verbosemessage>" +
				"              </response>" +
				"                </verbosemessage>" +
				"</response>";
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		Response result = objectUnderTest.parseResponseXml(xml);
		assertThat(result.isSuccess(), is(false));
		assertThat(result.getResponsecode(), is("errUrlNotResolvable"));
		assertThat(result.getMessage(), is("Failed to mint DOI"));
//		assertThat(result.getVerbosemessage(), is("[TERN-DOI] The url does not exist.")); //FIXME we can't handle objects in the verbosemessage field, only text
	}
	
	/**
	 * Can we parse a success message?
	 */
	@Test
	public void testParseResponseXml02() throws DoiClientServiceException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<response type=\"success\">" +
				"    <responsecode>MT001</responsecode>" +
				"    <message>DOI 10.5072/05/519BF4450C682 was successfully minted.</message>" +
				"    <doi>10.5072/05/519BF4450C682</doi>" +
				"    <url>http://portal.aekos.org.au</url>" +
				"    <app_id>313ba574c47f1cdd8f626942dd8b6509441f23a9</app_id>" +
				"    <verbosemessage>OK</verbosemessage>" +
				"</response>";
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		Response result = objectUnderTest.parseResponseXml(xml);
		assertThat(result.isSuccess(), is(true));
		assertThat(result.getResponsecode(), is("MT001"));
		assertThat(result.getMessage(), is("DOI 10.5072/05/519BF4450C682 was successfully minted."));
		assertThat(result.getUrl(), is("http://portal.aekos.org.au"));
		assertThat(result.getApp_id(), is("313ba574c47f1cdd8f626942dd8b6509441f23a9"));
		assertThat(result.getVerbosemessage(), is("OK"));
	}
	
	/**
	 * Can we parse a failure message with no verbose message?
	 */
	@Test
	public void testParseResponseXml03() throws DoiClientServiceException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<response type=\"failure\">" +
				"    <responsecode>MT005</responsecode>" +
				"    <message>The ANDS Cite My Data service is currently unavailable. Please try again at a later time. If you continue to experience problems please contact services@ands.org.au.</message>" +
				"    <doi>10.5072/05/519AC7A621171</doi>" +
				"    <url>http://portal.aekos.org.au</url>" +
				"    <app_id>313ba574c47f1cdd8f626942dd8b6509441f23a9</app_id>" +
				"    <verbosemessage/>" +
				"</response>";
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		Response result = objectUnderTest.parseResponseXml(xml);
		assertThat(result.isSuccess(), is(false));
		assertThat(result.getResponsecode(), is("MT005"));
		assertThat(result.getMessage(), is("The ANDS Cite My Data service is currently unavailable. Please try again at a later time. If you continue to experience problems please contact services@ands.org.au."));
		assertThat(result.getDoi(), is("10.5072/05/519AC7A621171"));
		assertThat(result.getUrl(), is("http://portal.aekos.org.au"));
		assertThat(result.getApp_id(), is("313ba574c47f1cdd8f626942dd8b6509441f23a9"));
		assertThat(result.getVerbosemessage(), is(""));
	}
	
	/**
	 * Can we extract all the relevant information from a failure message?
	 */
	@Test(expected = DoiClientServiceException.class)
	public void testValidateResponseAndRetrieveDOI01() throws Throwable {
		String xml = "<?xml version=\"1.0\"?>" +
			"<response type=\"failure\">" +
				"<responsecode>errUrlNotResolvable</responsecode>" +
				"<message>Failed to mint DOI</message>" +
				"<verbosemessage>[TERN-DOI] Permission denied.Your account has not been approved yet.Please contact TERN office if you need it urgently.</verbosemessage>" +
			"</response>";
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		HttpResponse httpResponse = mock(HttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(httpResponse.getEntity()).thenReturn(new StringEntity(xml));
		objectUnderTest.validateResponseAndRetrieveDOI(httpResponse);
	}
	
	/**
	 * Can we survive no 'verbosemessage' element?
	 */
	@Test(expected = DoiClientServiceException.class)
	public void testValidateResponseAndRetrieveDOI02() throws Throwable {
		String xml = "<?xml version=\"1.0\"?>" +
			"<response type=\"failure\">" +
				"<responsecode>errUrlNotResolvable</responsecode>" +
				"<message>Failed to mint DOI</message>" +
			"</response>";
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		HttpResponse httpResponse = mock(HttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(httpResponse.getEntity()).thenReturn(new StringEntity(xml));
		objectUnderTest.validateResponseAndRetrieveDOI(httpResponse);
	}
	
	/**
	 * Can we build the endpoint URI for a 'create' operation from the config?
	 */
	@Test
	public void testBuildCreateEndpointURI01() throws DoiClientServiceException{
		DoiClientConfig config = new DoiClientConfig();
		config.setUserId("first.last@adelaide.edu.au");
		config.setAppId("eb4ff233f145bc50b66bc3a85a0036c0");
		config.setTopLevelUrl("http://www.example.com");
		config.setDoiMintingServiceUrl("https://doi.tern.uq.edu.au/test/index.php");
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		objectUnderTest.setDoiClientConfig(config);
		URI result = objectUnderTest.buildCreateEndpointURI("/dataset/123");
		assertThat(result.toString(), 
			is("https://doi.tern.uq.edu.au/test/index.php?r=api/create" +
					"&user_id=first.last@adelaide.edu.au" +
					"&app_id=eb4ff233f145bc50b66bc3a85a0036c0" +
					"&url=http://www.example.com/dataset/123"));
	}
	
	/**
	 * Can we build the endpoint URI for an 'update' operation from the config?
	 */
	@Test
	public void testBuildUpdateEndpointURI01() throws DoiClientServiceException{
		DoiClientConfig config = new DoiClientConfig();
		config.setUserId("first.last@adelaide.edu.au");
		config.setAppId("eb4ff233f145bc50b66bc3a85a0036c0");
		config.setTopLevelUrl("http://www.example.com");
		config.setDoiMintingServiceUrl("https://doi.tern.uq.edu.au/test/index.php");
		DoiClientServiceImpl objectUnderTest = new DoiClientServiceImpl();
		objectUnderTest.setDoiClientConfig(config);
		URI result = objectUnderTest.buildUpdateEndpointURI("10.4227/05/OLDDOI", "/dataset/123");
		assertThat(result.toString(), 
			is("https://doi.tern.uq.edu.au/test/index.php?r=api/update" +
					"&user_id=first.last@adelaide.edu.au" +
					"&app_id=eb4ff233f145bc50b66bc3a85a0036c0" +
					"&url=http://www.example.com/dataset/123" +
					"&doi=10.4227/05/OLDDOI"));
	}
}
