package au.edu.aekos.shared.service.security;

import com.nimbusds.jwt.SignedJWT;

public interface JWTValidationService {
	
	boolean verifyTokenSignature(SignedJWT jwt);

	boolean validateJWTClaims(SignedJWT jwt);
	
}
