package au.edu.aekos.shared.service.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import au.edu.aekos.shared.data.dao.StatsDownloadRequestDao;
import au.edu.aekos.shared.data.entity.stats.StatsDownloadRequest;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Autowired
	@Qualifier(value="taskExecutor")
	private TaskExecutor taskExecutor;

	@Autowired
	private StatsDownloadRequestDao statsDownloadRequestDao;
	
	public void asyncRecordDownloadRequest(Long submissionId,
			String emailAddress) {
		taskExecutor.execute(new SaveDownloadRequestStatTask(submissionId, emailAddress));
	}
	
	public void recordDownloadRequest(Long submissionId, String emailAddress) {
		StatsDownloadRequest sdr = new StatsDownloadRequest();
		sdr.setEmail(emailAddress);
		sdr.setSubmissionId(submissionId);
		statsDownloadRequestDao.insertNewDownloadRequest(sdr);
	}
	
	private class SaveDownloadRequestStatTask implements Runnable {
		
		Long submissionId;
		String emailAddress;
		
		public SaveDownloadRequestStatTask(Long submissionId, String emailAddress) {
			this.submissionId = submissionId;
			this.emailAddress = emailAddress;
		}

		public void run() {
			recordDownloadRequest(submissionId, emailAddress);
		}
	}
	
	
	
}
