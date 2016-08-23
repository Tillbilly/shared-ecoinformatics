package au.org.aekos.shared.api.model.dataset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class SharedMetaDataMapTest {

	/**
	 * Can we get the first entry for a given tag?
	 */
	@Test
	public void testGetFirstEntry01() {
		SharedMetaDataMap objectUnderTest = new SharedMetaDataMap();
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "wombat", "Wombat"));
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "kangaroo", "Kangaroo"));
		SharedResponse result = objectUnderTest.getFirstEntry("SHD.keyWords");
		assertThat(result.getCodeValue(), is("wombat"));
	}

	/**
	 * Is null returned when we ask for a meta tag that isn't there?
	 */
	@Test
	public void testGetFirstEntry02() {
		SharedMetaDataMap objectUnderTest = new SharedMetaDataMap();
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "wombat", "Wombat"));
		SharedResponse result = objectUnderTest.getFirstEntry("SHD.NotThere");
		assertNull(result);
	}
	
	/**
	 * Can we get all the entries for a given tag?
	 */
	@Test
	public void testGetEntries01() {
		SharedMetaDataMap objectUnderTest = new SharedMetaDataMap();
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "elephant", "Elephant"));
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "giraffe", "Giraffe"));
		List<SharedResponse> result = objectUnderTest.getEntries("SHD.keyWords");
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getCodeValue(), is("elephant"));
		assertThat(result.get(1).getCodeValue(), is("giraffe"));
	}
	
	/**
	 * Is an empty List returned when we ask for a meta tag that isn't there?
	 */
	@Test
	public void testGetEntries02() {
		SharedMetaDataMap objectUnderTest = new SharedMetaDataMap();
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "elephant", "Elephant"));
		List<SharedResponse> result = objectUnderTest.getEntries("SHD.NotThere");
		assertThat(result.size(), is(0));
	}

	/**
	 * Can we get all the entries for a given tag?
	 */
	@Test
	public void testDistinctFieldCount01() {
		SharedMetaDataMap objectUnderTest = new SharedMetaDataMap();
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "elephant", "Elephant"));
		objectUnderTest.put(new SharedResponse("SHD.keyWords", "giraffe", "Giraffe"));
		int result = objectUnderTest.getDistinctFieldCount();
		assertThat(result, is(1));
	}
}
