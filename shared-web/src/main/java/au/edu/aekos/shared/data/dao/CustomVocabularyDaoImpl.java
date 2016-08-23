package au.edu.aekos.shared.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.CustomVocabulary;

@Repository
public class CustomVocabularyDaoImpl extends AbstractHibernateDao<CustomVocabulary, Long> implements CustomVocabularyDao {

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	@Override
	public Class<CustomVocabulary> getEntityClass() {
		return CustomVocabulary.class;
	}

	@SuppressWarnings("unchecked") @Transactional(propagation=Propagation.REQUIRED)
	public List<String> retrieveActiveCustomVocabularyNames(){
		Criteria c = createCriteria().add(Restrictions.eq("active", Boolean.TRUE));
		c.setProjection(Projections.distinct( Projections.property("vocabularyName")) );
		c.addOrder(Order.asc("vocabularyName"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<CustomVocabulary> retrieveCustomVocabularyByName(String vocabularyName){
		Criteria c = createCriteria()
				.add(Restrictions.eq("active", Boolean.TRUE))
		        .add(Restrictions.eq("vocabularyName", vocabularyName))
		        .addOrder(Order.asc("displayValue"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<CustomVocabulary> retrieveCustomVocabularyUnsorted(String vocabularyName){
		Criteria c = createCriteria()
				.add(Restrictions.eq("active", Boolean.TRUE))
		        .add(Restrictions.eq("vocabularyName", vocabularyName))
		        .addOrder(Order.asc("id"));
		return c.list();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteAllEntries() {
		String hql = "delete from CustomVocabulary";
		Query query = getCurrentSession().createQuery(hql);
		query.executeUpdate();
	}

	public void batchLoad(List<CustomVocabulary> customVocabularyList) throws SQLException {
		//For some reason transactions keep rolling back when I run this as a direct test.
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("INSERT INTO custom_vocabularies( id, active, display, loaddate, loadedby, value, name, description, parent) " +
                           "VALUES (nextval('hibernate_sequence'), ?, ?, ?, ?, ?, ?,?,?)" );
		
		for(CustomVocabulary cv : customVocabularyList   ){
			ps.setBoolean(1, true);
			ps.setString(2, cv.getDisplayValue());
			ps.setDate(3, new java.sql.Date( cv.getLoadDate().getTime()) );
			ps.setString(4, cv.getLoadedBy());
			ps.setString(5, cv.getValue());
			ps.setString(6, cv.getVocabularyName());
			ps.setString(7, cv.getDisplayValueDescription());
			ps.setString(8, cv.getParentValue());
			ps.addBatch();
		}
		try{
		    ps.executeBatch();
		}catch(java.sql.BatchUpdateException ex){
			System.out.println( ex.getMessage() );
			ex.printStackTrace();
			ex.getNextException().getMessage();
			ex.getNextException().printStackTrace();
			
		}
		//c.commit();
		c.close();
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public List<CustomVocabulary> getAllForExtract(){
		List<CustomVocabulary> finalList = new ArrayList<CustomVocabulary>();
		List<String> cvNames = retrieveActiveCustomVocabularyNames() ;
		
		for(String vocabName : cvNames ){
			List<CustomVocabulary> cvList = retrieveCustomVocabularyByName(vocabName);
			List<String> topLevelValues = new ArrayList<String>();
			Map<String, CustomVocabulary> cvMap = new HashMap<String, CustomVocabulary>();
			Map<String, List<String>> parentToChildMap = new HashMap<String, List<String>>();
			for(CustomVocabulary cv : cvList){
				cvMap.put(cv.getValue(), cv);
				if(StringUtils.hasLength( cv.getParentValue()) ){
					if(!parentToChildMap.containsKey(cv.getParentValue())){
						parentToChildMap.put(cv.getParentValue(), new ArrayList<String>());
					}
					parentToChildMap.get(cv.getParentValue()).add(cv.getValue());
				}else{
					topLevelValues.add(cv.getValue());
				}
			}
			for(String top : topLevelValues){
				iterateOnCVToFindChildrenInOrder(top, cvMap, parentToChildMap,finalList  );
			}
		}
		
		return finalList;
	}
	
	private void iterateOnCVToFindChildrenInOrder(String value, 
			Map<String, CustomVocabulary> cvMap, 
			Map<String, List<String>> parentToChildMap,
			List<CustomVocabulary> finalList  ){
		finalList.add(cvMap.get(value));
		if(parentToChildMap.containsKey(value)){
			List<String> children = parentToChildMap.get(value);
			for(String child : children){
				iterateOnCVToFindChildrenInOrder(child, cvMap, parentToChildMap,finalList  );
			}
		}
	}
	
	public void batchLoadNotWorkingUnderTransaction(List<CustomVocabulary> customVocabularyList) throws SQLException {
		for(CustomVocabulary cv : customVocabularyList){
			System.out.println("saving " + cv.getVocabularyName() + " " + cv.getValue());
			saveOrUpdate(cv);
			System.out.println(cv.getId());
		}
		getCurrentSession().clear();
		getCurrentSession().flush();
	}

	@Override
	public CustomVocabulary retrieveCustomVocabularyByName(
			String vocabularyName, String customVocabValue) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("vocabularyName", vocabularyName))
		  .add(Restrictions.eq("active", Boolean.TRUE))
		  .add(Restrictions.eq("value", customVocabValue));
		return (CustomVocabulary) c.uniqueResult();
	}
}
