package au.edu.aekos.shared.service.integration;

import java.util.List;

import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;
import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;

/**
 * SubmissionSummaryRow objects are used both internally and externally to shared-web,
 *  so live in the shared API project.
 * @author btill
 */
public interface SubmissionInfoModelSummaryService {
	/**
	 * Builds the submissionSummaryResponse object all set for Json serialisation - used in the aekos portal
	 * @param datasetId			ID of the dataset/submission to retrieve
	 * @param checkPublished	<code>true</code> if we should assert the dataset has already been published, <code>false</code> otherwise
	 * @return					a simple dataset summary object that can be serialised to JSON
	 */
	ResponseGetDatasetSummary buildSubmissionSummaryForPortal(Long datasetId, boolean checkPublished);
	
	/**
	 * Common info model data format for both jasper report rendering and aekos portal.
	 * 
	 * @param submissionId
	 * @param checkPublished
	 * @return
	 */
	List<SubmissionSummaryRow> retrieveSubmissionSummaryRows(Long submissionId, boolean checkPublished);
}
