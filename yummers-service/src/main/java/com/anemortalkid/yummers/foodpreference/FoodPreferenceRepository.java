package com.anemortalkid.yummers.foodpreference;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.anemortalkid.yummers.associates.Associate;

/**
 * Repository for {@link FoodPreference} objects
 * 
 * @author JMonterrubio
 *
 */
public interface FoodPreferenceRepository extends MongoRepository<FoodPreference, String> {

	/**
	 * Finds the unique FoodPreference's with the given type
	 * 
	 * @param preferenceType
	 *            the {@link FoodPreferenceType}
	 * @return the unique FoodPreference's with the given type
	 */
	List<FoodPreference> findDistinctByPreferenceType(FoodPreferenceType preferenceType);

	/**
	 * Finds the {@link FoodPreference} tied to the given {@link Associate}
	 * 
	 * @param associate
	 *            the {@link Associate} whose {@link FoodPreference} should be
	 *            found
	 * @return a {@link FoodPreference} if one exists or <code>null</code> if
	 *         non existed.
	 */
	FoodPreference findByAssociate(Associate associate);

}