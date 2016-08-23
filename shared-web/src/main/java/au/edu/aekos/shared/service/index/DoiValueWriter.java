package au.edu.aekos.shared.service.index;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;

public class DoiValueWriter implements ManualSubmissionValueWriter {

	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		String doi = metaInfoExtractor.getSubmission().getDoi();
		if(StringUtils.hasLength(doi)){
		    doc.addField(indexField.getIndexFieldName(), doi);	
		}
		
	}

}
