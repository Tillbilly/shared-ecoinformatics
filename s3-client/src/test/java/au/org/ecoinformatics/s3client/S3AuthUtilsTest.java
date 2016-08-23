package au.org.ecoinformatics.s3client;

import static org.junit.Assert.assertEquals;

import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class S3AuthUtilsTest {

	private static final String SECRET_KEY = "4b834e1a23d84be2a09a19cc6e32ad37";
	
	/**
	 * Can we construct an auth key?
	 */
	@Test
	public void testCalculateRFC2104HMAC01() throws SignatureException{
		String result = S3AuthUtils.calculateRFC2104HMAC("blerbler", SECRET_KEY);
		assertEquals("gEoTeVFLAPJTpa2/W4WMG6ADfEE=", result);
	}
	
	/**
	 * Can we construct a date string from a date?
	 */
	@Test
	public void testConstructDateString01() throws ParseException{
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = isoFormat.parse("2012-12-23T11:59:33");
		String result = S3AuthUtils.constructDateString(date);
		assertEquals("Sun, 23 Dec 2012 11:59:33 GMT", result);
	}
}
