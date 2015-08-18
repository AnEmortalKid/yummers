package com.anemortalkid.yummers.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.RC2ParameterSpec;

import org.hamcrest.BaseMatcher;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.experiment.FridayFinder;
import com.anemortalkid.yummers.foodpreference.FoodPreference;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceType;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.rotation.RotationRepository;
import com.anemortalkid.yummers.slots.Slot;
import com.anemortalkid.yummers.slots.SlotRepository;

import edu.emory.mathcs.backport.java.util.Arrays;

public class FoodEventScheduler {

	@Autowired
	private SlotRepository slotRepository;

	@Autowired
	private RotationController rotationController;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private FoodEventRepository foodEventRepository;

	public Rotation scheduleNewRotation() {
		// get the preferences
		List<FoodPreference> breakfastPrefs = foodPreferenceRepository
				.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST);
		List<Associate> breakfastAssociates = FoodPreferenceController.extractAssociates(breakfastPrefs);

		List<FoodPreference> snackPres = foodPreferenceRepository
				.findDistinctByPreferenceType(FoodPreferenceType.SNACK);
		List<Associate> snackAssociates = FoodPreferenceController.extractAssociates(snackPres);

		// check conditions
		if (breakfastAssociates.size() != snackAssociates.size()) {
			int difference = Math.abs(breakfastAssociates.size() - snackAssociates.size());
			if (difference > 1) {
				throw new IllegalArgumentException();
			}
		}

		/*
		 * check that at least there's 2 of each otherwise we cant's schedule a
		 * rotation
		 */
		if (breakfastAssociates.size() < 2 || snackAssociates.size() < 2) {
			throw new IllegalArgumentException();
		}

		// conditions guaranteed - check previous rotation
		YummersResponseEntity<Rotation> response = rotationController.getCurrentRotation();
		Rotation currentRotation = response.getBody();

		List<Associate> breakfastSchedulable = new ArrayList<>();
		List<Associate> snackSchedulable = new ArrayList<>();

		// get how many we need to schedule so they're even
		int maxSize = Math.max(breakfastAssociates.size(), snackAssociates.size());

		// we need them to be even so they can be scheduled evenly
		if (maxSize % 2 != 0) {
			maxSize += 1;
		}

		int breakfastIndex = 0;
		int snackIndex = 0;

		// get index of next rotation only if data wasn't stale
		if (currentRotation != null && !rotationController.shouldRegenerate()) {

			String breakfastId = currentRotation.getNextBreakfastStarter();
			String snackId = currentRotation.getNextSnackStarter();

			Associate breakfastStarter = associateRepository.findOne(breakfastId);
			Associate snackStarter = associateRepository.findOne(snackId);

			breakfastIndex = breakfastAssociates.indexOf(breakfastStarter);
			snackIndex = snackAssociates.indexOf(snackStarter);
		}

		for (int i = 0; i < maxSize; i++) {
			Associate breakfastAssoc = breakfastAssociates.get(breakfastIndex);
			Associate snackAssoc = snackAssociates.get(snackIndex);

			// add them to the list
			breakfastSchedulable.add(breakfastAssoc);
			snackSchedulable.add(snackAssoc);

			// calculate next index
			breakfastIndex = (breakfastIndex + 1) % breakfastAssociates.size();
			snackIndex = (snackIndex + 1) % snackAssociates.size();
		}

		/**
		 * Only rotate push to the next guy if we were even, otherwise the next
		 * index will autoadjust
		 */
		if (breakfastAssociates.size() == maxSize) {
			breakfastIndex = (breakfastIndex + 1) % breakfastAssociates.size();
		}
		if (snackAssociates.size() == maxSize) {
			snackIndex = (snackIndex + 1) % snackAssociates.size();
		}

		Associate nextBreakfastStarter = breakfastAssociates.get(breakfastIndex);
		Associate nextSnackStarter = snackAssociates.get(snackIndex);

		// create new rotation and inactivate the previous one
		Rotation rotation = new Rotation(extractIds(breakfastSchedulable), extractIds(snackSchedulable),
				nextBreakfastStarter.getAssociateId(), nextSnackStarter.getAssociateId(), true, false);
		rotationController.insertNewRotation(rotation);

		// Schedule the events
		List<FoodEvent> foodEventSchedule = createSchedule(breakfastSchedulable, snackSchedulable);
		// remove all
		foodEventRepository.deleteAll();
		foodEventRepository.save(foodEventSchedule);

		return rotation;
	}

	private static List<String> extractIds(List<Associate> associates) {
		return associates.parallelStream().map(x -> x.getAssociateId()).collect(Collectors.toList());
	}

	private List<FoodEvent> createSchedule(List<Associate> balancedBreakfast, List<Associate> balancedSnack) {

		// check how many fridays we need
		int fridaysNeeded = balancedBreakfast.size() / 2;
		List<Slot> slots = slotRepository.findAll();

		if (slots.size() < fridaysNeeded) {
			// get next fridays
			int fridaysToRequest = fridaysNeeded - slots.size();

			// find the next x+1 so we at least have 1 for next time
			fridaysToRequest++;

			// get last friday available
			DateTime lastSlotDate = null;
			if (slots.isEmpty()) {
				// well we are scheduling so get the time here
				lastSlotDate = new DateTime();
			} else {
				Slot lastSlot = slots.get(slots.size() - 1);
				lastSlotDate = lastSlot.getSlotDate();
			}
			List<DateTime> newFridays = FridayFinder.getNextFridaysFromDate(lastSlotDate, fridaysToRequest);
			// insert them slots
			newFridays.forEach(x -> slotRepository.save(new Slot(x)));
			slots = slotRepository.findAll();
		}

		// schedule that
		List<FoodEvent> foodEvents = new ArrayList<>();
		for (int i = 0; i < balancedBreakfast.size(); i += 2) {
			// get the pairs
			Associate b1 = balancedBreakfast.get(i);
			Associate b2 = balancedBreakfast.get(i + 1);
			Associate s1 = balancedSnack.get(i);
			Associate s2 = balancedSnack.get(i + 1);
			FoodEvent newEvent = new FoodEvent(getAssociateIds(b1, b2), getAssociateIds(s1, s2), slots.remove(0));
			foodEvents.add(newEvent);
		}

		return foodEvents;
	}

	private static List<String> getAssociateIds(Associate... associates) {
		List<String> associateIds = new ArrayList<>();
		for (Associate associate : associates) {
			associateIds.add(associate.getAssociateId());
		}
		return associateIds;
	}

}
