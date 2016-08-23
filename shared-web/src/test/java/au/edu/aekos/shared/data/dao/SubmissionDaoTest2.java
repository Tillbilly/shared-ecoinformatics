package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.hibernate.PersistentObjectException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubmissionDaoTest2 {

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	/*
	 * These next ordered tests are about trying to first reproduce the following error,
	 * then ensure it does'nt happen anymore - 
	 * 
	 * I recently put the inverse Submission -> SubmissionData relationship in SubmissionData 
	 * (ie the Submission in SubmissionData ) to facilitate the creation of the s3 object store manifest.
	 * 
	 *  I think that could cause this error some how - or the 'DRAFT' status being missed in the service or something similar . . 
	 * 
	 * org.hibernate.PersistentObjectException: detached entity passed to persist: au.edu.aekos.shared.data.entity.SubmissionData
        org.hibernate.event.internal.DefaultPersistEventListener.onPersist(DefaultPersistEventListener.java:141)
        org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:835)
        org.hibernate.internal.SessionImpl.persist(SessionImpl.java:828)
        org.hibernate.engine.spi.CascadingAction$7.cascade(CascadingAction.java:315)
        org.hibernate.engine.internal.Cascade.cascadeToOne(Cascade.java:380)
        org.hibernate.engine.internal.Cascade.cascadeAssociation(Cascade.java:323)
        org.hibernate.engine.internal.Cascade.cascadeProperty(Cascade.java:208)
        org.hibernate.engine.internal.Cascade.cascadeCollectionElements(Cascade.java:409)
        org.hibernate.engine.internal.Cascade.cascadeCollection(Cascade.java:350)
        org.hibernate.engine.internal.Cascade.cascadeAssociation(Cascade.java:326)
        org.hibernate.engine.internal.Cascade.cascadeProperty(Cascade.java:208)
        org.hibernate.engine.internal.Cascade.cascade(Cascade.java:165)
        org.hibernate.event.internal.AbstractSaveEventListener.cascadeAfterSave(AbstractSaveEventListener.java:448)
        org.hibernate.event.internal.AbstractSaveEventListener.performSaveOrReplicate(AbstractSaveEventListener.java:293)
        org.hibernate.event.internal.AbstractSaveEventListener.performSave(AbstractSaveEventListener.java:193)
        org.hibernate.event.internal.AbstractSaveEventListener.saveWithGeneratedId(AbstractSaveEventListener.java:136)
        org.hibernate.event.internal.DefaultPersistEventListener.entityIsTransient(DefaultPersistEventListener.java:208)
	 * 
	 */
	
	public static  SubmissionData detachedSubmissionData;
	public static Long detachedSubmissionDataId;
	
	/**
	 * Can we store a 'submission data' entity? We also DO NOT rollback and keep a reference.
	 */
	@Test @Transactional @Rollback(false)
	public void dtestSetUpSaveSubmissionDataWithoutSubmission(){
        SubmissionData sd = new SubmissionData();
        FileSystemStorageLocation fssl = new FileSystemStorageLocation();
        fssl.setObjectName("test");
        sd.getStorageLocations().add(fssl);
        sd.setFileName("Test");
        submissionDataDao.saveOrUpdate(sd);
        submissionDataDao.flush();
        detachedSubmissionData = sd;
        detachedSubmissionDataId = sd.getId();
        assertNull(detachedSubmissionData.getSubmission());
        assertNotNull(detachedSubmissionDataId);
	}
	
	/**
	 * Can we trigger the expected exception when we try to save the detached entity,
	 * then can we successfuly save when we update the reference to the entity?
	 * Finally, we clean up after ourselves so we don't pollute the in memory database and affect
	 * other tests.
	 */
	@Test @Transactional
	public void etestSaveSubmissionWithDetachedSubmissionData(){
		SharedUser su = new SharedUser();
		su.setUsername("john-doe-nutts");
		sharedUserDao.save(su);
		sharedUserDao.flush();
		Submission sub = new Submission();
		sub.setTitle("Detached data submission");
		sub.setStatus(SubmissionStatus.SUBMITTED);
		sub.setSubmitter(su);
		sub.getSubmissionDataList().add(detachedSubmissionData);
		try{
	        submissionDao.save(sub);
	        fail();
		}catch(PersistentObjectException ex){
			// Success!
		}
		sub.getSubmissionDataList().clear();
		SubmissionData sdSession = submissionDataDao.findById(detachedSubmissionDataId);
		detachedSubmissionData = sdSession;
		sub.getSubmissionDataList().add(sdSession);
		try{
	        submissionDao.save(sub);
		}catch(PersistentObjectException ex){
			fail();
		}
		submissionDataDao.delete(detachedSubmissionData);
		submissionDao.delete(sub); // cleaning up for the dtestSetUpSaveSubmissionDataWithoutSubmission test because it doesn't rollback
		submissionDao.flush();
	}
}
