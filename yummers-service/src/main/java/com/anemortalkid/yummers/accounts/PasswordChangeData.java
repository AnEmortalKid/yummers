package com.anemortalkid.yummers.accounts;

/**
 * Wrapper object for changing password requests
 * 
 * @author jmonterrubio
 *
 */
public class PasswordChangeData {

	private String username;
	private String oldPassword;
	private String newPassword;

	public PasswordChangeData() {
		// free json
	}

	/**
	 * @return username whos password should be changed
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            whos password should be changed
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username's current password which will become old
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword
	 *            the username's current password which will become old
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	/**
	 * @return the new password for the user
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the new password for the user
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PasswordChangeData [username=");
		builder.append(username);
		builder.append(", oldPassword=");
		builder.append(oldPassword);
		builder.append(", newPassword=");
		builder.append(newPassword);
		builder.append("]");
		return builder.toString();
	}

}
