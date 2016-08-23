package au.edu.aekos.shared.data.dao;

import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.EmailNotification;

@Repository
public class EmailNotificationDaoImpl extends AbstractHibernateDao<EmailNotification, Long> implements EmailNotificationDao{
	
	@Override
	public Class<EmailNotification> getEntityClass() {
		return EmailNotification.class;
	}

}
