package au.org.ecoinformatics.s3client;

import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.iharder.Base64;

/**
 * Used to set up standard headers for the s3 requests
 * @author btill
 */
public class S3AuthUtils {
	
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	public static String constructDateString(Date d) throws ParseException{
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return  dateFormatGmt.format(d) + " GMT" ;
	}
	//GET\n\n\nFri, 14 Dec 2012 01:53:41 GMT\n/shared_submissions
	public static String canonicaliseRequestHeaders(String restVERB, String dateString, String resource){
		return restVERB + "\n" + "\n" + "\n" + dateString + "\n" +  resource;
	}
	
	public static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException{
		String result;
		try {
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = Base64.encodeBytes(rawHmac);
			//result = Encoding.EncodeBase64(rawHmac);
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}
}
