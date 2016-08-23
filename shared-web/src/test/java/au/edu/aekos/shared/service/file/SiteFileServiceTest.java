package au.edu.aekos.shared.service.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import au.edu.aekos.shared.web.json.JsonSite;
import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;

public class SiteFileServiceTest {
	
	/**
	 * Can we handle an empty file?
	 */
	@Test
	public void testParseSiteFile01(){
		SiteFileServiceImpl objectUnderTest = new SiteFileServiceImpl();
		try {
			objectUnderTest.parseSiteFileToJson(new File(""),"EPSG:4283", true);
			Assert.fail();
		} catch (IOException e) {
			// Success!
		}
	}
	
	/**
	 * Can we handle a populated file?
	 */
	@Test
	public void testParseSiteFile02() throws IOException{
		SiteFileServiceImpl objectUnderTest = new SiteFileServiceImpl();
		Resource rs = new ClassPathResource("sites.csv");
		JsonUploadSiteFileResponse response = null;
		response = objectUnderTest.parseSiteFileToJson(rs.getFile(),"EPSG:4283", true);
		Assert.assertEquals(4, response.getSites().size());
	}
	
	@Test
	public void testPathNamesEtc() throws IOException{
		Resource rs = new ClassPathResource("sites.csv");
		File f = rs.getFile();
		System.out.println(f.getAbsolutePath());
		System.out.println(f.getCanonicalPath());
		System.out.println(f.getPath());
		String path = f.getPath();
		System.out.println(path.replaceAll(f.getName(), ""));
	}
	

	/**
	 * Can we parse a valid line?
	 */
	@Test
	public void testParseSiteFileLine01() {
		SiteFileServiceImpl objectUnderTest = new SiteFileServiceImpl();
		String line = "NTBHR001,137.34,-38.33,\"zone\",\"comment\"";
		JsonSite result = objectUnderTest.parseSiteFileLine(line);
		assertEquals("NTBHR001", result.getSiteId());
		assertEquals("137.34", result.getxCoord());
		assertEquals("-38.33", result.getyCoord());
		assertEquals("comment", result.getComments());
		assertEquals("zone", result.getZone());
	}
	
	/**
	 * Can we recognise an invalid line (too few fields)?
	 */
	@Test
	public void testParseSiteFileLine02() {
		SiteFileServiceImpl objectUnderTest = new SiteFileServiceImpl();
		String lineMissingTwoFields = "NTBHR001,137.34,-38.33";
		try {
			objectUnderTest.parseSiteFileLine(lineMissingTwoFields);
			fail();
		} catch (InvalidSiteFileFormatException e) {
			// Success!
		}
	}
	
	
	/**
	 * Can we recognise an invalid line (too many fields)?
	 */
	@Test
	public void testParseSiteFileLine04() {
		SiteFileServiceImpl objectUnderTest = new SiteFileServiceImpl();
		String linewithTwoExtraFields = "123,01/12/2003,NTBHR001,137.34,-38.33,\"\",\"\"";
		try {
			objectUnderTest.parseSiteFileLine(linewithTwoExtraFields);
			fail();
		} catch (InvalidSiteFileFormatException e) {
			// Success!
		}
	}
	
	@Test
	public void testBoundsValidation4283() throws IOException{
		SiteFileServiceImpl objectUnderTest = new SiteFileServiceImpl();
		Resource rs = new ClassPathResource("sites_oob4283.csv");
		JsonUploadSiteFileResponse response = null;
		response = objectUnderTest.parseSiteFileToJson(rs.getFile(),"EPSG:4283", true);
		Assert.assertNotNull( response.getError() );
		System.out.println(response.getError());
	}
	
	
}
