package com.anemortalkid.yummers.accounts;

/**
 * Enumerates the types of access the system allows
 * 
 * @author JMonterrubio
 *
 */
public enum YummersAccessLevel {

	/**
	 * Basically just gets
	 */
	ROLE_BASIC,

	/**
	 * Some POST access allowed
	 */
	ROLE_ADMIN,

	/**
	 * Everything super duper highly confidential that no one else can access,
	 * not sure what this is at the moment, but we can change that
	 */
	ROLE_SUPER,

}
