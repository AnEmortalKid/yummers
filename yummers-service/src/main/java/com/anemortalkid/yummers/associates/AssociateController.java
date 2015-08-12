package com.anemortalkid.yummers.associates;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.foodpreference.FoodPreference;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceType;
import com.anemortalkid.yummers.responses.ResponseFactory;

@RestController
@RequestMapping("/associates")
public class AssociateController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private FoodPreferenceController foodPreferenceController;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<List<Associate>> list() {
		return new ResponseEntity<List<Associate>>(
				associateRepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Associate> register(String firstName, String lastName)
			throws URISyntaxException {
		// check if it has been registered already
		Associate foundAssociate = associateRepository
				.findByFirstNameAndLastName(firstName, lastName);
		if (foundAssociate != null) {
			URI uri = new URI("/associates/register");
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setLocation(uri);
			responseHeaders.set("I_AM_A_TEAPOT", "Associate already exists.");
			return new ResponseEntity<Associate>(foundAssociate,
					responseHeaders, HttpStatus.I_AM_A_TEAPOT);
		}

		Associate associate = new Associate(firstName, lastName);
		associateRepository.save(associate);
		LOGGER.info("Created associate with id: " + associate);
		return new ResponseEntity<Associate>(associate, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}/setPreference", method = RequestMethod.POST)
	public ResponseEntity<FoodPreference> setPreferenceForAssociate(
			@PathVariable("id") String id, String foodPreference)
			throws URISyntaxException {
		String path = MessageFormat.format("/associates/{0}/setPreference", id);
		//
		// // Get a valid preference
		// FoodPreferenceType parsed = FoodPreferenceType
		// .parseString(foodPreference);
		// if (parsed == null) {
		// String foodPreferencesAllowed = Arrays.toString(FoodPreferenceType
		// .values());
		// return ResponseFactory.respondFail(path,
		// "Invalid preferences, valid ones are: "
		// + foodPreferencesAllowed);
		// }
		//
		// // Check if the associate exists
		// Associate foundAssociate = associateRepository.findOne(id);
		// if (foundAssociate == null) {
		// return ResponseFactory.respondFail(path,
		// "No associate exists with id =" + id);
		// }
		//
		// // Otherwise create it
		// FoodPreference fp = new FoodPreference(foundAssociate, parsed);
		// foodPreferenceRepository.save(fp);
		//
		// return ResponseFactory.respondCreated(fp);
		return foodPreferenceController.setFoodPreference(path, id,
				foodPreference);
	}

	@RequestMapping(value = "/preferences", method = RequestMethod.GET)
	public ResponseEntity<List<FoodPreference>> listAllPreferences() {
		return ResponseFactory.respondFound(foodPreferenceRepository.findAll());
	}

}
