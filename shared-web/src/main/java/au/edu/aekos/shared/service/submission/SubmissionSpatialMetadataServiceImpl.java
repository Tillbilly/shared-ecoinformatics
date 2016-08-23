package au.edu.aekos.shared.service.submission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.service.file.SiteFileService;
import au.edu.aekos.shared.spatial.BBOX;
import au.edu.aekos.shared.spatial.CRSBounds;
import au.edu.aekos.shared.spatial.GeometryReprojectionService;
import au.edu.aekos.shared.spatial.GeometryReprojectionServiceImpl;
import au.edu.aekos.shared.web.json.JsonFeature;
import au.edu.aekos.shared.web.json.JsonGeoFeatureSet;
import au.edu.aekos.shared.web.json.JsonSpatialUtil;

import com.google.gson.Gson;

@Service
public class SubmissionSpatialMetadataServiceImpl implements SubmissionSpatialMetadataService {

	private Logger logger = LoggerFactory.getLogger(SubmissionSpatialMetadataServiceImpl.class);
	
	@Autowired
	private SiteFileService siteFileService;
	
	@Autowired
	private GeometryReprojectionService reprojectionService;
	
	@Transactional
	public String createDcmiBBOXStringForSubmission(
			MetaInfoExtractor metaInfo, String siteFileMetatag,
			String pickedGeometryMetatag) {
		BBOX bbox = null;
		try{
			SubmissionAnswer siteFileAnswer = metaInfo.getSubmissionAnswerForMetatag(siteFileMetatag);
			if(siteFileAnswer!=null && siteFileAnswer.hasResponse()){
				for(SubmissionData sd : metaInfo.getSubmission().getSubmissionDataList() ){
					if(SubmissionDataType.SITE_FILE.equals( sd.getSubmissionDataType()) ){
						bbox = siteFileService.calculateBBOX(sd.getFileName(), sd.getId());
						if(bbox != null){
							break;
						}
					}
				}
			}else{  //Try and calculate the BBOX for a user picked geometry
				SubmissionAnswer geoFeatureSetAnswer = metaInfo.getSubmissionAnswerForMetatag(pickedGeometryMetatag);
				if(geoFeatureSetAnswer!=null && geoFeatureSetAnswer.hasResponse()){
					Gson gson = new Gson();
					JsonGeoFeatureSet geoFeatureSet = gson.fromJson(geoFeatureSetAnswer.getResponse(), JsonGeoFeatureSet.class);
					bbox = JsonSpatialUtil.calculateBBOXFromJsonGeoFeatureSet(geoFeatureSet);
				}
			}
		}catch(MetaInfoExtractorException ex){
			logger.error("Error extracting DCMI BBOX", ex);
		}
		if(bbox == null){
		    return null;
		}
		return bbox.getIso19139dcmiBox();
	}

    @Transactional
	public String createDcmiBBOXStringForSubmission(
			MetaInfoExtractor extractor, String siteFileMetatag,
			String pickedGeometryMetatag, String bboxMinXMetatag,
			String bboxMinYMetatag, String bboxMaxXMetatag,
			String bboxMaxYMetatag, String bboxSRSMetatag) {
		String bboxStr = createDcmiBBOXStringForSubmission(extractor, siteFileMetatag, pickedGeometryMetatag);
		if(StringUtils.hasLength(bboxStr) ){
			return bboxStr;
		}
		try{
			BBOX bbox = createBBOXFromMetatags(extractor, bboxMinXMetatag,
					bboxMinYMetatag, bboxMaxXMetatag,
					bboxMaxYMetatag);
			if(bbox == null){
				return null;
			}
			String coordSys = extractor.getSingleResponseForMetatagIgnoreDisplayValue(bboxSRSMetatag);
			if(! StringUtils.hasLength(coordSys)){
				coordSys = GeometryReprojectionServiceImpl.EPSG4283;
			}
			if(! CRSBounds.isCoordSystemSupport(coordSys) ){
				logger.error("Coordinate System " + coordSys + " not supported.");
				return null;
			}
			if(! GeometryReprojectionServiceImpl.EPSG4283.equals(coordSys)){
				bbox = reprojectionService.reprojectBBOXToEPSG4283(bbox, coordSys, 2);
			}
			if(bbox == null){
				return null;
			}
			return bbox.getIso19139dcmiBox();
		}catch(MetaInfoExtractorException ex){
			logger.error("Error extracting bbox metatags",ex);
		}catch(NumberFormatException nfex){
			logger.error("Error parsing bbox coordinates ", nfex);
		}
		return null;
	}

