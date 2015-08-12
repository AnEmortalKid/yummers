package com.anemortalkid.yummers.foodpreference;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.anemortalkid.yummers.associates.Associate;

public interface FoodPreferenceRepository extends
		MongoRepository<FoodPreference, String> {

	List<FoodPreference> findDistinctByPreferenceType(FoodPreferenceType preferenceType);
	
	FoodPreference findByAssociateId(String associateId);
	
	FoodPreference findByAssociate(Associate associate);
	
}
