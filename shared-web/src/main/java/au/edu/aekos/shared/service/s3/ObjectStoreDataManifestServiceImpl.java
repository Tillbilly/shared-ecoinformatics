package au.edu.aekos.shared.service.s3;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDataDao;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.org.ecoinformatics.s3client.S3RESTHttpClient;

/**
 * Despite myriad options, we'll make the manifest a simple csv.
 * @author btill
 */
@Service
public class ObjectStoreDataManifestServiceImpl implements
		ObjectStoreDataManifestService {

	public static final String REMOTE_MANIFEST_FILENAME = "SHaRED-manifest.csv";
	
	@Value("${submission.upload.tempdir}" )
	private String uploadDir;
	
	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Autowired
	private ObjectStoreService objectStoreService;
	
	@Override @Transactional
	public void writeManifestToObjectStore(String objectStoreIdentifier) throws Exception {
		List<ManifestRowEntry> manifestData = new ArrayList<ManifestRowEntry>(); 
		
		List<SubmissionData> submissionDataList = submissionDataDao.getAll();
        for(SubmissionData sd : submissionDataList){
        	if(sd.getSubmission() != null ){
	        	for(ObjectStoreLocation osLoc : sd.getObjectStoreLocations() ){
	        		if(objectStoreIdentifier.equals(osLoc.getObjectStoreIdentifier()) ){
	        			ManifestRowEntry row = buildManifestRowEntry(sd, osLoc);
	        			manifestData.add(row);
	        		}
	        	}
        	}
        }
        
        if(manifestData.size() > 0){
        	File tempFile = new File( uploadDir + objectStoreIdentifier+   "-" + REMOTE_MANIFEST_FILENAME );
        	FileWriter writer = new FileWriter(tempFile, false);
            for(ManifestRowEntry row : manifestData){
            	writer.write(row.getRowString());
            }
        	writer.close();
        	S3RESTHttpClient s3Client = objectStoreService.getClientForIdentifier(objectStoreIdentifier);
        	s3Client.putFile(tempFile, REMOTE_MANIFEST_FILENAME);
        }
	}
	
	private ManifestRowEntry buildManifestRowEntry(SubmissionData sd, ObjectStoreLocation osLoc){
		ManifestRowEntry mre = new ManifestRowEntry();
		mre.setSubmissionId(sd.getSubmission().getId());
		mre.setSubmissionStatus(sd.getSubmission().getStatus().name());
		mre.setSubmissionDataId(sd.getId());
		mre.setStorageLocationId(osLoc.getId());
		mre.setOriginalFileName(sd.getFileName());
		mre.setDataType(sd.getSubmissionDataType());
		mre.setDescription(sd.getFileDescription());
		mre.setObjectId(osLoc.getObjectId());
		mre.setObjectName(osLoc.getObjectName());
		return mre;
	}

}
