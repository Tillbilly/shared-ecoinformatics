package au.edu.aekos.shared.service.notification;

import au.edu.aekos.shared.data.entity.EmailNotification;

/**
 * This exists to support transaction wrapping service calls via the spring proxy instance.
 * @author btill
 */
public interface NotificationPersisterService {
	void persistEmailNotificationRecord(EmailNotification notification, String username);
	
}
