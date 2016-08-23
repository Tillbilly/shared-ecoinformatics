package au.edu.aekos.shared.service.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.service.submission.SubmissionDataService;
import au.edu.aekos.shared.spatial.BBOX;
import au.edu.aekos.shared.spatial.CRSBounds;
import au.edu.aekos.shared.spatial.GeometryReprojectionService;
import au.edu.aekos.shared.web.json.JsonSite;
import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;
import au.edu.aekos.shared.web.map.CoordSysDisplayPair;
import au.edu.aekos.shared.web.map.CoordinateSystemConfig;

@Service
public class SiteFileServiceImpl implements SiteFileService {
	
	private static final int NUMBER_OF_EXPECTED_COLUMNS = 5;

	private Logger logger = LoggerFactory.getLogger(SiteFileServiceImpl.class);
	
	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Autowired
	private SubmissionDataService submissionDataService;
	
	@Autowired
	private CoordinateSystemConfig coordinateSystemConfig;
	
	@Autowired
	private GeometryReprojectionService reprojectionService;
	
    private static final String CACHE_NAME= "site_file_json_cache";
	
	private Cache siteFileJsonCache = null;
	
	private Cache getCache(){
		if(siteFileJsonCache == null){
		    CacheManager cacheManager = CacheManager.getInstance();
		    siteFileJsonCache = cacheManager.getCache(CACHE_NAME);
		}
		return siteFileJsonCache;
	}
	
	//I think, so long as the coord system is'nt equal "Other"
	public boolean isCoordSysSupported(String coordSys){
		return ! "OTHER".equalsIgnoreCase(coordSys);
	}
	
	//site_id, coord-x, coord-y, zone(optional), comments
	public JsonUploadSiteFileResponse parseSiteFileToJson(File siteFile, String coordSysString, boolean validateBounds) throws IOException{
		JsonUploadSiteFileResponse siteFileJsonResponse = new JsonUploadSiteFileResponse();
		siteFileJsonResponse.setCoordSys(coordSysString);
		BufferedReader br = new BufferedReader(new FileReader(siteFile));
		String line;
		int lineNumber = 1;
		
		while ((line = br.readLine()) != null) {
			if(! StringUtils.hasLength(line)){
				break;
			}
			try {
				JsonSite site = parseSiteFileLine(line);
				//Check bounds for site
				if( validateBounds && !isSiteWithinCoordsysBounds(site, coordSysString)){
					writeBoundsError(site,coordSysString, siteFileJsonResponse);
				}else{
				    siteFileJsonResponse.getSites().add(site);
				}
			} catch (InvalidSiteFileFormatException e) {
				siteFileJsonResponse.setError("Error parsing study location file at line " + lineNumber + ": Invalid format, " 
					+ NUMBER_OF_EXPECTED_COLUMNS + " columns required.");
				br.close();
				return siteFileJsonResponse;
			}
			lineNumber++;
		}
		br.close();
		String message = "" + siteFileJsonResponse.getSites().size() + " sites uploaded.";
		siteFileJsonResponse.setMessage(message);
		return siteFileJsonResponse;
	}
	
	private void writeBoundsError(JsonSite site, String coordSysString, JsonUploadSiteFileResponse siteFileJsonResponse ){
		String errorMessage = "Site " + site.getSiteId() + " is out of the allowed CRS bounds for the specified coordinate system<br/>";
		BBOX bounds = CRSBounds.getBoundsBBOX(coordSysString);
		errorMessage += "x must be between " + bounds.getXmin().toString() + " and " + bounds.getXmax().toString()  + "<br/>";
		errorMessage += "y must be between " + bounds.getYmin().toString() + " and " + bounds.getYmax().toString()  ;
		siteFileJsonResponse.setError( StringUtils.hasLength(siteFileJsonResponse.getError()) ? siteFileJsonResponse.getError() +"<br/>" + errorMessage : errorMessage);
	}
	
	/**
	 * Parses an individual line from a site file.
	 * 
	 * @param line			The line to parse
	 * @return				The parsed line
	 */
	JsonSite parseSiteFileLine(String line) {
		JsonSite result = new JsonSite();
		String [] pieces = line.split(",");
		if (pieces.length != NUMBER_OF_EXPECTED_COLUMNS) {
			throw new InvalidSiteFileFormatException();
		}
		result.setSiteId(pieces[0].replaceAll("\"", ""));
		result.setxCoord(pieces[1].replaceAll("\"", ""));
		result.setyCoord(pieces[2].replaceAll("\"", ""));
		result.setZone(pieces[3].replaceAll("\"", ""));
		
		result.setComments(pieces[4].replaceAll("\"", ""));
		return result;
	}
	
