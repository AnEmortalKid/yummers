package com.anemortalkid.yummers.associates;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for Associates. Contains custom queries.
 * 
 * @author JMonterrubio
 *
 */
public interface AssociateRepository extends MongoRepository<Associate, String> {

	Associate findByFirstNameAndLastName(String firstName, String lastName);
	
}
