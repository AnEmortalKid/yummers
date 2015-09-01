package com.anemortalkid.yummers.associates;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.foodpreference.FoodPreference;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.google.common.base.Optional;

/**
 * Controller for Associates and Associate related operations
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
		List<FoodPreference> preferences = foodPreferenceController.getAllPreferences();
		return ResponseFactory.respondOK(callingPath, preferences);
	}

	/**
	 * Registers the given Associate
	 * 
	 * @param associate
	 *            the Associate to register
	 * @return a {@link YummersResponseEntity} with the registered associate
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Associate> register(@RequestBody Associate associate) {
		String callingPath = "/associates/register";

		// check if valid
		if (!isValidAssociate(associate)) {
			return ResponseFactory.respondFail(callingPath, "The given request for associate does not have all the required fields.");
		}

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

	/**
	 * Registers an Associate with a given food preference
	 * 
	 * @param associateWithPreference
	 *            the Associate and FoodPreference combination
	 * @return a {@link FoodPreference} with the data for both the associate and
	 *         the preference
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/registerWithPreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<FoodPreference> registerAssociateWithPreference(@RequestBody AssociateWithPreference associateWithPreference) {
		String callingPath = "/associates/registerWithPreference";

		String associateId = associateWithPreference.getAssociateId();

		// check if existerinos
		Optional<Associate> optionalAssociate = createIfNotExists(associateId, associateWithPreference.getFirstName(), associateWithPreference.getLastName());
		if (!optionalAssociate.isPresent()) {
			String errorMessage = "Associate with given id = " + associateId + " already existed. Did you mean to use /{associateId}/setPreference ?";
			return ResponseFactory.respondFail(callingPath, errorMessage);
		}

		// Otherwise create a preference
		return foodPreferenceController.setFoodPreference(callingPath, associateId, associateWithPreference.getFoodPreference());
	}

	/**
	 * Registers multiple associates at once
	 * 
	 * @param associates
	 *            the associates to register
	 * @return a {@link YummersResponseEntity} with the List of
	 *         {@link Associate}s that were registered
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/registerMultiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<List<Associate>> registerMultiple(@RequestBody List<Associate> associates) {
		String callingPath = "associates/registerMultiple";
		List<Associate> registeredAssociates = new ArrayList<Associate>();
		List<Associate> associatesToRegister = associates.stream().filter(associate -> isValidAssociate(associate)).collect(Collectors.toList());

		List<YummersResponseEntity<Associate>> responses = new ArrayList<YummersResponseEntity<Associate>>();
		associatesToRegister.forEach(associate -> responses.add(register(associate)));

		responses.forEach(yre -> {
			if (yre.getStatusCode().equals(HttpStatus.CREATED))
				registeredAssociates.add(yre.getBody());
		});

		return ResponseFactory.respondCreated(callingPath, registeredAssociates);
	}

	/**
	 * Retrieves the {@link FoodPreference} for a particular {@link Associate}
	 * with the given id
	 * 
	 * @param associateId
	 *            the id for the associate
	 * @return a {@link YummersResponseEntity} with the {@link FoodPreference}
	 *         for the associate, or failed responses if the associateId did not
	 *         exist or the associate did not have a food preference
	 */
	@RequestMapping(value = "/{associateId}/foodPreference", method = RequestMethod.GET)
	public YummersResponseEntity<FoodPreference> getPreferenceForAssociate(@PathVariable("associateId") String associateId) {
		String callingPath = MessageFormat.format("/associates/{0}/foodPreference", associateId);
		Associate associate = associateRepository.findOne(associateId);
		if (associate == null) {
			return ResponseFactory.respondFail(callingPath, "Associate with id=" + associateId + " did not exist in the system.");
		}
		FoodPreference foodPreference = foodPreferenceController.getFoodPreferenceForAssociate(associate);
		if (foodPreference == null) {
			return ResponseFactory.respondFail(callingPath, " No food preference found for associate with id=" + associateId);
		}
		return ResponseFactory.respondFound(callingPath, foodPreference);
	}

	/**
	 * Sets the preference for the associate with ID to the desired preference
	 * 
	 * @param associateId
	 *            the id for the associate
	 * @param foodPreference
	 *            the preference for the associate
	 * @return a {@link YummersResponseEntity} with the new
	 *         {@link FoodPreference} for the associate
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{associateId}/setPreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<FoodPreference> setPreferenceForAssociate(@PathVariable("associateId") String associateId, @RequestBody AssociateFoodPreference associateFoodPreference) {
		String foodPreference = associateFoodPreference.getFoodPreference();
		String path = MessageFormat.format("/associates/{0}/setPreference", associateId);
		return foodPreferenceController.setFoodPreference(path, associateId, foodPreference);
	}

	/**
	 * Unregisters an associate with the given associate id
	 * 
	 * @param associateId
	 *            the id for the {@link Associate} to unregister
	 * @return a {@link YummersResponseEntity} with <code>true</code> if the
	 *         associate was unregistered or a failed response with an error
	 *         message
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{associateId}/unregister", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> unregisterAssociate(@PathVariable("associateId") String associateId) {
		String callingPath = "associates/{associateId}/unregister";
		Associate associateToUnregister = associateRepository.findOne(associateId);

		if (associateToUnregister != null) {
			associateRepository.delete(associateToUnregister);
			return ResponseFactory.respondOK(callingPath, true);
		}

		String errorMessage = "No associate with id=" + associateId + " existed to be unregistered.";
		return ResponseFactory.respondFail(callingPath, errorMessage);
	}

	/**
	 * Returns a list of all the associates within the repository
	 * 
	 * @return a list of all the associates within the repository
	 */
	public List<Associate> getAllAssociates() {
		return associateRepository.findAll();
	}

	/**
	 * Returns an associate with the matching associateId, or <code>null</code>
	 * if the associate couldn't be found
	 * 
	 * @param associateId
	 *            the associateId to match
	 * @return an Associate with that Id, or <code>null</code> if that
	 *         associateId did not match to an associate
	 */
	public Associate findById(String associateId) {
		return associateRepository.findOne(associateId);
	}

	/**
	 * Creates an associate if it does not exist in the system
	 * 
	 * @param associate
	 *            the associate to create
	 * @return an Optional of the associate that was created, or absent if the
	 *         associate already existed
	 */
	private Optional<Associate> createIfNotExists(Associate associate) {
		String associateid = associate.getAssociateId();
		Associate found = associateRepository.findOne(associateid);
		if (found == null) {
			Associate saved = associateRepository.save(associate);
			return Optional.of(saved);
		}
		return Optional.absent();
	}

	/**
	 * Creates an associate if it does not exist in the system
	 * 
	 * @param associate
	 *            the associate to create
	 * @param firstName
	 *            the first name of the associate
	 * @param lastName
	 *            the last name of the associate
	 * @return an Optional of the associate that was created, or absent if the
	 *         associate already existed
	 */
	private Optional<Associate> createIfNotExists(String associateId, String firstName, String lastName) {
		Associate found = associateRepository.findOne(associateId);
		if (found == null) {
			Associate newAssociate = new Associate(associateId, firstName, lastName);
			Associate saved = associateRepository.save(newAssociate);
			return Optional.of(saved);
		}
		return Optional.absent();
	}

	/**
	 * Validates that the associate has at least the three required fields
	 * populated
	 * 
	 * @param associate
	 *            the associate to validate
	 * @return <code>true</code> if the associate is valid, false otherwise
	 */
	private boolean isValidAssociate(Associate associate) {
		if (StringUtils.isBlank(associate.getAssociateId())) {
			return false;
		}
		if (StringUtils.isBlank(associate.getFirstName())) {
			return false;
		}
		if (StringUtils.isBlank(associate.getLastName())) {
			return false;
		}
		return true;
	}

}
