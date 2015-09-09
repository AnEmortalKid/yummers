package com.anemortalkid.yummers.accounts;

/**
 * A request body for checking the level for the credentials
 * 
 * @author JMonterrubio
 *
 */
public class CredentialData {

	private String username;
	private String password;

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

}
