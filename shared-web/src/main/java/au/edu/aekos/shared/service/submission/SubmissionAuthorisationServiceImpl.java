package au.edu.aekos.shared.service.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.Submission;


/**
 * Contains placeholder methods for when ( if ) we implement group submission access.
 * 
 * Currently only the submitter will be able to write to a submission
 * 
 *
 * 
 * @author btill
 */
@Service
public class SubmissionAuthorisationServiceImpl implements
		SubmissionAuthorisationService {

	@Autowired
	private SubmissionDao submissionDao;
	
	@Override //@Transactional(propagation=Propagation.REQUIRED)
	public boolean userCanRead(String username, Long submissionId) {
		return true;
	}

	@Override @Transactional(propagation=Propagation.REQUIRED)
	public boolean userCanWrite(String username, Long submissionId) {
		Submission sub = submissionDao.findById(submissionId);
		String submitter = sub.getSubmitter().getUsername();
		return username.equals(submitter);
	}

}
