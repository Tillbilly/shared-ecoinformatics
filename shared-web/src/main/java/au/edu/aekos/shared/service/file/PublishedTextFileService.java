package au.edu.aekos.shared.service.file;

import au.edu.aekos.shared.web.model.PublishedTextFileViewModel;


public interface PublishedTextFileService {

	PublishedTextFileViewModel getViewModel(Long submissionId, Long submissionDataId);
	
}
