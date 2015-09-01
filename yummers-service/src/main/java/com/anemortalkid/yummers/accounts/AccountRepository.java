package com.anemortalkid.yummers.accounts;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A Repository for {@link Account} data
 * 
 * @author jmonterrubio
 *
 */
public interface AccountRepository extends MongoRepository<Account, String> {

	/**
	 * Finds an {@link Account} by the Account's username
	 * 
	 * @param username
	 *            the username for the account
	 * @return an {@link Account} or null if no account was foun
	 */
	Account findByUsername(String username);

}
