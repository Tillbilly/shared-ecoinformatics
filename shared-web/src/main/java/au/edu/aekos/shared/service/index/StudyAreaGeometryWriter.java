package au.edu.aekos.shared.service.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.spatial.ConvexHullUtil;
import au.edu.aekos.shared.web.json.JsonFeature;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;

import com.google.gson.Gson;
import com.vividsolutions.jts.io.ParseException;

public class StudyAreaGeometryWriter implements ManualSubmissionValueWriter {

	private Logger logger = LoggerFactory.getLogger(StudyAreaGeometryWriter.class);

	//If a value is set for the convex hull field, calculate the convexHull as wkt, and set it to this field.
    private String convexHullFieldName = null;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		
    	SubmissionAnswer submissionAnswer = null;
		try {
			submissionAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(indexField.getSharedTag());
		} catch (MetaInfoExtractorException e) {
			logger.warn("Error retrieving Answer for metatag " + indexField.getSharedTag(), e);
		}
    	if(submissionAnswer != null && 
    			submissionAnswer.hasResponse() && 
			ResponseType.GEO_FEATURE_SET.equals( submissionAnswer.getResponseType()) ){
    		String jsonStr = submissionAnswer.getResponse();
    		Gson gson = new Gson();
    		JsonGeoFeatureSet geoFeatureSet = gson.fromJson(jsonStr, JsonGeoFeatureSet.class);
    		if(geoFeatureSet != null && geoFeatureSet.getFeatures() != null && geoFeatureSet.getFeatures().size() > 0 ){
    			List<String> geometryWktStrings = new ArrayList<String>();
    			for(JsonFeature jf: geoFeatureSet.getFeatures() ){
    		    	String geometry = jf.getGeometry();
    		    	if(StringUtils.hasLength(geometry)){
    		    		doc.addField(indexField.getIndexFieldName(), geometry );
    		    		geometryWktStrings.add(geometry);
    		    	}
    		    }
    		    addConvexHull(geometryWktStrings, doc, metaInfoExtractor.getSubmissionId());
    		}
		}
    }

	private void addConvexHull(List<String> wktStrings, SolrInputDocument doc, Long submissionId){
		if(StringUtils.hasLength(convexHullFieldName)){
			try{
				String convexHull = ConvexHullUtil.calculateConvexHullFromWktGeometryList(wktStrings, true);
				if(StringUtils.hasLength(convexHull)){
					doc.addField(convexHullFieldName, convexHull);
				}
			}catch(ParseException pex){
				logger.error("Error tying to parse site file points for submission ID:" + submissionId, pex);
			}
		}
	}
	
	
	
	public String getConvexHullFieldName() {
		return convexHullFieldName;
	}

	public void setConvexHullFieldName(String convexHullFieldName) {
		this.convexHullFieldName = convexHullFieldName;
	}
}
