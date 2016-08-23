package au.edu.aekos.shared.valid;

import java.io.File;

import au.edu.aekos.shared.data.entity.SubmissionDataType;

public interface QuestionFileUploadValidator {

	/**
	 * 
	 * @param file
	 * @param dataType
	 * @throws FileUploadValidationException
	 */
	void validateUploadedFile(File file, SubmissionDataType dataType) throws FileUploadValidationException;
	
}
