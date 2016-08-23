package au.edu.aekos.shared.service.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SharedAuthorityDao;
import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SharedAuthority;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.web.model.ActiveSessionModel;

@Service
public class SecurityServiceImpl implements SecurityService {

	private static final int SECONDS_TO_MILLIS = 1000;
	private static final int MILLIS_TO_MINUTES = 1000 * 60;

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SharedAuthorityDao sharedAuthorityDao;
	
	@Autowired @Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;
	
	@Transactional
	public SharedUser getCurrentUser() {
		String username = getLoggedInUsername();
		if(StringUtils.hasLength( username) ){
			return sharedUserDao.findUserByUsername(username);
		}
		return null;
	}

	//For Dev purposes!
	@Transactional @Deprecated
	public SharedUser getCreateDefaultUser(String userName){
		SharedUser user = sharedUserDao.findUserByUsername(userName);
        if( user == null ){
        	SharedUser sharedUser = new SharedUser();
        	sharedUser.setUsername(userName);
        	sharedUserDao.save(sharedUser);
        	return sharedUser;
        }
        return user;
	}
	
	public String getLoggedInUsername(){
		SecurityContext secContext = SecurityContextHolder.getContext();
		if(secContext != null && secContext.getAuthentication() != null ) {
		    Authentication auth = secContext.getAuthentication();
		    String username = auth.getName();
		    return username;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasRole(SharedRole role){
		SecurityContext secContext = SecurityContextHolder.getContext();
		Authentication auth = secContext.getAuthentication();
		for(GrantedAuthority ga :  (Collection<GrantedAuthority>) auth.getAuthorities() ){
			if(ga.getAuthority().equals( role.name() ) ){
				return true;
			}
		}
		return false; 
	}
	
	@SuppressWarnings("unchecked")
	public boolean notHasRole(SharedRole role){
		SecurityContext secContext = SecurityContextHolder.getContext();
		Authentication auth = secContext.getAuthentication();
		for(GrantedAuthority ga :  (Collection<GrantedAuthority>) auth.getAuthorities() ){
			if(ga.getAuthority().equals( role.name() ) ){
				return false;
			}
		}
		return true; 
	}

	@Override
	@Transactional
	//Currently fairly unsophisticated - but the stub is here for ownership operations.
	public boolean canEditSubmission(Long submissionId) {
		String username = getLoggedInUsername();
		Submission sub = submissionDao.findById(submissionId);
		if(sub == null){
			return false;
		}
		return sub.getSubmitter().getUsername().equals(username);
	}

	@Override @Transactional
	public void updateUserRoles(String username, List<SharedRole> roleList) {
		SharedUser user = sharedUserDao.findUserByUsername(username);
		List<SharedRole> workList = new ArrayList<SharedRole>();
		workList.addAll(roleList);
		//For some reason the cascade wasn't working on the AuthorityTable
		List<Long> removedSharedAuthorityIds = new ArrayList<Long>();
		if(user != null){
			Set<SharedAuthority> authorities = user.getRoles();
			if(authorities != null && authorities.size() > 0){
				//First remove any roles not specified in the role list
				Iterator<SharedAuthority> authIter = authorities.iterator();
				while(authIter.hasNext()){
					SharedAuthority sauth = authIter.next();
					SharedRole authRole = sauth.getSharedRole();
					if(! workList.contains(authRole)){
						removedSharedAuthorityIds.add(sauth.getId());
						authIter.remove();
					}else{
						workList.remove(authRole);
					}
				}
			}
			//Now create roles remaining in the workList
			for(SharedRole newRole : workList){
				SharedAuthority newAuth = new SharedAuthority();
				newAuth.setSharedRole(newRole);
				newAuth.setSharedUser(user);
				user.getRoles().add(newAuth);
			}
			sharedUserDao.saveOrUpdate(user);
			sharedUserDao.flush();
			//Now clean up un-deleted shared authorities -this is a cludgey hack, 11th hour, 
			//but don't want to mess up authorisation on release day.
			for(Long saId : removedSharedAuthorityIds){
			    SharedAuthority sa = sharedAuthorityDao.findById(saId);
			    sharedAuthorityDao.delete(sa);
			}
		}
	}
	
	@Transactional @Deprecated
	public void resetUsersPassword(String username, String password) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException{
		SharedUser user = sharedUserDao.findUserByUsername(username);
		String passKey = HmacSHA512Util.generateSaltedHmacSHA512Key(password, username);
		user.setPassword(passKey);
		sharedUserDao.saveOrUpdate(user);	
	}
	
	@Transactional @Deprecated
	public String generatePasswordAsFirstPartOfEmailAddress(String username){
		SharedUser user = sharedUserDao.findUserByUsername(username);
		String emailAddress = user.getEmailAddress();
		String password = emailAddress.substring(0, emailAddress.indexOf("@"));
		return password;
	}

	@Override
	public void sendUserChangePasswordEmail(String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ActiveSessionModel> getListOfUsersWithAnActiveSession(int sessionTimeoutSeconds) {
		Date now = new Date();
		List<ActiveSessionModel> activeSessionList = new ArrayList<ActiveSessionModel>();
		List<Object> principals = sessionRegistry.getAllPrincipals();
		for(Object obj : principals){
			List<SessionInformation> sessionInfoList = sessionRegistry.getAllSessions(obj, false);
			UserDetails user = (UserDetails) obj;
			for(SessionInformation si : sessionInfoList){
				activeSessionList.add(calculateActiveSessionExpiry(sessionTimeoutSeconds, now, user.getUsername(), si.getLastRequest()));
			}
		}
		return activeSessionList;
	}

	static ActiveSessionModel calculateActiveSessionExpiry(int sessionTimeoutSeconds, Date now, String username, Date lastRequest) {
		int sessionTimeoutMillis = sessionTimeoutSeconds * SECONDS_TO_MILLIS;
		long timeOfSessionTimeoutMillis = lastRequest.getTime() + sessionTimeoutMillis;
		Date timeOfSessionTimeoutDate = new Date(timeOfSessionTimeoutMillis);
		int minutesUntilTimeout = (int) ((timeOfSessionTimeoutMillis - now.getTime()) / MILLIS_TO_MINUTES);
		return new ActiveSessionModel(username, lastRequest, minutesUntilTimeout, timeOfSessionTimeoutDate);
	}
}
