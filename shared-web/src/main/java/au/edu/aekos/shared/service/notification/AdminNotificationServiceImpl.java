package au.edu.aekos.shared.service.notification;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class AdminNotificationServiceImpl implements AdminNotificationService {

	@Value("${shared.sysadmin.email}")
	private String sysadminEmail;
	
	private Logger logger = LoggerFactory.getLogger(AdminNotificationServiceImpl.class);
	
	@Autowired
	private NotificationEmailService notificationEmailService;
	
	@Override
	public void notifyAdminByEmail(String subject, String mailMessage, Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		notifyAdminByEmail(subject, mailMessage + "\n\n" + stackTrace );
	}

	@Override
	public void notifyAdminByEmail(String subject, String mailMessage) {
		try{
		    notificationEmailService.sendEmail(sysadminEmail, subject, mailMessage);
		}catch(MailException ex){
			logger.error("Sending notification to admin failed", ex);
			logger.error(subject);
			logger.error(mailMessage);
		}
	}
}
