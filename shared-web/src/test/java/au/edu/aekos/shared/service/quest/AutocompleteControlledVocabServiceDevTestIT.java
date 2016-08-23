package au.edu.aekos.shared.service.quest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:autocomplete-lucene-localdb-data-context.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
@Transactional
public class AutocompleteControlledVocabServiceDevTestIT {

	@Autowired
	RAMDirectoryVocabService autoService;
	
	@Test
	public void testSpringWiring(){
		Assert.assertNotNull(autoService);
	}
	
	@Test
	public void testSomeSearches() throws IOException{
		List<TraitValue> tvList = autoService.performSearch("Aca", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME, true);
		Assert.assertTrue(tvList.size() > 0);
		for(TraitValue tv : tvList){
			System.out.println(tv.getDisplayString());
		}
	}
	
	@Test
	public void testRedShowsUp() throws IOException{
		List<TraitValue> tvList = autoService.performSearch("Red", ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME , true);
		Assert.assertTrue(tvList.size() > 0);
		for(TraitValue tv : tvList){
			System.out.println("|" + tv.getDisplayString()+"|"  + " *** |" + tv.getTraitValue()+"|");
		}
	}
	
	@Test
	public void testDuplicatesRed() throws IOException{
		List<TraitValue> tvList = autoService.performSearch("red", ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME , true);
		Assert.assertTrue(tvList.size() > 0);
		//Test for duplicate display strings and values
		Map<String,List<TraitValue>> displayStringMatch = new HashMap<String,List<TraitValue>>();
		
		Map<String,List<TraitValue>> displayValueMatch = new HashMap<String,List<TraitValue>>();
		
		for(TraitValue tv : tvList){
            if(! displayStringMatch.containsKey(tv.getDisplayString())){
            	displayStringMatch.put(tv.getDisplayString(), new ArrayList<TraitValue>() );
            }
            displayStringMatch.get(tv.getDisplayString()).add(tv);
            
            if(! displayValueMatch.containsKey(tv.getTraitValue())){
            	displayValueMatch.put(tv.getTraitValue(), new ArrayList<TraitValue>() );
            }
            displayValueMatch.get(tv.getTraitValue()).add(tv);
		}
		
		for(String key: displayStringMatch.keySet()){
			if(displayStringMatch.get(key).size() > 1){
				System.out.println(key);
			}
		}
		System.out.println("**");
		for(String key: displayValueMatch.keySet()){
			if(displayValueMatch.get(key).size() > 1){
				System.out.println(key);
			}
		}
	}
	
	@Test
	public void testMatchFuzzy() throws IOException{
		List<TraitValue> tvList = autoService.performSearchForSpeciesMatching("Red Rod", ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME,  5 );
		Assert.assertTrue(tvList.size() > 0);
		for(TraitValue tv : tvList){
			System.out.println("|" + tv.getDisplayString()+"|"  + " *** |" + tv.getTraitValue()+"|");
		}
		
		tvList = autoService.performSearchForSpeciesMatching("Red Rad", ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME,  5 );
		Assert.assertTrue(tvList.size() > 0);
		for(TraitValue tv : tvList){
			System.out.println("|" + tv.getDisplayString()+"|"  + " *** |" + tv.getTraitValue()+"|");
		}
	}
	
	@Test
	public void testMatchFuzzy_SandSpurrey() throws IOException{
		List<TraitValue> tvList3 = autoService.performSearchForSpeciesMatching("RedSandSpurrey",ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME, 1 );
		Assert.assertEquals(1, tvList3.size());
		for(TraitValue tv : tvList3){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		
		List<TraitValue> tvList4 = autoService.performSearchForSpeciesMatching("Red Sand Spurry",ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME, 1 );
		for(TraitValue tv : tvList4){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		Assert.assertEquals(1, tvList4.size());
		
		tvList4 = autoService.performSearchForSpeciesMatching("Rod Sand Spurry",ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME, 1 );
		for(TraitValue tv : tvList4){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		Assert.assertEquals(1, tvList4.size());
		
		tvList4 = autoService.performSearchForSpeciesMatching("Rude Sand Spurrey",ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME, 1 );
		for(TraitValue tv : tvList4){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		Assert.assertEquals(1, tvList4.size());
		
		
		tvList4 = autoService.performSearchForSpeciesMatching("Rod Send Spurry",ControlledVocabularyServiceImpl.COMMON_FLORA_TRAIT_NAME, 1 );
		for(TraitValue tv : tvList4){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		Assert.assertEquals(0, tvList4.size());
	}
	
	@Test
	public void testMatchFuzzy_Atriplex() throws IOException{
		List<TraitValue> tvList3 = autoService.performSearchForSpeciesMatching("Atriplex acutibractea var. karoniensis",ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME, 5 );
		for(TraitValue tv : tvList3){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		
		tvList3 = autoService.performSearchForSpeciesMatching("Atriplex karoniensis", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME, 5 );
		for(TraitValue tv : tvList3){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		
		tvList3 = autoService.performSearch("Atriplex karoniensis", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME, true);
		for(TraitValue tv : tvList3){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
		
		//tvList3 = autoService.performSearch("Atriplex", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME, true);
		//for(TraitValue tv : tvList3){
		//	System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		//}
		
		tvList3 = autoService.performSearch("karoniensis", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME, true);
		for(TraitValue tv : tvList3){
			System.out.println("|" + tv.getDisplayString()+"|"  + " ****** |" + tv.getTraitValue()+"|");
		}
	}
	
	/**
	 * Can we perform an exact match for an entry that exists in the database?
	 */
	@Test
	public void testPerformExactMatchSearchTraitValue01() throws IOException{
		TraitValue result = autoService.performExactMatchSearchTraitValue("Atriplex spongiosum", ControlledVocabularyServiceImpl.FLORA_TAXA_TRAIT_NAME);
		assertNotNull("Species should've been matched", result);
		assertEquals("Atriplex spongiosum", result.getTraitValue());
	}
}
