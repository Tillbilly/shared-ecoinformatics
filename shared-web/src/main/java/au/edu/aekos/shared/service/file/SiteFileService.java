package au.edu.aekos.shared.service.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.spatial.BBOX;
import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;

public interface SiteFileService {

	boolean isCoordSysSupported(String coordSys);
	
	String getDescriptionForEpsgCode(String epsgCode);
	
	JsonUploadSiteFileResponse parseSiteFileToJson(File siteFile, String coordSysString, boolean validateBounds) throws IOException;
	
	JsonUploadSiteFileResponse retrieveJsonSiteFileResponse(String filename, Long sitefileDataId);
	
	List<String> retrieveStudyLocationWktPointList(SubmissionData submissionData);
	
	List<String> retrieveStudyLocationWktPointListGDA94(SubmissionData submissionData);
	
	BBOX calculateBBOX(String filename, Long siteFileDataId);

	BBOX calculateBBOX(JsonUploadSiteFileResponse siteFileJson);
	
}