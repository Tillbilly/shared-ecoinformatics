package au.edu.aekos.shared.service.notification;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.EmailNotificationDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.EmailNotification;
import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.SharedUser;

@Service
public class NotificationEmailServiceImpl implements NotificationEmailService{

	private static final Logger logger = LoggerFactory.getLogger(NotificationEmailServiceImpl.class);
	
	@Autowired
	private EmailNotificationDao emailNotificationDao;
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private SimpleMailMessage message;
	
	@Value("${shared.system.email.subject.prefix:[programmer error - email prefix not set]}")
	private String environmentSubjectPrefix;
	
	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private NotificationPersisterService notificationPersisterService;
	
	@Autowired
	@Qualifier(value="taskExecutor")
	private TaskExecutor taskExecutor;

	@Override
	public void sendEmail(String emailAddress, String subject, String mailMessage) throws MailException {
		SimpleMailMessage msg = new SimpleMailMessage(message);
        msg.setTo(emailAddress);
        msg.setSubject(environmentSubjectPrefix + subject);
        msg.setText(mailMessage);
        sender.send(msg);		
	}

	@Override
	@Transactional
	public void processNotification(String subject, String mailMessage, String username, NotificationType notificationType) {
		logger.info("Attempting " + notificationType.name() + " notification to " + username );
		SharedUser recipient = sharedUserDao.findById(username);
		boolean exceptionOccured = false;
		if(recipient.getEmailAddress() == null ){
		    logger.error("Can't send notification to user :" + username + ". No email for user.");
		    exceptionOccured = true;
		}else{
			try{
			    sendEmail(recipient.getEmailAddress(),  subject, mailMessage  );
			}catch(MailException e){
				logger.error( e.getMessage() );
				exceptionOccured = true;
			}
		}
		//Now save the record of the notification into the db
		EmailNotification notification = new EmailNotification();
		notification.setMessage(mailMessage);
		notification.setSentDate(new Date());
		notification.setSharedUser(recipient);
		notification.setSubject(subject);
		notification.setExceptionOccured(exceptionOccured);
		notification.setToAddress(recipient.getEmailAddress());
		
		emailNotificationDao.save(notification);
	}
	
	@Transactional
	public void saveNotificationMessage(String mailMessage, SharedUser recipient, String subject , Boolean exceptionOccured ){
		EmailNotification notification = new EmailNotification();
		notification.setMessage(mailMessage);
		notification.setSentDate(new Date());
		notification.setSharedUser(recipient);
		notification.setSubject(subject);
		notification.setExceptionOccured(exceptionOccured);
		notification.setToAddress(recipient.getEmailAddress());
		
		emailNotificationDao.save(notification);
	}
	
	@Override
	@Transactional
	public void asyncProcessNotification(String subject, String mailMessage, String username, NotificationType notificationType) {
		logger.info("Attempting asynchronous " + notificationType.name() + " notification to " + username );
		SharedUser recipient = sharedUserDao.findById(username);
		NotificationTask task = new NotificationTask(recipient, subject, mailMessage, notificationType);
		try{
		    taskExecutor.execute(task);
		}catch(TaskRejectedException e){
			logger.error("Notification Task Rejected, manually running task",e);
			task.run();
		}
	}
	
	@Override
	@Transactional
	public void asyncProcessNotification(String subject, String mailMessage, String username, String emailAddress, NotificationType notificationType) {
		logger.info("Attempting asynchronous " + notificationType.name() + " notification to " + username );
		NotificationTask task = new NotificationTask(username, emailAddress, subject, mailMessage, notificationType);
		try{
		    taskExecutor.execute(task);
		}catch(TaskRejectedException e){
			logger.error("Notification Task Rejected, manually running task",e);
			task.run();
		}
	}
	
	public void setSender(JavaMailSender sender) {
		this.sender = sender;
	}

	public void setMessage(SimpleMailMessage message) {
		this.message = message;
	}

	public void setEnvironmentSubjectPrefix(String environmentSubjectPrefix) {
		this.environmentSubjectPrefix = environmentSubjectPrefix;
	}

	private class NotificationTask implements Runnable {
		
        private String username; 
		
		private String recipientEmail;
		
		private String emailSubject;
		
		private String mailMessage;
		
		private NotificationType notificationType;
		
		public NotificationTask(String username, String emailAddress, String emailSubject, String mailMessage, NotificationType notificationType){
			this.recipientEmail = emailAddress;
			this.username = username;
			this.emailSubject = emailSubject;
			this.mailMessage = mailMessage;
			this.notificationType = notificationType;
		}
		
		public NotificationTask(SharedUser user, String emailSubject, String mailMessage, NotificationType notificationType){
			this.recipientEmail = user.getEmailAddress();
			this.username = user.getUsername();
			this.emailSubject = emailSubject;
			this.mailMessage = mailMessage;
			this.notificationType = notificationType;
		}
		
		@Override
		public void run() {
			logger.info("Executing Notification Task for " + recipientEmail + " " + notificationType.name() );
			boolean exceptionOccured = false;
			try{
			    sendEmail(this.recipientEmail,  emailSubject, mailMessage  );
			}catch(MailException e){
				logger.error( e.getMessage() );
				exceptionOccured = true;
			}
			//Now save the record of the notification into the db
			EmailNotification notification = new EmailNotification();
			notification.setMessage(mailMessage);
			notification.setSentDate(new Date());
			notification.setNotificationType(notificationType);
			//notification.setSharedUser(recipient);
			notification.setSubject(emailSubject);
			notification.setExceptionOccured(exceptionOccured);
			notification.setToAddress(recipientEmail);
			notificationPersisterService.persistEmailNotificationRecord(notification, username);
		}
	}
}
