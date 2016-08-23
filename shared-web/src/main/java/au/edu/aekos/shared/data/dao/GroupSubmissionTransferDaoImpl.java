package au.edu.aekos.shared.data.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.groupadmin.GroupSubmissionTransfer;

@Repository
public class GroupSubmissionTransferDaoImpl extends AbstractHibernateDao<GroupSubmissionTransfer, Long> implements GroupSubmissionTransferDao{

	private final Logger logger = LoggerFactory.getLogger(GroupSubmissionTransferDaoImpl.class);
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Override
	public Class<GroupSubmissionTransfer> getEntityClass() {
		return GroupSubmissionTransfer.class;
	}

	@Override
	public void migrateTansferRecordsToSubmissionId(Long originalId, Long newId) {
		Submission newSubmission = submissionDao.findById(newId);
		if(newSubmission == null){
			logger.warn("Group Transfer migration requested for submission ID " + newId + " but it does not exist!");
			return;
		}
		Criteria c = createCriteria();
		c.add(Restrictions.eq("submission.id", originalId));
		List<GroupSubmissionTransfer> transferRecords = c.list();
		if(transferRecords != null && transferRecords.size() > 0){
			for(GroupSubmissionTransfer transfer : transferRecords){
				transfer.setSubmission(newSubmission);
				saveOrUpdate(transfer);
			}
		}
		
	}

	@Override
	public void deleteTransferRecordsForSubmission(Long submissionId) {
		Criteria c = createCriteria();
		Set<Long> transferIdsToDelete = new HashSet<Long>();
		c.add(Restrictions.eq("submission.id", submissionId));
		List<GroupSubmissionTransfer> transferRecords = c.list();
		if(transferRecords != null && transferRecords.size() > 0){
			for(GroupSubmissionTransfer transfer : transferRecords){
				transferIdsToDelete.add(transfer.getId());
			}
			for(Long transferIdToDelete : transferIdsToDelete){
				GroupSubmissionTransfer transfer = findById(transferIdToDelete);
				if(transfer != null){
				    delete(transfer);
				}
			}
		}
	}

}
