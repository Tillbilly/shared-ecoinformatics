package au.edu.aekos.shared.service.security;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class AAFPreAuthUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>{

	private AAFAuthService aafAuthService;
	
	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
		AAFUserDetails principal = (AAFUserDetails) token.getPrincipal();
		//Check the username exists in out system, if it does, return principal OR create a new user details??
		if( ! aafAuthService.doesUsernameExist( principal.getUsername() ) ){
			throw new UsernameNotFoundException("Username " + principal.getUsername() + " not found.");
		}
		
		return principal;
	}

	public AAFAuthService getAafAuthService() {
		return aafAuthService;
	}

	public void setAafAuthService(AAFAuthService aafAuthService) {
		this.aafAuthService = aafAuthService;
	}

}
