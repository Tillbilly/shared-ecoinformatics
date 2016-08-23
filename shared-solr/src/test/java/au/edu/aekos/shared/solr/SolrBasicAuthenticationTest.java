package au.edu.aekos.shared.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;



//This is an integration test, used to develop code in au.edu.aekos.shared.service.index.SolrIndexServiceImpl
public class SolrBasicAuthenticationTest {

    public String username = "solr";
    public String password = "solr";
    
	public SolrServer getServer(){
		return new HttpSolrServer("http://115.146.85.142:8081/solr");
	}
	
	
	@Test @Ignore
	public void testAccessToBasicProtectedSolrServer_NoAuth() throws IOException{
		SolrServer ss = getServer();
		try{
		    UpdateResponse ur = ss.add(buildTextDoc("reduce1","reduce1 test","reduce1 description","tester_t","tester","tester2_t","tester"));
	    
		}catch(SolrServerException ex){
			
		}catch(SolrException ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return;
		}
	    Assert.fail();	
	}
	
	
	@Test @Ignore
	public void testAccessToBasicProtectedSolrServer_OK() throws IOException, SolrServerException{
		HttpSolrServer ss = (HttpSolrServer) getServer();
		DefaultHttpClient dhc = (DefaultHttpClient) ss.getHttpClient();
		dhc.getCredentialsProvider().setCredentials(
                new AuthScope("115.146.85.142", 8081),
                new UsernamePasswordCredentials(username,password));
		dhc.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0 );
		try{
			long time = System.currentTimeMillis();
		    UpdateResponse ur = ss.add(buildTextDoc("reduce4","reduce4 test","reduce4 description","tester_t","tester","tester2_t","tester 4dawg"));
		    long elapsed = System.currentTimeMillis() - time;
		    System.out.println("Time to add doc: " + elapsed);
	        Assert.assertEquals(0, ur.getStatus());
		}catch(SolrServerException ex){
			ex.printStackTrace();
			Assert.fail();
		}catch(SolrException ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			Assert.fail();
		}
		long timeC = System.currentTimeMillis();
		ss.commit();
		long elapsedC = System.currentTimeMillis() - timeC;
		System.out.println("Commit elapsed: " + elapsedC );
		//Now lets try do a query
		SolrQuery query = new SolrQuery();
	    query.setQuery("id:reduce4");
	    long timeQ = System.currentTimeMillis();
	    QueryResponse qr = ss.query( query );
	    long elapsedQ = System.currentTimeMillis() - timeQ;
	    System.out.println("QueryTime: " + elapsedQ );
	    Assert.assertEquals(1, qr.getResults().size());
	    	
		
	}
	
	public SolrInputDocument buildTextDoc(String id, String title, String description, String... textFieldPairs){
		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField( "id", id );
	    doc.addField( "title", title);
	    String titleSrch = title.replaceAll(" ", "");
	    doc.addField("titlesort_t", titleSrch);
	    doc.addField("description", description);
	    if(textFieldPairs != null && textFieldPairs.length > 1){
	    	for(int x = 0; x < textFieldPairs.length - 1 ; x = x + 2 ){
	    		doc.addField(textFieldPairs[x], textFieldPairs[x + 1]);
	    	}
	    }
	   return doc;
	}
	
	static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
	    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
	        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
	        // If no auth scheme avaialble yet, try to initialize it
	        // preemptively
	        if (authState.getAuthScheme() == null) {
	            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
	            HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
	            Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
	            if (creds == null)
	                throw new HttpException("No credentials for preemptive authentication");
	            authState.update(new BasicScheme(), creds);
	            //authState.setCredentials(creds);
	        }
	    }
	}
	
}
