package au.edu.aekos.shared.data.entity.groupadmin;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="userGroupAdminPriv")
@Table(name="USER_GROUP_ADMIN_PRIV")
public class UserGroupAdminPrivilage {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private GroupAdminPrivilageEnum privilage;

	public UserGroupAdminPrivilage() {}

	public UserGroupAdminPrivilage(GroupAdminPrivilageEnum privilage) {
		super();
		this.privilage = privilage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GroupAdminPrivilageEnum getPrivilage() {
		return privilage;
	}

	public void setPrivilage(GroupAdminPrivilageEnum privilage) {
		this.privilage = privilage;
	}

}
