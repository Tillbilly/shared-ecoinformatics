package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.data.entity.storage.ObjectStoreLocation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class SubmissionDataDaoTestIT {

	private static final Logger logger = Logger.getLogger(SubmissionDataDaoTestIT.class);
	
	@Autowired
	SubmissionDataDao submissionDataDao;
	
	@Autowired
	SharedUserDao sharedUserDao;
	
	@Autowired
	SubmissionDao submissionDao;
	
	@Test
	public void testFindSubmissionDataIDsWithoutObjectStoreLocation(){
		List<Long> dirtyIds = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation(false);
		for(Long id : dirtyIds){
			submissionDataDao.delete(submissionDataDao.findById(id));
		}
		submissionDataDao.flush();
		//Load up 2 SubmissionDatas, 1 with an object store loc, one without
		SubmissionData sd = new SubmissionData();
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		sd.getStorageLocations().add(fssl);
		ObjectStoreLocation osl = new ObjectStoreLocation();
        osl.setObjectStoreIdentifier("object_store");
        sd.getStorageLocations().add(osl);
		submissionDataDao.save(sd);
		submissionDataDao.flush();
		assertNotNull(sd.getId());
		List<Long> submissionDataIds = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation(false);
        assertEquals(0, submissionDataIds.size());
        
        SubmissionData sd2 = new SubmissionData();
		FileSystemStorageLocation fssl2 = new FileSystemStorageLocation();  //Load up a file storage locale, no object store
		sd2.getStorageLocations().add(fssl2);
		submissionDataDao.save(sd2);
		submissionDataDao.flush();
		List<Long> submissionDataIds2 = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation(false);
		
        assertEquals(1, submissionDataIds2.size()); 
        assertEquals(sd2.getId(),submissionDataIds2.get(0));  //Should only return the ID for sd2
	}
	
	@Test
	public void testFindSubmissionDataIDsWithoutObjectStoreLocationByName(){
		int dataIdsWithoutOSLocationsBeforeTestCount = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation("object_store", false).size();
		SubmissionData sd = new SubmissionData();
		FileSystemStorageLocation fssl = new FileSystemStorageLocation();
		sd.getStorageLocations().add(fssl);
		ObjectStoreLocation osl = new ObjectStoreLocation();
        osl.setObjectStoreIdentifier("object_store");
        sd.getStorageLocations().add(osl);
		submissionDataDao.save(sd);
		submissionDataDao.flush();
		
        SubmissionData sd2 = new SubmissionData();
		FileSystemStorageLocation fssl2 = new FileSystemStorageLocation();  //Load up a file storage locale, no object store
		sd2.getStorageLocations().add(fssl2);
		ObjectStoreLocation osl2 = new ObjectStoreLocation();
        osl2.setObjectStoreIdentifier("object_store");
        sd2.getStorageLocations().add(osl2);
		submissionDataDao.save(sd2);
		submissionDataDao.flush();
		
		List<Long> list2 = submissionDataDao.findSubmissionDataIDsWithoutObjectStoreLocation("object_store", false);
		logger.info(sd2.getId());
		
        assertEquals(dataIdsWithoutOSLocationsBeforeTestCount, list2.size()); 
	}
}
