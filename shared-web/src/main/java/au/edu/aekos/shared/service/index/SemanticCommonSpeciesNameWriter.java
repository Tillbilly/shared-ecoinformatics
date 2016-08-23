package au.edu.aekos.shared.service.index;

import java.io.IOException;
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
import au.edu.aekos.shared.service.quest.RAMDirectoryVocabService;
import au.edu.aekos.shared.service.quest.TraitValue;

/**
 * Get the scientific name for the common name, 
 * match it against the scientific trait vocabulary, 
 * Then do semantic expansion.
 * 
 * Only needs IndexFieldName defined in the SubmissionIndexField, 
 * this field is where matches on the scientific name and expansions are written
 * 
 * @author btill
 */
public class SemanticCommonSpeciesNameWriter implements ManualSubmissionValueWriter {

	Logger logger = LoggerFactory.getLogger(SemanticCommonSpeciesNameWriter.class);
	
	private String traitName;
	
	private String scientificTraitName;

	@Autowired
	protected RAMDirectoryVocabService vocabSearchService;

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
			try{
			   performTermIndexing(term, indexField, doc);
			}catch(IOException e){
				logger.error("Error occured querying RAMDirectory for " + term  );
			}
		}
	}
	
	//For each term, fuzzy match the term from the vocab, to try and get the Scientific Name
	//We should have already indexed the list value, and we are dealing with values selected from the list in the first place
	public void performTermIndexing(String term, SubmissionIndexField indexField, SolrInputDocument doc) throws IOException {
		TraitValue tv = vocabSearchService.getBestFuzzyMatch(term, traitName);
		if(tv != null && tv.getScientificNames() != null && tv.getScientificNames().size() > 0){
			for(String speciesTaxaTerm : tv.getScientificNames()){
				TraitValue scientificTraitValue = vocabSearchService.getBestFuzzyMatch(speciesTaxaTerm, scientificTraitName);
				if(scientificTraitValue != null){
					speciesTaxaTerm = scientificTraitValue.getTraitValue();
				}
				doc.addField(indexField.getSpeciesIndexFieldName(), speciesTaxaTerm);
				semanticExpander.expandAndIndexTerms(speciesTaxaTerm, scientificTraitName, doc, indexField.getSpeciesIndexFieldName());
			}
		}
	}
	
	public String getTraitName() {
		return traitName;
	}

	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}

	public String getScientificTraitName() {
		return scientificTraitName;
	}

	public void setScientificTraitName(String scientificTraitName) {
		this.scientificTraitName = scientificTraitName;
	}

	public SpeciesTermIndexSemanticExpander getSemanticExpander() {
		return semanticExpander;
	}

	public void setSemanticExpander(
			SpeciesTermIndexSemanticExpander semanticExpander) {
		this.semanticExpander = semanticExpander;
	}
	
	

}
