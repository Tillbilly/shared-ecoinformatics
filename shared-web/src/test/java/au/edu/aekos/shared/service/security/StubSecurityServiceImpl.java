package au.edu.aekos.shared.service.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.web.model.ActiveSessionModel;

public class StubSecurityServiceImpl implements SecurityService {

	@Autowired
	private SharedUserDao sharedUserDao;
	
	@Override
	public SharedUser getCurrentUser() {
		SharedUser user = sharedUserDao.findUserByUsername("testUser");
        if( user == null ){
        	SharedUser sharedUser = new SharedUser();
        	sharedUser.setUsername("testUser");
        	sharedUserDao.save(sharedUser);
        	return sharedUser;
        }
        return user;
//		SharedUser result = new SharedUser();
//		result.setFullName("Test User");
//		result.setPostalAddress("123 test st");
//		result.setEmailAddress("test@test.com");
//		result.setPhoneNumber("83216547");
//		return result;
	}

	@Override
	public SharedUser getCreateDefaultUser(String userName) {
		return null;
	}

	@Override
	public String getLoggedInUsername() {
		return null;
	}

	@Override
	public boolean hasRole(SharedRole role) {
		return false;
	}

	@Override
	public boolean notHasRole(SharedRole role) {
		return false;
	}

	@Override
	public boolean canEditSubmission(Long submissionId) {
		return false;
	}

	@Override
	public void updateUserRoles(String username, List<SharedRole> roleList) {
		
	}

	@Override
	public void resetUsersPassword(String username, String password)
			throws InvalidKeyException, UnsupportedEncodingException,
			NoSuchAlgorithmException {
		
	}

	@Override
	public String generatePasswordAsFirstPartOfEmailAddress(String username) {
		return null;
	}

	@Override
	public void sendUserChangePasswordEmail(String email) {
		
	}

	@Override
	public List<ActiveSessionModel> getListOfUsersWithAnActiveSession(int sessionTimeoutSeconds) {
		return null;
	}

}
