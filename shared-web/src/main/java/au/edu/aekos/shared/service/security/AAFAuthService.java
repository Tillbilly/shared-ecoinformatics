package au.edu.aekos.shared.service.security;

import com.nimbusds.jwt.SignedJWT;

public interface AAFAuthService {

    AAFUserDetails retrieveUserDetails(SignedJWT jwt) ;
    
    boolean doesUsernameExist(String username);
	
}
