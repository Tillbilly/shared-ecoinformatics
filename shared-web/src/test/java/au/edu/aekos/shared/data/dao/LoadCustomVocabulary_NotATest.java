package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.CustomVocabulary;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.ControlledVocabularyServiceImpl;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:load-customvocab-data-context-hssql.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class LoadCustomVocabulary_NotATest {
	
	private String testResource_vocabFileName = "SHaREDvocabulariesv20150903.csv";
	
	@Autowired
	private CustomVocabularyDao customVocabularyDao;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	//Only run this to do a fresh load of the vocab file
	@Test @Ignore
	@Rollback(false)
	@Transactional
	public void loadVocabFileFresh() throws IOException, SQLException{
		customVocabularyDao.deleteAllEntries();
		Resource rs = new ClassPathResource(testResource_vocabFileName);
		controlledVocabularyService.loadCustomVocabularyFile(rs.getFile());
		
		List<String> nameList = customVocabularyDao.retrieveActiveCustomVocabularyNames();
		for(String str : nameList){
			System.out.println(str);
		}
		assertTrue(true);
	}
	
	@Test
	public void testReadVocabularyLine(){
		ControlledVocabularyService cvs = new ControlledVocabularyServiceImpl();
		String lineWithNullParent = "vocabName,valuex,,valuey,valuez";
		CustomVocabulary cv = cvs.buildCustomVocabFromFileLine(lineWithNullParent);
		assertEquals("vocabName", cv.getVocabularyName());
		assertEquals("valuex", cv.getValue());
		assertNull(cv.getParentValue());
		assertEquals("valuey", cv.getDisplayValue());
		assertEquals("valuez", cv.getDisplayValueDescription());
		
		String lineWithParentValue = "vocabName,valuex,parentVal,valuey,valuez";
		cv = cvs.buildCustomVocabFromFileLine(lineWithParentValue);
		assertEquals("parentVal", cv.getParentValue());
	}
	
	
}
