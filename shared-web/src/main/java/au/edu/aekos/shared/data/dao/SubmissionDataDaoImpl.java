package au.edu.aekos.shared.data.dao;

import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;
import au.edu.aekos.shared.data.entity.storage.StorageLocation;

@Repository
public class SubmissionDataDaoImpl extends AbstractHibernateDao<SubmissionData, Long> implements SubmissionDataDao {

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;  //Used by the object store check queries - raw jdbc
	
	@Autowired
	private SubmissionHistoryDao submissionHistoryDao;
	
	public Class<SubmissionData> getEntityClass() {
		return SubmissionData.class;
	}

	public Long addStorageLocation(Long submissionDataId,
			StorageLocation location) {
		SubmissionData subData = findById(submissionDataId);
		if(subData == null){
			return null;
		}
		subData.getStorageLocations().add(location);
		save(subData);
		return location.getId();
	}

	
	@Override
	public void removeStorageLocation(Long submissionDataId,
			Long storageLocationId) {
		SubmissionData subData = findById(submissionDataId);
		if(subData == null){
			return ;
		}
		boolean removed = false;
		for (Iterator<StorageLocation> iter = subData.getStorageLocations().iterator(); iter.hasNext(); ) {
			StorageLocation loc = iter.next();
			if(loc.getId().equals(storageLocationId) ){
				iter.remove();
				removed = true;
				break;
			}
		}
		if(removed){
			save(subData);
		}
	}

	@Override
	public SubmissionData findByIdEagerLocations(Long id) {
		Criteria c = createCriteria()
				.setFetchMode("storageLocations", FetchMode.JOIN)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		c.add(Restrictions.eq("id", id));
		return (SubmissionData) c.uniqueResult();
	}
	
	@Transactional
	public boolean submissionDataExists(Long id) {
		SubmissionData sd = findById(id);
		return ( sd != null ? true : false );
	}
	
	@Transactional
	public Long transactionalAddStorageLocation(Long submissionDataId,
			StorageLocation location) {
		return addStorageLocation(submissionDataId, location);
	}
	
	public static final String NO_OBJECT_STORE_SL_QUERY =
	"select distinct sd.id " +
	"from submission_data sd " +
	"where not exists ( " +
	"  select sl.data_id " +
	"  from storage_location sl " +
	"  where sl.data_id = sd.id " +
	"  and sl.type='OBJECT_STORE'" +
	") ";
	
	public static final String NO_OBJECT_STORE_SL_QUERY_EXCL =
			"select distinct sd.id " +
			"from submission_data sd " +
			"where not exists ( " +
			"  select sl.data_id " +
			"  from storage_location sl " +
			"  where sl.data_id = sd.id " +
			"  and sl.type='OBJECT_STORE'" +
			") and sd.submission_id is not null";
	
	public static final String NO_OBJECT_STORE_SL_OF_NAME_QUERY = 
			"select distinct sd.id " +
			"from submission_data sd " +
			"where not exists ( " +
			"  select sl.data_id " +
			"  from storage_location sl " +
			"  where sl.data_id = sd.id " +
			"  and sl.type='OBJECT_STORE' " +
			"  and sl.objectstoreidentifier = ? " +
			") ";
	
	public static final String NO_OBJECT_STORE_SL_OF_NAME_QUERY_EXCL = 
			"select distinct sd.id " +
			"from submission_data sd " +
			"where not exists ( " +
			"  select sl.data_id " +
			"  from storage_location sl " +
			"  where sl.data_id = sd.id " +
			"  and sl.type='OBJECT_STORE' " +
			"  and sl.objectstoreidentifier = ? " +
			") and sd.submission_id is not null";
	
	
	@Transactional
	public List<Long> findSubmissionDataIDsWithoutObjectStoreLocation(boolean excludeNullSubmissions){
        JdbcTemplate jdbc = new JdbcTemplate();
        jdbc.setDataSource(dataSource);
        if(excludeNullSubmissions){
            return jdbc.queryForList(NO_OBJECT_STORE_SL_QUERY_EXCL, Long.class);
        }
        return jdbc.queryForList(NO_OBJECT_STORE_SL_QUERY, Long.class);
	}
	
	/**
	 * Used by system task to keep object store up to date, and to check on s3 migrations etc
	 * @param objectStoreName
	 * @return
	 */
	@Transactional
	public List<Long> findSubmissionDataIDsWithoutObjectStoreLocation(String objectStoreName, boolean excludeNullSubmissions){
		JdbcTemplate jdbc = new JdbcTemplate();
        jdbc.setDataSource(dataSource);
        if(excludeNullSubmissions){
        	jdbc.queryForList(NO_OBJECT_STORE_SL_OF_NAME_QUERY_EXCL, Long.class, objectStoreName);
        }
        return jdbc.queryForList(NO_OBJECT_STORE_SL_OF_NAME_QUERY, Long.class, objectStoreName);
	}

	@SuppressWarnings("unchecked")
	@Override @Transactional //Orphan as in not belonging to a submission - update: check submission history records also
	public List<SubmissionData> findOrphanSubmissionData() {
		Criteria c = createCriteria();
		c.add(Restrictions.isNull("submission"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<SubmissionData> submissionDataList = c.list();
		if(submissionDataList == null || submissionDataList.size() == 0 ){
			return submissionDataList;
		}
		//Need to check whether each SubmissionData is part of a history record
		return submissionHistoryDao.determineOrphanedSubmissionData(submissionDataList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubmissionData> getSubmissionDataWithSamePhysicalFileSystemStorageLocation(
			FileSystemStorageLocation fssl, Long submissionDataIdToExclude) {
		Criteria c = createCriteria();
		if(submissionDataIdToExclude != null){
			c.add(Restrictions.ne("id", submissionDataIdToExclude));
		}
		Criteria slCriteria = c.createCriteria("storageLocations");
		slCriteria.add(Restrictions.eq("objectName", fssl.getObjectName()))
		          .add(Restrictions.eq("fspath", fssl.getFspath()));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubmissionData> getSubmissionDataWithSamePhysicalObjectStoreLocation(
			ObjectStoreLocation osl, Long submissionDataIdToExclude) {
		Criteria c = createCriteria();
		if(submissionDataIdToExclude != null){
			c.add(Restrictions.ne("id", submissionDataIdToExclude));
		}
		Criteria slCriteria = c.createCriteria("storageLocations");
		slCriteria.add(Restrictions.eq("objectId", osl.getObjectId()))
		          .add(Restrictions.eq("objectStoreIdentifier", osl.getObjectStoreIdentifier()));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@Override
	public void deleteStorageLocations(SubmissionData submissionData) {
		if(submissionData.getStorageLocations() != null){
			for(StorageLocation sl : submissionData.getStorageLocations()){
				getCurrentSession().delete(sl);
			}
		}
		submissionData.getStorageLocations().clear();
		update(submissionData);
		flush();
	}


}
