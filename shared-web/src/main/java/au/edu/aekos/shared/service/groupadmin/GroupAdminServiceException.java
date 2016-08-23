package au.edu.aekos.shared.service.groupadmin;

public class GroupAdminServiceException extends Throwable {

	private static final long serialVersionUID = 1L;

	public GroupAdminServiceException(String message) {
		super(message);
	}

	public GroupAdminServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
