package au.edu.aekos.shared.service.index;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.spatial.BBOX;
import au.edu.aekos.shared.spatial.GeometryReprojectionService;

public class StudyAreaBoundingBoxWriter implements ManualSubmissionValueWriter, CompositeMetatagWriter {
	
	@Autowired
	private GeometryReprojectionService reprojectionService;
	
	Logger logger = LoggerFactory.getLogger(StudyAreaBoundingBoxWriter.class);
	
	private String minXTag;
	private String minYTag;
	private String maxXTag;
	private String maxYTag;
	private String crsTag;

	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor)  {
		//All values need to be non null, be numbers, and we need to support the coordinate system 
		try{
			Double minX = getDouble(minXTag, metaInfoExtractor);
		    Double minY = getDouble(minYTag, metaInfoExtractor);
		    Double maxX = getDouble(maxXTag, metaInfoExtractor);
		    Double maxY = getDouble(maxYTag, metaInfoExtractor);
		    String crs  = metaInfoExtractor.getSingleResponseForMetatag(crsTag);
		    //All of the above values need to be non-null
		    if(minX == null || minY == null || maxX == null || maxY == null || !StringUtils.hasLength(crs)){
		    	logger.warn("BBOX definition for SubmissionID" + metaInfoExtractor.getSubmission().getId() + " has null values.");
		    	StringBuilder sb = new StringBuilder(minX == null ? "null" : minX.toString( )).append(":");
		    	sb.append(minY == null ? "null" : minY.toString( )).append(":").append(maxX == null ? "null" : maxX.toString( )).append(":");
		    	sb.append(maxY == null ? "null" : minY.toString( )).append(":").append(!StringUtils.hasLength(crs) ? "null" : crs);
		    	logger.warn(sb.toString());
		    	return;
		    }
		    //Now create a BBOX Geometry wkt Polygon
		    BBOX bbox = new BBOX(minX,minY,maxX,maxY);
		    String wktPoly = bbox.getWktPolygon();
		    if(wktPoly == null){
		    	return;
		    }
		    String rprPoly = reprojectionService.reprojectWktPolygonToEPSG4283(wktPoly, crs, 6);
		    if(rprPoly == null){
		    	return;
		    }
		    doc.addField(indexField.getIndexFieldName(), rprPoly );
		}catch(MetaInfoExtractorException miex){
			logger.warn("Error trying to write BBOX field for SubmissionID" + metaInfoExtractor.getSubmission().getId(), miex);
		}
	}
	
	//Gets a double value or returns null
	private Double getDouble(String tag, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		String valStr = metaInfoExtractor.getSingleResponseForMetatag(tag);
		if(!StringUtils.hasLength(valStr)){
			return null;
		}
		Double doubleValue = null;
		try{
		    doubleValue = Double.valueOf(valStr);
		}catch(NumberFormatException ex){
			logger.error("Error converting BBOX field to double '" + valStr + "' . tag: " + tag + " Submission ID " + metaInfoExtractor.getSubmission().getId(), ex);
		}
		return doubleValue;
	}
	
	public String getMinXTag() {
		return minXTag;
	}

	public void setMinXTag(String minXTag) {
		this.minXTag = minXTag;
	}

	public String getMinYTag() {
		return minYTag;
	}

	public void setMinYTag(String minYTag) {
		this.minYTag = minYTag;
	}

	public String getMaxXTag() {
		return maxXTag;
	}

	public void setMaxXTag(String maxXTag) {
		this.maxXTag = maxXTag;
	}

	public String getMaxYTag() {
		return maxYTag;
	}

	public void setMaxYTag(String maxYTag) {
		this.maxYTag = maxYTag;
	}

	public String getCrsTag() {
		return crsTag;
	}

	public void setCrsTag(String crsTag) {
		this.crsTag = crsTag;
	}

	@Override
	public List<String> getRequiredMetatags() {
		return Arrays.asList(minXTag,minYTag,maxXTag,maxYTag,crsTag);
	}
}
