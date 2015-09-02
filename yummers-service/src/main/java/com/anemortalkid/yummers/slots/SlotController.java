package com.anemortalkid.yummers.slots;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.banned.BannedDateController;
import com.anemortalkid.yummers.experiment.FridayFinder;

@RestController
@RequestMapping("/slots")
public class SlotController {

	@Autowired
	private SlotRepository slotRepository;

	@Autowired
	private BannedDateController bannedDateController;
	
	public List<Slot> getNextXSlots(int slotCount) {
		List<Slot> slots = slotRepository.findAll();

		// remove the banned ones
		List<Slot> banned = slots.stream().filter((slot) -> bannedDateController.isBannedDate(slot.getSlotDate()))
				.collect(Collectors.toList());
		if (!banned.isEmpty()) {
			// remove the banned ones from the slots
			System.out.println("Found some banned dates");
			banned.forEach((slot) -> slotRepository.delete(slot));
			slots = slotRepository.findAll();
		}

		if (slots.size() < slotCount) {
			// get next fridays
			int fridaysToRequest = slotCount - slots.size();

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
			List<DateTime> newFridays = getNextSchedulableFridays(lastSlotDate, fridaysToRequest);

			// insert them slots
			newFridays.forEach(x -> slotRepository.save(new Slot(x)));
			slots = slotRepository.findAll();
		}

		// add them to a list and delete them from the slots
		List<Slot> returnable = new ArrayList<Slot>();
		for (int i = 0; i < slotCount; i++) {
			returnable.add(slots.get(i));
		}

		return returnable;
	}

	private List<DateTime> getNextSchedulableFridays(DateTime startDate, int fridaysNeeded) {
		List<DateTime> possibleFridays = new ArrayList<>();
		DateTime lastDate = startDate;
		int fridaysToRequest = fridaysNeeded;
		while (possibleFridays.size() < fridaysNeeded) {
			List<DateTime> nextFridays = FridayFinder.getNextFridaysFromDate(lastDate, fridaysToRequest);
			List<DateTime> nonBanned = nextFridays.stream()
					.filter(dateTime -> !bannedDateController.isBannedDate(dateTime)).collect(Collectors.toList());
			possibleFridays.addAll(nonBanned);
			// set next date to be last of possibleFridays
			lastDate = possibleFridays.get(possibleFridays.size() - 1);
			fridaysToRequest = fridaysNeeded - nonBanned.size();
		}
		return possibleFridays;
	}

	public void removeSlot(Slot slot) {
		slotRepository.delete(slot);
	}
}
