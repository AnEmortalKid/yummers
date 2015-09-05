package com.anemortalkid.yummers.rotation;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for {@link Rotation} objects
 * 
 * @author JMonterrubio
 *
 */
public interface RotationRepository extends MongoRepository<Rotation, String> {

	/**
	 * Returns a List of rotations matching the active criteria
	 * 
	 * @param active
	 *            whether the {@link Rotation} should be active or not
	 * @return a List of Rotations with the given criteria
	 */
	List<Rotation> findByActive(boolean active);

}