	@Transactional
	public JsonUploadSiteFileResponse retrieveJsonSiteFileResponse(String filename, Long sitefileDataId){
		JsonUploadSiteFileResponse jsonSiteFile = getSiteFileJsonFromCache(filename, sitefileDataId);
		if(jsonSiteFile != null){
			return jsonSiteFile;
		}
		SubmissionData submissionData = submissionDataDao.findByIdEagerLocations(sitefileDataId);
		if( ! submissionData.getFileName().equals(filename) ){
			return null;
		}
		File siteFile = retrieveSiteFile(submissionData);
		if(siteFile != null){
			try {
				JsonUploadSiteFileResponse siteFileJson = parseSiteFileToJson(siteFile, submissionData.getSiteFileCoordinateSystem(), true);
				siteFileJson.setCoordSys( submissionData.getSiteFileCoordinateSystem() );
				siteFileJson.setCoordSysOther( submissionData.getSiteFileCoordSysOther() );
				addToCache(siteFileJson, filename, sitefileDataId);
				return siteFileJson;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private File retrieveSiteFile(SubmissionData submissionData){
	   return submissionDataService.retrieveFileToLocal(submissionData);
	}
	
	private JsonUploadSiteFileResponse getSiteFileJsonFromCache(String filename, Long sitefileDataId){
		Element el = getCache().get(new SiteFileJsonCacheKey(filename, sitefileDataId ));
		if(el != null){
		    return (JsonUploadSiteFileResponse) el.getObjectValue();
		}
		return null;
	}
	
	private void addToCache(JsonUploadSiteFileResponse siteFileJson, String filename, Long sitefileDataId ){
		Element el = new Element(new SiteFileJsonCacheKey(filename, sitefileDataId) , siteFileJson);
		getCache().put(el);
	}

	@Override
	public String getDescriptionForEpsgCode(String epsgCode) {
		for(CoordSysDisplayPair coordSysPair : coordinateSystemConfig.getCoordSysList() ){
            if(epsgCode.equals(coordSysPair.getEpsgCode())){
            	return coordSysPair.getDisplayStr();
            }
		}
		return epsgCode;
	}

	@Override
	public List<String> retrieveStudyLocationWktPointList(
			SubmissionData submissionData) {
		List<String> wktPointStringList = new ArrayList<String>();
		File siteFile = retrieveSiteFile(submissionData);
		if(siteFile == null || !siteFile.canRead()){
			return wktPointStringList;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(siteFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Error reading site file " + submissionData.getFileName() + " subDataId:" + submissionData.getId());
			logger.error(e.getMessage(),e);
			return wktPointStringList;
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if(! StringUtils.hasLength(line)){
					break;
				}
				String [] pieces = line.split(",");
				if(pieces.length != 5){
					continue;
				}
				String xCoord = pieces[1].replaceAll("\"", "");
				String yCoord= pieces[2].replaceAll("\"", "");
				String wktPoint = "POINT(" + xCoord + " " + yCoord + ")";
				wktPointStringList.add(wktPoint);
			}
		    br.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error reading site file " + submissionData.getFileName() + " subDataId:" + submissionData.getId());
			logger.error(e.getMessage(),e);
		}
		return wktPointStringList;
	}

	//if the coord sys string is'nt supported we can't test the bounds - return true
	private boolean isSiteWithinCoordsysBounds(JsonSite site, String coordSys){
		if(CRSBounds.isCoordSystemSupport(coordSys)){
			return CRSBounds.isCoordinateInBounds(Double.parseDouble(site.getxCoord() ) , Double.parseDouble(site.getyCoord()), coordSys);
		}
		return true; 
		
	}
	
	@Override
	@Transactional
	public BBOX calculateBBOX(String filename, Long siteFileDataId) {
		JsonUploadSiteFileResponse siteFileJson = retrieveJsonSiteFileResponse(filename, siteFileDataId);
		return calculateBBOX(siteFileJson);
	}
	
	@Override
	@Transactional
	public BBOX calculateBBOX(JsonUploadSiteFileResponse siteFileJson) {
		if(siteFileJson != null && siteFileJson.getSites() != null && siteFileJson.getSites().size() > 0){
			Double xmin = Double.MAX_VALUE;
			Double xmax = Double.NEGATIVE_INFINITY;
			Double ymin = Double.MAX_VALUE;
			Double ymax = Double.NEGATIVE_INFINITY;
			BBOX bbox = new BBOX();
			for(JsonSite site : siteFileJson.getSites() ){
				Double siteX = Double.parseDouble( site.getxCoord());
				Double siteY = Double.parseDouble( site.getyCoord());
				if( xmin.compareTo(siteX ) > 0){
		        	xmin = siteX;
		        }
		        if(xmax.compareTo(siteX) < 0){
		        	xmax = siteX;
		        }
		        if(ymin.compareTo(siteY) > 0){
		        	ymin = siteY;
		        }
		        if(ymax.compareTo(siteY) < 0){
		        	ymax = siteY;
		        }
			}
			bbox.setCoordinateSystem( coordinateSystemConfig.getProjectionStringForEPSGCode( siteFileJson.getCoordSys() ) );
			bbox.setValues(xmin, ymin, xmax, ymax);
			return bbox;
		}
		return null;
	}
	
	@Override
	public List<String> retrieveStudyLocationWktPointListGDA94(
			SubmissionData submissionData) {
		List<String> studyLocationPoints = retrieveStudyLocationWktPointList(submissionData);
		if(studyLocationPoints == null || studyLocationPoints.size() == 0){
			return null;
		}
		if("EPSG:4283".equals(submissionData.getSiteFileCoordinateSystem()) ){
			return studyLocationPoints;
		}else if("OTHER".equalsIgnoreCase(submissionData.getSiteFileCoordinateSystem())){
			return null;
		}else{
			List<String> reprojectedStudyLocationPoints = reprojectionService.reprojectWktPointsToEPSG4283(studyLocationPoints, submissionData.getSiteFileCoordinateSystem(), 6);
			return reprojectedStudyLocationPoints;
		}
	}
}
