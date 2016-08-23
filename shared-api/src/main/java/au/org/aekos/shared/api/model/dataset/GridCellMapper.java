package au.org.aekos.shared.api.model.dataset;

import org.apache.solr.common.SolrDocument;

abstract class GridCellMapper {
	protected final String label, indexField;
	
	public GridCellMapper(String label, String indexField) {
		this.label = label;
		this.indexField = indexField;
	}
	
	public boolean canExecute(SolrDocument doc) {
		return doc.containsKey(indexField);
	}
	
	abstract SharedGridField map(SolrDocument doc);
}