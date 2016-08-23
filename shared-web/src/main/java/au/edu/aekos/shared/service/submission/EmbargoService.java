package au.edu.aekos.shared.service.submission;

import java.util.Date;

import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;

public interface EmbargoService {
	
	Date getCurrentEmbargoDate(Long submissionId);
	
	boolean isSubmissionUnderEmbargo(Long submissionId);
	
	boolean isSubmissionUnderEmbargo(MetaInfoExtractor metaInfoExtractor);
	
	void updateEmbargoDate(Long submissionId, Date embargoDate) throws MetaInfoExtractorException, Exception;
	
}
