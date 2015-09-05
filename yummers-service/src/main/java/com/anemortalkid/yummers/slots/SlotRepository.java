package com.anemortalkid.yummers.slots;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository for slots
 * 
 * @author JMonterrubio
 *
 */
public interface SlotRepository extends MongoRepository<Slot, String> {

	/**
	 * Reeturns slots that match the schedulable criteria
	 * 
	 * @param isSchedulable
	 *            whether the slot should be schedulable or not
	 * @return a List of Slots with the schedulable criteria
	 */
	List<Slot> findByIsSchedulable(boolean isSchedulable);
}
