package au.org.aekos.shared.api.model.dataset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SharedResponseTest {

	/**
	 * Does the constructor work?
	 */
	@Test
	public void testConstructor01() {
		SharedResponse objectUnderTest = new SharedResponse("metaTag", "codeValue", "displayValue");
		assertThat(objectUnderTest.getMetaTag(), is("metaTag"));
		assertThat(objectUnderTest.getCodeValue(), is("codeValue"));
		assertThat(objectUnderTest.getDisplayValue(), is("displayValue"));
	}

	/**
	 * Can we tell when a response is NOT empty?
	 */
	@Test
	public void testIsEmpty01() {
		SharedResponse objectUnderTest = new SharedResponse("metaTag", "codeValue", "displayValue");
		assertThat(objectUnderTest.isEmpty(), is(false));
	}
	
	/**
	 * Can we tell when a response is empty due to a zero length string?
	 */
	@Test
	public void testIsEmpty02() {
		SharedResponse objectUnderTest = new SharedResponse("metaTag", "", "displayValue");
		assertThat(objectUnderTest.isEmpty(), is(true));
	}
	
	/**
	 * Can we tell when a response is empty due to a null string?
	 */
	@Test
	public void testIsEmpty03() {
		SharedResponse objectUnderTest = new SharedResponse("metaTag", null, "displayValue");
		assertThat(objectUnderTest.isEmpty(), is(true));
	}
}
