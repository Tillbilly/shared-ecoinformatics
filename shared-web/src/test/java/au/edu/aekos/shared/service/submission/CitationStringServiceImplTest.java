package au.edu.aekos.shared.service.submission;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import au.edu.aekos.shared.service.citation.CitationDataProvider;
import au.edu.aekos.shared.service.citation.CitationStringServiceImpl;

public class CitationStringServiceImplTest {

	/**
	 * Is the web citation string fully populated when all values are supplied?
	 */
	@Test
	public void testAssembleCitationString01() throws ParseException {
		final List<String> authors = Arrays.asList(new String[] {"Aaronson, A.", "Bobson", "Carlson, C."});
		final String datasetPublicationYear = "2013";
		final String datasetVersion = "33-3";
		final String datasetNameForCitation = "Super Important Ecological Dataset";
		final List<String> legalContactOrgs = Arrays.asList(new String[] {"James Cook University", "University of Adelaide"});
		final String doi = "10.5072/05/53390E1055F91";
		final String accessDate = "31 Mar 2014";
		final String newLineFragment = "<br />";
		CitationDataProvider source = new CitationDataProvider(authors, datasetPublicationYear, datasetVersion, datasetNameForCitation, legalContactOrgs, doi, accessDate, newLineFragment);
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String result = objectUnderTest.assembleCitationString(source);
		String expected = "Aaronson, A., Bobson, Carlson, C. (2013). Super Important Ecological Dataset, Version 33-3.<br />"
				+ "http://doi.org/10.5072/05/53390E1055F91.<br />"
				+ "&AElig;KOS Data Portal, rights owned by James Cook University, University of Adelaide."
				+ "<br />Accessed 31 Mar 2014.";
		assertThat(result, is(expected));
	}

	/**
	 * Is the web citation string populated with substitutes when optional values aren't supplied?
	 */
	@Test
	public void testAssembleCitationString02() throws ParseException {
		final List<String> authors = Collections.emptyList();
		final String datasetPublicationYear = "";
		final String datasetVersion = "";
		final String datasetNameForCitation = "";
		final List<String> legalContactOrgs = Collections.emptyList();
		final String doi = "DOI";
		final String accessDate = "ACCESS_DATE";
		final String newLineFragment = "<br />";
		CitationDataProvider source = new CitationDataProvider(authors, datasetPublicationYear, datasetVersion, datasetNameForCitation, legalContactOrgs, doi, accessDate, newLineFragment);
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String result = objectUnderTest.assembleCitationString(source);
		assertEquals("AUTHORS (PUBLICATION_YEAR). DATASET_NAME.<br />"
				+ "http://doi.org/DOI.<br />"
				+ "&AElig;KOS Data Portal, rights owned by LEGAL_CONTACT_ORGANISATIONS.<br />"
				+ "Accessed ACCESS_DATE.", result);
	}
	
	/**
	 * Is the print citation string fully populated when all values are supplied?
	 */
	@Test
	public void testAssembleCitationStringForPrint01() throws ParseException {
		final List<String> authors = Arrays.asList(new String[] {"Aaronson, A.", "Bobson", "Carlson, C."});
		final String datasetPublicationYear = "2013";
		final String datasetVersion = "33-3";
		final String datasetNameForCitation = "Super Important Ecological Dataset";
		final List<String> legalContactOrgs = Arrays.asList(new String[] {"James Cook University", "University of Adelaide"});
		final String doi = "10.5072/05/53390E1055F91";
		final String accessDate = "31 Mar 2014";
		final String newLineFragment = "<br />";
		CitationDataProvider source = new CitationDataProvider(authors, datasetPublicationYear, datasetVersion, datasetNameForCitation, legalContactOrgs, doi, accessDate, newLineFragment);
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String result = objectUnderTest.assembleCitationStringForPrint(source);
		String expected = "Aaronson, A., Bobson, Carlson, C. (2013). Super Important Ecological Dataset, Version 33-3.<br />"
				+ "http://doi.org/10.5072/05/53390E1055F91.<br />"
				+ "\u00C6KOS Data Portal, rights owned by James Cook University, University of Adelaide."
				+ "<br />Accessed 31 Mar 2014.";
		assertThat(result, is(expected));
	}
	
