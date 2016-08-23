package au.edu.aekos.shared.service.index;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.service.quest.RAMDirectoryVocabService;
import au.edu.aekos.shared.service.quest.TraitValue;


/**
 * Class to match species names, first term tokens, species tokens from scientific taxa controlled vocabs
 * 
 * If using from a common species, pass in the scientific name
 * 
 * @author btill
 */
@Component
public class SpeciesTermIndexSemanticExpander {

	private Logger logger = LoggerFactory.getLogger(SpeciesTermIndexSemanticExpander.class);
	
	@Autowired
	private RAMDirectoryVocabService vocabSearchService;
	
	public void expandAndIndexTerms(String speciesName, String vocabName, SolrInputDocument doc, String indexFieldName){
		Set<String> expandedTerms = getExpandedTermsForSpeciesName(speciesName, vocabName);
		if(expandedTerms != null && expandedTerms.size() > 0){
			for(String term : expandedTerms ){
				doc.addField(indexFieldName, term);
			}
		}
	}
	
	
	//Does'nt hurt to query fauna the same way
	public Set<String> getExpandedTermsForSpeciesName(String speciesName, String vocabName){
		Set<String> matchedTerms = new HashSet<String>();
		String queryTerm = SpeciesFileWriter.createAndCapitaliseFirstToken(speciesName);
		if(StringUtils.hasLength(queryTerm)){
			try {
				TraitValue firstTokenTV  = vocabSearchService.performExactMatchSearchTraitValue(queryTerm, vocabName);
				if(firstTokenTV != null){
					matchedTerms.add(firstTokenTV.getTraitValue());
				}
				TraitValue speciesTV  = vocabSearchService.performExactMatchSearchTraitValue(queryTerm + " sp.", vocabName);
				if(speciesTV != null){
					matchedTerms.add(speciesTV.getTraitValue());
				}
			} catch (IOException e) {
				logger.warn("Error occured matching " + speciesName + " to vocab " + vocabName, e);
			}
		}
		
		return matchedTerms;	
	}
	
}
