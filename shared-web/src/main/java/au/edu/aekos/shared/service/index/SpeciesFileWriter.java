package au.edu.aekos.shared.service.index;

import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.file.FileLineReadingService;
import au.edu.aekos.shared.service.quest.RAMDirectoryVocabService;

public class SpeciesFileWriter implements ManualSubmissionValueWriter {

	Logger logger = LoggerFactory.getLogger(SpeciesFileWriter.class);
	
	private String traitName;
	
	@Autowired
	private FileLineReadingService speciesFileService;

	@Autowired
	protected RAMDirectoryVocabService vocabSearchService;

	@Autowired
	private SpeciesTermIndexSemanticExpander semanticExpander;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		//The species file is a list of values in a single column in a file
		List<String> speciesList = getSpeciesList(indexField, metaInfoExtractor);
		if(speciesList != null){
			for(String speciesName : speciesList){
				doc.addField(indexField.getIndexFieldName(), speciesName);
			}
		}
	}

	protected List<String> getSpeciesList(SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor){
		SubmissionAnswer sa = null;
		try {
			sa = metaInfoExtractor.getSubmissionAnswerForMetatag(indexField.getSharedTag());
		} catch (MetaInfoExtractorException e) {
			logger.warn("Error writing species file to index " + indexField.getSharedTag() + "  " + metaInfoExtractor.getSubmissionId()  , e);
			return null;
		}
	
	    if(sa != null && ResponseType.SPECIES_LIST.equals(sa.getResponseType()) 
	    		&& StringUtils.hasLength(sa.getResponse())){
	    	for(SubmissionData sd : metaInfoExtractor.getSubmission().getSubmissionDataList() ){
	    		if(sa.getResponse().equals( sd.getFileName()) ){
	    			return speciesFileService.readFileLinesAsList(sd);
	    		}
	    	}
		}
	    return null;
	}
	
	public static String createAndCapitaliseFirstToken(String string){
		String workString = string.trim();
		if(workString.length() == 0){
			return null;
		}else if(workString.length() == 1){
			return workString.toUpperCase();
		}
		if(workString.contains(" ")){
			workString = workString.substring(0,workString.indexOf(" "));
		}
		workString = workString.toLowerCase();
		return workString.substring(0,1).toUpperCase() + workString.substring(1);
	}
	
	protected void expandAndIndexTerms(String speciesName, String vocabName, SolrInputDocument doc, String indexFieldName){
		Set<String> expandedTerms = semanticExpander.getExpandedTermsForSpeciesName(speciesName, vocabName);
		if(expandedTerms != null && expandedTerms.size() > 0){
			for(String term : expandedTerms ){
				doc.addField(indexFieldName, term);
			}
		}
	}
	
	public String getTraitName() {
		return traitName;
	}

	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}
}
