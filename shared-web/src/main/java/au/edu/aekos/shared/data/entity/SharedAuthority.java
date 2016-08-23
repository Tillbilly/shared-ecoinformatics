package au.edu.aekos.shared.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

@Entity
@Table(name="SHARED_AUTHORITIES")
public class SharedAuthority {

	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="authority" )
	private SharedRole sharedRole;

	@Index(name="username_ix")
	@ManyToOne
	@ForeignKey(name="username_authority_fk")
	@JoinColumn(name="username")
	private SharedUser sharedUser;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SharedRole getSharedRole() {
		return sharedRole;
	}

	public void setSharedRole(SharedRole sharedRole) {
		this.sharedRole = sharedRole;
	}

	public SharedUser getSharedUser() {
		return sharedUser;
	}

	public void setSharedUser(SharedUser sharedUser) {
		this.sharedUser = sharedUser;
	}
	
	
	
}
