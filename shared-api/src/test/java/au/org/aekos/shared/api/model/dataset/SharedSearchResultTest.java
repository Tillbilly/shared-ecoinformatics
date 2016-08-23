package au.org.aekos.shared.api.model.dataset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SharedSearchResultTest {

	/**
	 * Can we tell when we DO have a license type?
	 */
	@Test
	public void testHasLicenseType01() {
		String validLicenseType = "TERN-BY 1.0";
		SharedSearchResult objectUnderTest = new SharedSearchResult("12", "/3a-tn.jpg", "/3a.jpg", 
				"Study", "huge trees", "POINT(125.5292969 -22.9353184)", true, validLicenseType);
		boolean result = objectUnderTest.hasLicenseType();
		assertThat(result, is(true));
	}

	/**
	 * Can we tell when we DO NOT have a license type due to a blank string?
	 */
	@Test
	public void testHasLicenseType02() {
		String blankLicenseType = "";
		SharedSearchResult objectUnderTest = new SharedSearchResult("12", "/3a-tn.jpg", "/3a.jpg", 
				"Study", "huge trees", "POINT(125.5292969 -22.9353184)", true, blankLicenseType);
		boolean result = objectUnderTest.hasLicenseType();
		assertThat(result, is(false));
	}
	
	/**
	 * Can we tell when we DO NOT have a license type due to a null?
	 */
	@Test
	public void testHasLicenseType03() {
		String nullLicenseType = null;
		SharedSearchResult objectUnderTest = new SharedSearchResult("12", "/3a-tn.jpg", "/3a.jpg", 
				"Study", "huge trees", "POINT(125.5292969 -22.9353184)", true, nullLicenseType);
		boolean result = objectUnderTest.hasLicenseType();
		assertThat(result, is(false));
	}
}
