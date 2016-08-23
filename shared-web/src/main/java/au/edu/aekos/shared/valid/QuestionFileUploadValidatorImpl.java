package au.edu.aekos.shared.valid;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.service.file.FileLineReadingService;
import au.edu.aekos.shared.service.file.SiteFileService;
import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;


/**
 * In here we simply wish to 
 * @author btill
 */
@Component
public class QuestionFileUploadValidatorImpl implements QuestionFileUploadValidator {
	
	private static int MAX_SPECIES_NAME_LENGTH = 200;
	
	@Autowired
	private SiteFileService siteFileService;
	
	@Autowired
	private FileLineReadingService fileLineReadingService;
	
	@Override
	public void validateUploadedFile(File file, SubmissionDataType dataType)
			throws FileUploadValidationException {
		switch(dataType){
			case SITE_FILE :
				validateSiteFile(file);
			case SPECIES_LIST:
				validateSpeciesFile(file);
			default:break;
		}
	}
	
	private void validateSiteFile(File file) throws FileUploadValidationException{
		JsonUploadSiteFileResponse jusr = null;
		try {
			jusr = siteFileService.parseSiteFileToJson(file, "EPSG:4283", false);
		} catch (IOException e) {
			throw new FileUploadValidationException(e.getMessage(), e);
		}
		if(StringUtils.hasLength(jusr.getError()) ){
			throw new FileUploadValidationException(jusr.getError());
		}
	}
	
	//This is in response to Martin uploading an excel spreadsheet,
	//We only accept .txt files at this stage.
	private void validateSpeciesFile(File file) throws FileUploadValidationException{
		if(! file.getPath().endsWith("txt")){
			throw new FileUploadValidationException("Species file must be a .txt file, one species name on each line");
		}
		
		//Try and open the file and read the lines.  Any line longer than say 200 characters probably means an error.
		List<String> lines = fileLineReadingService.readFileLinesAsList(file);
		if(lines == null || lines.size() == 0 ){
			throw new FileUploadValidationException("Can't read species. Species file must be a .txt file, one species name on each line");
		}
		for(int x = 0; x < lines.size() ; x++){
			String line = lines.get(x);
			if(line != null && line.length() > MAX_SPECIES_NAME_LENGTH){
				throw new FileUploadValidationException("Species name too long on line " + Integer.toString(x + 1) + ". Species file must be a .txt file, one species name on each line, name < 200 characters");
			}
		}
	}
}
