package au.edu.aekos.shared.service.publication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

@Service
public class AsyncPublishServiceImpl implements AsyncPublishService{
	
	private Logger logger = LoggerFactory.getLogger(AsyncPublishServiceImpl.class);
	
	@Autowired
	private SubmissionPublicationService submissionPublicationService;
	
	@Autowired
	@Qualifier(value="taskExecutor")
	private TaskExecutor taskExecutor;
	
	@Value(value="${shared.publish.asynchronous}")
	private Boolean publishAsynchronously = Boolean.TRUE;
	
	@Override
	public void publishSubmission(Long submissionId) {
		logger.info("Attempting to publish submission ID " + submissionId);
		if(! publishAsynchronously){
			logger.info("Configured to publish in the current thread.");
			submissionPublicationService.publishSubmission(submissionId);
			return;
		}
		
		try{
			logger.info("Publishing Asynchronously");
		    taskExecutor.execute(new PublishSubmissionTask(submissionId));
		}catch(TaskRejectedException ex){
			logger.error("Task executor failed!! Running publish in the current thread", ex);
			submissionPublicationService.publishSubmission(submissionId);
		}
	}
	
	private class PublishSubmissionTask implements Runnable {
		Long submissionId;
		PublishSubmissionTask (Long subId){
			this.submissionId = subId;
		}
		@Override
		public void run() {
			submissionPublicationService.publishSubmission(submissionId);
		}
	}
}
