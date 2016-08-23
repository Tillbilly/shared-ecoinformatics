package au.edu.aekos.shared.service.security;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.web.util.SharedUrlUtils;

import com.nimbusds.jwt.SignedJWT;


public class AAFAuthServiceImpl implements AAFAuthService {

	public static final String AAF_ATTRIBUTES_CLAIM_STR = "https://aaf.edu.au/attributes";
	
	public static final String AAF_EDUPERSONPRINCIPALNAME = "edupersonprincipalname"; //I think this is the 'a' number
	public static final String AAF_COMMONNAME = "cn";
	public static final String AAF_MAIL = "mail";
	
	private static final String AAF_ATTR_QUERY = "select username from AUTH_AAF_ATTR where mail=? and cn=? and edupersonprincipalname=?";
	private static final String AAF_ATTR_QUERY_CN_NULL = "select username from AUTH_AAF_ATTR where mail=? and ( cn IS NULL or cn='') and edupersonprincipalname=?";
	private static final String AAF_ATTR_QUERY_EDUPPN_NULL = "select username from AUTH_AAF_ATTR where mail=? and cn=? and (edupersonprincipalname IS NULL or edupersonprincipalname='')";
	private static final String AAF_ATTR_QUERY_BOTH_NULL = "select username from AUTH_AAF_ATTR where mail=? and (cn IS NULL or cn='') and (edupersonprincipalname IS NULL or edupersonprincipalname='')";
	private static final String INSERT_AAF_ATTRIBUTES_QUERY = "insert into AUTH_AAF_ATTR ( id, cn, edupersonprincipalname, mail, username ) values ( nextval('hibernate_sequence'), ? , ? , ? , ? )";
	private static final String INSERT_AUTHORITY_QUERY = "INSERT INTO shared_authorities ( id, authority, username ) VALUES ( ?, ?, ?)";
	private static final String INSERT_USER_AUTHORITY_QUERY = "INSERT INTO shared_user_shared_authorities ( shared_user_username, roles_id ) VALUES (?, ? )";
	private static final String SELECT_NEXTVAL_HIBERNATE_SEQUENCE = "SELECT nextval('hibernate_sequence') AS new_seq_value"; //returns bigint
	private static final String USER_AUTHORITIES_QUERY = "select authority from shared_authorities where username = ?";
	private static final String SHARED_USER_USERNAME_QUERY = "select username from SHARED_USER where username = ?";
	
	//TODO create a join query to determine whether user is enable at the same time as checking the AAF attributes. ( performance )
	private static final String SHARED_USER_USERNAME_ENABLED_QUERY = "select enabled from SHARED_USER where username = ?";
	
	private static final String CREATE_AAF_SHARED_USER_QUERY = "insert into SHARED_USER (username, emailaddress, enabled , fullname , aafuser , aafregistered ) VALUES (?, ?, ?,?,?,?)" ;
	
	private Logger logger = LoggerFactory.getLogger(AAFAuthServiceImpl.class);
	
	private DataSource dataSource;
	
	@Override
	public AAFUserDetails retrieveUserDetails(SignedJWT jwt) {
		Map<String,String> attributesMap = getAAFAttributesMapFromJwt(jwt);
		if(attributesMap == null || attributesMap.size() == 0){
			return null;
		}
		//At minimum, we require the 'mail' attribute to be set, we can use this to uniquely identify the aaf user. 
		if(! attributesMap.containsKey(AAF_MAIL)){
			logger.info("JWT aaf attributes do not contain mail");
			return null;
		}
		String cn = attributesMap.get(AAF_COMMONNAME);
		String mail = attributesMap.get(AAF_MAIL);
		String edupersonprincipalname = attributesMap.get(AAF_EDUPERSONPRINCIPALNAME);
		
		return getUserDetailsFromAAFAttributes(mail, cn, edupersonprincipalname);
	}

	/**
	 * So in the future, it may be feasible for several AAF logins related to the same shared user.
	 * There would be user/admin related processing in order to link the different log ins the the 
	 * SHaRED user.  Ahh I`ll build the capability in now, but not all of the linking functionality.
	 * 
	 * For the time being though, a unique combination of mail, cn, edupersonprincipalname will create
	 * a new SharedUser , with username either edupersonprincipalname or the pre @ mail name,
	 * if the mail name is not unique, append a number to it .
	 * 
	 * So for SHaRED 1 AAF integration, we'll query the AUTH_AAF_ATTR for the given attributes.
	 * If no record exists, need to create a new user with unique username based on AAF attributes,
	 * then add the AAF attribute record, and add a 'SHARED_USER' role to the authorities table.
	 * 
	 */
	private AAFUserDetails getUserDetailsFromAAFAttributes(String mail, String cn, String edupersonprincipalname){
		String username = null;
		JdbcTemplate template = new JdbcTemplate(dataSource);
		try{
			if(StringUtils.hasLength(cn) && StringUtils.hasLength(edupersonprincipalname)){
				username = template.queryForObject(AAF_ATTR_QUERY, String.class, mail,cn, edupersonprincipalname);
			}else if(!StringUtils.hasLength(cn) && !StringUtils.hasLength(edupersonprincipalname)){
				username = template.queryForObject(AAF_ATTR_QUERY_BOTH_NULL, String.class, mail);
			}else if(!StringUtils.hasLength(cn)){
				username = template.queryForObject(AAF_ATTR_QUERY_CN_NULL, String.class, mail,edupersonprincipalname);
			}else if(!StringUtils.hasLength(edupersonprincipalname)){
				username = template.queryForObject(AAF_ATTR_QUERY_EDUPPN_NULL, String.class, mail,cn);
			}
		}catch(org.springframework.dao.EmptyResultDataAccessException ex){ 
			//JdbcTemplate quirk - does not like to return a null result.
			logger.debug("Shared User for " + mail + " does not exist. Creating a new SharedUser.");
			username = null;
		}
		if(username == null){
			//Create a new Shared User, give it SHARED_USER role, load AAF attributes with given username
			username = createNewAAFSharedUser(mail, cn, edupersonprincipalname, template );
			loadAafAttributes(username, mail, cn, edupersonprincipalname, template);
			loadAAFAuthority( username, template );
		}else if( ! checkUserEnabled(username, template)  ){  //Check the user has not been disabled
			logger.info("Attempted AAF log in from disabled user " + username);
			return null;
		}
		List<GrantedAuthority> grantedAuthorities = retrieveAuthoritiesForUsername(username, template);
		return new AAFUserDetails(username, grantedAuthorities);
	}

