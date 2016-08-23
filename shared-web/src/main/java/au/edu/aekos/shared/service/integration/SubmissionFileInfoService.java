package au.edu.aekos.shared.service.integration;

import java.util.List;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.web.controllers.integration.PublishedTextFileController;
import au.org.aekos.shared.api.json.SpeciesFileNameEntry;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfoResponse;

/**
 * Creates the JSON serializable Submission File Information, for use by the
 * aekos portal file extract.
 * 
 * @author btill
 */
public interface SubmissionFileInfoService {

	/**
	 * Does what it says on the tin. If submission not found, returns
	 * errorMessage contained in response object.
	 * 
	 * @param submissionId
	 * @return
	 */
	SubmissionFileInfoResponse retrieveSubmissionFileInfo(Long submissionId);

	/**
	 * Retrieves minimal information about any species files for the submission
	 * with the supplied ID. This method will retrieve the submission by the ID,
	 * if you already have the submission, use
	 * {@link SubmissionFileInfoService#retrieveSpeciesFileNames(Submission)}
	 * This is intended to be paired with
	 * {@link PublishedTextFileController#viewPublishedTextFile(Long, Long, org.springframework.ui.Model)}
	 * , which can get more information based on what we return here.
	 * 
	 * @param submissionId
	 *            id of the submission to search for species files
	 * @return a list of species file names if any are found, <code>null</code>
	 *         otherwise
	 */
	List<SpeciesFileNameEntry> retrieveSpeciesFileNames(Long submissionId);

	/**
	 * Retrieves minimal information about any species files for the given
	 * submission. This is intended to be paired with
	 * {@link PublishedTextFileController#viewPublishedTextFile(Long, Long, org.springframework.ui.Model)}
	 * , which can get more information based on what we return here.
	 * 
	 * @param submissionId
	 *            id of the submission to search for species files
	 * @return a list of species file names if any are found, <code>null</code>
	 *         otherwise
	 */
	List<SpeciesFileNameEntry> retrieveSpeciesFileNames(Submission submission);

}
