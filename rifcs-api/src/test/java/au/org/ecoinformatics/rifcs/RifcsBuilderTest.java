package au.org.ecoinformatics.rifcs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xmlunit.builder.Input;

import au.org.ecoinformatics.rifcs.vocabs.CollectionRelationTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.CollectionTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DatesTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DescriptionTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.LicenseTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.PhysicalAddressTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.SpatialTypeEnum;



public class RifcsBuilderTest {
	
	/**
	 * Can we build the XML for a fully populated document?
	 */
	@Test
	public void testGetXml01() throws Exception{
		String key = "myCollectionKey";
		String originatingSource = "http://hello.org";
		String group = "GROUP";
		Date dateModified = date("12-12-2012");
		Date dateAccessioned = date("10-10-2010");
		CollectionTypeEnum collectionType = CollectionTypeEnum.DATASET;
		
		RifcsBuilder objectUnderTest = RifcsBuilder.getCollectionBuilderInstance(key, originatingSource, group,
				RifcsUtils.convertDateToW3CDTF(dateModified), RifcsUtils.convertDateToW3CDTF(dateAccessioned), collectionType);
		assertNotNull(objectUnderTest);
		
		List<String> authorList = new ArrayList<String>();
		authorList.add("Author 1 String");
		authorList.add("Author 2 String");
		
		objectUnderTest
			.addCollectionIdentifier("aekos.org.au/collection/shared/58991")
            .addCollectionDates(DatesTypeEnum.DATE_SUBMITTED, "2010-10-20" )
            .addCollectionDates(DatesTypeEnum.AVAILABLE, "2010-10-21" )
            .addCollectionPrimaryName("Global Human Vital Rates")
            .addCollectionPhysicalAddress(PhysicalAddressTypeEnum.STREET_ADDRESS, "My House, In The Middle Of My Street", "0408899788", null)
            .addCollectionElectronicAddress(null, "http://www.google.com")
            .addCollectionTemporalCoverage("2011-10-01", "2012-12-12")
            .addCollectionSpatialCoverage(SpatialTypeEnum.TEXT, "Spatial Description")
            .addCollectionSpatialCoverage(SpatialTypeEnum.ISO19139DCMIBOX, "BBOX ME")
            .addCollectionRelatedObject("2345-33255-SOME-ID", CollectionRelationTypeEnum.IS_OWNED_BY)
            .addCollectionSubject("Subject1", "subject-type1")
            .addCollectionSubject("Subject2", "subject-type2")
            .addCollectionDescription("description blah blah", DescriptionTypeEnum.FULL)
            .addRelatedInfo("10.4227/05/53B1F1C02C8E7", "Digital Object Identifier", "Title of some other related stuff")
            .addCollectionRights("Rights Statement Yall", "The License String", LicenseTypeEnum.CC_BY, "Access Rights String")
            .addCollectionFullCitationString("This is the full citation String", null)
            .addCollectionCitationMetadata("MY-DOI", "My Dataset Title", "The Publisher", "v6.6.6", "2001-01-01", "2010-01-01", authorList);
		String result = objectUnderTest.getXml();
		assertThat(result, isIdenticalTo(Input.from(getExpectedText("/au/org/ecoinformatics/rifcs/RifcsBuilderTest-expectedOutput1.xml"))));
	}

	/**
	 * Is the version element omitted when we don't supply one?
	 */
	@Test
	public void testGetXml02() throws Exception{
		String key = "myCollectionKey";
		String originatingSource = "http://hello.org";
		String group = "GROUP";
		Date dateModified = date("12-12-2012");
		Date dateAccessioned = date("10-10-2010");
		CollectionTypeEnum collectionType = CollectionTypeEnum.DATASET;
		
		RifcsBuilder objectUnderTest = RifcsBuilder.getCollectionBuilderInstance(key, originatingSource, group,
				RifcsUtils.convertDateToW3CDTF(dateModified), RifcsUtils.convertDateToW3CDTF(dateAccessioned), collectionType);
		
		List<String> authorList = Arrays.asList(new String[] {"Auth1", "Auth2"});
		String version = null;
		objectUnderTest
            .addCollectionCitationMetadata("MY-DOI", "My Dataset Title", "The Publisher", version, "2001-01-01", "2010-01-01", authorList);
		String result = objectUnderTest.getXml();
		assertThat(result, isIdenticalTo(Input.from(getExpectedText("/au/org/ecoinformatics/rifcs/RifcsBuilderTest-expectedOutput2.xml"))));
	}
	
	private Date date(String dateString) throws ParseException {
		return new SimpleDateFormat("dd-mm-yyyy").parse(dateString);
	}

	private static String getExpectedText(String filename) throws IOException {
		InputStream is = RifcsBuilder.class.getResourceAsStream(filename);
		OutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		return baos.toString();
	}
}
