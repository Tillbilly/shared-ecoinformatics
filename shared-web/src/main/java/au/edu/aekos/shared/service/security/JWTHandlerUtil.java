package au.edu.aekos.shared.service.security;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

public class JWTHandlerUtil {
	
	public static Logger logger = LoggerFactory.getLogger(JWTHandlerUtil.class);
	
	//private String secretKey = ""; // = "jwLbkBM.,B%f~~1YXOo2lAFs9TNyVu_L";

	public static SignedJWT parseToken(String assertion) throws JWTHandlerException {
		SignedJWT jws = null;
		try {
			jws = SignedJWT.parse(assertion);
		} catch (ParseException e) {
			logger.error("Error occured parsing assertion:" + assertion, e);
			throw new JWTHandlerException("Error occured parsing assertion:" + assertion, e );
		}
		return jws;
	}
	
	public static boolean verifySignedJWT(SignedJWT jws, String secretKey) throws JWTHandlerException {
        JWSVerifier verifier = new MACVerifier(secretKey);
		try {
			return jws.verify(verifier);
		} catch (JOSEException e) {
			logger.error("Signature verification exception", e);
			throw new JWTHandlerException("Signature verification exception", e);
		}
	}

}
