package au.org.aekos.shared.api;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.After;

/**
 * Common behaviour for testing classes that use Solr.
 * Yeah, AbstractSolrTestCase exists but it doesn't do what we want.
 */
public abstract class AbstractSharedSolrTest {
	private static final String DEFAULT_HOME = "../shared-solr/embedded-4.5/solr-orig";
	protected SolrServer solrServer;
	
	public AbstractSharedSolrTest() {
		System.setProperty("solr.solr.home", getSolrHomeLocation());
		System.setProperty("solr.clustering.enabled","false"); 
		CoreContainer coreContainer = new CoreContainer(getSolrHomeLocation());
		coreContainer.load();
		solrServer = new EmbeddedSolrServer(coreContainer, "collection1");
		try {
			solrServer.deleteByQuery("*:*");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String getSolrHomeLocation() {
		return DEFAULT_HOME;
	}
	
	/*
	 * Set to final so no-one overrides it and forgets to call super().
	 */
	@After 
	public final void after() throws Exception {
		solrServer.deleteByQuery("*:*");
		solrServer.shutdown();
	}
}
