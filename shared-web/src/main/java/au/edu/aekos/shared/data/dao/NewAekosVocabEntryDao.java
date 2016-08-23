package au.edu.aekos.shared.data.dao;

import java.util.List;

import au.edu.aekos.shared.data.entity.NewAekosVocabEntry;

public interface NewAekosVocabEntryDao extends HibernateDao<NewAekosVocabEntry, Long>{
	List<NewAekosVocabEntry> retrieveNewEntriesForVocabulary(String vocabularyName);
	List<NewAekosVocabEntry> retrieveNewEntries();

}
