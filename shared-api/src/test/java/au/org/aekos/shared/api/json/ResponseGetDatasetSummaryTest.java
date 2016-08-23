package au.org.aekos.shared.api.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class ResponseGetDatasetSummaryTest {

	/**
	 * Can we build a new instance that indicates failure?
	 */
	@Test
	public void testNewInstance01() {
		ResponseGetDatasetSummary result = ResponseGetDatasetSummary.newInstanceFailure("KA_BOOM!");
		assertThat(result.isSuccess(), is(false));
	}

	/**
	 * Can we build a new instance that indicates success with a published dataset?
	 */
	@Test
	public void testNewInstance02() {
		SharedDatasetSummary source = new SharedDatasetSummary("123", "some title", new Date(), new Date(),false);
		ResponseGetDatasetSummary result = ResponseGetDatasetSummary.newInstanceSuccess(source);
		assertThat(result.isSuccess(), is(true));
	}
	
	/**
	 * Can we build a new instance that indicates success with a pending publish dataset?
	 */
	@Test
	public void testNewInstance03() {
		SharedDatasetSummary source = new SharedDatasetSummary("123", "some title", new Date(), new Date(),false);
		ResponseGetDatasetSummary result = ResponseGetDatasetSummary.newInstancePendingPublish(source);
		assertThat(result.isSuccess(), is(true));
	}
	
	/**
	 * Can we correctly identify approved datasets as pending publish?
	 */
	@Test
	public void testsPendingPublish01() {
		ResponseGetDatasetSummary objectUnderTest = ResponseGetDatasetSummary.newInstancePendingPublish(
				new SharedDatasetSummary("1", "x", new Date(), new Date(), false));
		boolean result = objectUnderTest.isPendingPublish();
		assertTrue("Approved datasets should be considered pending publish", result);
	}
	
	/**
	 * Can we correctly identify published datasets as NOT pending publish?
	 */
	@Test
	public void testsPendingPublish02() {
		ResponseGetDatasetSummary objectUnderTest = ResponseGetDatasetSummary.newInstanceSuccess(
				new SharedDatasetSummary("1", "x", new Date(), new Date(), false));
		boolean result = objectUnderTest.isPendingPublish();
		assertFalse("Published datasets should NOT be considered pending publish", result);
	}
}
