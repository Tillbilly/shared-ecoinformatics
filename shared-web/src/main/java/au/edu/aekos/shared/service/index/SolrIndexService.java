package au.edu.aekos.shared.service.index;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;


public interface SolrIndexService {
    /**
     * The returned Integer is the server response status, we would like a response of 0
     * @param sub
     * @return
     * @throws SolrServerException
     * @throws IOException
     * @throws MetaInfoExtractorException 
     */
	Integer addSubmissionToSolr(Submission sub) throws SolrServerException, IOException, MetaInfoExtractorException;
	
	void reindexAllPublishedSubmissions();
	
	SolrServer getSolrServer();
	
}
