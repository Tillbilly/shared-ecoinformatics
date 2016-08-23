package au.edu.aekos.shared.service.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;

/**
 * The intention is for this class to write the search result type description
 * contents to the document. TBD at this stage.
 * 
 * @author btill
 */
public class DescriptionWriter implements ManualSubmissionValueWriter, CompositeMetatagWriter {

	private Logger logger = LoggerFactory.getLogger(DescriptionWriter.class);
	
	private String abstractTag;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		if(StringUtils.hasLength(abstractTag)){
			try {
				String description = metaInfoExtractor.getSingleResponseForMetatag(abstractTag);
				if(StringUtils.hasLength(description)){
					doc.addField(indexField.getIndexFieldName(), description );
				}
			} catch (MetaInfoExtractorException e) {
				logger.warn("Error mapping description",e);
			}
		}else{
			logger.warn("No configuration for abstractTag for index DescriptionWriter");
		}
	}

	public String getAbstractTag() {
		return abstractTag;
	}

	public void setAbstractTag(String abstractTag) {
		this.abstractTag = abstractTag;
	}

	public List<String> getRequiredMetatags() {
		List<String> metatags = new ArrayList<String>();
		if(StringUtils.hasLength(abstractTag)){
			metatags.add(abstractTag);
		}
		return metatags;
	}

}
