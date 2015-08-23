package com.anemortalkid.yummers.foodevent;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodEventRepository extends MongoRepository<FoodEvent, String> {

	List<FoodEvent> findByIsActive(boolean isActive);

}
