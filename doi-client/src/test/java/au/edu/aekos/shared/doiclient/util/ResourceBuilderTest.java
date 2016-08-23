package au.edu.aekos.shared.doiclient.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import au.edu.aekos.shared.doiclient.jaxb.ContributorType;
import au.edu.aekos.shared.doiclient.jaxb.DateType;
import au.edu.aekos.shared.doiclient.jaxb.DescriptionType;
import au.edu.aekos.shared.doiclient.jaxb.RelatedIdentifierType;
import au.edu.aekos.shared.doiclient.jaxb.RelationType;
import au.edu.aekos.shared.doiclient.jaxb.Resource;
import au.edu.aekos.shared.doiclient.jaxb.ResourceType;

public class ResourceBuilderTest {

	private static Marshaller m = getMarshaller();
	
	/**
	 * Can we build a basic resource with a contributor?
	 */
	//@Test
	public void testGetResource01() throws Exception{
		ResourceBuilder builder = new ResourceBuilder();
		builder.addCreator("Till, Benjamin").setTitle("Test").setPublisher("AEKOS");
		builder.addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON );
		Resource r = builder.getResource();
	    StringWriter sw = new StringWriter();
	    m.marshal(r, sw);
	    assertEquals(getExpectedText("/au/edu/aekos/shared/doiclient/ResourceBuilder-expectedOutput1.xml"), sw.toString());
	}
	
	/**
	 * Can we build a resource with descriptions?
	 */
	@SuppressWarnings("deprecation")
	//@Test
	public void testGetResource02() throws Exception{
		ResourceBuilder builder = new ResourceBuilder();
		builder.addCreator("Till, Benjamin").setTitle("Test").setPublisher("AEKOS");
		builder.addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON );
		List<String> descLines = new ArrayList<String>();
		descLines.add("description line 1");
		descLines.add("description line 2");
		builder.addDescription(descLines, DescriptionType.ABSTRACT);
		Resource r = builder.getResource();
	    StringWriter sw = new StringWriter();
	    m.marshal(r, sw);
	    assertEquals(getExpectedText("/au/edu/aekos/shared/doiclient/ResourceBuilder-expectedOutput2.xml"), sw.toString());
	}
	
	/**
	 * Can we build a resource with formats?
	 */
	//@Test
	public void testGetResource03() throws Exception{
		ResourceBuilder builder = new ResourceBuilder();
		builder.addCreator("Till, Benjamin").setTitle("Test").setPublisher("AEKOS");
		builder.addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON );
		builder.addFormat(".accdb");
		Resource r = builder.getResource();
	    StringWriter sw = new StringWriter();
	    m.marshal(r, sw);
	    assertEquals(getExpectedText("/au/edu/aekos/shared/doiclient/ResourceBuilder-expectedOutput3.xml"), sw.toString());
	}
	
	/**
	 * Can we build a resource with everything?
	 */
	//@Test
	public void testGetResource04() throws Exception{
		ResourceBuilder builder = new ResourceBuilder();
		builder.addCreator("Till, Benjamin").setTitle("Test").setPublisher("AEKOS").setPublicationYear("2020")  
		       .addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON )
		       .addFormat(".accdb")
		       .addAlternateIdentifier("ALT_ID", "Internal ID")
		       .addDate(date("2016-01-14"), DateType.CREATED).addDate(date("2016-01-14"), DateType.START_DATE);
		builder.addDescription("description line 1", DescriptionType.ABSTRACT)
		       .addLanguage("English")
		       .addRelatedIdentifier("123", RelatedIdentifierType.URN, RelationType.IS_NEW_VERSION_OF)
		       .addResourceType("database", ResourceType.DATASET)
		       .addSubject("Ecology").addSubject("Climate change")
		       .setVersion("0.0.1");
        Resource r = builder.getResource();
	    StringWriter sw = new StringWriter();
	    m.marshal(r, sw);
		assertEquals(getExpectedText("/au/edu/aekos/shared/doiclient/ResourceBuilder-expectedOutput4.xml"), sw.toString());
	}
	
	private Date date(String dateString) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
	}

	private static String getExpectedText(String filename) throws IOException {
		InputStream is = ResourceBuilder.class.getResourceAsStream(filename);
		OutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		return baos.toString();
	}
	
	private static Marshaller getMarshaller() {
		try {
			JAXBContext context = JAXBContext.newInstance(Resource.class);
			Marshaller result = context.createMarshaller();
			result.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			result.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-2.1 http://schema.datacite.org/meta/kernel-2.1/metadata.xsd");
			return result;
		} catch (PropertyException e) {
			throw new RuntimeException("Failed to create the marshaller", e);
		} catch (JAXBException e) {
			throw new RuntimeException("Failed to create the marshaller", e);
		}
	}
}
