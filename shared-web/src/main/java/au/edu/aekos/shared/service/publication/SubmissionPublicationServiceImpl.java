package au.edu.aekos.shared.service.publication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.PublicationLogDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.PublicationLog;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionPublicationStatus;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.service.doi.DoiMintingException;
import au.edu.aekos.shared.service.doi.DoiMintingService;
import au.edu.aekos.shared.service.groupadmin.GroupAdminService;
import au.edu.aekos.shared.service.index.SolrIndexService;
import au.edu.aekos.shared.service.notification.FreemarkerEmailTemplateService;
import au.edu.aekos.shared.service.notification.NotificationEmailService;
import au.edu.aekos.shared.service.rifcs.RifcsFileService;
import au.edu.aekos.shared.service.rifcs.RifcsService;
import au.edu.aekos.shared.service.rifcs.RifcsServiceException;

import com.jcraft.jsch.JSchException;


/**
 * This service is responsible for publishing a submission to
 * 3 separate distributed systems.
 * 
 * Firstly a DOI must be minted using TERN's DOI minting service.
 * This is managed by the DoiMinting Service.
 * If a doi can`t be minted, the publication process fails, and an admin is notified.
 * 
 * Then the Shared Index is populated, making the submission record viewable in the aekos portal.
 * 
 * Then a rifcs record is created and posted to the jOAI service for harvesting.
 * 
 * A submission is viewable before the RIFCS is created, but it is not considered 
 * 'published' by the systems perspective.
 * 
 * Any errors etc are written to the publicationLog db table, and also out to a log file ( log4j config ).
 * 
 * 
 * @author btill
 */
@Service
public class SubmissionPublicationServiceImpl implements SubmissionPublicationService {
	
	private Logger logger = LoggerFactory.getLogger(SubmissionPublicationServiceImpl.class);

	@Value("${shared.sysadmin.email}")
	private String sysadminEmail;
	
	@Autowired
	private PublicationLogDao publicationLogDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private DoiMintingService doiMintingService;
	
	@Autowired
	private SolrIndexService solrIndexService;

	@Autowired
	private RifcsService rifcsService;
	
	@Autowired
	private RifcsFileService rifcsFileService;
	
	@Autowired
	private FreemarkerEmailTemplateService emailTemplateService;
	
	@Autowired
	private NotificationEmailService notificationEmailService;
	
	@Autowired
	private GroupAdminService groupAdminService;
	
	@Transactional
	public SubmissionPublicationStatus publishSubmission(Long submissionId){
		logger.info("Publishing submission " + submissionId.toString());
		return publishSubmission(submissionDao.findById(submissionId) );
	}
	
	@Override @Transactional
	public SubmissionPublicationStatus publishSubmission(Submission submission) {
		PublicationLog currentProcessLog = initialisePublicationLog(submission);
		SubmissionPublicationStatus status = submission.getPublicationStatus() == null ? SubmissionPublicationStatus.ATTEMPTED : submission.getPublicationStatus();
		try{
			while(! SubmissionPublicationStatus.PUBLISHED.equals(status)){
				status = performPublishOperation(status, submission, currentProcessLog);
			}
		}	
		catch(Exception e ){
			//log the error, notify system administrator of an error via email
			sendPublicationErrorNotificationToSysAdmin(submission, e, currentProcessLog );
			logger.error("Submission publication error for submission ID " + submission.getId(), e);
		}
		publicationLogDao.saveOrUpdate(currentProcessLog);
		submission.setPublicationStatus(status);
		//submission.setDoi(currentProcessLog.getDoi());
		if(SubmissionPublicationStatus.PUBLISHED.equals(status)){
			logger.info("Submission id '" + submission.getId() + "' successfully published.");
			logger.info("Submission id '" + submission.getId() + "' DOI :" + submission.getDoi());
			logger.info("Submission id '" + submission.getId() + "' RIFCS file :" + currentProcessLog.getFilepath());
			
			submission.setStatus(SubmissionStatus.PUBLISHED);
			sendPublishedNotificationToSubmitter(submission);
		}
		submissionDao.saveOrUpdate(submission);
		return status;
	}
	
	@Transactional
	private SubmissionPublicationStatus performPublishOperation(SubmissionPublicationStatus status, Submission submission, PublicationLog publishLog) throws DoiMintingException, SolrServerException, IOException, MetaInfoExtractorException, RifcsServiceException, JSchException{
		switch(status){
		case ATTEMPTED:
			status = mintDoiOperation(submission, publishLog);
			break;
		case DOI_MINTED:
			status = writeAekosIndexOperation(submission, publishLog);
			break;
		case AEKOS_INDEX_WRITTEN:
			status = rifcsDocumentOperation(submission, publishLog);
			break;
		case RIFCS_DOC_CREATED:
			status = rifcsFileTransferOperation(submission, publishLog);
			break;
		case PUBLISHED:
			break;
		}
		return status;
	}
	
