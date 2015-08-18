package com.anemortalkid.yummers.rotation;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RotationRepository extends MongoRepository<Rotation, String> {

	List<Rotation> findByActive(boolean active);
	
}
