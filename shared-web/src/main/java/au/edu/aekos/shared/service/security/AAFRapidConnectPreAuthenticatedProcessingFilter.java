package au.edu.aekos.shared.service.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.StringUtils;

import com.nimbusds.jwt.SignedJWT;

public class AAFRapidConnectPreAuthenticatedProcessingFilter extends
		AbstractPreAuthenticatedProcessingFilter {
	
	private JWTValidationService jwtValidationService;
	
	private AAFAuthService aafAuthService;
	
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		if(!"POST".equals(request.getMethod())){
			return null;
		}
		String assertion = request.getParameter("assertion");
		if(!StringUtils.hasLength(assertion)){
			return null;
		}
		SignedJWT jws;
		try {
			jws = JWTHandlerUtil.parseToken(assertion);
		} catch (JWTHandlerException e1) {
			logger.error("SignedJWT unable to be parsed from token " + assertion, e1);
			return null;
		}
		
		if(! jwtValidationService.verifyTokenSignature(jws) 
				|| ! jwtValidationService.validateJWTClaims(jws) ){
			return null;
		}
		return aafAuthService.retrieveUserDetails(jws);
	}

	
	//Hmmmm we don't really get token credentials??  
	//Perhaps just put the assertion String in?  Or a non-null String
	//The PreAuthenticatedAuthenticationProvider checks for non-null credentials,
	//So if assertion exists in the request, return that. ( non null credentials mandatory, even if we don't check them )
	@Override   
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		if(!"POST".equals(request.getMethod())){
			return null;
		}
		return request.getParameter("assertion");
	}

	public JWTValidationService getJwtValidationService() {
		return jwtValidationService;
	}


	public void setJwtValidationService(
			JWTValidationService jwtValidationService) {
		this.jwtValidationService = jwtValidationService;
	}

	public AAFAuthService getAafAuthService() {
		return aafAuthService;
	}

	public void setAafAuthService(AAFAuthService aafAuthService) {
		this.aafAuthService = aafAuthService;
	}

}
