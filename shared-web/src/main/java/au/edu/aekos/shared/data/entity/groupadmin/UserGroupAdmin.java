package au.edu.aekos.shared.data.entity.groupadmin;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.persistence.CascadeType;
import org.hibernate.annotations.ForeignKey;

import au.edu.aekos.shared.data.entity.SharedUser;

@Entity(name="userGroupAdmin")
@Table(name="USER_GROUP_ADMIN")
public class UserGroupAdmin {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ForeignKey(name="GROUP_ADMIN_SHARED_USER_FK")
	@ManyToOne
	@JoinColumn(name="USERNAME")
	private SharedUser administrator;
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="GROUP_ADMIN_ID")
	@ForeignKey(name="PRIVILAGE_GROUP_ADMIN_FK")
	private Set<UserGroupAdminPrivilage> privilages = new HashSet<UserGroupAdminPrivilage>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SharedUser getAdministrator() {
		return administrator;
	}

	public void setAdministrator(SharedUser administrator) {
		this.administrator = administrator;
	}

	public Set<UserGroupAdminPrivilage> getPrivilages() {
		return privilages;
	}

	public void setPrivilages(Set<UserGroupAdminPrivilage> privilages) {
		this.privilages = privilages;
	}

}
