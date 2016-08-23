package au.org.aekos.shared.api.informationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import au.org.aekos.shared.api.model.infomodel.InformationModelEntry;
import au.org.aekos.shared.api.model.infomodel.SharedInformationModel;


/**
 * The InformationModelService reads the SHaRED Information Model csv in src/main/resources/ and returns 
 * a SharedInformationModel object, ripe for json serialisation.
 * 
 * This is pretty crude, but will get the job done.
 * 
 * @author btill
 */
public class InformationModelFactory {
	
	public static final int METATAG_INDX = 0;
	public static final int DISPLAY_TXT_INDX = 1;
	public static final int DESCRIPTION_INDX = 2;
	public static final int ANSWER_OR_DERIVED_INDX = 3;
	public static final int MANDATORY_INDX = 4;
	public static final int MULTI_VALUES_INDX = 5;
	public static final int VOCABULARY_INDX = 6;
	public static final int RELATED_METATAG_INDX = 7;
	public static final int SECTION_INDX = 8;
	public static final int SUBSECTION_INDX = 9;
	public static final int GROUP_INDX = 10;
	private static final Logger logger = LoggerFactory.getLogger(InformationModelFactory.class);
	
	public static String INFO_MODEL_RESOURCE_NAME = "SHaRED_Information_Model.csv";
	
	private static final SharedInformationModel sharedInformationModel  = readInformationModelFromResource(INFO_MODEL_RESOURCE_NAME);

	public static SharedInformationModel getSharedInformationModel(){
		return sharedInformationModel.clone();
	}
	
	public static SharedInformationModel readInformationModelFromResource(String informationModelResourceName){
		SharedInformationModel model = new SharedInformationModel();
		Resource rs = new ClassPathResource(informationModelResourceName);
		BufferedReader buf;
		try {
			buf = new BufferedReader(new InputStreamReader(rs.getInputStream()));
		} catch (IOException e1) {
			logger.error("Failed when loading the information model file", e1);
			return null;
		}
		String line;
		try {
			while ((line = buf.readLine()) != null) {
				if(StringUtils.hasLength(line) && line.startsWith("SHD.")){
					InformationModelEntry entry = parseCsvLineToInfoModelEntry(line);
					model.getEntryList().add(entry);
				}
			}
		} catch (IOException e) {
			logger.error("Failed when processing the information model file", e);
			return null;
		}
		return model;
	}
	
	//Metatag Name, Display Text, Description, Answer or Derived, Mandatory, Multiple Values, Vocabulary, Related Metatag, Classification ( Section ), Group, Comments
	//SHD.datasetFormalName, Submission Name, SHaRED submission Title used to identify the submission in SHaRED/AEKOS, A, Y, N, , , , ,
	//Forget comments
	public static InformationModelEntry parseCsvLineToInfoModelEntry(String line){
		String [] values = stringTokeniseModelEntryLine(line);
		InformationModelEntry entry = new InformationModelEntry();
		entry.setMetatag(values[METATAG_INDX]);
		entry.setDisplayText(values[DISPLAY_TXT_INDX]);
		entry.setDescription(values[DESCRIPTION_INDX]);
		entry.setQuestionnaireAnswer("A".equals(values[ANSWER_OR_DERIVED_INDX]));
		entry.setMandatory("Y".equals(values[MANDATORY_INDX]));
		entry.setMultipleValues("Y".equals(values[MULTI_VALUES_INDX]));
		entry.setVocabulary(values[VOCABULARY_INDX]);
		entry.setRelatedMetatag(values[RELATED_METATAG_INDX]);
		entry.setSection(values[SECTION_INDX]);
		entry.setSubsection(values[SUBSECTION_INDX]);
		entry.setGroup(values[GROUP_INDX]);
		return entry;
	}
	
	//At the moment we are only grabbing the first 11 columns
	public static String [] stringTokeniseModelEntryLine(String line){
		int tokenIndex = 0;
		int charIndex = 0;
		int startIndex = 0;
		String [] tokens = new String[11];
		boolean openQuote = false;
		while(charIndex < line.length()){
			char c = line.charAt(charIndex);
			if(c == '"'){
				openQuote = ! openQuote;
			}else if(c == ',' && ! openQuote){
				if(startIndex == charIndex){
					tokens[tokenIndex] = null;
				}else{
				    String token = line.substring(startIndex, charIndex);
				    tokens[tokenIndex] = token.replaceAll("\"", "").trim();
				}
				startIndex = charIndex + 1;
				tokenIndex++;
				if(tokenIndex == 11){
					break;
				}
			}
			charIndex++;
		}
		return tokens;
	}
}