	private SubmissionPublicationStatus mintDoiOperation(Submission submission, PublicationLog publishLog) throws DoiMintingException{
		String mintedDoi = null;
		publishLog.setDoiProcessTime(new Date());
		try {
			mintedDoi = doiMintingService.mintDoiForSubmission(submission);
		} catch (DoiMintingException e) {
			logger.error("Error thrown from doiMintingService.  submissionId " + submission.getId(), e);
			publishLog.setDoiMintFailure(e.getClass().getName() + " " + e.getMessage());
			publishLog.writeExceptionStackTraceToInformation(e);
			throw e;
		}catch (RuntimeException e) {
			logger.error("Error thrown from doiMintingService.  submissionId " + submission.getId(), e);
			publishLog.setDoiMintFailure(e.getClass().getName() + " " + e.getMessage());
			publishLog.writeExceptionStackTraceToInformation(e);
			throw e;
		}
		
		if(!StringUtils.hasLength(mintedDoi)){
			publishLog.setDoiSuccess(false);
			publishLog.setDoiErrorMessage("Doi not minted, no exception thrown");
			logger.error("Doi not minted, no exception thrown.   submission ID " + submission.getId());
			throw new DoiMintingException("Doi not minted, no exception thrown");
		}
		publishLog.setDoi(mintedDoi);
		publishLog.setDoiSuccess(true);
		logger.info("DOI minted for submission " + submission.getId() +"   " + mintedDoi +"    returning status  DOI_MINTED");
		submission.setDoi(mintedDoi);
		return SubmissionPublicationStatus.DOI_MINTED;
	}
	
	@Transactional
	private SubmissionPublicationStatus writeAekosIndexOperation(Submission submission, PublicationLog publishLog) throws SolrServerException, IOException, MetaInfoExtractorException{
		publishLog.setAekosIndexTime(new Date());
		Integer solrServerResponse = null;
		try {
			solrServerResponse = solrIndexService.addSubmissionToSolr(submission);
		} catch (SolrServerException e) {
			publishLog.setIndexFailure(e);
			logger.error("Error writing solr document for submission " + submission.getId(), e);
			throw e;
		} catch (IOException e) {
			publishLog.setIndexFailure(e);
			logger.error("Error writing solr document for submission " + submission.getId(), e);
			throw e;
		} catch (RuntimeException e) {
			publishLog.setIndexFailure(e);
			logger.error("Error writing solr document for submission " + submission.getId(), e);
			throw e;
		} catch (MetaInfoExtractorException e) {
			publishLog.setIndexFailure(e);
			logger.error("Error writing solr document for submission " + submission.getId(), e);
			throw e;
		}
		//We want a solr server response of 0
		if(solrServerResponse == null || solrServerResponse.intValue() != 0 ){
			if(solrServerResponse == null){
				solrServerResponse = 99;
			}
			publishLog.setIndexSuccess(false);
			publishLog.setIndexErrorMessage("Solr Server Response Code " + solrServerResponse.toString());
			logger.error("Error writing solr document for submission " + submission.getId() + " server response code:" + solrServerResponse.toString() );
			throw new SolrServerException("solr response code " + solrServerResponse.toString() + " for submission Id " + submission.getId());
		}
		publishLog.setIndexSuccess(true);
		return SubmissionPublicationStatus.AEKOS_INDEX_WRITTEN;
	}
	@Transactional
	private SubmissionPublicationStatus rifcsDocumentOperation(Submission submission, PublicationLog publishLog) throws MetaInfoExtractorException, RifcsServiceException, IOException{
		publishLog.setRifcsFileTime(new Date());
		File rifcsFile = null;
		try {
			rifcsFile = rifcsService.generateRifcsFileForSubmission(submission);
		} catch (MetaInfoExtractorException e) {
			publishLog.setRifcsFileFailure(e);
			logger.error("Error generationg rifcs document for submission id " + submission.getId(), e);
			throw e;
		} catch (RifcsServiceException e) {
			publishLog.setRifcsFileFailure(e);
			logger.error("Error generationg rifcs document for submission id " + submission.getId(), e);
			throw e;
		} catch (IOException e) {
			publishLog.setRifcsFileFailure(e);
			logger.error("Error generationg rifcs document for submission id " + submission.getId(), e);
			throw e;
		}catch (RuntimeException e) {
			publishLog.setRifcsFileFailure(e);
			logger.error("Error generationg rifcs document for submission id " + submission.getId(), e);
			throw e;
		}
		
		publishLog.setFilepath(rifcsFile.getPath());
		publishLog.setFileGenerated(true);
		return SubmissionPublicationStatus.RIFCS_DOC_CREATED;
	}
	
