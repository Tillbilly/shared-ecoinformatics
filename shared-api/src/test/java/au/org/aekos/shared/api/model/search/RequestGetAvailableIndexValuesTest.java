package au.org.aekos.shared.api.model.search;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RequestGetAvailableIndexValuesTest {

	/**
	 * Can we tell when the search string is supplied
	 */
	@Test
	public void testIsSearchStringSupplied01() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("title", "u", 0, 10);
		boolean result = objectUnderTest.isSearchStringSupplied();
		assertThat(result, is(true));
	}

	/**
	 * Can we tell when the search string is NOT supplied
	 */
	@Test
	public void testIsSearchStringSupplied02() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("title", " ", 0, 10);
		boolean result = objectUnderTest.isSearchStringSupplied();
		assertThat(result, is(false));
	}
	
	/**
	 * Can we tell when the search string is NOT supplied
	 */
	@Test
	public void testIsSearchStringSupplied03() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("title", RequestGetAvailableIndexValues.SEARCH_FOR_ALL_VALUES, 0, 10);
		boolean result = objectUnderTest.isSearchStringSupplied();
		assertThat(result, is(false));
	}
	
	/**
	 * Can we determine the facet field name for a _ft suffix?
	 */
	@Test
	public void testGetFacetFieldName01() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("datasetFormalName_ft", "u", 0, 10);
		String result = objectUnderTest.getFacetFieldName();
		assertThat(result, is("datasetFormalName_facet_s"));
	}
	
	/**
	 * Can we determine the facet field name for a _stxt suffix?
	 */
	@Test
	public void testGetFacetFieldName02() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("ecologicalTheme_stxt", "u", 0, 10);
		String result = objectUnderTest.getFacetFieldName();
		assertThat(result, is("ecologicalTheme_facet_ss"));
	}
	
	/**
	 * Can we fail gracefully for an unmapped suffix?
	 */
	@Test
	public void testGetFacetFieldName03() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("ecologicalTheme_wtf", "u", 0, 10);
		try {
			objectUnderTest.getFacetFieldName();
			fail();
		} catch (RuntimeException e) {
			// success!
		}
	}
	
	/**
	 * Can we determine the facet field name for a _ftxt suffix?
	 */
	@Test
	public void testGetFacetFieldName04() {
		RequestGetAvailableIndexValues objectUnderTest = new RequestGetAvailableIndexValues("author_ftxt", "u", 0, 10);
		String result = objectUnderTest.getFacetFieldName();
		assertThat(result, is("author_facet_ss"));
	}
	
	/**
	 * Can we tell when a field name is valid for SHaRED?
	 */
	@Test
	public void testIsValidSharedColumnName01() {
		assertTrue(RequestGetAvailableIndexValues.isValidSharedColumnName("ecologicalTheme_stxt"));
		assertTrue(RequestGetAvailableIndexValues.isValidSharedColumnName("author_ftxt"));
		assertTrue(RequestGetAvailableIndexValues.isValidSharedColumnName("datasetFormalName_ft"));
		assertTrue(RequestGetAvailableIndexValues.isValidSharedColumnName("text"));
		assertTrue(RequestGetAvailableIndexValues.isValidSharedColumnName("wktConvexHull"));
	}
	
	/**
	 * Can we tell when a field name is NOT valid for SHaRED?
	 */
	@Test
	public void testIsValidSharedColumnName02() {
		assertFalse(RequestGetAvailableIndexValues.isValidSharedColumnName("ecologicalTheme_wtf"));
	}
}
