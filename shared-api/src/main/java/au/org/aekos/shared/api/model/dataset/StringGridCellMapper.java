package au.org.aekos.shared.api.model.dataset;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;

public class StringGridCellMapper extends GridCellMapper {
	public StringGridCellMapper(String label, String indexField) {
		super(label, indexField);
	}

	@Override
	public SharedGridField map(SolrDocument doc) {
		Collection<Object> values = doc.getFieldValues(indexField);
		return new SharedGridField(label, StringUtils.join(values, ", "));
	}
}