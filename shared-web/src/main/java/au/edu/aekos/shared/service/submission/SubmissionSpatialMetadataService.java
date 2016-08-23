package au.edu.aekos.shared.service.submission;

import java.util.List;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;

public interface SubmissionSpatialMetadataService {

	
	String createDcmiBBOXStringForSubmission(MetaInfoExtractor extractor, String siteFileMetatag, String pickedGeometryMetatag);
	
	String createDcmiBBOXStringForSubmission(MetaInfoExtractor extractor, 
			                                 String siteFileMetatag, 
			                                 String pickedGeometryMetatag,
			                                 String bboxMinXMetatag,
			                                 String bboxMinYMetatag,
			                                 String bboxMaxXMetatag,
			                                 String bboxMaxYMetatag,
			                                 String bboxSRSMetatag );
	
	List<String> getWktStringsForSubmission(MetaInfoExtractor extractor,
			                                 String siteFileMetatag, 
			                                 String pickedGeometryMetatag,
			                                 String bboxMinXMetatag,
			                                 String bboxMinYMetatag,
			                                 String bboxMaxXMetatag,
			                                 String bboxMaxYMetatag,
			                                 String bboxSRSMetatag);
}
