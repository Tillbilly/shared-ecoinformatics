package au.edu.aekos.shared.service.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;

public class AuthorWriter implements ManualSubmissionValueWriter, CompositeMetatagWriter {

	private Logger logger = LoggerFactory.getLogger(AuthorWriter.class);
	
	private String givenNamesTag;
	
	private String surnameTag;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		try {
			List<String> authorNameList = metaInfoExtractor.getAuthorNameList(givenNamesTag, surnameTag);
			for(String authorName : authorNameList){
				if(StringUtils.hasLength(authorName)){
					doc.addField(indexField.getIndexFieldName(), authorName);
				}
			}
		} catch (MetaInfoExtractorException e) {
			logger.warn("Error extracting author information for solr index, submissionId:" + metaInfoExtractor.getSubmissionId(), e);
		}
	}

	public String getGivenNamesTag() {
		return givenNamesTag;
	}

	public void setGivenNamesTag(String givenNamesTag) {
		this.givenNamesTag = givenNamesTag;
	}

	public String getSurnameTag() {
		return surnameTag;
	}

	public void setSurnameTag(String surnameTag) {
		this.surnameTag = surnameTag;
	}

	//Used for questionnaire validation
	public List<String> getRequiredMetatags() {
		List<String> metatags = new ArrayList<String>();
		if(StringUtils.hasLength(givenNamesTag)){
			metatags.add(givenNamesTag);
		}
		if(StringUtils.hasLength(surnameTag)){
			metatags.add(surnameTag);
		}
		return metatags;
	}

}
