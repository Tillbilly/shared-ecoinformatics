package au.edu.aekos.shared.service.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;

public class HmacSHA512Util {
	private static final String ENCODING_FOR_ENCRYPTION = "UTF-8";
	private static final String ALGORITHM = "HmacSHA512";
	
	public static String generateSaltedHmacSHA512Key(String password, String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
		SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(ENCODING_FOR_ENCRYPTION),ALGORITHM);
		Mac mac = Mac.getInstance(ALGORITHM);
		mac.init(secretKey);
        byte [] hmacData = mac.doFinal(salt.getBytes(ENCODING_FOR_ENCRYPTION));
        String key = new String(Base64.encode(hmacData), ENCODING_FOR_ENCRYPTION);
		return key;
	}
	
	
}
