package com.anemortalkid.yummers.slots;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.banned.BannedDateController;

/**
 * A controller for Slots
 * 
 * @author JMonterrubio
 *
 */
@RestController
@RequestMapping("/slots")
public class SlotController {

	@Autowired
	private SlotRepository slotRepository;

	@Autowired
	private BannedDateController bannedDateController;

	public List<Slot> getNextXSlots(int slotCount) {
		List<Slot> slots = slotRepository.findByIsSchedulable(true);

		// remove the banned ones
		List<Slot> banned = slots.stream().filter((slot) -> bannedDateController.isBannedDate(slot.getSlotDate())).collect(Collectors.toList());
		if (!banned.isEmpty()) {
			// remove the banned ones from the slots
			System.out.println("Found some banned dates");
			banned.forEach((slot) -> {
				slot.setSchedulable(false);
				slotRepository.save(slot);
			});
			slots = slotRepository.findByIsSchedulable(true);
		}

		if (slots.size() < slotCount) {
			// get next fridays
			int fridaysToRequest = slotCount - slots.size();

			// find the next x+1 so we at least have 1 for next time
			fridaysToRequest++;

			// get last friday available
			LocalDate lastSlotDate = null;
			if (slots.isEmpty()) {
				// well we are scheduling so get the time here
				DateTime instant = new DateTime();
				lastSlotDate = instant.toLocalDate();
			} else {
				Slot lastSlot = slots.get(slots.size() - 1);
				lastSlotDate = lastSlot.getSlotDate();
			}
			List<LocalDate> newFridays = getNextSchedulableFridays(lastSlotDate, fridaysToRequest);

			// insert them slots
			newFridays.forEach(x -> slotRepository.save(new Slot(x)));
			slots = slotRepository.findByIsSchedulable(true);
		}

		// add them to a list and delete them from the slots
		List<Slot> returnable = new ArrayList<Slot>();
		for (int i = 0; i < slotCount; i++) {
			Slot slot = slots.get(i);
			slot.setSchedulable(false);
			slotRepository.save(slot);
			returnable.add(slot);
		}

		return returnable;
	}

	private List<LocalDate> getNextSchedulableFridays(LocalDate startDate, int fridaysNeeded) {
		List<LocalDate> possibleFridays = new ArrayList<>();
		LocalDate lastDate = startDate;
		int fridaysToRequest = fridaysNeeded;
		while (possibleFridays.size() < fridaysNeeded) {
			List<LocalDate> nextFridays = FridayFinder.getNextFridaysFromDate(lastDate, fridaysToRequest);
			List<LocalDate> nonBanned = nextFridays.stream().filter(dateTime -> !bannedDateController.isBannedDate(dateTime)).collect(Collectors.toList());
			possibleFridays.addAll(nonBanned);
			// set next date to be last of possibleFridays
			lastDate = possibleFridays.get(possibleFridays.size() - 1);
			fridaysToRequest = fridaysNeeded - nonBanned.size();
		}
		return possibleFridays;
	}

	/**
	 * Sets the given slot to not be schedulable
	 * 
	 * @param slot
	 *            the slot to set unschedulable on
	 */
	public void removeSlot(Slot slot) {
		slot.setSchedulable(false);
		slotRepository.save(slot);
	}

	/**
	 * For usage only by the actual running application, this should be called
	 * at some period of time.
	 * 
	 * @param slot
	 *            the slot to delete
	 */
	public void deleteSlot(Slot slot) {
		slotRepository.delete(slot);
	}
}
