package au.edu.aekos.shared.service.index;

import java.util.HashSet;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

/**
 * These terms have already been selected from the vocabulary, 
 * in this writer we wish just to do the semantic expansion against the same vocab list.
 * 
 * 
 * 
 * @author btill
 */
public class SemanticSpeciesWriter implements ManualSubmissionValueWriter {

	Logger logger = LoggerFactory.getLogger(SemanticSpeciesWriter.class);
	
	private String traitName;

	@Autowired
	private SpeciesTermIndexSemanticExpander semanticExpander;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		String sharedTag = indexField.getSharedTag();
		SubmissionAnswer sa;
		try {
			sa = metaInfoExtractor.getSubmissionAnswerForMetatag(sharedTag);
		} catch (MetaInfoExtractorException e) {
			logger.warn("Error for submission " + metaInfoExtractor.getSubmissionId() +" Field:" + indexField.getIndexFieldName() + " Tag:" + indexField.getSharedTag(),e);
			return;
		}
		if(sa == null || ! sa.hasResponse()){
			return;
		}
		Set<String> terms = new HashSet<String>();
		if(!ResponseType.getIsMultiselect(sa.getResponseType() ) ){
			terms.add(sa.getResponse());
		}else{
			for(SubmissionAnswer msa : sa.getMultiselectAnswerList() ){
				if(msa.hasResponse()){
					terms.add(msa.getResponse());
				}
			}
		}
		for(String term : terms){
			semanticExpander.expandAndIndexTerms(term, getTraitName(), doc, indexField.getIndexFieldName());
		}
		
	}
	
	public String getTraitName() {
		return traitName;
	}

	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}
	
	

}
