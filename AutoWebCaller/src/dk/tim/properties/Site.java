package dk.tim.properties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Site")
public class Site {
	private String loginUrl;
	private String loginButtonId; //System either uses loginId or loginName to find login button
	private String loginButtonName;
	private String usernameFieldId;
	private String passwordFieldId;
	private String username;
	private String passwordEncrypted;
	private String expetedUrlAfterLogin;

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLoginButtonId() {
		return loginButtonId;
	}

	public void setLoginButtonId(String loginButtonId) {
		this.loginButtonId = loginButtonId;
	}

	public String getLoginButtonName() {
		return loginButtonName;
	}

	public void setLoginButtonName(String loginButtonName) {
		this.loginButtonName = loginButtonName;
	}

	public String getUsernameFieldId() {
		return usernameFieldId;
	}

	public void setUsernameFieldId(String usernameFieldId) {
		this.usernameFieldId = usernameFieldId;
	}

	public String getPasswordFieldId() {
		return passwordFieldId;
	}

	public void setPasswordFieldId(String passwordFieldId) {
		this.passwordFieldId = passwordFieldId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordDecrypted() {
		return Encrypter.decrypt(passwordEncrypted);
	}

	public String getPasswordEncrypted() {
		return passwordEncrypted;
	}

	public void setPasswordEncrypted(String passwordEncrypted) {
		this.passwordEncrypted = passwordEncrypted;
	}

	public String getExpetedUrlAfterLogin() {
		return expetedUrlAfterLogin;
	}

	public void setExpetedUrlAfterLogin(String expetedUrlAfterLogin) {
		this.expetedUrlAfterLogin = expetedUrlAfterLogin;
	}

	@Override
	public String toString() {
		return String.format("Site [loginUrl=%s, loginButtonId=%s, usernameFieldId=%s, passwordFieldId=%s, username=%s, passwordEncrypted=%s, expetedUrlAfterLogin=%s]", loginUrl, loginButtonId, usernameFieldId, passwordFieldId, username, passwordEncrypted, expetedUrlAfterLogin);
	}
}