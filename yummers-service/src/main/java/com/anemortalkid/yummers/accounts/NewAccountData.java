package com.anemortalkid.yummers.accounts;

public class NewAccountData {

	private String userName;
	private String password;
	private YummersAccessLevel accessLevel;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public YummersAccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(YummersAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

}