	private String createNewAAFSharedUser(String mail, String cn, String edupersonprincipalname, JdbcTemplate template ){
		String newUsername = getUniqueSharedUsername(mail, edupersonprincipalname, template );
		template.update(CREATE_AAF_SHARED_USER_QUERY, newUsername, mail , Boolean.TRUE , cn, Boolean.TRUE , Boolean.FALSE );
		logger.info("Created new AAF SHaRED user with username " + newUsername + " and email " + mail );
		return newUsername;
	}
	
	//All this effort is because the 'a' number may not be unique - there is nothing stopping a non aaf user from
	//having an 'a' number type username.  Yes it is unlikely,  but not impossible.
	//Update: was'nt actually that much trouble
	private String getUniqueSharedUsername(String mail, String edupersonprincipalname, JdbcTemplate template){
        if(StringUtils.hasLength(edupersonprincipalname) && ! doesUsernameExist(edupersonprincipalname, template)){
        	return edupersonprincipalname;
		}
        String baseUsername = SharedUrlUtils.retrieveUsernameFromEmailAddress(mail);
        int suffix = 1;
        String processUsername = baseUsername;
        while( doesUsernameExist(processUsername, template) ){
        	processUsername = baseUsername + Integer.toString(suffix);
        	suffix++;
        }
		return processUsername;
	}
	
	public boolean doesUsernameExist(String username){
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return doesUsernameExist(username, template);
	}
	
	private boolean doesUsernameExist(String username, JdbcTemplate template){
		String shd_username = null;
		try{
		    shd_username = template.queryForObject(SHARED_USER_USERNAME_QUERY, String.class, username );
		}catch(org.springframework.dao.EmptyResultDataAccessException ex){
			return false;
		}
		return shd_username != null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,String> getAAFAttributesMapFromJwt(SignedJWT jwt){
		Map<String,String> claimsMap = null;
		try {
			claimsMap =  (Map<String,String>) jwt.getJWTClaimsSet().getClaim(AAF_ATTRIBUTES_CLAIM_STR);
		} catch (ParseException e) {
			logger.error("Error retrieving AAF attributes map from jwt", e);
			return null;
		}
		return claimsMap;
	}

	private void loadAafAttributes(String username, String mail, String cn, String edupersonprincipalname, JdbcTemplate template){
    	template.update(INSERT_AAF_ATTRIBUTES_QUERY, cn, edupersonprincipalname, mail, username );
    }

	private void loadAAFAuthority( String username, JdbcTemplate template ){
    	Long authorityId = getNextHibernateSequenceValue(template);
    	template.update(INSERT_AUTHORITY_QUERY, authorityId, SharedRole.ROLE_USER.name(), username );
    	template.update(INSERT_USER_AUTHORITY_QUERY, username, authorityId);
    }
	
	private Long getNextHibernateSequenceValue(JdbcTemplate template){
		return template.queryForObject(SELECT_NEXTVAL_HIBERNATE_SEQUENCE, Long.class);
	}

	private List<GrantedAuthority> retrieveAuthoritiesForUsername(String username, JdbcTemplate template){
		List<String> authorities = template.queryForList(USER_AUTHORITIES_QUERY, String.class, username);
		List<GrantedAuthority> gaList = new ArrayList<GrantedAuthority>();
		if(authorities != null ){
			for(String auth : authorities){
				gaList.add(new SimpleGrantedAuthority(auth));
			}
		}
		return gaList;
	}
	
	private boolean checkUserEnabled(String username, JdbcTemplate template){
		Boolean userEnabled = null;
		try{
			userEnabled = template.queryForObject(SHARED_USER_USERNAME_ENABLED_QUERY, Boolean.class, username);
		}
		catch(org.springframework.dao.EmptyResultDataAccessException ex){ 
			logger.debug("Error searching user enabled, username " + username, ex);
		}
		if(userEnabled == null){
			return false;
		}
		return userEnabled.booleanValue();
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
