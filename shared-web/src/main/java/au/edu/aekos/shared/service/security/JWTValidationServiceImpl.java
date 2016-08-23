package au.edu.aekos.shared.service.security;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Handles Authentication and log in for AAF Rapid Connect Generated Signed JWT Tokens
 *
 * By default , 
 * validates a unique JTI claim,
 * the aud claim is from a list of known url's
 * nbf date ( not before )
 * exp date ( expired )
 *
 * @author btill
 */
public class JWTValidationServiceImpl implements JWTValidationService {

	private Logger logger = LoggerFactory.getLogger(JWTValidationServiceImpl.class);
	
	private String secretKey = "jwLbkBM.,B%f~~1YXOo2lAFs9TNyVu_L";  //Default dev value.  Do not check in the secret value used in production.
	
	public static final String JTI_CLAIM_KEY = "jti";
	public static final String AUD_CLAIM_KEY = "aud";
	public static final String NBF_CLAIM_KEY = "nbf";
	public static final String EXP_CLAIM_KEY = "exp";
	
	private Boolean validateJti = true;  //Validate a unique ID for the token - to stop a replay attack
	private Boolean validateAud = true;  //Ensure request comes from a known host location
	private Boolean validateNbf = true;  //Validate the system date is Not before the nbf claim date 
	private Boolean validateExp = true;  //Validate the system date is not after the exp claim
	
	private static final String JTI_CACHE_NAME = "jti_auth_cache"; //configured in ehcache.xml
	private Cache jtiCache = null;
	
	private Set<String> audAllowedLocation = new HashSet<String>();
	private String audWebAppName = "shared-web"; //shared-web by default, can be changed by bean wiring.
	
	@Override
	public boolean verifyTokenSignature(SignedJWT jwt) {
		try {
			if(! JWTHandlerUtil.verifySignedJWT(jwt, secretKey)){
				return false;
			}
		} catch (JWTHandlerException e1) {
			logger.error("SignedJWT failed secretKey verfication", e1);
			return false;
		}
		return true;
	}
	
	public boolean validateJWTClaims(SignedJWT jwt){
		ReadOnlyJWTClaimsSet claimsSet = null;
		try {
			claimsSet = jwt.getJWTClaimsSet();
		} catch (ParseException e) {
			logger.info("Can't parse claim set", e);
			return false;
		}
		if(!validateJtiClaim(claimsSet) ||
				!validateAudClaim(claimsSet) ||
				!validateNbfClaim(claimsSet) ||
				!validateExpClaim(claimsSet)){
			return false;
		}
		return true;
	}
	
	/**
	 * jti validation - check to see if the 'unique' jti claim has been made before. 
	 * The cache expels elements after half an hour. 
	 * @param claimsSet
	 * @return true if jti is unique and 'new', false for any other reason
	 */
	public boolean validateJtiClaim(ReadOnlyJWTClaimsSet claimsSet){
		if(! validateJti){
			return true;
		}
		if(!claimsSet.getAllClaims().containsKey(JTI_CLAIM_KEY)){
			logger.info("Token failed validation - no jti claim");
			return false;
		}
		Object jtiClaim = claimsSet.getAllClaims().get(JTI_CLAIM_KEY);
		if(jtiClaim == null || jtiClaim.toString().length() == 0){
			logger.info("Null or zero length jti");
			return false;
		}
		if(isJtiInCache(jtiClaim)){
			logger.info("Possible replay attack for jti " + jtiClaim.toString());
			return false;
		}
		Element el = new Element(jtiClaim, jtiClaim);
		getCache().put(el);
		return true;
	}
	
	private Cache getCache(){
		if(jtiCache == null){
		    CacheManager cacheManager = CacheManager.getInstance();
		    jtiCache = cacheManager.getCache(JTI_CACHE_NAME);
		}
		return jtiCache;
	}

	public boolean isJtiInCache(Object jti){
		return getCache().isKeyInCache(jti);
	}
	
	
	
