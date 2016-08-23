package au.org.aekos.shared.api.model.search;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import au.org.aekos.shared.api.model.dataset.SharedSearchResult;

public class ResponseDatasetSearchTest {

	/**
	 * Does passing an exception indicate failure?
	 */
	@Test
	public void testConstructor01() {
		ResponseDatasetSearch objectUnderTest = new ResponseDatasetSearch(new RuntimeException());
		assertThat(objectUnderTest.isSuccess(), is(false));
	}

	/**
	 * Does passing all the required args indicate success?
	 */
	@Test
	public void testConstructor02() throws Exception {
		List<SharedSearchResult> datasets = Arrays.asList(new SharedSearchResult("123", "path/to/thumb.png", "path/to/image.png", 
				"Study of wombats", "We looked at a lot of wombats", "POINT(1,2)", true, "TERN-BY"));
		ResponseDatasetSearch objectUnderTest = new ResponseDatasetSearch(11L, 0L, datasets);
		assertThat(objectUnderTest.isSuccess(), is(true));
	}
	
	/**
	 * Can we tell when the result is NOT empty?
	 */
	@Test
	public void testIsEmpty01() throws Exception {
		List<SharedSearchResult> datasets = Arrays.asList(new SharedSearchResult("123", "path/to/thumb.png", "path/to/image.png", 
				"Study of wombats", "We looked at a lot of wombats", "POINT(1,2)", false, "CC-BY"));
		ResponseDatasetSearch objectUnderTest = new ResponseDatasetSearch(11L, 0L, datasets);
		assertThat(objectUnderTest.isEmpty(), is(false));
	}
	
	/**
	 * Can we tell when the result is empty?
	 */
	@Test
	public void testIsEmpty02() throws Exception {
		List<SharedSearchResult> emptyList = Collections.emptyList();
		ResponseDatasetSearch objectUnderTest = new ResponseDatasetSearch(11L, 0L, emptyList);
		assertThat(objectUnderTest.isEmpty(), is(true));
	}
}
