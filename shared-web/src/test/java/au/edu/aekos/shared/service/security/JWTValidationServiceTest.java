package au.edu.aekos.shared.service.security;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class JWTValidationServiceTest {
   
	public String assertion = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3JhcGlkLnRlc3QuYWFmLmVkdS5hdSIsImlhdCI6MTM5MjAwODE2NSwianRpIjoiUFJ3OWE0ZTEyZUdBS0JSME1YVFg1Nm1rRWE2dlYyZmMiLCJuYmYiOjEzOTIwMDgxMDUsImV4cCI6MTM5MjAwODI4NSwidHlwIjoiYXV0aG5yZXNwb25zZSIsImF1ZCI6Imh0dHA6Ly9zaGFyZWQtZGV2LmVjb2luZm9ybWF0aWNzLm9yZy5hdTo4MDgwL3NoYXJlZC13ZWIiLCJzdWIiOiJodHRwczovL3JhcGlkLnRlc3QuYWFmLmVkdS5hdSFodHRwOi8vc2hhcmVkLWRldi5lY29pbmZvcm1hdGljcy5vcmcuYXU6ODA4MC9zaGFyZWQtd2ViIW5LbDdvNS9wcUNPTjNuVUs1WG5yVGFzd3k4QT0iLCJodHRwczovL2FhZi5lZHUuYXUvYXR0cmlidXRlcyI6eyJjbiI6IkJlbmphbWluIFRpbGwiLCJtYWlsIjoiYmVuamFtaW4udGlsbEBhZGVsYWlkZS5lZHUuYXUiLCJkaXNwbGF5bmFtZSI6IkJlbmphbWluIFRpbGwiLCJnaXZlbm5hbWUiOiIiLCJzdXJuYW1lIjoiIiwiZWR1cGVyc29udGFyZ2V0ZWRpZCI6Imh0dHBzOi8vcmFwaWQudGVzdC5hYWYuZWR1LmF1IWh0dHA6Ly9zaGFyZWQtZGV2LmVjb2luZm9ybWF0aWNzLm9yZy5hdTo4MDgwL3NoYXJlZC13ZWIhbktsN281L3BxQ09OM25VSzVYbnJUYXN3eThBPSIsImVkdXBlcnNvbnNjb3BlZGFmZmlsaWF0aW9uIjoibWVtYmVyQHZoby50ZXN0LmFhZi5lZHUuYXUiLCJlZHVwZXJzb25wcmluY2lwYWxuYW1lIjoiIn19.-ZZuXbk2EJR6NtBOJhUH05e8EV-wLZXmF14ePRpGDW4";
	private String secretKey = "jwLbkBM.,B%f~~1YXOo2lAFs9TNyVu_L";
	
	@Test  //i.e. to stop a replay attack
	public void testJtiValidation() throws JWTHandlerException{
		JWTValidationServiceImpl authService = new JWTValidationServiceImpl();
		authService.setValidateAud(false);
		authService.setValidateNbf(false);
		authService.setValidateExp(false);
		SignedJWT signedJWT = JWTHandlerUtil.parseToken(assertion);
		if(! JWTHandlerUtil.verifySignedJWT(signedJWT, secretKey) ){
			Assert.fail();
		}
		Assert.assertTrue( authService.validateJWTClaims(signedJWT) );
		Assert.assertFalse( authService.validateJWTClaims(signedJWT) );
	}
	
	@Test  //The test token should have aud of   [http://shared-dev.ecoinformatics.org.au:8080/shared-web]
	public void testAudValidation() throws JWTHandlerException{
		JWTValidationServiceImpl authService = new JWTValidationServiceImpl();
		authService.setValidateJti(false);
		authService.setValidateNbf(false);
		authService.setValidateExp(false);
		authService.getAudAllowedLocation().add("shared-dev.ecoinformatics.org.au");
		
		SignedJWT signedJWT = JWTHandlerUtil.parseToken(assertion);
		if(! JWTHandlerUtil.verifySignedJWT(signedJWT, secretKey) ){
			Assert.fail();
		}
		Assert.assertTrue( authService.validateJWTClaims(signedJWT) );
		
		authService.getAudAllowedLocation().clear();
		authService.getAudAllowedLocation().add("aekos.org.au");
		Assert.assertFalse( authService.validateJWTClaims(signedJWT) );
		
		authService.getAudAllowedLocation().add("shared-dev.ecoinformatics.org.au");
		Assert.assertTrue( authService.validateJWTClaims(signedJWT) );
		
		authService.getAudAllowedLocation().clear();
		authService.getAudAllowedLocation().add("shared-dev.ecoinformatics.org.au");
		authService.setAudWebAppName("google-apps");
		Assert.assertFalse( authService.validateJWTClaims(signedJWT) );
	}
	
	@Test
	public void testNbfValidation() throws JWTHandlerException, InterruptedException{
		JWTValidationServiceImpl authService = new JWTValidationServiceImpl();
		authService.setValidateJti(false);
		authService.setValidateAud(false);
		authService.setValidateExp(false);
		
		JWTClaimsSet claimsSet = new JWTClaimsSet();
		claimsSet.setNotBeforeTime(new Date());
		Thread.sleep(1000);
		Assert.assertTrue(authService.validateNbfClaim(claimsSet) );
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MINUTE, 1);
		claimsSet.setNotBeforeTime(cal.getTime());
		Assert.assertFalse(authService.validateNbfClaim(claimsSet) );
	}
	
	@Test
    public void testExpValidation() throws JWTHandlerException, InterruptedException{
    	JWTValidationServiceImpl authService = new JWTValidationServiceImpl();
		authService.setValidateJti(false);
		authService.setValidateAud(false);
		authService.setValidateNbf(false);
		
		JWTClaimsSet claimsSet = new JWTClaimsSet();
		claimsSet.setExpirationTime(new Date());
		Thread.sleep(1000);
		Assert.assertFalse(authService.validateExpClaim(claimsSet) );
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MINUTE, -1);
		claimsSet.setNotBeforeTime(cal.getTime());
		Assert.assertTrue(authService.validateNbfClaim(claimsSet) );
		
	}
	
	
	
	
}
