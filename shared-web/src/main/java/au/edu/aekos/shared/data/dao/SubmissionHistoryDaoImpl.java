package au.edu.aekos.shared.data.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionHistory;
import au.edu.aekos.shared.data.entity.SubmissionReview;

@Repository
public class SubmissionHistoryDaoImpl extends AbstractHibernateDao<SubmissionHistory, Long> implements SubmissionHistoryDao {

	@Autowired
	private SubmissionDao submissionDao;
	
	public Class<SubmissionHistory> getEntityClass() {
		return SubmissionHistory.class;
	}

	@Override
	public SubmissionHistory createSubmissionHistory(Submission submission) {
        SubmissionHistory subHistory = new SubmissionHistory(submission);
        save(subHistory);
        flush();
		return subHistory;
	}

	@Override
	public SubmissionHistory createSubmissionHistory(Submission submission, SubmissionReview submissionReview) {
		SubmissionHistory subHistory = new SubmissionHistory(submission, submissionReview);
		save(subHistory);
        flush();
		return subHistory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubmissionHistory> retrieveSubmissionHistoryForSubmission(
			Long submissionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("submission.id", submissionId)).addOrder(Order.desc("historyCreateDate"));
		return c.list();
	}

	@Override
	public SubmissionHistory createSubmissionHistory(Long submissionId) {
		Submission sub = submissionDao.findById(submissionId);
		if(sub != null){
			return createSubmissionHistory(sub);
		}
		return null;
	}
	
	private static final int SD_BATCH_SIZE = 500;
	
	//Needs to be processed in batches due to limits on WHERE IN queries in postgres  ~1000 from memory - I`ll make it 500
	@Override  
	public List<SubmissionData> determineOrphanedSubmissionData(List<SubmissionData> orphanedSubmissionDataList) {
		LinkedHashMap<Long, SubmissionData> orphanCheckMap = new LinkedHashMap<Long, SubmissionData>();
		List<Long> submissionDataIds = new ArrayList<Long>();
		for(SubmissionData sd : orphanedSubmissionDataList ){
			orphanCheckMap.put(sd.getId(), sd);
			submissionDataIds.add(sd.getId());
		}
		
		List<Long> dataIdsForQuery = new ArrayList<Long>();
		for(int x = 0; x < submissionDataIds.size(); x++){
			dataIdsForQuery.add(submissionDataIds.get(x));
			if(dataIdsForQuery.size() == SD_BATCH_SIZE){
				processSubmissionDataHistoryBatch(dataIdsForQuery , orphanCheckMap);
				dataIdsForQuery = new ArrayList<Long>();
			}
		}
		if(dataIdsForQuery.size() > 0){
			processSubmissionDataHistoryBatch(dataIdsForQuery , orphanCheckMap);
		}
		List<SubmissionData> resultList = new ArrayList<SubmissionData>();
		if(orphanCheckMap.size() > 0){
			resultList.addAll(orphanCheckMap.values());
		}
		return resultList;
	}
	
	private void processSubmissionDataHistoryBatch(List<Long> dataIdsForQuery , LinkedHashMap<Long, SubmissionData> orphanCheckMap){
		Criteria c = createCriteria();
		Criteria subDataCriteria = c.createCriteria("submissionDataList");
		subDataCriteria.add(Restrictions.in("id", dataIdsForQuery));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<SubmissionHistory> subHistoryList = c.list();
		if(subHistoryList != null && subHistoryList.size() > 0){
			for(SubmissionHistory subHistory : subHistoryList){
				if(subHistory.getSubmissionDataList() != null && subHistory.getSubmissionDataList().size() > 0 ){
					for(SubmissionData sd : subHistory.getSubmissionDataList() ){
						if(orphanCheckMap.containsKey(sd.getId())){
							orphanCheckMap.remove(sd.getId());
						}
					}
				}
			}
		}
	}
	
}
