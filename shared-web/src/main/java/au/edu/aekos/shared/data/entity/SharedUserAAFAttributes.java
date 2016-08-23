package au.edu.aekos.shared.data.entity;

import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name="aafAttributes")
@Table(name="AUTH_AAF_ATTR",uniqueConstraints={@UniqueConstraint(columnNames={"cn","mail","edupersonprincipalname"})})
public class SharedUserAAFAttributes {
	
	@Id
	private Long id;
	
	@Column
	private String cn;
	
	@Column
	private String mail;
	
	@Column
	private String edupersonprincipalname;
	
	@Column
	private String username;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getEdupersonprincipalname() {
		return edupersonprincipalname;
	}

	public void setEdupersonprincipalname(String edupersonprincipalname) {
		this.edupersonprincipalname = edupersonprincipalname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
