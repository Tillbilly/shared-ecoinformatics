package au.edu.aekos.shared.service.integration;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.service.submission.EmbargoService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.org.aekos.shared.api.json.SpeciesFileNameEntry;
import au.org.aekos.shared.api.json.fileinfo.StorageLocationInfo;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfo;
import au.org.aekos.shared.api.json.fileinfo.SubmissionFileInfoResponse;

@Service
public class SubmissionFileInfoServiceImpl implements SubmissionFileInfoService {

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private EmbargoService embargoService;
	
	@Override @Transactional
	public SubmissionFileInfoResponse retrieveSubmissionFileInfo(Long submissionId) {
		SubmissionFileInfoResponse response = new SubmissionFileInfoResponse();
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub == null){
			response.setErrorMessage("No submission ID '" + submissionId.toString() +"' exists.");
			return response;
		}
		response.setSubmissionId(submissionId);
		if( embargoService.isSubmissionUnderEmbargo(submissionId) ){
			response.setErrorMessage("Submission ID '" + submissionId.toString() +"' is under embargo. No data currently available");
			return response;
		}
		response.setSubmissionTitle(sub.getTitle());
		response.setNumFiles(sub.getSubmissionDataList().size());
		if(sub.getSubmissionDataList().size() > 0){
			for(SubmissionData sd : sub.getSubmissionDataList()){
				SubmissionFileInfo sfi = mapSubmissionDataToSubmissionFileInfo(sd);
				response.getFiles().add(sfi);
			}
		}
		return response;
	}
	
	private SubmissionFileInfo mapSubmissionDataToSubmissionFileInfo(SubmissionData sd){
		SubmissionFileInfo sfi = new SubmissionFileInfo();
		sfi.setId(sd.getId());
		sfi.setName(sd.getFileName());
		sfi.setDescription(sd.getFileDescription());
		sfi.setFormat(sd.getFormat());
		sfi.setFormatVersion(sd.getFormatVersion());
		if(sd.getFileSizeBytes() != null){
			sfi.setSize(sd.getHumanReadableFileSize());
			sfi.setSizeBytes(sd.getFileSizeBytes().toString());
		}
		sfi.setType(sd.getSubmissionDataType().name());
		if(SubmissionDataType.SITE_FILE.equals(sd.getSubmissionDataType())){
			String cs = StringUtils.hasLength(sd.getSiteFileCoordSysOther() )? sd.getSiteFileCoordSysOther() : sd.getSiteFileCoordinateSystem();
			sfi.setSiteFileCS(cs);
		}
		for(ObjectStoreLocation osl : sd.getObjectStoreLocations() ){
			StorageLocationInfo locationInfo = new StorageLocationInfo();
			locationInfo.setObjectId(osl.getObjectId());
			locationInfo.setObjectStoreIdentifier(osl.getObjectStoreIdentifier());
			sfi.getS3Locations().add(locationInfo);
		}
		return sfi;
	}

	public void setSubmissionService(SubmissionService submissionService) {
		this.submissionService = submissionService;
	}

	@Override
	public List<SpeciesFileNameEntry> retrieveSpeciesFileNames(Long submissionId) {
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		return processSubmissionForSpeciesFiles(sub);
	}
	
	@Override
	public List<SpeciesFileNameEntry> retrieveSpeciesFileNames(Submission submission) {
		return processSubmissionForSpeciesFiles(submission);
	}

	/**
	 * Extracts the species file names from the supplied submission.
	 * 
	 * @param sub	submission to extract species file names from
	 * @return		list of species file names if they exist, <code>null</code> otherwise
	 */
	private List<SpeciesFileNameEntry> processSubmissionForSpeciesFiles(Submission sub) {
		if(sub == null){
			return Collections.emptyList();
		}
		List<SpeciesFileNameEntry> result = new LinkedList<SpeciesFileNameEntry>();
		for(SubmissionData sd : sub.getSubmissionDataList()){
			if (!sd.isSpeciesFile()) {
				continue;
			}
			SpeciesFileNameEntry entry = mapSubmissionDataToSpeciesFileNameEntry(sd);
			result.add(entry);
		}
		return result;
	}

	static SpeciesFileNameEntry mapSubmissionDataToSpeciesFileNameEntry(SubmissionData sd) {
		long id = sd.getId();
		String fileName = sd.getFileName();
		return new SpeciesFileNameEntry(id, fileName);
	}
}
