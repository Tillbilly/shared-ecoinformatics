package au.edu.aekos.shared.service.stats;

public interface StatisticsService {
	
	void asyncRecordDownloadRequest(Long submissionId, String emailAddress);
	
	void recordDownloadRequest(Long submissionId, String emailAddress);

}
