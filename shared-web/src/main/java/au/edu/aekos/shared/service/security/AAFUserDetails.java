package au.edu.aekos.shared.service.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AAFUserDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private String username;
	private List<GrantedAuthority> authorities;

	public AAFUserDetails(String username,
			List<GrantedAuthority> grantedAuthorities) {
		super();
		this.username = username;
		this.authorities = Collections.unmodifiableList(grantedAuthorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
