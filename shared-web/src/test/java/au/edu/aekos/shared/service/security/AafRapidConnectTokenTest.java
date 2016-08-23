package au.edu.aekos.shared.service.security;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Test out the usage of the nimbus jose-jwt package used by the PreAuth filter.
 * The supplied assertin was retrieved from the test url
 * https://rapid.test.aaf.edu.au/jwt/authnrequest/research/LBV6-lwcx_fy1uGR
 * 
 * Not sure of the redirect URL that allows you to pick an 
 * organisation / then enter username / password
 * 
 * 
 * @author btill
 */
public class AafRapidConnectTokenTest {

	public String assertion = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3JhcGlkLnRlc3QuYWFmLmVkdS5hdSIsImlhdCI6MTM5MjAwODE2NSwianRpIjoiUFJ3OWE0ZTEyZUdBS0JSME1YVFg1Nm1rRWE2dlYyZmMiLCJuYmYiOjEzOTIwMDgxMDUsImV4cCI6MTM5MjAwODI4NSwidHlwIjoiYXV0aG5yZXNwb25zZSIsImF1ZCI6Imh0dHA6Ly9zaGFyZWQtZGV2LmVjb2luZm9ybWF0aWNzLm9yZy5hdTo4MDgwL3NoYXJlZC13ZWIiLCJzdWIiOiJodHRwczovL3JhcGlkLnRlc3QuYWFmLmVkdS5hdSFodHRwOi8vc2hhcmVkLWRldi5lY29pbmZvcm1hdGljcy5vcmcuYXU6ODA4MC9zaGFyZWQtd2ViIW5LbDdvNS9wcUNPTjNuVUs1WG5yVGFzd3k4QT0iLCJodHRwczovL2FhZi5lZHUuYXUvYXR0cmlidXRlcyI6eyJjbiI6IkJlbmphbWluIFRpbGwiLCJtYWlsIjoiYmVuamFtaW4udGlsbEBhZGVsYWlkZS5lZHUuYXUiLCJkaXNwbGF5bmFtZSI6IkJlbmphbWluIFRpbGwiLCJnaXZlbm5hbWUiOiIiLCJzdXJuYW1lIjoiIiwiZWR1cGVyc29udGFyZ2V0ZWRpZCI6Imh0dHBzOi8vcmFwaWQudGVzdC5hYWYuZWR1LmF1IWh0dHA6Ly9zaGFyZWQtZGV2LmVjb2luZm9ybWF0aWNzLm9yZy5hdTo4MDgwL3NoYXJlZC13ZWIhbktsN281L3BxQ09OM25VSzVYbnJUYXN3eThBPSIsImVkdXBlcnNvbnNjb3BlZGFmZmlsaWF0aW9uIjoibWVtYmVyQHZoby50ZXN0LmFhZi5lZHUuYXUiLCJlZHVwZXJzb25wcmluY2lwYWxuYW1lIjoiIn19.-ZZuXbk2EJR6NtBOJhUH05e8EV-wLZXmF14ePRpGDW4";
	private String secretKey = "jwLbkBM.,B%f~~1YXOo2lAFs9TNyVu_L";
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTokenAnalysis(){
		SignedJWT jws = null;
		try {
			jws = SignedJWT.parse(assertion);
		} catch (ParseException e) {
			Assert.fail();
		}
		
		JWSVerifier verifier = new MACVerifier(secretKey);
        
		try {
			boolean verifiedSignature = jws.verify(verifier);
			if(!verifiedSignature){
				Assert.fail();
			}
		} catch (JOSEException e) {
			Assert.fail();
		}
		ReadOnlyJWTClaimsSet claimsSet = null;
		try {
			claimsSet = jws.getJWTClaimsSet();
		} catch (ParseException e) {
			Assert.fail();
		}
		
		/*
		 Ensure jti does not indicate a replay attack, and check nbf, exp, aud and other relevant claims for validity
		 Store https://aaf.edu.au/attributes claim in your application's session store and mark the session as successfully authenticated
		*/
		//jti , nbf, exp, aud
		for(String key : claimsSet.getAllClaims().keySet() ){
			System.out.println(key);
//			Object obj = claimsSet.getAllClaims().get(key);
			System.out.println(claimsSet.getAllClaims().get(key) );
		}
		Object attributes = claimsSet.getClaim("https://aaf.edu.au/attributes");
		Map<String,String> claimsMap =  ( Map<String,String> ) attributes;
		Assert.assertTrue(claimsMap.size() > 0);
		for(Map.Entry<String,String> entry : claimsMap.entrySet()){
			System.out.println(entry.getKey() + "   :   " + entry.getValue());
		}
		
		//What is the aud object?
		Object obj = claimsSet.getAllClaims().get("aud");
		System.out.println(obj.getClass().getCanonicalName() );
		//Cast to an ArrayList<String>
		List<String> audList = ( List<String> ) obj;
		Assert.assertTrue(audList.size() > 0);
		Assert.assertNotNull(audList.get(0));
	}
	
}
