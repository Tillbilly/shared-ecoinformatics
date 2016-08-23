package au.edu.aekos.shared.service.quest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.CustomVocabulary;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;
import au.edu.aekos.shared.web.json.dynatree.DynatreeNode;

public interface ControlledVocabularyService {
	
	public static String ORGANISATION_TRAIT = "organisation";
	
	List<TraitValue> getTraitValueList(String traitName, Boolean isCustom);
	
    List<TraitValue> getTraitValueList(String traitName, Boolean isCustom, boolean sortAlpha);
    
    Map<String,List<TraitValue>> prepareControlledVocabListsForPage(QuestionnairePage page) throws ControlledVocabularyNotFoundException;
    
    String getTraitDisplayText(String traitName, String traitValue);
    
    List<DynatreeNode> getDynatreeNodeVocabRepresentation(String traitName);
    
    /**
     * csv file with top row vocabularyName, value, displayValue
     * @param file
     * @throws IOException 
     * @throws SQLException 
     */
    void loadCustomVocabularyFile(File file) throws IOException, SQLException;
    
    void loadCustomVocabularyFromInputStream(InputStream is) throws IOException, SQLException;
    
    CustomVocabulary buildCustomVocabFromFileLine(String line);
    
    List<String> getListOfAvailableTraits();
    
    List<String> getListOfAvailableCustomTraits();
    
    boolean traitListContainsValue(String traitName, boolean isCustom, String value);
    
    String getDisplayStringForTraitValue(String traitName, boolean isCustom, String value);
    
    Map<Submission, List<SubmissionAnswer>> getSubmissionAnswersContainingVocabValueAnswer(String vocabValue, String vocabularyName);
    
	TraitValue getCustomTraitValue(String vocabularyName, String vocabValue);
	
	TraitValue getTraitValue(String vocabularyName, String vocabValue);
	
	void removeCustomVocabValue(String vocabularyName, String vocabValue);
	
	void addCustomVocabValue(String vocabularyName, TraitValue value);
	
	void addSuggestedValuesToVocab(String vocabularyName, List<TraitValue> traitList);

	boolean checkCustomVocabValueExists(String vocabularyName, TraitValue value);
	
	void updateCustomVocabValue(String vocabularyName, String originalValue,
			TraitValue value);
	void streamCustomVocabulariesToOutputStream(OutputStream outputStream);

	void addSuggestedAekosVocabValue(String vocabularyName, TraitValue value);
}
