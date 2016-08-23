package au.edu.aekos.shared.service.index;

import java.io.IOException;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.service.quest.TraitValue;

/**
 * Pass in a SubmissionIndexField with both the indexFieldName and speciesIndexFieldName set.
 * All values will be written to just indexFieldName if speciesIndexFieldName is null
 *  
 * @author btill
 */
public class CommonSemanticSpeciesFileWriter extends SpeciesFileWriter implements ManualSubmissionValueWriter {

	Logger logger = LoggerFactory.getLogger(CommonSemanticSpeciesFileWriter.class);
	
	private String scientificNameTrait;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		//Ensure the trait name is set
		if(!StringUtils.hasLength(getTraitName()) ){
			logger.warn("Trait name not configured for common semantic species file writer - index not written");
			return;
		}
		
		List<String> commonNameList = getSpeciesList(indexField, metaInfoExtractor);
		if(commonNameList == null || commonNameList.size() == 0){
			return;
		}
		
		//We are going to try and match common names with an entry in the species list index used by
		//the shared submission form pickers.
		//If we get a match using the fuzzy match,
		//Check if there is a scientific name, index that to the field.
		//Then see if we can index on genus / family name
		//All values are written to the same index field
		
		boolean indexScientific = ( StringUtils.hasLength(scientificNameTrait) && StringUtils.hasLength(indexField.getSpeciesIndexFieldName()) );
		
		for(String commonName : commonNameList){
			try {
				TraitValue tv = vocabSearchService.getBestFuzzyMatch(commonName, getTraitName());
				if(tv != null){
					doc.addField(indexField.getIndexFieldName(), tv.getTraitValue() );
				}
				if(indexScientific && tv.getScientificNames() != null && tv.getScientificNames().size() > 0){
					//If there are multiple scientific names for the common name, we will index each of the scientific names.
					//This may cause false positive search results, but the other option is to not index when there are multiple
					//Scientific names - is no result better than a false result ( with a chance of being the right result ? )
					//We have said no, its better, to get a possible false result than no result.
					for(String scientificName : tv.getScientificNames()){
						doc.addField(indexField.getSpeciesIndexFieldName(), scientificName );
						TraitValue stv = vocabSearchService.getBestFuzzyMatch(scientificName, scientificNameTrait);
						String speciesIndexField = StringUtils.hasLength(indexField.getSpeciesIndexFieldName()) ? indexField.getSpeciesIndexFieldName() : indexField.getIndexFieldName() ; 
						if(stv != null ){
							if(!scientificName.equalsIgnoreCase(stv.getTraitValue())){
								doc.addField(speciesIndexField, stv.getTraitValue() );
							}
							expandAndIndexTerms(stv.getTraitValue(), scientificNameTrait, doc, speciesIndexField);
						}else{
							expandAndIndexTerms(tv.getTraitValue(), scientificNameTrait, doc, speciesIndexField);
						}
					}
				}
			} catch (IOException e) {
				logger.warn("Error occures searching fuzzy match for '"+ commonName +"' for trait " + getTraitName() , e);
			}
		}
	}
	
	public String getScientificNameTrait() {
		return scientificNameTrait;
	}

	public void setScientificNameTrait(String scientificNameTrait) {
		this.scientificNameTrait = scientificNameTrait;
	}

	
}
