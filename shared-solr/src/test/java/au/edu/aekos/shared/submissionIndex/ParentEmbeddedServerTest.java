package au.edu.aekos.shared.submissionIndex;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.After;
import org.junit.Before;

public abstract class ParentEmbeddedServerTest {

	public abstract String getIndexLocation(); // Init this in child classes
	
	public EmbeddedSolrServer getEmbeddedServer(){
		System.setProperty("solr.solr.home",getIndexLocation() );
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(getIndexLocation());
		coreContainer.load();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");
		return server;
	}
	
	public EmbeddedSolrServer server = null;
	
	@Before
	public void initServer(){
		server = getEmbeddedServer();
	}
	@After
	public void cleanUp(){
		if(server != null){
			server.shutdown();
			server = null;
		}
	}
	

}
