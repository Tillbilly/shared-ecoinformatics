package au.edu.aekos.shared.service.notification;

import org.springframework.mail.MailException;

import au.edu.aekos.shared.data.entity.NotificationType;
import au.edu.aekos.shared.data.entity.SharedUser;


public interface NotificationEmailService {
	
	void processNotification(String subject, String mailMessage, String username, NotificationType notificationType);
	
	void asyncProcessNotification(String subject, String mailMessage, String username, NotificationType notificationType);
	
	void asyncProcessNotification(String subject, String mailMessage, String username, String emailAddress, NotificationType notificationType);
	
	/**
	 * Sends an email to the supplied recipient. It will contain the supplied
	 * subject and content and be sent from the configured SHaRED system email address.
	 * 
	 * @param emailAddress	recipient email address
	 * @param subject		subject of the email
	 * @param mailMessage	content of the email
	 * @throws MailException	when something goes wrong
	 */
	void sendEmail(String emailAddress, String subject, String mailMessage) throws MailException;
	
	void saveNotificationMessage(String mailMessage, SharedUser recipient, String subject , Boolean exceptionOccured);
}
