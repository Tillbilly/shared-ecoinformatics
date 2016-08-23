package au.edu.aekos.shared.doiclient.service;

public class DoiClientConfig {
	private String userId;
	private String appId;
    private String topLevelUrl;
    private String doiMintingServiceUrl;
    private String keystoreFilePath;
    private String keystorePassword;
    
    //Quick fix to stop the TERN Service barfing on non ASCII characters
    private Boolean asciiCleanse = Boolean.TRUE;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTopLevelUrl() {
		return topLevelUrl;
	}
	public void setTopLevelUrl(String topLevelUrl) {
		this.topLevelUrl = topLevelUrl;
	}
	public String getDoiMintingServiceUrl() {
		return doiMintingServiceUrl;
	}
	public void setDoiMintingServiceUrl(String doiMintingServiceUrl) {
		this.doiMintingServiceUrl = doiMintingServiceUrl;
	}
	public String getKeystoreFilePath() {
		return keystoreFilePath;
	}
	public void setKeystoreFilePath(String keystoreFilePath) {
		this.keystoreFilePath = keystoreFilePath;
	}
	public String getKeystorePassword() {
		return keystorePassword;
	}
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}
	public Boolean getAsciiCleanse() {
		return asciiCleanse;
	}
	public void setAsciiCleanse(Boolean asciiCleanse) {
		this.asciiCleanse = asciiCleanse;
	}
}
