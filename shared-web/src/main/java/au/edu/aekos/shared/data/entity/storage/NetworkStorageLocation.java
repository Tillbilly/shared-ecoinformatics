package au.edu.aekos.shared.data.entity.storage;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("NETWORK")
public class NetworkStorageLocation extends StorageLocation {
	
	@Transient
	public static final StorageType storageType = StorageType.NETWORK;
	
	@Column(name="KEY_FILE")
	private String privateKeyPem;

	@Column(name="REMOTE_PATH")
	private String remotePath;
	
	public String getPrivateKeyPem() {
		return privateKeyPem;
	}

	public void setPrivateKeyPem(String privateKeyPem) {
		this.privateKeyPem = privateKeyPem;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
}
