package au.edu.aekos.shared.data.dao;

import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.PublicationLog;
import au.edu.aekos.shared.data.entity.Submission;

public interface PublicationLogDao extends HibernateDao<PublicationLog, Long>{
	
	List<PublicationLog> retrieveLogEntriesForSubmission(Submission sub);
	
	Map<Long, PublicationLog> getLatestPublicationLogEntriesForSubmissions(List<Long> submissionIdList);

}
