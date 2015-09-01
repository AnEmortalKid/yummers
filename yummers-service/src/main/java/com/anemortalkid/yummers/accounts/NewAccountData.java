package com.anemortalkid.yummers.accounts;

/**
 * A wrapper object for requesting new account registration
 * 
 * @author jmonterrubio
 *
 */
public class NewAccountData {

	private String username;
	private String password;
	private YummersAccessLevel accessLevel;

	/**
	 * @return username for the new account
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            username for the new account
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @return password for the new account
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            password for the new account
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return access level for the new account
	 */
	public YummersAccessLevel getAccessLevel() {
		return accessLevel;
	}

	/**
	 * @param accessLevel
	 *            access level for the new account
	 */
	public void setAccessLevel(YummersAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewAccountData [username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", accessLevel=");
		builder.append(accessLevel);
		builder.append("]");
		return builder.toString();
	}

}
