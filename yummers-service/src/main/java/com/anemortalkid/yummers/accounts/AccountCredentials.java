package com.anemortalkid.yummers.accounts;

/**
 * Object which returns the username and role level of an account, for callers
 * that wish to check for authentication access prior to calling any of the
 * authenitcated methods
 * 
 * @author JMonterrubio
 *
 */
public class AccountCredentials {

	private String username;
	private YummersAccessLevel accessLevel;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public YummersAccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(YummersAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

}
