package au.org.aekos.shared.api.client;

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
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;

public class SharedSolrServerFactory {

    private HttpSolrServer solrServer;
	
	@Value("${shared.api.client.solr.server.hostname}")
	private String host;
	
	@Value("${shared.api.client.solr.server.port}")
	private int port;
	
	@Value("${shared.api.client.solr.server.username}")
	private String username;
	
	@Value("${shared.api.client.solr.server.password}")
	private String password;
	
	public SolrServer createSolrServerWithPreemptiveAuth() {
		DefaultHttpClient dhc = (DefaultHttpClient) solrServer.getHttpClient();
		dhc.getCredentialsProvider().setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));
		// TODO do we need this interceptor?
		dhc.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0 );
		return solrServer;
	}
	
	static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
	    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
	        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
	        // If no auth scheme available yet, try to initialise it preemptively
	        if (authState.getAuthScheme() == null) {
	            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
	            HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
	            Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
	            if (creds == null)
	                throw new HttpException("No credentials for preemptive authentication");
	            authState.update(new BasicScheme(), creds);
	        }
	    }
	}

	public void setSolrServer(HttpSolrServer solrServer) {
		this.solrServer = solrServer;
	}
}
