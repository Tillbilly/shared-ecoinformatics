package au.edu.aekos.shared.service.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class DefaultValueWriter implements ManualSubmissionValueWriter {
	
	Logger logger = LoggerFactory.getLogger(DefaultValueWriter.class);
	
	public void writeValueToDocument( SolrInputDocument doc,
			SubmissionIndexField indexField,
			MetaInfoExtractor metaInfoExtractor ) {
		String sharedTag = indexField.getSharedTag();
		SubmissionAnswer sa;
		try {
			sa = metaInfoExtractor.getSubmissionAnswerForMetatag(sharedTag);
		} catch (MetaInfoExtractorException e) {
			logger.warn("Error for submission " + metaInfoExtractor.getSubmissionId() +" Field:" + indexField.getIndexFieldName() + " Tag:" + indexField.getSharedTag(),e);
			return;
		}
		if(sa != null && sa.hasResponse() ){
			writeSubmissionAnswerToDocument(indexField, sa, doc, metaInfoExtractor);
		}
	}
	
	private void writeSubmissionAnswerToDocument(SubmissionIndexField indexField, SubmissionAnswer answer, SolrInputDocument doc, MetaInfoExtractor metaInfoExtractor) {
		if(ResponseType.getIsMultiselect(answer.getResponseType()) ){
			List<String> responses = metaInfoExtractor.getResponsesFromMultiselectAnswer(answer);
			if(responses != null && responses.size() > 0){
				for(String response : responses){
					addResponseToDoc(indexField, response, doc);
				}
			}
		}else{
			try{
			    String response = metaInfoExtractor.getSingleResponseForMetatag(indexField.getSharedTag());
			    addResponseToDoc(indexField, response, doc);
			}catch(MetaInfoExtractorException ex){
				logger.error("Error getting single response for " + indexField.getSharedTag());
			}
		}
	}
	
	private void addResponseToDoc(SubmissionIndexField indexField, String responseStr, SolrInputDocument doc){
		if(StringUtils.hasLength(responseStr)){
    		if(indexField.getConvertValueToClass() == null ){
    			doc.addField(indexField.getIndexFieldName(), responseStr);
    		}else{
    			Object writeResponse =  convertToClass(responseStr,indexField.getConvertValueToClass() );
    			if(writeResponse != null){
    			    doc.addField(indexField.getIndexFieldName(), writeResponse );
    			}else{
    				logger.error("Could not convert " + responseStr + " to " + indexField.getConvertValueToClass() + ". " + indexField.getSharedTag() + " not written.");
    			}
    		}
    	}
	}
	
	private Object convertToClass(String responseStr, @SuppressWarnings("rawtypes") Class convertToClass ){
		if(Date.class.equals(convertToClass)){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				return sdf.parse(responseStr);
			} catch (ParseException e) {
				logger.error("date parse failed for " + responseStr , e );
				return responseStr;
			}
		}else if(Integer.class.equals(convertToClass)){
			try{
				int parsedInt = Integer.parseInt(responseStr.trim());
				return new Integer(parsedInt);
			}catch(NumberFormatException ex){
				logger.error("Couldn't parse " + responseStr + " to Integer", ex);
			}
			return null;
		}
		return responseStr;
	}
	

}
