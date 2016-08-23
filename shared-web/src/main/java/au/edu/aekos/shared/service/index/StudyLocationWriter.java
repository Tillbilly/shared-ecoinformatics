package au.edu.aekos.shared.service.index;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vividsolutions.jts.io.ParseException;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.service.file.SiteFileService;
import au.edu.aekos.shared.spatial.ConvexHullUtil;


/**
 * If a site file has been uploaded, and is in an appropriate coordinate system ( 4283, 4326 )
 * then add the points to the document
 * 
 * Need to reproject the site file coords to 4283 and store them internally . . . .
 * actually,  reproject the coords at index time, let the site file service sort that out calling the 
 * new geometry reprojection service
 * 
 * @author btill
 */
public class StudyLocationWriter implements ManualSubmissionValueWriter {

	private Logger logger = LoggerFactory.getLogger(StudyLocationWriter.class);
	
	//If a value is set for the convex hull field, calculate the convexHull as wkt, and set it to this field.
	private String convexHullFieldName = null;
	
	@Autowired
	private SiteFileService siteFileService;
	
	@Override
	public void writeValueToDocument(SolrInputDocument doc,
			SubmissionIndexField indexField, MetaInfoExtractor metaInfoExtractor) {
		for(SubmissionData sd : metaInfoExtractor.getSubmission().getSubmissionDataList() ){
			if( SubmissionDataType.SITE_FILE.equals( sd.getSubmissionDataType() ) && siteFileService.isCoordSysSupported(sd.getSiteFileCoordinateSystem()) ){
				List<String> wktPointList = siteFileService.retrieveStudyLocationWktPointListGDA94(sd);
				for(String wktPoint : wktPointList){
					doc.addField(indexField.getIndexFieldName(), wktPoint);
				}
				addConvexHull(wktPointList, doc, metaInfoExtractor.getSubmissionId());
				return;
			}
		}
		logger.info("No site file found for submission ID:" + metaInfoExtractor.getSubmission().getId());
	}

	private void addConvexHull(List<String> wktPointList, SolrInputDocument doc, Long submissionId){
		if(StringUtils.hasLength(convexHullFieldName)){
			try{
				String convexHull = ConvexHullUtil.calculateConvexHullFromWktPoints(wktPointList);
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
