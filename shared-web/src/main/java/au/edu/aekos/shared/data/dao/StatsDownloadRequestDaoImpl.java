package au.edu.aekos.shared.data.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.stats.StatsDownloadRequest;

@Repository
public class StatsDownloadRequestDaoImpl extends AbstractHibernateDao<StatsDownloadRequest, Long> implements StatsDownloadRequestDao{

	public Class<StatsDownloadRequest> getEntityClass() {
		return StatsDownloadRequest.class;
	}
	
	@Transactional
	public void insertNewDownloadRequest(StatsDownloadRequest sdReq){
		saveOrUpdate(sdReq);
	}
	
}
