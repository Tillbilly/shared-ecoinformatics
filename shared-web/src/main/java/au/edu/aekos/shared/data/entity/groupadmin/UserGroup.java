package au.edu.aekos.shared.data.entity.groupadmin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.persistence.CascadeType;
import org.hibernate.annotations.ForeignKey;

import au.edu.aekos.shared.data.entity.SharedUser;

@Entity(name="userGroup")
@Table(name="USER_GROUP")
public class UserGroup {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String name;
	
	@Column
	private String organisation;
	
	@Column(name="PEER_REVIEW_ACTIVE")
	private Boolean peerReviewActive = Boolean.FALSE;
	
	@ManyToMany
	@ForeignKey(name = "USER_GROUP_SHARED_USER_FK", inverseName = "SHARED_USER_USER_GROUP_FK")
	@JoinTable(name="USER_GROUP_MEMBERS",
			joinColumns=
            @JoinColumn(name="GROUP_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="USERNAME", referencedColumnName="USERNAME"))
	private List<SharedUser> memberList = new ArrayList<SharedUser>();
	
	@OneToMany(cascade={CascadeType.ALL })
	@ForeignKey(name="GROUP_ADMIN_GROUP_FK")
	@JoinColumn(name="GROUP_ID")
	private List<UserGroupAdmin> groupAdministratorList = new ArrayList<UserGroupAdmin>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public List<SharedUser> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<SharedUser> memberList) {
		this.memberList = memberList;
	}

	public List<UserGroupAdmin> getGroupAdministratorList() {
		return groupAdministratorList;
	}

	public void setGroupAdministratorList(
			List<UserGroupAdmin> groupAdministratorList) {
		this.groupAdministratorList = groupAdministratorList;
	}
	
	public List<String> getListOfGroupUsernames(){
		List<String> groupUsernames = new ArrayList<String>();
		for(SharedUser su : memberList){
			groupUsernames.add(su.getUsername());
		}
		return groupUsernames;
	}

	public Boolean getPeerReviewActive() {
		return peerReviewActive;
	}

	public void setPeerReviewActive(Boolean peerReviewActive) {
		this.peerReviewActive = peerReviewActive;
	}
	
	
	
	
}
