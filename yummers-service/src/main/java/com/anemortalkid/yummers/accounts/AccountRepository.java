package com.anemortalkid.yummers.accounts;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String> {

	Account findByUsername(String username);

}
