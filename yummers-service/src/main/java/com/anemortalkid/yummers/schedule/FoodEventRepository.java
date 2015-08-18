package com.anemortalkid.yummers.schedule;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodEventRepository extends MongoRepository<FoodEvent, String> {

}
