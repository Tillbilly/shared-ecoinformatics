package au.edu.aekos.shared.web.model;

import au.edu.aekos.shared.data.entity.SharedUser;

public class RegistrationModel {

	private String username;
	
	private String password;
	
	private String emailAddress;
	
	private String fullName;
	
	private String organisation;
	
	private String organisationOther;
	
	private String postalAddress;
	
	private String phoneNumber;
	
	private String website;
	
	private Boolean aafUser = Boolean.FALSE;
	
	public static final String OTHER_ORG_KEY = "OTHER";
	
	//Used for changing details, if a submission has the user details, as contact details,
	//this flag determines whether the user would like the submission updated to reflect the new
	//contact details - this might be an index only / publish function
	private boolean updateSubmissionsWithChangedDetails = false;

	public RegistrationModel() {
		super();
	}
	
	public RegistrationModel(SharedUser user) {
		super();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.emailAddress = user.getEmailAddress();
		this.fullName = user.getFullName();
		this.organisation = user.getOrganisation();
		this.postalAddress = user.getPostalAddress();
		this.phoneNumber = user.getPhoneNumber();
		this.website = user.getWebsite();
		this.aafUser = user.getAafUser();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getOrganisationOther() {
		return organisationOther;
	}

	public void setOrganisationOther(String organisationOther) {
		this.organisationOther = organisationOther;
	}

	public boolean isUpdateSubmissionsWithChangedDetails() {
		return updateSubmissionsWithChangedDetails;
	}

	public void setUpdateSubmissionsWithChangedDetails(
			boolean updateSubmissionsWithChangedDetails) {
		this.updateSubmissionsWithChangedDetails = updateSubmissionsWithChangedDetails;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Boolean getAafUser() {
		return aafUser;
	}

	public void setAafUser(Boolean aafUser) {
		this.aafUser = aafUser;
	}
	
	//Had some issues with JSTL Boolean evaluation
	public boolean isAafRegisteredUser(){
		if(aafUser == null ){
			return false;
		}
		return aafUser.booleanValue();
	}
}
