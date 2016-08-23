package au.edu.aekos.shared.service.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.web.model.PublishedTextFileViewModel;

@Service
public class PublishedTextFileServiceImpl implements PublishedTextFileService {

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private FileLineReadingService fileLineReadingService;
	
	@Override @Transactional
	public PublishedTextFileViewModel getViewModel(Long submissionId,
			Long submissionDataId) {
		PublishedTextFileViewModel fileViewModel = new PublishedTextFileViewModel();
		fileViewModel.setSubmissionId(submissionId.toString());
		fileViewModel.setSubmissionDataId(submissionDataId.toString());
		
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub == null){
			fileViewModel.setErrorMessage("No submission exists with ID " + submissionId);
			return fileViewModel;
		}
		if(! SubmissionStatus.PUBLISHED.equals(sub.getStatus()) ){
			fileViewModel.setErrorMessage("This submission has not been published hence its data is unnavailable.");
			return fileViewModel;
		}
		
		fileViewModel.setSubmissionTitle(sub.getTitle());
		SubmissionData submissionDataEntity = null;
		for(SubmissionData sd : sub.getSubmissionDataList()){
			if(sd.getId().equals(submissionDataId)){
				submissionDataEntity = sd;
				break;
			}
		}
		if(submissionDataEntity == null){
			fileViewModel.setErrorMessage("Submission ID " + submissionId + " does not contain any data with ID " + submissionDataId);
			return fileViewModel;
		}
		if( ! SubmissionDataType.SITE_FILE.equals(submissionDataEntity.getSubmissionDataType() ) &&
				! SubmissionDataType.SPECIES_LIST.equals(submissionDataEntity.getSubmissionDataType() ) ){
			fileViewModel.setErrorMessage("Requested file is not of an appropriate type : 'Site File or Species List'");
			return fileViewModel;
		}
		
		fileViewModel.setFilename(submissionDataEntity.getFileName());
		List<String> fileLines = fileLineReadingService.readFileLinesAsList(submissionDataEntity);
		if(fileLines != null && fileLines.size() > 0){
			fileViewModel.setLines(fileLines);
		}
		return fileViewModel;
	}

}
