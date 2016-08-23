package au.edu.aekos.shared.service.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.service.submission.SubmissionService;

/**
 * Static test helper methods. Designed to be used with a static import:
 * <br />
 * <code>import static au.edu.aekos.shared.service.integration.SharedWebTestSupport.*;</code>
 */
public class SharedWebTestSupport {

	private SharedWebTestSupport() {}
	
	static SubmissionFileInfoServiceImpl getObjectUnderTestWithData(SubmissionData...data) {
		SubmissionFileInfoServiceImpl result = new SubmissionFileInfoServiceImpl();
		SubmissionService mockSubmissionService = mock(SubmissionService.class);
		result.setSubmissionService(mockSubmissionService);
		Submission returnValue = submissionWith(data);
		when(mockSubmissionService.retrieveSubmissionById(123L)).thenReturn(returnValue);
		return result;
	}

	static Submission submissionWith(SubmissionData... data) {
		Submission returnValue = new Submission();
		returnValue.setSubmissionDataList(Arrays.asList(data));
		return returnValue;
	}
	
	static SubmissionData submissionData(long id, String fileName, SubmissionDataType submissionDataType) {
		SubmissionData result = new SubmissionData();
		result.setId(id);
		result.setFileName(fileName);
		result.setSubmissionDataType(submissionDataType);
		return result;
	}
}
