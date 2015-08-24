package com.anemortalkid.yummers.associates;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

/**
 * Controller for Associates
 * 
 * @author JMonterrubio
 *
 */
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

	/**
	 * Returns a response with a list of all the {@link Associate}s
	 * 
	 * @return a {@link YummersResponseEntity} with a list of all the
	 *         {@link Associate}s
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public YummersResponseEntity<List<Associate>> list() {
		String callingPath = "/associates/list";
		return ResponseFactory.respondOK(callingPath, getAllAssociates());
	}

	/**
	 * Returns a response with a list of all the {@link FoodPreference}s
	 * 
	 * @return a response with a list of all the {@link FoodPreference}s
	 */
	@RequestMapping(value = "/preferences", method = RequestMethod.GET)
	public YummersResponseEntity<List<FoodPreference>> listAllPreferences() {
		String callingPath = "associates/preferences";
		List<FoodPreference> preferences = foodPreferenceRepository.findAll();
		return ResponseFactory.respondOK(callingPath, preferences);
	}

	/**
	 * Registers the given Associate
	 * 
	 * @param associate
	 *            the Associate to register
	 * @return a {@link YummersResponseEntity} with the registered associate
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Associate> register(
			@RequestBody Associate associate) {
		String callingPath = "/associates/register";

		// check if existerinos
		Optional<Associate> optionalAssociate = createIfNotExists(associate);
		if (!optionalAssociate.isPresent()) {
			String errorMessage = "Associate with given id = "
					+ associate.getAssociateId() + " already existed";
			return ResponseFactory.respondFail(callingPath, errorMessage);
		}

		Associate created = optionalAssociate.get();
		LOGGER.info("Created associate=" + created);
		return ResponseFactory.respondCreated(callingPath, created);
	}

	/**
	 * Registers an Associate with a given food preference
	 * 
	 * @param associateWithPreference
	 *            the Associate and FoodPreference combination
	 * @return a {@link FoodPreference} with the data for both the associate and
	 *         the preference
	 */
	@RequestMapping(value = "/registerWithPreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<FoodPreference> registerAssociateWithPreference(
			@RequestBody AssociateWithPreference associateWithPreference) {
		String callingPath = "/associates/registerWithPreference";

		String associateId = associateWithPreference.getAssociateId();

		// check if existerinos
		Optional<Associate> optionalAssociate = createIfNotExists(associateId,
				associateWithPreference.getFirstName(),
				associateWithPreference.getLastName());
		if (!optionalAssociate.isPresent()) {
			String errorMessage = "Associate with given id = "
					+ associateId
					+ " already existed. Did you mean to use /{associateId}/setPreference ?";
			return ResponseFactory.respondFail(callingPath, errorMessage);
		}

		// Otherwise create a preference
		return foodPreferenceController.setFoodPreference(callingPath,
				associateId, associateWithPreference.getFoodPreference());
	}

	@RequestMapping(value = "/registerMultiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<List<Associate>> registerMultiple(
			@RequestBody List<Associate> associates) {
		String callingPath = "associates/registerMultiple";
		List<Associate> registeredAssociates = new ArrayList<Associate>();
		List<YummersResponseEntity<Associate>> responses = new ArrayList<YummersResponseEntity<Associate>>();
		associates.forEach(associate -> responses.add(register(associate)));
		responses.forEach(yre -> {
			if (yre.getStatusCode().equals(HttpStatus.CREATED))
				registeredAssociates.add(yre.getBody());
		});
		return ResponseFactory
				.respondCreated(callingPath, registeredAssociates);
	}

	@RequestMapping(value = "/{associateId}/setPreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<FoodPreference> setPreferenceForAssociate(
			@PathVariable("associateId") String associateId,
			@RequestBody String foodPreference) throws URISyntaxException {
		String path = MessageFormat.format("/associates/{0}/setPreference",
				associateId);
		return foodPreferenceController.setFoodPreference(path, associateId,
				foodPreference);
	}

	/**
	 * Returns a list of all the associates within the repository
	 * 
	 * @return a list of all the associates within the repository
	 */
	public List<Associate> getAllAssociates() {
		return associateRepository.findAll();
	}

	public Associate findById(String associateId) {
		return associateRepository.findOne(associateId);
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

	private Optional<Associate> createIfNotExists(String associateId,
			String firstName, String lastName) {
		Associate found = associateRepository.findOne(associateId);
		if (found == null) {
			Associate newAssociate = new Associate(associateId, firstName,
					lastName);
			Associate saved = associateRepository.save(newAssociate);
			return Optional.of(saved);
		}
		return Optional.absent();
	}

}
