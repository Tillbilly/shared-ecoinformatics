package au.edu.aekos.shared.service.notification;

public interface AdminNotificationService {
	
	void notifyAdminByEmail(String subject, String mailMessage, Throwable t);
	
	void notifyAdminByEmail(String subject, String mailMessage);
	
}
