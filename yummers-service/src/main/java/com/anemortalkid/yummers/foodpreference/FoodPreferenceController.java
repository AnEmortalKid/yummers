package com.anemortalkid.yummers.foodpreference;

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
@RequestMapping("/foodPreferences")
public class FoodPreferenceController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@RequestMapping(value = "/snack", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> associatesWithSnack() {
		String callingPath = "/foodPreferences/snack";
		List<FoodPreference> foodPreferences = foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.SNACK);
		List<Associate> associatesWithSnack = extractAssociates(foodPreferences);
		return ResponseFactory.respondFound(callingPath, associatesWithSnack);
	}

	@RequestMapping(value = "/breakfast", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> associatesWithBreakfast() {
		String callingPath = "/foodPreferences/breakfast";
		List<FoodPreference> foodPreferences = foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST);
		List<Associate> breakfast = foodPreferences.parallelStream().map(fp -> fp.getAssociate()).collect(Collectors.toList());
		return ResponseFactory.respondFound(callingPath, breakfast);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public YummersResponseEntity<List<FoodPreference>> listAllPreferences() {
		String callingPath = "/foodPreferences/list";
		List<FoodPreference> allPreferences = foodPreferenceRepository.findAll();
		return ResponseFactory.respondFound(callingPath, allPreferences);
	}

	@RequestMapping(value = "/missing", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> associatesWithoutPreference() {
		String callingPath = "/foodPreferences/missing";

		// get all the associates
		List<Associate> associates = associateRepository.findAll();

		// find all our preferences
		List<FoodPreference> preferences = foodPreferenceRepository.findAll();

		Set<String> associatesWithPreferences = preferences.parallelStream().map(foodPreference -> foodPreference.getAssociate().getAssociateId()).collect(Collectors.toSet());

		List<Associate> missingPreferences = associates.parallelStream().filter(associate -> !associatesWithPreferences.contains(associate.getAssociateId())).collect(Collectors.toList());

		return ResponseFactory.respondOK(callingPath, missingPreferences);
	}

	/**
	 * Sets the preference on the Associate with the given ID to the preferred
	 * preference
	 * 
	 * @param callingPath
	 *            the location from which we are calling this method, a rest url
	 * @param associateId
	 *            the id for the associate to register a preference for
	 * @param foodPreference
	 *            the display of the associates food preference
	 * @return a {@link FoodPreference} with the associate and the enumeration
	 *         value for the preference
	 */
	public YummersResponseEntity<FoodPreference> setFoodPreference(String callingPath, String associateId, String foodPreference) {
		// Get a valid preference
		FoodPreferenceType parsed = FoodPreferenceType.parseString(foodPreference);
		if (parsed == null) {
			String foodPreferencesAllowed = Arrays.toString(FoodPreferenceType.values());
			return ResponseFactory.respondFail(callingPath, "Invalid preferences, valid ones are: " + foodPreferencesAllowed);
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
			LOGGER.info("Created preference of " + parsed + " for associate with id " + foundAssociate.getAssociateId());
			return ResponseFactory.respondCreated(callingPath, fp);
		} else {
			existing.setPreferenceType(parsed);
			foodPreferenceRepository.save(existing);
			LOGGER.info("Set preference to " + parsed + " for associate with id " + foundAssociate.getAssociateId());
			return ResponseFactory.respondOK(callingPath, existing);
		}
	}

	/**
	 * TODO DOC
	 * 
	 * @param associate
	 * @return
	 */
	public FoodPreference getFoodPreferenceForAssociate(Associate associate) {
		return foodPreferenceRepository.findByAssociate(associate);
	}

	public List<Associate> getAssociatesWithBreakfast() {
		List<FoodPreference> foodPreferences = foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST);
		List<Associate> associatesWithBreakfast = extractAssociates(foodPreferences);
		return associatesWithBreakfast;
	}

	public List<Associate> getAssociatesWithSnack() {
		List<FoodPreference> foodPreferences = foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.SNACK);
		List<Associate> associatesWithSnack = extractAssociates(foodPreferences);
		return associatesWithSnack;
	}

	public List<FoodPreference> getAllPreferences() {
		return foodPreferenceRepository.findAll();
	}

	/**
	 * Given a list of FoodPrefereence, this method extracts the Associate
	 * objects for each preference
	 * 
	 * @param foodPreferences
	 *            a list of {@link FoodPreference} to extract associates from
	 * @return a List of extracted {@link Associate} objects
	 */
	public static List<Associate> extractAssociates(List<FoodPreference> foodPreferences) {
		return foodPreferences.parallelStream().map(fp -> fp.getAssociate()).collect(Collectors.toList());
	}

}