	private SubmissionPublicationStatus rifcsFileTransferOperation(Submission submission, PublicationLog publishLog) throws MetaInfoExtractorException, RifcsServiceException, IOException, JSchException{
		if(! StringUtils.hasLength( publishLog.getFilepath() ) ){
			return rifcsDocumentOperation(submission, publishLog);
		}
		publishLog.setRifcsScpTime(new Date());
		File f = new File(publishLog.getFilepath());
		try {
			rifcsFileService.scpToOaiServer(f);
		} catch (JSchException e) {
			publishLog.setScpSuccess(false);
			publishLog.setScpErrorMessage(e.getMessage());
			publishLog.writeExceptionStackTraceToInformation(e);
			logger.error("RIFCS File transfer failed for submission Id " + submission.getId() + " for file " + publishLog.getFilepath(), e);
			throw e;
		}
		publishLog.setScpSuccess(true);
		return SubmissionPublicationStatus.PUBLISHED;
	}
	

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private PublicationLog initialisePublicationLog(Submission sub){
		if(sub.getPublicationStatus() == null){
			PublicationLog currentProcessLog = new PublicationLog();
			currentProcessLog.setSubmission(sub);
			return currentProcessLog;
		}
		PublicationLog currentProcessLog;
		List<PublicationLog> publicationLogList = publicationLogDao.retrieveLogEntriesForSubmission(sub);
		if(publicationLogList != null && publicationLogList.size() > 0){
			currentProcessLog = new PublicationLog(publicationLogList.get(0));
		}else{
			currentProcessLog = new PublicationLog();
		}
		currentProcessLog.setSubmission(sub);
		return currentProcessLog;
	}
	
	private void sendPublicationErrorNotificationToSysAdmin(Submission submission, Exception e, PublicationLog currentProcessLog ){
		StringBuilder emailMessage = new StringBuilder("An error has occured during the publication of Shared Submission ID:");
		emailMessage.append(submission.getId().toString()).append("\n\n");
		emailMessage.append(e.getMessage()).append("\n").append(currentProcessLog.getInformation());
	    try{	
		    notificationEmailService.sendEmail(sysadminEmail, "SHaRED Submission Publication Error " + submission.getId(), emailMessage.toString());
	    }catch(MailException me){
	    	logger.error("Error occured sending sysadmin message for publication failure\n" + emailMessage.toString(), me);
	    }
	}

	@Override @Transactional
	public List<Submission> getListOfPublicationFailedSubmissions() {
		return submissionDao.getListOfPublicationFailedSubmissions();
	}
	
	@Transactional
	public Map<Long, PublicationLog> getLatestPublicationLogEntriesForSubmissionList(List<Submission> submissionList){
		List<Long> submissionIdList = new ArrayList<Long>();
		if(submissionList != null && submissionList.size() > 0){
			for(Submission sub : submissionList ){
				submissionIdList.add(sub.getId());
			}
		}
		return getLatestPublicationLogEntriesForSubmissions(submissionIdList);
	}
	
	@Transactional
	public Map<Long, PublicationLog> getLatestPublicationLogEntriesForSubmissions(List<Long> submissionIdList){
		return publicationLogDao.getLatestPublicationLogEntriesForSubmissions(submissionIdList);
	}

	@Override
	//Need to be very careful about this, 
	//I think, for each submission, try and build the replacement Solr doc,
	//Query the old doc, keep it whilst deleting it in the solr index, then replace it with the new one
	//Keep track of what happens for each submission
	public void reindexSubmissions() {
		
	}
	
	private void sendPublishedNotificationToSubmitter(Submission submission){
		//Need the DOI, and the placeholder URL for the submission in aekos portal
		String mintedDoi = submission.getDoi();
		Long submissionId = submission.getId();
		String submissionName= submission.getTitle();
		SharedUser submitter = submission.getSubmitter();
		String mailSubject = "SHaRED Submission " + submissionId.toString() + " '" + submissionName +"' published into AEKOS";
		String submissionUrl = doiMintingService.getDoiLandingPageBaseUrl() + submissionId.toString();
		String mailMessage = null;
		try{
		    mailMessage = emailTemplateService.getSubmissionPublishedEmailText(submitter.getUsername(), submissionName, submissionUrl, mintedDoi);
		}catch(Exception e){
			logger.error("Error generating freemarker email for publishing submission ID" + submission.getId(), e);
			notificationEmailService.asyncProcessNotification(mailSubject, "Your SHaRED submission " + submissionName + " has been published.", submitter.getUsername(), NotificationType.PUBLISH);
			return;
		}
		notificationEmailService.asyncProcessNotification(mailSubject, mailMessage, submitter.getUsername(), NotificationType.PUBLISH);
		//If submission owned by a group member, also send a publication notification to the group administrators
		notifyPublishToGroupAdministrators(submission.getId(), mailSubject, mailMessage);
	}
	
	private void notifyPublishToGroupAdministrators(Long submissionId, String mailSubject, String mailMessage){
		List<SharedUser> admins = groupAdminService.getGroupAdministratorsForSubmission(submissionId);
		if(admins != null && admins.size() > 0){
			String adminEmailText = "A submission from your group has been published.\nThe message sent to the submission owner is included below:\n\n" + mailMessage;
			for(SharedUser su : admins){
				notificationEmailService.asyncProcessNotification(mailSubject, adminEmailText, su.getUsername(), NotificationType.GROUP_PUBLISH);
			}
		}
	}
}
