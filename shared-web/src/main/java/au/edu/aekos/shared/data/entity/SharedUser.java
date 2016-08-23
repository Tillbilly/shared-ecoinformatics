package au.edu.aekos.shared.data.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.util.StringUtils;

@Entity(name="sharedUser")
@Table(name="SHARED_USER")
public class SharedUser {
	
	@Id
	private String username;
	
	@Column
	private String password;
	
	@Column
	private Boolean enabled = Boolean.TRUE;
	
	@Column
	private String emailAddress;
	
	@Column
	private String fullName;
	
	@Column
	private String organisation;
	
	@Column
	private String postalAddress;
	
	@Column 
	private String phoneNumber;
	
	@Column 
	private String website;
	
	@Column
	private String registrationToken;
	
	@Column
	private String changePasswordToken;
	
	@Column
	private Boolean aafUser = Boolean.FALSE;
	
	@Column
	private Boolean aafRegistered = Boolean.FALSE;
	
	@OneToMany( cascade=CascadeType.ALL, fetch=FetchType.EAGER )
	@ForeignKey(name="fk_authorities_users")
	private Set<SharedAuthority> roles = new HashSet<SharedAuthority>();
	
	@OneToMany( cascade=CascadeType.ALL , mappedBy="username")
	@ForeignKey(name = "shared_user_fk")
	private Set<SharedUserAAFAttributes> aafCredentials = new HashSet<SharedUserAAFAttributes>();

	@OneToMany(cascade=CascadeType.ALL,mappedBy="submitter" )
	private List<Submission> submissions = new ArrayList<Submission>(); 
	
	public List<Submission> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(List<Submission> submissions) {
		this.submissions = submissions;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Set<SharedAuthority> getRoles() {
		return roles;
	}

	public void setRoles(Set<SharedAuthority> roles) {
		this.roles = roles;
	}

	public String getRegistrationToken() {
		return registrationToken;
	}

	public void setRegistrationToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getChangePasswordToken() {
		return changePasswordToken;
	}

	public void setChangePasswordToken(String changePasswordToken) {
		this.changePasswordToken = changePasswordToken;
	}

	public String getCleanedPostalAddress() {
		if(StringUtils.hasLength(postalAddress ) ){
		    return postalAddress.replaceAll("(\r)?\n", ", ");
		}
		return postalAddress;
	}

	public Boolean getAafUser() {
		return aafUser;
	}

	public void setAafUser(Boolean aafUser) {
		this.aafUser = aafUser;
	}

	public Set<SharedUserAAFAttributes> getAafCredentials() {
		return aafCredentials;
	}

	public void setAafCredentials(Set<SharedUserAAFAttributes> aafCredentials) {
		this.aafCredentials = aafCredentials;
	}

	public Boolean getAafRegistered() {
		return aafRegistered;
	}

	public void setAafRegistered(Boolean aafRegistered) {
		this.aafRegistered = aafRegistered;
	}
	
	public boolean hasRole(SharedRole role){
		if(roles == null){
			return false;
		}
		for(SharedAuthority authority : roles ){
			if(role.equals(authority.getSharedRole())){
				return true;
			}
		}
		return false;
	}
	
	//Used on the user management page
	public Boolean getIsGroupAdministrator(){
		return hasRole(SharedRole.ROLE_GROUP_ADMIN );
	}

	@Override
	public String toString() {
		return "SharedUser [username=" + username + ", password=" + password + ", enabled=" + enabled
				+ ", emailAddress=" + emailAddress + ", fullName=" + fullName + ", registrationToken="
				+ registrationToken + ", roles=" + roles + "]";
	}
	
	
	
}
