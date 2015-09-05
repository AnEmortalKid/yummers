package com.anemortalkid.yummers.foodevent;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for {@link FoodEvent} objects
 * 
 * @author JMonterrubio
 *
 */
public interface FoodEventRepository extends MongoRepository<FoodEvent, String> {

	/**
	 * Returns the FoodEvents that match the isActive criteria
	 * 
	 * @param isActive
	 *            whether the event should be active or not
	 * @return the FoodEvents that match the isActive criteria
	 */
	List<FoodEvent> findByIsActive(boolean isActive);

}
