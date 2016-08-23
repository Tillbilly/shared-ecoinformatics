package au.edu.aekos.shared.web.model;

import java.io.Serializable;

public class EditGroupUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String email;
	
	public EditGroupUser() {
		super();
	}

	public EditGroupUser(String username, String email) {
		this.username = username;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
