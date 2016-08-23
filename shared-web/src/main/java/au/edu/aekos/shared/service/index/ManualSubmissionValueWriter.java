package au.edu.aekos.shared.service.index;

import org.apache.solr.common.SolrInputDocument;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;

public interface ManualSubmissionValueWriter {
	
	/**
	 * 
	 * @param doc
	 * @param indexField
	 * @param metaInfoExtractor
	 */
	void writeValueToDocument(SolrInputDocument doc,
                             SubmissionIndexField indexField, 
                             MetaInfoExtractor metaInfoExtractor);
	
	
}
