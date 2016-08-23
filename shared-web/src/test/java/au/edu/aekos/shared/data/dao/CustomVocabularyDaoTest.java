package au.edu.aekos.shared.data.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.CustomVocabulary;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql-cv.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class CustomVocabularyDaoTest {
	
	@Autowired
	private CustomVocabularyDao objectUnderTest;
	
	/**
	 * Can we get everything that is loaded?
	 */
	@Test
	public void testGetAllForExtract01() {
		objectUnderTest.save(customVocab("anzsrcfor","0503"));
		objectUnderTest.save(customVocab("anzsrcfor","0608"));
		objectUnderTest.save(customVocabWithParent("anzsrcfor","060802","0608"));
		objectUnderTest.save(customVocab("anzsrcseo","9601"));
		objectUnderTest.save(customVocab("anzsrcseo","9602"));
		List<CustomVocabulary> result = objectUnderTest.getAllForExtract();
		assertEquals(5, result.size());
	}
	
	/**
	 * Are orphaned child entries left out of the extract and valid ones included?
	 * Not suggestion this is good but just asserting the behaviour so it's clear.
	 */
	@Test
	public void testGetAllForExtract02() {
		objectUnderTest.save(customVocab("anzsrcfor","0503"));
		objectUnderTest.save(customVocabWithParent("anzsrcfor","050302","503")); // Orphan due to typo in parent
		objectUnderTest.save(customVocab("anzsrcfor","0608"));
		objectUnderTest.save(customVocabWithParent("anzsrcfor","060802","0608")); // Valid child
		objectUnderTest.save(customVocabWithParent("anzsrcfor","010101","101")); // Orphan with no parent, same effect as a typo
		List<CustomVocabulary> result = objectUnderTest.getAllForExtract();
		assertEquals(3, result.size());
		assertEquals("0503", result.get(0).getValue());
		assertEquals("0608", result.get(1).getValue());
		assertEquals("060802", result.get(2).getValue());
	}
	
	/**
	 * Can we retrieve vocab names as expected?
	 */
	@Test 
	public void testRetrieveActiveCustomVocabularyNames01(){
		objectUnderTest.save(customVocab("anzsrcfor","0503"));
		objectUnderTest.save(customVocab("anzsrcfor","0608"));
		objectUnderTest.save(customVocab("anzsrcseo","9601"));
		objectUnderTest.save(customVocab("anzsrcseo","9602"));
		objectUnderTest.save(customVocab("artefacts", "DNA"));
		objectUnderTest.save(customVocab("artefacts", "Still"));
		objectUnderTest.save(customVocab("artefacts", "Blood Sample"));
		List<String> result = objectUnderTest.retrieveActiveCustomVocabularyNames();
		assertEquals(3, result.size());
		assertEquals("anzsrcfor", result.get(0));
		assertEquals("anzsrcseo", result.get(1));
		assertEquals("artefacts", result.get(2));
	}
	
	/**
	 * Can we delete all vocabs?
	 */
	@Test 
	public void testDeleteAllEntries01(){
		objectUnderTest.save(customVocab("anzsrcfor","0503"));
		objectUnderTest.save(customVocab("anzsrcseo","9601"));
		objectUnderTest.save(customVocab("artefacts", "DNA"));
		assertEquals("Entries should exist here", 3, objectUnderTest.retrieveActiveCustomVocabularyNames().size());
		objectUnderTest.deleteAllEntries();
		List<String> result = objectUnderTest.retrieveActiveCustomVocabularyNames();
		assertEquals(0, result.size());
	}

	private CustomVocabulary customVocab(String vocabName, String value){
		return customVocabWithParent(vocabName, value, null);
	}
	
	private CustomVocabulary customVocabWithParent(String vocabName, String value, String parent) {
		CustomVocabulary result = new CustomVocabulary();
		result.setActive(true);
		result.setVocabularyName(vocabName);
		result.setValue(value);
		result.setDisplayValue("display" + value);
		if (StringUtils.hasLength(parent)) {
			result.setParentValue(parent);
		}
		return result;
	}
}
