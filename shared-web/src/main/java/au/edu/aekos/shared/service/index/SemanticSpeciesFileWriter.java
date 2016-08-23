package au.edu.aekos.shared.service.index;

import java.io.IOException;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.service.quest.TraitValue;

public class SemanticSpeciesFileWriter extends SpeciesFileWriter implements ManualSubmissionValueWriter {
	Logger logger = LoggerFactory.getLogger(SemanticSpeciesFileWriter.class);
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		//The species file is a list of values in a single column in a file
		List<String> speciesList = getSpeciesList(indexField, metaInfoExtractor);
		if(speciesList != null){
			for(String speciesName : speciesList){
				TraitValue tv = null;
				try {
					tv = vocabSearchService.getBestFuzzyMatch(speciesName, getTraitName());
					if(tv != null){
					    doc.addField(indexField.getIndexFieldName(), tv.getTraitValue() );
					    expandAndIndexTerms(tv.getTraitValue(), getTraitName(), doc, indexField.getIndexFieldName());
					}
				} catch (IOException e) {
					logger.error("Error occured fuzzy match "+ speciesName + " " + getTraitName() ,e);
				}
				if(tv == null){
				    expandAndIndexTerms(speciesName, getTraitName(), doc, indexField.getIndexFieldName());
				}
			}
		}
	}
}
