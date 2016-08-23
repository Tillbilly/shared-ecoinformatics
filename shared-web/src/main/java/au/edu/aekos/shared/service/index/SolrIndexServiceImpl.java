package au.edu.aekos.shared.service.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.web.util.SharedUrlUtils;

@Service
public class SolrIndexServiceImpl implements SolrIndexService {

	private static final Logger logger = LoggerFactory.getLogger(SolrIndexServiceImpl.class);
	
	@Qualifier(value="pubindexConfig")
	@Autowired
	private SubmissionIndexConfig submissionIndexConfig;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Override @Transactional
	public Integer addSubmissionToSolr(Submission sub) throws SolrServerException, IOException, MetaInfoExtractorException {
		SolrServer solrServer = null;
		Integer responseStatus = null;
		try{
			//Get a freshly hydrated submission entity
			Submission submission = submissionService.retrieveSubmissionById(sub.getId());
			SolrInputDocument doc = buildSubmissionSolrDocument(submission);
			
	        solrServer = getSolrServer();
	        logger.info("Deleteing document id " + sub.getId().toString() + " if it exists");
	        solrServer.deleteById(sub.getId().toString());
	        logger.info("About to load document to solr");
			logger.info(doc.toString());
		    UpdateResponse ur = solrServer.add(doc);
		    responseStatus = ur.getStatus();
		    solrServer.commit();
		}
		finally{
			if(solrServer != null){
                solrServer.shutdown();
			}
		}
		return responseStatus;
	}

	@Override @Transactional
	public void reindexAllPublishedSubmissions() {
		//Given we don't know how many submissions will ultimately be in the system, going to be wary of query size / memory 
		//and retrieve only the Submission IDs, and process them individually.  
		//I don't anticipate the reindex operation will happen that often, if it does happen, and there are more than say 1000 submissions, we
		//may need to asynchronously reindex in multiple threads.  But linear processing is OK for now . . .
		logger.info("Reindexing ALL submissions");
		List<Long> publishedSubmissionList = submissionService.getPublishedSubmissionIdList();
		if(publishedSubmissionList != null && publishedSubmissionList.size() > 0){
			for(Long subId : publishedSubmissionList){
				Submission sub = submissionService.retrieveSubmissionById(subId);
				if(sub != null){
					try{
					    addSubmissionToSolr(sub);
					}catch(Exception e){
						logger.error("Error occured reindexing submission " + subId, e);
					}
				}
			}
		}
		logger.info("Reindexing complete. Removing 'REMOVED' submissions from the index");
		
		//A submission might have had its status changed to 'REMOVED', in which case it needs to be removed from the index.
		List<Long> removedSubmissionIdList = submissionService.getRemovedSubmissionIdList();
		if(removedSubmissionIdList != null && removedSubmissionIdList.size() > 0){
			List<String> idStringList = new ArrayList<String>();
			for(Long id : removedSubmissionIdList ){
				logger.info("Reindex - REMOVE document " + id.toString());
				idStringList.add(id.toString());
			}
			SolrServer solrServer = null;
			try{
			    solrServer =  getSolrServer();
			    solrServer.deleteById(idStringList);
			    solrServer.commit();
			}catch(Exception e){
				logger.error("Error occured trying to remove submissions from index", e);
				String idMessage ="";
				for(String idStr : idStringList){
					idMessage += idStr + " ";	
				}
				logger.error("Failed remove ids " + idMessage);
			} finally{
				solrServer.shutdown();
			}
		}
		logger.info("Remove operation complete");
	}
	
	private SolrInputDocument buildSubmissionSolrDocument(Submission submission) throws MetaInfoExtractorException{
		MetaInfoExtractor metaInfoExtractor = null;
		try{
		    metaInfoExtractor = metaInfoExtractorFactory.getInstance(submission.getId(), true);
		}catch(Exception e){
			throw new MetaInfoExtractorException("Error occurred initialising meta info extractor", e);
		}
		SolrInputDocument doc = new SolrInputDocument();
		
		for(SubmissionIndexField indexField : submissionIndexConfig.getFieldMappingList() ){
            String metaTag = indexField.getSharedTag();
            try{
				if(submissionIndexConfig.getManualFieldWriterMap().containsKey(metaTag)){
					ManualSubmissionValueWriter writer = submissionIndexConfig.getManualFieldWriterMap().get(metaTag);
					try{
					    writer.writeValueToDocument(doc, indexField, metaInfoExtractor);
					}catch(Exception ex){
						logger.error("Error occured with ManualFieldWriter " + writer.getClass().getName());
						throw ex;
					}
				}else{
					writeValueToDocument(doc, indexField,metaInfoExtractor);
				}
            }catch(Exception e){
            	logger.error("Error occured writing field " + indexField.toString(), e);
            }
		}
	
		return doc;
	}
	
	/**
	 * This used to cater for embedded server, not fixed since I upgraded to solr 4.5 - 
	 * browse repo history to see the embedded server code.
	 */
	public SolrServer getSolrServer(){
		HttpSolrServer solrServer = new HttpSolrServer( submissionIndexConfig.getSolrUrl() );
		if(submissionIndexConfig.getAuthRequired()){
			DefaultHttpClient dhc = (DefaultHttpClient) solrServer.getHttpClient();
			dhc.getCredentialsProvider().setCredentials(
	                new AuthScope(
	                		SharedUrlUtils.retrieveTopLevelAddressFromUrlString(submissionIndexConfig.getSolrUrl()), 
	                		Integer.parseInt(SharedUrlUtils.retrievePortFromUrlString(submissionIndexConfig.getSolrUrl()))),
	                new UsernamePasswordCredentials(submissionIndexConfig.getUsername(),submissionIndexConfig.getPassword()));
			dhc.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0 );
		}
		return solrServer;
	}
	
	private void writeValueToDocument( SolrInputDocument doc,
			SubmissionIndexField indexField,
			MetaInfoExtractor metaInfoExtractor ) {
		DefaultValueWriter writer = new DefaultValueWriter();
		writer.writeValueToDocument(doc, indexField, metaInfoExtractor);
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
