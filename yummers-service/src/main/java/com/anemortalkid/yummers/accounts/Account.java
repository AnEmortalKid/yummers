package com.anemortalkid.yummers.accounts;

import org.springframework.data.annotation.Id;

public class Account {

	@Id
	private String id;
	private String username;
	private String password;
	private String accessLevel;

	public Account() {
	}

	public Account(String username, String password, String accessLevel) {
		this.username = username;
		this.password = password;
		this.accessLevel = accessLevel;
	}

	public Account(String userName, String password, YummersAccessLevel yummersAccessLevel) {
		this.username = userName;
		this.password = password;
		this.accessLevel = yummersAccessLevel.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

}