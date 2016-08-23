package au.edu.aekos.shared.service.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;

/**
 * For use when the same metatag gets written by more than 1 writer, e.g. the species writers.
 * @author btill
 */
public class MultiWriter implements ManualSubmissionValueWriter {

	public List<ManualSubmissionValueWriter> writerList = new ArrayList<ManualSubmissionValueWriter>();
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		if(writerList != null && writerList.size() > 0){
			for(ManualSubmissionValueWriter writer : writerList){
				writer.writeValueToDocument(doc, indexField, metaInfoExtractor);
			}
		}
	}

	public List<ManualSubmissionValueWriter> getWriterList() {
		return writerList;
	}

	public void setWriterList(List<ManualSubmissionValueWriter> writerList) {
		this.writerList = writerList;
	}

}
