package au.edu.aekos.shared.data.dao;

import au.edu.aekos.shared.data.entity.stats.StatsDownloadRequest;

public interface StatsDownloadRequestDao  extends HibernateDao<StatsDownloadRequest, Long>{
	
	void insertNewDownloadRequest(StatsDownloadRequest sdReq);

}
