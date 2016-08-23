package au.edu.aekos.shared.data.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.NewAekosVocabEntry;

@Repository
public class NewAekosVocabEntryDaoImpl extends AbstractHibernateDao<NewAekosVocabEntry, Long> implements NewAekosVocabEntryDao{

	@Override
	public Class<NewAekosVocabEntry> getEntityClass() {
		return NewAekosVocabEntry.class;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED)
	public List<NewAekosVocabEntry> retrieveNewEntriesForVocabulary(String vocabularyName){
		Criteria c = createCriteria()
				.add(Restrictions.eq("active", Boolean.TRUE))
		        .add(Restrictions.eq("vocabularyName", vocabularyName))
		        .addOrder(Order.asc("value"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED)
	public List<NewAekosVocabEntry> retrieveNewEntries(){
		Criteria c = createCriteria()
				.add(Restrictions.eq("active", Boolean.TRUE))
		        .addOrder(Order.asc("value"));
		return c.list();
	}
}
