package com.anemortalkid.yummers.foodpreference;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;

@RestController
@RequestMapping("/preferences")
public class FoodPreferenceController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@RequestMapping(value = "/snack", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> associatesWithSnack() {
		String callingPath = "/preferences/snack";
		List<FoodPreference> foodPreferences = foodPreferenceRepository
				.findDistinctByPreferenceType(FoodPreferenceType.SNACK);
		List<Associate> associatesWithSnack = extractAssociates(foodPreferences);
		return ResponseFactory.respondFound(callingPath, associatesWithSnack);
	}

	@RequestMapping(value = "/breakfast", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> associatesWithBreakfast() {
		String callingPath = "/preferences/breakfast";
		List<FoodPreference> foodPreferences = foodPreferenceRepository
				.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST);
		List<Associate> associatesWithSnack = extractAssociates(foodPreferences);
		return ResponseFactory.respondFound(callingPath, associatesWithSnack);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public YummersResponseEntity<List<FoodPreference>> getAllPreferences() {
		String callingPath = "/preferences/list";
		return ResponseFactory.respondFound(callingPath, foodPreferenceRepository.findAll());
	}

	@RequestMapping(value = "/missing", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> associatesWithoutPreference() {
		String callingPath = "/preferences/missing";

		// get all the associates
		List<Associate> associates = associateRepository.findAll();

		// find all our preferences
		List<FoodPreference> preferences = foodPreferenceRepository.findAll();

		Set<String> associatesWithPreferences = preferences.parallelStream()
				.map(foodPreference -> foodPreference.getAssociate().getAssociateId()).collect(Collectors.toSet());

		List<Associate> missingPreferences = associates.parallelStream()
				.filter(associate -> !associatesWithPreferences.contains(associate.getAssociateId()))
				.collect(Collectors.toList());

		return ResponseFactory.respondOK(callingPath, missingPreferences);
	}

	/**
	 * Public facing duh
	 * 
	 * @param callingPath
	 * @param associateId
	 * @param foodPreference
	 * @return
	 * @throws URISyntaxException
	 */
	public YummersResponseEntity<FoodPreference> setFoodPreference(String callingPath, String associateId,
			String foodPreference) throws URISyntaxException {
		// Get a valid preference
		FoodPreferenceType parsed = FoodPreferenceType.parseString(foodPreference);
		if (parsed == null) {
			String foodPreferencesAllowed = Arrays.toString(FoodPreferenceType.values());
			return ResponseFactory.respondFail(callingPath,
					"Invalid preferences, valid ones are: " + foodPreferencesAllowed);
		}

		// Check if the associate exists
		Associate foundAssociate = associateRepository.findOne(associateId);
		if (foundAssociate == null) {
			return ResponseFactory.respondFail(callingPath, "No associate exists with associateId=" + associateId);
		}

		// Check to see if it exists
		FoodPreference existing = foodPreferenceRepository.findByAssociate(foundAssociate);
		if (existing == null) {
			FoodPreference fp = new FoodPreference(foundAssociate, parsed);
			foodPreferenceRepository.save(fp);
			LOGGER.info(
					"Created preference of " + parsed + " for associate with id " + foundAssociate.getAssociateId());
			return ResponseFactory.respondCreated(callingPath, fp);
		} else {
			existing.setPreferenceType(parsed);
			foodPreferenceRepository.save(existing);
			LOGGER.info("Set preference to " + parsed + " for associate with id " + foundAssociate.getAssociateId());
			return ResponseFactory.respondOK(callingPath, existing);
		}
	}

	private List<Associate> extractAssociates(List<FoodPreference> foodPreferences) {
		return foodPreferences.parallelStream().map(fp -> fp.getAssociate()).collect(Collectors.toList());
	}

}
