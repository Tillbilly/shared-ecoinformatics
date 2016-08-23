package au.edu.aekos.shared.solr.tools;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Ignore;
import org.junit.Test;

//An emergency indexed document remover

@Ignore
public class ProdIndexHackingOperationsTest {
	
	//this is the current shared production solr install, on nectar
	private static final String SOLR_URL = "";
	private static final String HOST = "";
	private static final int PORT = 0;
	
	private static final String USERNAME = "solr";
	private static final String PASSWORD = "";
	
	
	
	@Test
	public void removeDocumentFromProdIndex() throws SolrServerException, IOException{
		SolrServer server = getSolrServer();
		//server.deleteById("905");
		server.commit();
		server.shutdown();
	}
	
	public SolrServer getSolrServer(){
		HttpSolrServer solrServer = new HttpSolrServer( SOLR_URL );
		DefaultHttpClient dhc = (DefaultHttpClient) solrServer.getHttpClient();
		dhc.getCredentialsProvider().setCredentials(
                new AuthScope(HOST, PORT),
                new UsernamePasswordCredentials(USERNAME,PASSWORD));
		dhc.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0 );
		return solrServer;
	}
	
	static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
	    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
	        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
	        // If no auth scheme available yet, try to initialize it
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