	/**
	 * Is the RIF-CS citation string fully populated when all values are supplied?
	 */
	@Test
	public void testAssembleCitationStringForRifcs01() throws ParseException {
		final List<String> authors = Arrays.asList(new String[] {"Aaronson, A.", "Bobson", "Carlson, C."});
		final String datasetPublicationYear = "2013";
		final String datasetVersion = "33-3";
		final String datasetNameForCitation = "Super Important Ecological Dataset";
		final List<String> legalContactOrgs = Arrays.asList(new String[] {"James Cook University", "University of Adelaide"});
		final String doi = "10.5072/05/53390E1055F91";
		final String accessDate = "31 Mar 2014";
		final String newLineFragment = "<br />";
		CitationDataProvider source = new CitationDataProvider(authors, datasetPublicationYear, datasetVersion, datasetNameForCitation, legalContactOrgs, doi, accessDate, newLineFragment);
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String result = objectUnderTest.assembleCitationStringForRifcs(source);
		String expected = "Aaronson, A., Bobson, Carlson, C. (2013). Super Important Ecological Dataset, Version 33-3.<br />"
				+ "http://doi.org/10.5072/05/53390E1055F91.<br />"
				+ "ÆKOS Data Portal, rights owned by James Cook University, University of Adelaide."
				+ "<br />Accessed 31 Mar 2014.";
		assertThat(result, is(expected));
	}
	
	/**
	 * Can we build an access statement when contact email and license are both supplied?
	 */
	@Test
	public void testBuildAccessStatement01() throws ParseException {
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String datasetContactEmail = "test@blah.com";
		String license = "Creative Commons 4.0 Int";
		String ecoinfContactEmail = "ecoinf@blah.com";
		String result = objectUnderTest.buildAccessStatement(ecoinfContactEmail, datasetContactEmail, license);
		String expected = "These data can be freely downloaded via the Advanced Ecological Knowledge and Observation System (ÆKOS) Data Portal "
				+ "and used subject to the Creative Commons 4.0 Int. Attribution and citation is required as described under License and Citation. "
				+ "We ask you to send citations of publications arising from work that use these data to TERN Eco-informatics at "
				+ "datacited@aekos.org.au and citation and copies of publications to test@blah.com";
		assertThat(result, is(expected));
	}
	
	/**
	 * Are empty contact email and license params handled gracefully?
	 */
	@Test
	public void testBuildAccessStatement02() throws ParseException {
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String datasetContactEmail = "";
		String license = "";
		String ecoinfContactEmail = "";
		String result = objectUnderTest.buildAccessStatement(ecoinfContactEmail, datasetContactEmail, license);
		assertNull(result);
	}
	
	/**
	 * Can we build a rights statement when all details are supplied?
	 */
	@Test
	public void testBuildRightsStatement01() throws ParseException {
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String license = "Creative Commons 4.0 Int";
		String publicationYear = "1987";
		String legalOrg = "Super Big Corp";
		String result = objectUnderTest.buildRightsStatement(license, publicationYear, legalOrg);
		String expected = "(C)1987 Super Big Corp. Rights owned by Super Big Corp. Rights licensed subject to Creative Commons 4.0 Int.";
		assertThat(result, is(expected));
	}
	
	/**
	 * Are empty params handled gracefully?
	 */
	@Test
	public void testBuildRightsStatement02() throws ParseException {
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		String license = "Creative Commons 4.0 Int";
		String publicationYear = "1987";
		String legalOrg = "";
		String result = objectUnderTest.buildRightsStatement(license, publicationYear, legalOrg);
		assertNull(result);
	}
	
	/**
	 * Can we build a publisher string when we provide data?
	 */
	@Test
	public void testBuildPublisherStringForRifcs01() throws ParseException {
		CitationStringServiceImpl objectUnderTest = new CitationStringServiceImpl();
		List<String> legalContactOrgList = Arrays.asList("Org1", "Org2");
		String result = objectUnderTest.buildPublisherStringForRifcs(legalContactOrgList);
		assertThat(result, is("ÆKOS Data Portal, rights owned by Org1, Org2"));
	}
}
