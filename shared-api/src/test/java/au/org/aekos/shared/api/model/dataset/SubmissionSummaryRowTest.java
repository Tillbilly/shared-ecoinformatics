package au.org.aekos.shared.api.model.dataset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SubmissionSummaryRowTest {

	/**
	 * Can we tell when a row has a group?
	 */
	@Test
	public void testHasGroup01() {
		SubmissionSummaryRow objectUnderTest = new SubmissionSummaryRow("Section One", "Subsection", "Project Name", "Kick ass");
		objectUnderTest.setGroupName("some Group");
		boolean result = objectUnderTest.hasGroup();
		assertThat(result, is(true));
	}

	/**
	 * Can we tell when a row DOES NOT have a group? The group is null.
	 */
	@Test
	public void testHasGroup02() {
		SubmissionSummaryRow objectUnderTest = new SubmissionSummaryRow("Section One", "Subsection", "Project Name", "Kick ass");
		objectUnderTest.setGroupName(null);
		boolean result = objectUnderTest.hasGroup();
		assertThat(result, is(false));
	}
	
	/**
	 * Can we tell when a row DOES NOT have a group? The group is an empty string.
	 */
	@Test
	public void testHasGroup03() {
		SubmissionSummaryRow objectUnderTest = new SubmissionSummaryRow("Section One", "Subsection","Project Name", "Kick ass");
		objectUnderTest.setGroupName("");
		boolean result = objectUnderTest.hasGroup();
		assertThat(result, is(false));
	}
}
