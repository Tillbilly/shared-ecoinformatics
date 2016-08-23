package au.edu.aekos.shared.service.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import au.edu.aekos.shared.data.entity.SubmissionLinkType;

public class InterimDatasetLinkTest {

	private static final boolean SOURCE = true;
	private static final boolean TARGET = false;
	
	/**
	 * Do links sort in the expected order (pointers to newer versions first, then by type and lastly by date)?
	 */
	@Test
	public void testCompareTo01() {
		List<InterimDatasetLink> links = new ArrayList<InterimDatasetLink>();
		links.add(anInterimDatasetLink(SubmissionLinkType.RELATED, "oldest related", theDate("07/07/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.IS_NEW_VERSION_OF, "is new version (target) older", theDate("01/01/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.IS_NEW_VERSION_OF, "is new version (target) newer", theDate("09/09/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.IS_NEW_VERSION_OF, "is new version (source) newer", theDate("09/09/2010"), SOURCE));
		links.add(anInterimDatasetLink(SubmissionLinkType.IS_NEW_VERSION_OF, "is new version (source) older", theDate("01/01/2010"), SOURCE));
		links.add(anInterimDatasetLink(SubmissionLinkType.RELATED, "middle related", theDate("08/08/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.HAS_NEW_VERSION, "has new version (source) newer", theDate("09/09/2010"), SOURCE));
		links.add(anInterimDatasetLink(SubmissionLinkType.HAS_NEW_VERSION, "has new version (source) older", theDate("01/01/2010"), SOURCE));
		links.add(anInterimDatasetLink(SubmissionLinkType.LINKED, "linked older", theDate("01/01/2010"), SOURCE));
		links.add(anInterimDatasetLink(SubmissionLinkType.LINKED, "linked newer", theDate("09/09/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.HAS_NEW_VERSION, "has new version (target) newer", theDate("09/09/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.HAS_NEW_VERSION, "has new version (target) older", theDate("01/01/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.RELATED, "newest related", theDate("09/09/2010"), TARGET));
		Collections.sort(links);
		assertThat(links.get(0).getLinkDescription(), is("has new version (source) newer"));
		assertThat(links.get(1).getLinkDescription(), is("has new version (source) older"));
		assertThat(links.get(2).getLinkDescription(), is("is new version (target) newer"));
		assertThat(links.get(3).getLinkDescription(), is("is new version (target) older"));
		assertThat(links.get(4).getLinkDescription(), is("has new version (target) newer"));
		assertThat(links.get(5).getLinkDescription(), is("has new version (target) older"));
		assertThat(links.get(6).getLinkDescription(), is("is new version (source) newer"));
		assertThat(links.get(7).getLinkDescription(), is("is new version (source) older"));
		assertThat(links.get(8).getLinkDescription(), is("linked newer"));
		assertThat(links.get(9).getLinkDescription(), is("linked older"));
		assertThat(links.get(10).getLinkDescription(), is("newest related"));
		assertThat(links.get(11).getLinkDescription(), is("middle related"));
		assertThat(links.get(12).getLinkDescription(), is("oldest related"));
	}
	
	/**
	 * Does a HAS_NEW_VERSION (source) link sort before an IS_NEW_VERSION_OF (source)? i.e. the HAS_NEW_VERSION points to the newer version
	 */
	@Test
	public void testCompareTo02() {
		List<InterimDatasetLink> links = new ArrayList<InterimDatasetLink>();
		links.add(anInterimDatasetLink(SubmissionLinkType.HAS_NEW_VERSION, "has new version (source)", theDate("02/02/2010"), SOURCE));
		links.add(anInterimDatasetLink(SubmissionLinkType.IS_NEW_VERSION_OF, "is new version of (source)", theDate("01/01/2010"), SOURCE));
		Collections.sort(links);
		assertThat(links.get(0).getLinkDescription(), is("has new version (source)"));
		assertThat(links.get(1).getLinkDescription(), is("is new version of (source)"));
	}
	
	/**
	 * Does an IS_NEW_VERSION_OF (target) link sort before a HAS_NEW_VERSION (target)? i.e. the IS_NEW_VERSION points to the newer version
	 */
	@Test
	public void testCompareTo03() {
		List<InterimDatasetLink> links = new ArrayList<InterimDatasetLink>();
		links.add(anInterimDatasetLink(SubmissionLinkType.HAS_NEW_VERSION, "has new version (target)", theDate("02/02/2010"), TARGET));
		links.add(anInterimDatasetLink(SubmissionLinkType.IS_NEW_VERSION_OF, "is new version of (target)", theDate("01/01/2010"), TARGET));
		Collections.sort(links);
		assertThat(links.get(0).getLinkDescription(), is("is new version of (target)"));
		assertThat(links.get(1).getLinkDescription(), is("has new version (target)"));
	}
	
	private InterimDatasetLink anInterimDatasetLink(SubmissionLinkType linkType, String description, Date theDate, boolean isSource) {
		// some stuff just isn't relevant for this test
		return new InterimDatasetLink("not important", 4, linkType, description, theDate, isSource);
	}


	private static Date theDate(String dateString) {
		String format = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			String msg = String.format("Couldn't parse: %s using format: %s", dateString, format);
			throw new IllegalArgumentException(msg, e);
		}
	}
}
