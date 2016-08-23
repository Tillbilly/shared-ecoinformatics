package au.edu.aekos.shared.service.index;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.AnswerImage;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class ImageLinkWriter implements ManualSubmissionValueWriter {
	
	private Logger logger = LoggerFactory.getLogger(ImageLinkWriter.class);
	
	@Override @Transactional
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		try {
			SubmissionAnswer sa = metaInfoExtractor.getSubmissionAnswerForMetatag(indexField.getSharedTag());
			if(sa == null || ! sa.hasResponse()){
				return;
			}
            if(! ResponseType.IMAGE.equals(sa.getResponseType())){
            	logger.warn("Error creating image link. meta " + indexField.getSharedTag() + " submissionId:" + metaInfoExtractor.getSubmissionId() + " Answer is not 'IMAGE' type");
            	return;
            }
            if(sa.hasResponse()){
                String indexFieldName = indexField.getIndexFieldName();
                doc.addField(indexFieldName, sa.getResponse());
                String imageFieldPrefix = getFieldPrefix(indexFieldName);
                
                AnswerImage answerImage = sa.getAnswerImage();
                String questionId = sa.getQuestionId();
                Long submissionId = metaInfoExtractor.getSubmissionId();
                if(StringUtils.hasLength( answerImage.getImageObjectId() ) ){
                	String imageUrl = constructGetImageUrl(submissionId, questionId, answerImage.getImageObjectId());
                	doc.addField(imageFieldPrefix+ "_link_s", imageUrl);
                }
                if(StringUtils.hasLength(answerImage.getImageThumbnailId()) ){
                	String imageUrl = constructGetImageUrl(submissionId, questionId, answerImage.getImageThumbnailId());
                	doc.addField(imageFieldPrefix+ "_linktn_s", imageUrl);
                }
            
            }
		} catch (MetaInfoExtractorException e) {
			logger.warn("Error creating image link. meta " + indexField.getSharedTag() + " submissionId:" + metaInfoExtractor.getSubmissionId(), e);
		}
		
		

	}
	
	private String constructGetImageUrl(Long submissionId, String questionId, String imageId){
		String imgLink = "/getSubmissionImage?submissionId="+submissionId.toString() +"&questionId="+questionId+"&imageId="+imageId ;
		return imgLink;
		/*try {
			return URLEncoder.encode(imgLink, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported encoding exception", e);
			return "";
		}*/
	}
	
	private String getFieldPrefix(String fieldName){
		return fieldName.substring(0, fieldName.indexOf('_') ) ;
	}

}
