package au.edu.aekos.shared.service.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.web.model.ActiveSessionModel;


public interface SecurityService {

	SharedUser getCurrentUser();
	
	//Used by unit tests
	SharedUser getCreateDefaultUser(String userName);
	
	String getLoggedInUsername();
	
	boolean hasRole(SharedRole role);
	
	boolean notHasRole(SharedRole role);
	
	boolean canEditSubmission(Long submissionId);
	
	void updateUserRoles(String username, List<SharedRole> roleList);
	
	void sendUserChangePasswordEmail(String email);
	
	@Deprecated //Could'nt match CryptoJS and Java crypt encrypted keys when passwords had non word characters
	void resetUsersPassword(String username, String password) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException;
	 
	@Deprecated
	String generatePasswordAsFirstPartOfEmailAddress(String username);
	 
	List<ActiveSessionModel> getListOfUsersWithAnActiveSession(int sessionTimeoutSeconds);
	
			
	
}
