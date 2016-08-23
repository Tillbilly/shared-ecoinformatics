package au.edu.aekos.shared.service.submission;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.dao.SubmissionLinkDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionLink;
import au.edu.aekos.shared.data.entity.SubmissionLinkType;
import au.edu.aekos.shared.service.security.SecurityService;


@Service
public class SubmissionLinkServiceImpl implements SubmissionLinkService{

	private Logger logger = LoggerFactory.getLogger(SubmissionLinkServiceImpl.class);
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionLinkDao submissionLinkDao;
	
	@Override @Transactional(propagation=Propagation.REQUIRED)
	public void linkSubmissions(Long sourceSubmissionId,
			Long targetSubmissionId, SubmissionLinkType linkType,
			String description) {
		
		Submission sourceSubmission = submissionDao.findById(sourceSubmissionId);
		Submission targetSubmission = submissionDao.findById(targetSubmissionId);
		SubmissionLink sl = createNewSubmissionLink(sourceSubmission, targetSubmission, linkType , description,true);
		SubmissionLink inverse = createNewSubmissionLink(targetSubmission, sourceSubmission, linkType , description,false);
		submissionLinkDao.save(sl);
		submissionLinkDao.save(inverse);
	}
	
	private SubmissionLink createNewSubmissionLink(Submission source, Submission linked, SubmissionLinkType linkType, String description, boolean sourceLink ){
		SubmissionLink link = new SubmissionLink();
		link.setSourceSubmission(source);
		link.setLinkedSubmission(linked);
		link.setDescription(description);
		link.setLinkType(linkType);
		link.setLinkedByUser(securityService.getCurrentUser());
		link.setLinkDate(new Date());
		link.setSourceLink(sourceLink);
		return link;
	}

	@Override @Transactional(propagation=Propagation.REQUIRED)
	public void unlinkSubmissions(Long sourceSubmissionId, Long targetSubmissionId) {
		logger.info("Unlinking " + sourceSubmissionId + " and " + targetSubmissionId );
		List<SubmissionLink> slList = submissionLinkDao.getSubmissionLinksBetweenSubmissions(sourceSubmissionId, targetSubmissionId);
		if(slList == null || slList.size() == 0 ){
			logger.error("No links found for " + sourceSubmissionId + " and " + targetSubmissionId);
			return;
		}
		if( slList.size() != 2 ){
			logger.error(slList.size() + " links found for " + sourceSubmissionId + " and " + targetSubmissionId +". Deleting anyway");
		}
		for(SubmissionLink link : slList){
			submissionLinkDao.delete(link);
		}
	}

}
