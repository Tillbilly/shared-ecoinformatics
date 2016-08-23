package au.edu.aekos.shared.service.index;

import org.apache.solr.common.SolrInputDocument;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;

public class IdValueWriter implements ManualSubmissionValueWriter {

	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		String id = new String(metaInfoExtractor.getSubmission().getId().toString());
		doc.addField(indexField.getIndexFieldName(), id);
	}

}
