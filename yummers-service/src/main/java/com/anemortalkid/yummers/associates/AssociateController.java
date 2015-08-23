package com.anemortalkid.yummers.associates;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.foodpreference.FoodPreference;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.google.common.base.Optional;

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
	public YummersResponseEntity<List<Associate>> list() {
		String callingPath = "/associates/list";
		return ResponseFactory.respondOK(callingPath, associateRepository.findAll());
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Associate> register(@RequestBody Associate associate) throws URISyntaxException {
		String callingPath = "/associates/register";

		// check if existerinos
		Optional<Associate> optionalAssociate = createIfNotExists(associate);
		if (!optionalAssociate.isPresent()) {
			String errorMessage = "Associate with given id = " + associate.getAssociateId() + " already existed";
			return ResponseFactory.respondFail(callingPath, errorMessage);
		}

		Associate created = optionalAssociate.get();
		LOGGER.info("Created associate=" + created);
		return ResponseFactory.respondCreated(callingPath, created);
	}

	@RequestMapping(value = "/registerWithPreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<FoodPreference> registerAssociateWithPreference(
			@RequestBody AssociateWithPreference associateWithPreference) throws URISyntaxException {
		String callingPath = "/associates/registerWithPreference";

		String associateId = associateWithPreference.getAssociateId();

		// check if existerinos
		Optional<Associate> optionalAssociate = createIfNotExists(associateId, associateWithPreference.getFirstName(),
				associateWithPreference.getLastName());
		if (!optionalAssociate.isPresent()) {
			String errorMessage = "Associate with given id = " + associateId
					+ " already existed. Did you mean to use /{associateId}/setPreference ?";
			return ResponseFactory.respondFail(callingPath, errorMessage);
		}

		// Otherwise create a preference
		return foodPreferenceController.setFoodPreference(callingPath, associateId,
				associateWithPreference.getFoodPreference());
	}

	public void compilePlox() {
		System.out.println("compilerinos");
	}

	@RequestMapping(value = "/registerMultiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<List<Associate>> registerMultiple(@RequestBody List<Associate> associates) {
		// TODO: impl this
		String callingPath = "associates/registerMultiple";
		associates.forEach(System.out::println);
		return ResponseFactory.respondCreated(callingPath, null);
	}

	@RequestMapping(value = "/{associateId}/setPreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<FoodPreference> setPreferenceForAssociate(
			@PathVariable("associateId") String associateId, @RequestBody String foodPreference)
					throws URISyntaxException {
		String path = MessageFormat.format("/associates/{0}/setPreference", associateId);
		return foodPreferenceController.setFoodPreference(path, associateId, foodPreference);
	}

	@RequestMapping(value = "/preferences", method = RequestMethod.GET)
	public YummersResponseEntity<List<FoodPreference>> listAllPreferences() {
		String callingPath = "associates/preferences";
		return ResponseFactory.respondFound(callingPath, foodPreferenceRepository.findAll());
	}

	private Optional<Associate> createIfNotExists(Associate associate) {
		String associateid = associate.getAssociateId();
		Associate found = associateRepository.findOne(associateid);
		if (found == null) {
			Associate saved = associateRepository.save(associate);
			return Optional.of(saved);
		}
		return Optional.absent();
	}

	public Associate findById(String associateId) {
		return associateRepository.findOne(associateId);
	}

	private Optional<Associate> createIfNotExists(String associateId, String firstName, String lastName) {
		Associate found = associateRepository.findOne(associateId);
		if (found == null) {
			Associate newAssociate = new Associate(associateId, firstName, lastName);
			Associate saved = associateRepository.save(newAssociate);
			return Optional.of(saved);
		}
		return Optional.absent();
	}

}
