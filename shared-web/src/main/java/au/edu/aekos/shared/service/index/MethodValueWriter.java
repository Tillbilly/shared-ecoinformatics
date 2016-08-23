package au.edu.aekos.shared.service.index;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;


/**
 * Should work the same for MethodName and Method Abstract
 * 
 * -- and any other question set grouped reponses . . . 
 * 
 * @author btill
 */
public class MethodValueWriter implements ManualSubmissionValueWriter {

	private Logger logger = LoggerFactory.getLogger(MethodValueWriter.class);
	
	public void writeValueToDocument(
			SolrInputDocument doc,
			Submission submission,
			SubmissionIndexField indexField,
			Map<String, String> metaTagToQuestionIdMap,
			Map<String, SubmissionAnswer> questionIdToAnswerMap,
			Map<String, List<SubmissionAnswer>> questIdToAnswerListForQuestionSetMap) {
        String metaTag = indexField.getSharedTag();
        String questionId = metaTagToQuestionIdMap.get(metaTag);
        if(!StringUtils.hasLength(questionId)){
        	logger.warn("Index configuration looking for Shared metatag :" + metaTag + " but no question ID exists for that tag");
        	return;
        }
        if(questIdToAnswerListForQuestionSetMap.containsKey(questionId) ){
        	List<SubmissionAnswer> answerList = questIdToAnswerListForQuestionSetMap.get(questionId);
        	if(answerList != null && answerList.size() > 0){
        		for(int x = 0; x < answerList.size() ; x++ ){
        			String indexFieldName = indexField.getIndexFieldName().replaceAll("_x_", "_" + x + "_");
        			SubmissionAnswer sa = answerList.get(x);
        			if(sa.hasResponse()){
        				doc.addField(indexFieldName, sa.getResponse());
        			}
        		}
        	}
        }
	}

	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		// TODO Auto-generated method stub
		
	}
}