	/**
	 * The aud claim compares a launching url to a list of known URLs configured in this service
	 * Make sure you initialise audAllowedLocation in the security context.
	 * @param claimsSet
	 * @return
	 */
	public boolean validateAudClaim(ReadOnlyJWTClaimsSet claimsSet){
		if(!validateAud){
			return true;
		}
		if(audAllowedLocation == null || audAllowedLocation.size() == 0){
			logger.error("Aud allowed locations not initialised - please check security context");
			return false;
		}
		if(!StringUtils.hasLength(audWebAppName)){
			logger.error("audWebAppName not initialised - please check security context");
			return false;
		}
		if(!claimsSet.getAllClaims().containsKey(AUD_CLAIM_KEY)){
			logger.info("Token failed validation - no aud claim");
			return false;
		}
		Object audClaim = claimsSet.getAllClaims().get(AUD_CLAIM_KEY);
		if(audClaim == null || !(audClaim instanceof List<?>)){
			logger.info("aud is null or not instance of List. aud validation fails");
			return false;
		}
		
		@SuppressWarnings("unchecked")
		List<Object> audClaimList = (List<Object>) audClaim;
		if(audClaimList == null || audClaimList.size() == 0){
			logger.info("No aud claims specified. alidation fails");
			return false;
		}
		String audClaimString = audClaimList.get(0).toString();
		return compareClaimStringToAllowedLocationsAndAppName(audClaimString);
	}
	
	public boolean compareClaimStringToAllowedLocationsAndAppName(String audClaimString){
		//first, remove http://  or  https://
		String audClaimRaw = audClaimString.replace("https://", "").replace("http://", "");
		boolean locFound = false;
		for(String allowedLoc : audAllowedLocation){
			if(audClaimRaw.startsWith(allowedLoc)){
				locFound = true;
				break;
			}
		}
		if(!locFound){
			logger.info(audClaimRaw + " is not an allowed location, aud validation failed");
			return false;
		}
		//Now check the audClaimRaw contains the web App name
		if(! audClaimRaw.endsWith(audWebAppName)){
			logger.info(audClaimRaw + " does not contain the defined web app name of " + audWebAppName + ". aud validation failed");
			return false;
		}
		return true;
	}
	
	public boolean validateNbfClaim(ReadOnlyJWTClaimsSet claimSet){
		if(! validateNbf){
			return true;
		}
		Date nbfClaim = retrieveDateClaimFromClaimSet(NBF_CLAIM_KEY, claimSet);
		if(nbfClaim == null){
			return false;
		}
		Date currentTime = new Date();
		if( nbfClaim.compareTo(currentTime) > 0){
			logger.info("nbf claim for date " + nbfClaim.toString() + " failed. Current Time " + currentTime.toString());
			return false;
		}
		return true;
	}
	
	public boolean validateExpClaim(ReadOnlyJWTClaimsSet claimSet){
		if(! validateExp){
			return true;
		}
		Date expClaim = retrieveDateClaimFromClaimSet(EXP_CLAIM_KEY, claimSet);
		if(expClaim == null){
			return false;
		}
		Date currentTime = new Date();
		if( expClaim.compareTo(currentTime) < 0){
			logger.info("exp claim for date " + expClaim.toString() + " failed. Current Time " + currentTime.toString());
			return false;
		}
		return true;
	}
	
	public Date retrieveDateClaimFromClaimSet(String claimKey, ReadOnlyJWTClaimsSet claimSet){
		Object obj = claimSet.getAllClaims().get(claimKey);
		if(obj == null){
			logger.info("Claim " + claimKey + " missing from token claimset. Validation failed.");
			return null;
		}
		if(! ( obj instanceof Date ) ){
			logger.info("Claim " + claimKey + " not an instance of Date. Validation failed.");
			return null;
		}
        return (Date) obj;		
	}
	

	
	
	public Boolean getValidateJti() {
		return validateJti;
	}

	public void setValidateJti(Boolean validateJti) {
		this.validateJti = validateJti;
	}

	public Boolean getValidateAud() {
		return validateAud;
	}

	public void setValidateAud(Boolean validateAud) {
		this.validateAud = validateAud;
	}

	public Set<String> getAudAllowedLocation() {
		return audAllowedLocation;
	}

	public void setAudAllowedLocation(Set<String> audAllowedLocation) {
		this.audAllowedLocation = audAllowedLocation;
	}

	public Boolean getValidateNbf() {
		return validateNbf;
	}

	public void setValidateNbf(Boolean validateNbf) {
		this.validateNbf = validateNbf;
	}

	public Boolean getValidateExp() {
		return validateExp;
	}

	public void setValidateExp(Boolean validateExp) {
		this.validateExp = validateExp;
	}

	public String getAudWebAppName() {
		return audWebAppName;
	}

	public void setAudWebAppName(String audWebAppName) {
		this.audWebAppName = audWebAppName;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}



}
