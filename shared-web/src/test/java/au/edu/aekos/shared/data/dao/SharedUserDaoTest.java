package au.edu.aekos.shared.data.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.test.context.transaction.TransactionConfiguration;
import au.edu.aekos.shared.data.entity.SharedUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class SharedUserDaoTest {

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Test
	@Rollback(false)
	public void testSharedUserDao(){
		SharedUser su = new SharedUser();
		su.setUsername("howdy");
		sharedUserDao.save(su);
		sharedUserDao.flush();
		
		Assert.assertNotNull(su.getUsername());
		SharedUser su1 = sharedUserDao.findById(su.getUsername());
		Assert.assertNotNull(su1);
		sharedUserDao.delete(su1);
		sharedUserDao.flush();
		Assert.assertNull(sharedUserDao.findById("howdy"));
	}
	
	
}
