package au.org.aekos.shared.api.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class SharedDatasetSummaryTest {

	/**
	 * Does the adder for species file name entries work?
	 */
	@Test
	public void testAddSpeciesFileNameEntry01() {
		SharedDatasetSummary objectUnderTest = new SharedDatasetSummary("123", "Some dataset", new Date(), new Date(), false);
		objectUnderTest.addSpeciesFileNameEntry(new SpeciesFileNameEntry(111l, "species.txt"));
		List<SpeciesFileNameEntry> result = objectUnderTest.getSpeciesFiles();
		assertThat(result.size(), is(1));
		assertThat(result.get(0).getId(), is(111l));
	}
	
	/**
	 * Does the addAll for species file name entries work?
	 */
	@Test
	public void testAddAllSpeciesFileNameEntry01() {
		SharedDatasetSummary objectUnderTest = new SharedDatasetSummary("123", "Some dataset", new Date(), new Date(), false);
		List<SpeciesFileNameEntry> listOfEntries = Arrays.asList(
				new SpeciesFileNameEntry(111l, "species.txt"), 
				new SpeciesFileNameEntry(222l, "species2.txt"));
		objectUnderTest.addAllSpeciesFileNameEntry(listOfEntries);
		List<SpeciesFileNameEntry> result = objectUnderTest.getSpeciesFiles();
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getId(), is(111l));
		assertThat(result.get(1).getId(), is(222l));
	}
	
	/**
	 * Does the addAll for species file name entries work with an empty list?
	 */
	@Test
	public void testAddAllSpeciesFileNameEntry02() {
		SharedDatasetSummary objectUnderTest = new SharedDatasetSummary("123", "Some dataset", new Date(), new Date(), false);
		List<SpeciesFileNameEntry> emptyList = Collections.emptyList();
		objectUnderTest.addAllSpeciesFileNameEntry(emptyList);
		List<SpeciesFileNameEntry> result = objectUnderTest.getSpeciesFiles();
		assertThat(result.size(), is(0));
	}
	
	/**
	 * Does the adder for links work?
	 */
	@Test
	public void testAddLink01() {
		SharedDatasetSummary objectUnderTest = new SharedDatasetSummary("222", "Some Dataset v2", new Date(), new Date(), false);
		String otherDatasetName = "Some Dataset v1";
		long otherDatasetId = 111l;
		String linkTypeTitle = "Is new version of";
		String linkDescription = "We added another year of data";
		DatasetLink link = new DatasetLink(otherDatasetName, otherDatasetId, linkTypeTitle, linkDescription, 1, new Date());
		objectUnderTest.addLink(link);
		List<DatasetLink> result = objectUnderTest.getLinks();
		assertThat(result.size(), is(1));
	}
	
	/**
	 * Does getLinks() not explode when there are no links?
	 */
	@Test
	public void testGetLinks01() {
		SharedDatasetSummary objectUnderTest = new SharedDatasetSummary("222", "Some Dataset v2", new Date(), new Date(), false);
		List<DatasetLink> result = objectUnderTest.getLinks();
		assertThat(result.size(), is(0));
	}
}
