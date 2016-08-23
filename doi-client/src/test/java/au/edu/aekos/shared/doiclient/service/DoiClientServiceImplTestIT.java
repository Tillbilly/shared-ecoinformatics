package au.edu.aekos.shared.doiclient.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.edu.aekos.shared.doiclient.jaxb.ContributorType;
import au.edu.aekos.shared.doiclient.jaxb.DateType;
import au.edu.aekos.shared.doiclient.jaxb.DescriptionType;
import au.edu.aekos.shared.doiclient.util.ResourceBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context.xml"})
public class DoiClientServiceImplTestIT {
	
	/**
	 * IMPORTANT: this must point to a URL that doesn't 404. The DOI minting service checks the
	 * passed URL before minting and if it doesn't exist, it refuses to mint. We could also
	 * use any other URL like google.com.au.
	 */
	public static final String URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS = "/dataset/38590";
	
	@Autowired
	DoiClientService objectUnderTest;
	
	/**
	 * Can we update a DOI?
	 */
	@Test
	public void testUpdateDoiRecord() throws DoiClientServiceException{
		ResourceBuilder builder = new ResourceBuilder();
		builder.addCreator("Till, Benjamin").setTitle("Test").setPublisher("AEKOS").setPublicationYear("2020")  
		       .addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON )
		       .addFormat(".accdb")
		       .addAlternateIdentifier("ALT_ID", "Internal ID")
		       .addDate(new Date(), DateType.CREATED)
		       .addDate(new Date(), DateType.START_DATE);
		String doi = objectUnderTest.mintDoi(builder.getResource(), URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
		assertNotNull(doi);
		
		//Lets try update
		builder.reset();
		builder.addCreator("Till, Benjamin").setTitle("Test Update").setPublisher("AEKOS").setPublicationYear("2021")  
		       .addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON )
		       .addFormat(".accdb")
		       .addAlternateIdentifier("ALT_ID", "Internal ID")
		       .addDate(new Date(), DateType.CREATED)
		       .addDate(new Date(), DateType.START_DATE)
		       .setDoi(doi);
		String result = objectUnderTest.updateDoiRecord(builder.getResource(), doi, URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
		assertThat(result, is(DoiClientService.UPDATE_SUCCESS_CODE));
	}
	
	/**
	 * Does DOI minting fail when we don't provide a creator?
	 */
	@Test
	public void testMintDoi01(){
		ResourceBuilder builder = new ResourceBuilder();
		builder.setTitle("Test").setPublisher("AEKOS").setPublicationYear("2020")  
		       .addContributor("Frederickson, Paul", ContributorType.CONTACT_PERSON )
		       .addFormat(".accdb")
		       .addAlternateIdentifier("ALT_ID", "Internal ID")
		       .addDate(new Date(), DateType.CREATED)
		       .addDate(new Date(), DateType.START_DATE);
		try {
			objectUnderTest.mintDoi(builder.getResource(), URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
			fail();
		} catch (DoiClientServiceException e) {
			// success!
		}
	}
	
	/**
	 * Can we mint a DOI when the description text doesn't have any special characters?
	 */
	@Test
	public void testMintDoi02() throws DoiClientServiceException{
		String textWithoutSpecialCharacters = "All about my dataset its totes wicked doods";
		ResourceBuilder builder = builderWithDescriptionAndTitle(textWithoutSpecialCharacters, "No special characters");
		String result = objectUnderTest.mintDoi(builder.getResource(), URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
		assertNotNull(result);
	}

	/**
	 * Can we mint a DOI when the description text has a UTF-8 single quote character?
	 */
	@Test  
	public void testMintDoi03() throws DoiClientServiceException{
		String textWithUTF8Char = "All about my " + DoiClientServiceImpl.UTF8_SINGLE_QUOTE + " dataset its totes wicked doods";
		ResourceBuilder builder = builderWithDescriptionAndTitle(textWithUTF8Char, "UTF-8 ' character");
		String result = objectUnderTest.mintDoi(builder.getResource(), URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
		assertNotNull(result);
	}
	
	/**
	 * Can we mint a DOI when the description text has a UTF-8 AE character?
	 */
	@Test  
	public void testMintDoi04() throws DoiClientServiceException{
		String textWithAEChar = "All about my " + DoiClientServiceImpl.UTF8_AE_CHAR + " dataset its totes wicked doods";
		ResourceBuilder builder = builderWithDescriptionAndTitle(textWithAEChar, "UTF-8 AE character");
		String result = objectUnderTest.mintDoi(builder.getResource(), URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
		assertNotNull(result);
	}
	
	/**
	 * Can we mint a DOI when the description text has an ampersand (&) character?
	 */
	@Test  
	public void testMintDoi05() throws DoiClientServiceException{
		String textWithAmpersandChar = "All about my dataset its & totes wicked doods";
		ResourceBuilder builder = builderWithDescriptionAndTitle(textWithAmpersandChar, "Ampersand character");
		String result = objectUnderTest.mintDoi(builder.getResource(), URL_SUFFIX_OF_DATASET_THAT_ACTUALLY_EXISTS);
		assertNotNull(result);
	}
	
	private ResourceBuilder builderWithDescriptionAndTitle(String desc, String title) {
		ResourceBuilder result = new ResourceBuilder();
		result
			.addCreator("Till, Benjamin")
			.setTitle(title)
			.setPublisher("AEKOS")
			.setPublicationYear("2009")
			.addSubject("501")
			.addDescription(desc, DescriptionType.ABSTRACT);
		return result;
	}
}
