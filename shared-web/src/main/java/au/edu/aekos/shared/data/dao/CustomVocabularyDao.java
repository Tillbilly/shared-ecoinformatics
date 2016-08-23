package au.edu.aekos.shared.data.dao;

import java.sql.SQLException;
import java.util.List;

import au.edu.aekos.shared.data.entity.CustomVocabulary;

public interface CustomVocabularyDao extends HibernateDao<CustomVocabulary, Long>{
	
	List<String> retrieveActiveCustomVocabularyNames();
	
	List<CustomVocabulary> retrieveCustomVocabularyByName(String vocabularyName);
	
	CustomVocabulary retrieveCustomVocabularyByName(String vocabularyName, String customVocabValue);
	
	List<CustomVocabulary> retrieveCustomVocabularyUnsorted(String vocabularyName);
	
	//Used for development purposes!!  In prod, need to just set the flag active to false/
	void deleteAllEntries();
	
	void batchLoad(List<CustomVocabulary> customVocabularyList) throws SQLException;
	
	List<CustomVocabulary> getAllForExtract();

	
	
}
