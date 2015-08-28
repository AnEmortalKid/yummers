package com.anemortalkid.yummers.associates;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for Associates. Contains custom queries.
 * 
 * @author JMonterrubio
 *
 */
public interface AssociateRepository extends MongoRepository<Associate, String> {

	/**
	 * Finds an associate by using both the first and last name
	 * 
	 * @param firstName
	 *            the associates first name
	 * @param lastName
	 *            the associates last name
	 * @return the Associate with the given names, <code>null</code> if not
	 *         found
	 */
	List<Associate> findByFirstNameAndLastName(String firstName, String lastName);
}