    private BBOX createBBOXFromMetatags(MetaInfoExtractor extractor, String bboxMinXMetatag,
			String bboxMinYMetatag, String bboxMaxXMetatag,
			String bboxMaxYMetatag) throws MetaInfoExtractorException{
    	
    	String minx = extractor.getSingleResponseForMetatag(bboxMinXMetatag);
		String miny = extractor.getSingleResponseForMetatag(bboxMinYMetatag);
		String maxx = extractor.getSingleResponseForMetatag(bboxMaxXMetatag);
		String maxy = extractor.getSingleResponseForMetatag(bboxMaxYMetatag);
		if(! StringUtils.hasLength(minx) || ! StringUtils.hasLength(miny) || ! StringUtils.hasLength(maxx) || ! StringUtils.hasLength(maxy)){
			return null;
		}
		
		BBOX bbox = new BBOX(Double.parseDouble(minx),
				             Double.parseDouble(miny),
				             Double.parseDouble(maxx),
				             Double.parseDouble(maxy) );
    	return bbox;
    	
    }
    
	@Override
	public List<String> getWktStringsForSubmission(MetaInfoExtractor extractor,
			String siteFileMetatag, String pickedGeometryMetatag,
			String bboxMinXMetatag, String bboxMinYMetatag,
			String bboxMaxXMetatag, String bboxMaxYMetatag,
			String bboxSRSMetatag) {
		try{
			SubmissionAnswer siteFileAnswer = extractor.getSubmissionAnswerForMetatag(siteFileMetatag);
			if(siteFileAnswer!=null && siteFileAnswer.hasResponse()){
				for(SubmissionData sd : extractor.getSubmission().getSubmissionDataList() ){
					if(SubmissionDataType.SITE_FILE.equals( sd.getSubmissionDataType()) ){
						return siteFileService.retrieveStudyLocationWktPointListGDA94(sd);
					}
				}
			}else{  //Try and calculate the BBOX for a user picked geometry
				SubmissionAnswer geoFeatureSetAnswer = extractor.getSubmissionAnswerForMetatag(pickedGeometryMetatag);
				if(geoFeatureSetAnswer!=null && geoFeatureSetAnswer.hasResponse()){
					Gson gson = new Gson();
					JsonGeoFeatureSet geoFeatureSet = gson.fromJson(geoFeatureSetAnswer.getResponse(), JsonGeoFeatureSet.class);
					List<String> geometryList = new ArrayList<String>();
					for(JsonFeature feature : geoFeatureSet.getFeatures() ){
						geometryList.add(feature.getGeometry());
					}
					return geometryList;
				}
			}
			//If we are still here, try get the geometry from defined BBOX
			BBOX bbox = createBBOXFromMetatags(extractor, bboxMinXMetatag, bboxMinYMetatag, bboxMaxXMetatag, bboxMaxYMetatag );
			if(bbox == null){
				return Collections.emptyList();
			}
			String coordSys = extractor.getSingleResponseForMetatag(bboxSRSMetatag);
			if(! StringUtils.hasLength(coordSys)){
				coordSys = GeometryReprojectionServiceImpl.EPSG4283;
			}
			if(! CRSBounds.isCoordSystemSupport(coordSys) ){
				logger.error("Coordinate System " + coordSys + " not supported.");
				return Collections.emptyList();
			}
			if(! GeometryReprojectionServiceImpl.EPSG4283.equals(coordSys)){
				bbox = reprojectionService.reprojectBBOXToEPSG4283(bbox, coordSys, 2);
			}
			if(bbox == null){
				return Collections.emptyList();
			}
			List<String> geometryList = new ArrayList<String>();
			geometryList.add(bbox.getWktPolygon() );
			return geometryList;
			
		}catch(Exception ox){
			logger.debug("Couldn't find spatial data", ox);
		}
	    return Collections.emptyList();
	}
	

}
