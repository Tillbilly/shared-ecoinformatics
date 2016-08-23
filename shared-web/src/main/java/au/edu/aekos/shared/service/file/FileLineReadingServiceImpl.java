package au.edu.aekos.shared.service.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.service.submission.SubmissionDataService;

@Service
public class FileLineReadingServiceImpl implements FileLineReadingService {

	private Logger logger = LoggerFactory.getLogger(FileLineReadingServiceImpl.class);
	
	@Autowired
	private SubmissionDataService submissionDataService;
	
	@Override @Transactional //Primarily for species lists, but can read any line based text file ( site file )
	public List<String> readFileLinesAsList(SubmissionData submissionData) {
		File f = submissionDataService.retrieveFileToLocal(submissionData);
		return readFileLinesAsList(f);
	}
	
	public List<String> readFileLinesAsList(File file){
		List<String> speciesList = new ArrayList<String>();
		if(file != null && file.canRead()){
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return speciesList;
			}
			String line;
			try {
				while ((line = br.readLine()) != null) {
					if(StringUtils.hasLength(line)){
						speciesList.add(line);
					}
				}
				br.close();
			} catch (IOException e) {
				logger.error("IO Error reading file, returning empty List", e);
				return speciesList;
			}
		}
	
		return speciesList;
	}
	
	
	
}
