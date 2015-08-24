package com.anemortalkid.yummers.rotation;

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
import com.anemortalkid.yummers.banned.BannedDate;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.anemortalkid.yummers.schedule.FoodEventScheduler;

@RestController
@RequestMapping("/rotations")
public class RotationController {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private RotationRepository rotationRepository;

	@Autowired
	private FoodPreferenceController foodPreferenceController;

	@Autowired
	private FoodEventScheduler foodEventScheduler;

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public YummersResponseEntity<Rotation> currentRotation() {
		String callingPath = "/rotations/current";
		Rotation current = getCurrentRotation();
		if (current == null) {
			return ResponseFactory.respondFail(callingPath, "no current rotations available");
		}
		return ResponseFactory.respondOK(callingPath, current);
	}

	@RequestMapping(value = "/past", method = RequestMethod.GET)
	public YummersResponseEntity<List<Rotation>> getPastRotations() {
		String callingPath = "/rotations/past";
	
		List<Rotation> inactiveRotations = rotationRepository.findByActive(false);
		return ResponseFactory.respondOK(callingPath, inactiveRotations);
	}

	public Rotation getCurrentRotation() {
		List<Rotation> activeRotations = rotationRepository.findByActive(true);
		if (activeRotations.isEmpty()) {
			return null;
		}
		if (activeRotations.size() > 1) {
			LOGGER.error("There are more active rotations (" + activeRotations.size() + ") than there should be.");
		}

		Rotation current = activeRotations.get(0);
		return current;
	}

	public void insertNewRotation(Rotation newRotation) {
		Rotation currRotation = getCurrentRotation();
		if (currRotation != null) {
			currRotation.setActive(false);
			rotationRepository.save(currRotation);
		}
		rotationRepository.save(newRotation);
	}

	public boolean shouldRegenerate() {
		Rotation currentRotation = getCurrentRotation();
		if (currentRotation == null) {
			return true;
		}

		Set<String> breakfastAssociates = currentRotation.getBreakfastAssociates().stream().collect(Collectors.toSet());
		Set<String> snackAssociates = currentRotation.getSnackAssociates().stream().collect(Collectors.toSet());

		List<Associate> associatesWithBreakfast = foodPreferenceController.getAssociatesWithBreakfast();
		List<Associate> associatesWithSnack = foodPreferenceController.getAssociatesWithSnack();

		// compare sizes first
		if (breakfastAssociates.size() != associatesWithBreakfast.size()) {
			return true;
		}

		if (snackAssociates.size() != associatesWithSnack.size()) {
			return true;
		}

		// check all ids contained
		Set<String> breakfastIds = associatesWithBreakfast.stream().map((associate) -> associate.getAssociateId())
				.collect(Collectors.toSet());
		for (String string : breakfastAssociates) {
			if (!breakfastIds.contains(string)) {
				return true;
			}
		}

		Set<String> snackIds = associatesWithSnack.stream().map((associate) -> associate.getAssociateId())
				.collect(Collectors.toSet());
		for (String string : snackAssociates) {
			if (!snackIds.contains(string)) {
				return true;
			}
		}

		return false;
	}

	public Rotation scheduleNewRotation() {
		return foodEventScheduler.scheduleNewRotation();
	}
}
