package au.org.ecoinformatics.rifcs;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class RifcsUtilsTest {

	/**
	 * Can we convert a date to the expected String?
	 */
	@Test
	public void testConvertDateToW3CDTF01() throws Throwable {
		String source = "2013-02-05";
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(source);
		String result = RifcsUtils.convertDateToW3CDTF(date);
		assertEquals(source, result);
	}

	/**
	 * Can we convert a date to the expected String?
	 */
	@Test
	public void testConvertDateToW3CDTFYearOnly01() throws Throwable {
		String source = "2013-02-05";
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(source);
		String result = RifcsUtils.convertDateToW3CDTFYearOnly(date);
		assertEquals("2013", result);
	}
	
	/**
	 * Can we handle input that contains cleanse-able characters?
	 */
	@Test
	public void testAsciiCleanse01() throws Throwable {
		String inputString = "blah blah magic charcters!!! ÆKOS \u0092something else\u0092";
		String result = RifcsUtils.asciiCleanse(inputString);
		assertEquals("blah blah magic charcters!!! AEKOS 'something else'", result);
	}
	
	/**
	 * Can we handle input that doesn't contain cleanse-able characters?
	 */
	@Test
	public void testAsciiCleanse02() throws Throwable {
		String inputString = "blah blah something else";
		String result = RifcsUtils.asciiCleanse(inputString);
		assertEquals("blah blah something else", result);
	}
	
	/**
	 * Can we handle a zero length string?
	 */
	@Test
	public void testAsciiCleanse03() throws Throwable {
		String inputString = "";
		String result = RifcsUtils.asciiCleanse(inputString);
		assertEquals("", result);
	}
}
