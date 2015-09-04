package com.anemortalkid.yummers.slots;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SlotRepository extends MongoRepository<Slot, String>{

	List<Slot> findByIsSchedulable(boolean isSchedulable);
}
