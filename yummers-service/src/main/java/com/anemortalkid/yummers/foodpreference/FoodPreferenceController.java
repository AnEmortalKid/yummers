package com.anemortalkid.yummers.foodpreference;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.responses.ResponseFactory;

@RestController
@RequestMapping("/preferences")
public class FoodPreferenceController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@RequestMapping(value = "/snack", method = RequestMethod.GET)
	public ResponseEntity<List<Associate>> associatesWithSnack() {
		List<FoodPreference> foodPreferences = foodPreferenceRepository
				.findDistinctByPreferenceType(FoodPreferenceType.SNACK);
		List<Associate> associatesWithSnack = extractAssociates(foodPreferences);
		return ResponseFactory.respondFound(associatesWithSnack);
	}

	@RequestMapping(value = "/breakfast", method = RequestMethod.GET)
	public ResponseEntity<List<Associate>> associatesWithBreakfast() {
		List<FoodPreference> foodPreferences = foodPreferenceRepository
				.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST);
		List<Associate> associatesWithSnack = extractAssociates(foodPreferences);
		return ResponseFactory.respondFound(associatesWithSnack);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<List<FoodPreference>> getAllPreferences() {
		return ResponseFactory.respondFound(foodPreferenceRepository.findAll());
	}

	@RequestMapping(value = "/{id}/set", method = RequestMethod.POST)
	public ResponseEntity<FoodPreference> setPreferenceForAssociate(
			@PathVariable("id") String id, String foodPreference)
			throws URISyntaxException {
		String path = MessageFormat.format("/preferences/{0}/set", id);
		return setFoodPreference(path, id, foodPreference);
	}

	@RequestMapping(value = "/missing", method = RequestMethod.GET)
	public ResponseEntity<List<Associate>> associatesWithoutPreference() {

		// get all the associates
		List<Associate> associates = associateRepository.findAll();

		// find all our preferences
		List<FoodPreference> preferences = foodPreferenceRepository.findAll();

		Set<String> associatesWithPreferences = preferences.parallelStream()
				.map(foodPreference -> foodPreference.getAssociate().getId())
				.collect(Collectors.toSet());

		List<Associate> missingPreferences = associates
				.parallelStream()
				.filter(associate -> !associatesWithPreferences
						.contains(associate.getId()))
				.collect(Collectors.toList());

		return ResponseFactory.respondOK(missingPreferences);
	}

	/**
	 * Public facing duh
	 * 
	 * @param callingPath
	 * @param id
	 * @param foodPreference
	 * @return
	 * @throws URISyntaxException
	 */
	public ResponseEntity<FoodPreference> setFoodPreference(String callingPath,
			String id, String foodPreference) throws URISyntaxException {
		// Get a valid preference
		FoodPreferenceType parsed = FoodPreferenceType
				.parseString(foodPreference);
		if (parsed == null) {
			String foodPreferencesAllowed = Arrays.toString(FoodPreferenceType
					.values());
			return ResponseFactory.respondFail(callingPath,
					"Invalid preferences, valid ones are: "
							+ foodPreferencesAllowed);
		}

		// Check if the associate exists
		Associate foundAssociate = associateRepository.findOne(id);
		if (foundAssociate == null) {
			return ResponseFactory.respondFail(callingPath,
					"No associate exists with id =" + id);
		}

		// Check to see if it exists
		FoodPreference existing = foodPreferenceRepository
				.findByAssociate(foundAssociate);
		if (existing == null) {
			FoodPreference fp = new FoodPreference(foundAssociate, parsed);
			foodPreferenceRepository.save(fp);
			return ResponseFactory.respondCreated(fp);
		} else {
			existing.setPreferenceType(parsed);
			foodPreferenceRepository.save(existing);
			return ResponseFactory.respondOK(existing);
		}

	}

	private List<Associate> extractAssociates(
			List<FoodPreference> foodPreferences) {
		return foodPreferences.parallelStream().map(fp -> fp.getAssociate())
				.collect(Collectors.toList());
	}

}
