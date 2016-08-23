package au.edu.aekos.shared.service.security;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;
//import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.codec.Base64;


@Deprecated //Couldn't get Java encryption to give the same result as CryptoJS when the username/password contained
//non word characters. Using JS only password encryption.
public class KeyEncodingForPasswordTest {

	private static final String ENCODING_FOR_ENCRYPTION = "UTF-8";

	private static final String ALGORITHM = "HmacSHA512";
	
	//This is the key generated from the javascript hmacsha512 base 64 key generation algorithm,
	//We'd like to achieve the same thing with java cryptography API
	String sharedKey = "mg7BcRgRuJiuNTlbiu4jXICg+KxDMbfmzimj5ZrJDClaJZuq2h5kfuYwqXo/SwV2FEw8ZFvhN8EsVp9VKPr1+g==";
	String username = "shared";
	String password = "shared";
	
	@Test
	public void testKeyGenerate() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
		
		SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(ENCODING_FOR_ENCRYPTION),ALGORITHM);
		Mac mac = Mac.getInstance(ALGORITHM);
		mac.init(secretKey);
        byte [] hmacData = mac.doFinal(username.getBytes(ENCODING_FOR_ENCRYPTION));
        String key = new String(Base64.encode(hmacData), ENCODING_FOR_ENCRYPTION);
        assertEquals(sharedKey, key);
	}
	
	@Test
	public void testHmacSHA512Util() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException{
		assertEquals(sharedKey,HmacSHA512Util.generateSaltedHmacSHA512Key(password, username) );
	}
	
	@Test
    public void testGenerateKey() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
    	String user = "fred.pokemon";
    	String pass = "benjamin.till";
		SecretKeySpec secretKey = new SecretKeySpec(pass.getBytes(ENCODING_FOR_ENCRYPTION),ALGORITHM);
		Mac mac = Mac.getInstance(ALGORITHM);
		mac.init(secretKey);
        byte [] hmacData = mac.doFinal(user.getBytes(ENCODING_FOR_ENCRYPTION));
        String key = new String(Base64.encode(hmacData), ENCODING_FOR_ENCRYPTION);
        assertEquals("LSy6czZ4d7RNDlPkBGE/u1DH6fb1qSXtm3BsTR9LPyFtgRetsGpzgNkfncBQnkjNcGqNUMsRxeRIqs963bRUHA==", key);
        
        //"tD1iqLeTQIr1aClnUJVaV7UPjpopcjxQRRMzosOcYji1PQeInup7NoI72YG5tZx5PhBEOfwzJ2g64Qxp88cqKA=="
	}
    
	@Test
    public void testRetrieveFirstPartOfEmailAddress(){
		String emailAddress = "ben.till@myemail.com";
		
        String password = emailAddress.substring(0, emailAddress.indexOf("@"));
        assertEquals("ben.till", password);
	}
}
