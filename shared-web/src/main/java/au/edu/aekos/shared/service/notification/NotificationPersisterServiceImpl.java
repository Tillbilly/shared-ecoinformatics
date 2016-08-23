package au.edu.aekos.shared.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.EmailNotificationDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.EmailNotification;
import au.edu.aekos.shared.data.entity.SharedUser;

@Service
public class NotificationPersisterServiceImpl implements
		NotificationPersisterService {

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private EmailNotificationDao emailNotificationDao;
	
	@Transactional
	public void persistEmailNotificationRecord(EmailNotification notification, String username){
		SharedUser user = sharedUserDao.findById(username);
		notification.setSharedUser(user);
		emailNotificationDao.save(notification);
	}

}
