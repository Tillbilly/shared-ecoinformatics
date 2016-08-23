package au.edu.aekos.shared.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.edu.aekos.shared.data.entity.SubmissionFileReviewOutcome;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.SubmissionFileModel;
import au.edu.aekos.shared.questionnaire.SubmissionFiles;
import au.edu.aekos.shared.service.submission.SubmissionModelService;
import au.edu.aekos.shared.web.model.SubmissionDataFileModel;
import au.edu.aekos.shared.web.model.SubmissionModel;
import au.edu.aekos.shared.web.model.SubmittedFileReviewModel;

@Component
public class SubmissionFilesValidator implements Validator {

	@Autowired
	private SubmissionModelService submissionManager;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return SubmissionFiles.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// Do nothing, we don't use this as part of the framework
	}

	public void validateFileDetails( SubmissionFiles submissionFiles, Errors errors, DisplayQuestionnaire questionnaire ) throws Exception{
		boolean hasRejectedFiles = false;
		SubmissionModel submissionModel = null;
		if( questionnaire.getSubmissionId() != null && questionnaire.getLastReview() != null ){
			hasRejectedFiles = questionnaire.getLastReview().getHasRejectedFiles();
			if(hasRejectedFiles){
			    submissionModel = submissionManager.getSubmission(questionnaire.getSubmissionId()) ;
			}
		}
		if(!thereAreFilesToValidate(submissionFiles)){
			return;
		}
		//Validate Description is not null
		for(int x = 0; x < submissionFiles.getSubmissionFileList().size(); x++ ){
			SubmissionFileModel file = submissionFiles.getSubmissionFileList().get(x);
			if(! file.getDeleted() && !StringUtils.hasLength( file.getDescription() ) ){
				errors.rejectValue("submissionFileList[" + x + "].description", "questionnaire.validation.file.descriptionRequired");
			}else if(! file.getDeleted() && hasRejectedFiles && file.getId() != null ){
				SubmittedFileReviewModel reviewModel = questionnaire.getLastReview().getFileReviews().get( file.getId() ) ;
				if( reviewModel != null && SubmissionFileReviewOutcome.REJECT.equals( reviewModel.getReviewOutcome() ) ){
					//TODO provide different rejection items -
					//Either the file is unacceptable and should have been deleted OR the description was unacceptable
					//Check whether the 
					for(SubmissionDataFileModel dfm : submissionModel.getFileList() ){
						if(dfm.getId().equals(file.getId() ) && dfm.getFileDescription().equals(file.getDescription()) ){
							errors.rejectValue("submissionFileList[" + x + "].description", "questionnaire.validation.file.descriptionNotModified");
							break;
						}
					}
				}
			}
		}
	}

	private boolean thereAreFilesToValidate(SubmissionFiles submissionFiles) {
		return submissionFiles.getSubmissionFileList() != null && submissionFiles.getSubmissionFileList().size() > 0;
	}
}
