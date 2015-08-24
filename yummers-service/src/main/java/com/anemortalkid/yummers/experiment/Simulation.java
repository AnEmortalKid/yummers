package com.anemortalkid.yummers.experiment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateController;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.banned.BannedDate;
import com.anemortalkid.yummers.banned.BannedDateController;
import com.anemortalkid.yummers.banned.BannedDateRepository;
import com.anemortalkid.yummers.foodevent.FoodEvent;
import com.anemortalkid.yummers.foodevent.FoodEventController;
import com.anemortalkid.yummers.foodevent.FoodEventRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.rotation.RotationRepository;
import com.anemortalkid.yummers.slots.Slot;
import com.anemortalkid.yummers.slots.SlotRepository;

@Component
public class Simulation {

	DateTimeFormatter pattern = DateTimeFormat.forPattern("dd/MM/yyyy");

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private SlotRepository slotRepository;

	@Autowired
	private RotationController rotationController;

	@Autowired
	private RotationRepository rotationRepository;

	@Autowired
	private FoodEventRepository foodEventRepository;

	@Autowired
	private FoodPreferenceController foodPreferenceController;

	@Autowired
	private AssociateController associateController;

	@Autowired
	private FoodEventController foodEventController;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private BannedDateController bannedDateController;

	@Autowired
	private BannedDateRepository bannedDateRepository;

	private DateTime simulationDate = new DateTime();

	private boolean setupData;

	private long associateId = 0L;

	private void tick() {
		simulationDate = simulationDate.plusDays(1);
	}

	private void setupData() {

		// clear repos
		associateRepository.deleteAll();
		slotRepository.deleteAll();
		foodEventRepository.deleteAll();
		foodPreferenceRepository.deleteAll();
		rotationRepository.deleteAll();
		bannedDateRepository.deleteAll();

		bannedDateController
				.addBannedDate(DateTime.parse("18/9/2015", pattern));
		bannedDateController.addBannedDate(DateTime
				.parse("24/12/2015", pattern));
		bannedDateController.addBannedDate(DateTime
				.parse("25/12/2015", pattern));
		bannedDateController.addBannedDate(DateTime
				.parse("30/12/2015", pattern));
		bannedDateController.addBannedDate(DateTime
				.parse("31/12/2015", pattern));
		bannedDateController.addBannedDate(DateTime
				.parse("01/01/2016", pattern));
		List<BannedDate> bannedDates = bannedDateController.getBannedDates();
		bannedDates.forEach(bannedDate -> System.out.println("banneDate="
				+ bannedDate.getBannedDate().toString(pattern)));

		for (int i = 0; i < 8; i++) {
			associateId++;
			Associate ass = new Associate(associateId + "",
					associateId + "-fn", associateId + "-ln");
			associateController.register(ass);
			foodPreferenceController.setFoodPreference("simulation",
					associateId + "", "Breakfast");
		}

		for (int i = 0; i < 8; i++) {
			associateId++;
			Associate ass = new Associate(associateId + "",
					associateId + "-fn", associateId + "-ln");
			associateController.register(ass);
			foodPreferenceController.setFoodPreference("simulation",
					associateId + "", "Snack");
		}
	}

	// XXX
	// @Scheduled(fixedRate = 2000)
	public void checkStateAndDoAction() {
		System.out.println("Time:"
				+ simulationDate.toString(DateTimeFormat
						.forPattern("dd/MM/yyyy")));
		if (simulationDate.getDayOfWeek() == DateTimeConstants.FRIDAY)
			System.out.println("FRIDAY");
		if (!setupData) {
			setupData();
			setupData = true;
		}

		// check if there's a rotation
		Rotation currentRotation = rotationController.getCurrentRotation();

		// if not generate one
		if (currentRotation == null) {
			Rotation newRotation = rotationController.scheduleNewRotation();
			// send invites?
		}
		shouldUpdateSchedule();
		tick();
	}

	private void shouldUpdateSchedule() {
		FoodEvent upcomingEvent = foodEventController.getUpcomingEvent();

		// check date and see if it is friday
		if (upcomingEvent == null) {
			System.out.println("Scheduled new rotation");
			rotationController.scheduleNewRotation();
			upcomingEvent = foodEventController.getUpcomingEvent();
			System.out.println("next upcoming "
					+ upcomingEvent.getDate().getSlotDate().toString(pattern));
		}
		Slot eventDate = upcomingEvent.getDate();
		DateTime slotDate = eventDate.getSlotDate();

		// check the time
		int sDay = slotDate.getDayOfMonth();
		int sMonth = slotDate.getMonthOfYear();
		int sYear = slotDate.getYear();

		// versus current
		int simDay = simulationDate.getDayOfMonth();
		int simMonth = simulationDate.getMonthOfYear();
		int simYear = simulationDate.getYear();

		if (sMonth == simMonth && sYear == simYear) {

			if (simDay == sDay) {
				// friday
				System.out
						.println("Scheduled event with breakfast="
								+ upcomingEvent.getBreakfastParticipants()
								+ ", and snack="
								+ upcomingEvent.getSnackParticipants());
			} else if (simDay == sDay + 1) {
				// make event obsolete
				System.out.println("Deactivating->" + upcomingEvent);
				foodEventController.deactivateEvent(upcomingEvent);
			}
		}

		// generate new associates with new preferences midway through so it has
		// to regenerate a new rotation
		if (simMonth == 1 && simYear == 2016 && simDay == 12) {
			System.out
					.println("Generating new associates, should regenerate should now be true");
			associateId++;
			Associate ass = new Associate(associateId + "",
					associateId + "-fn", associateId + "-ln");
			associateController.register(ass);
			foodPreferenceController.setFoodPreference("simulation",
					associateId + "", "Breakfast");

			associateId++;
			Associate ass2 = new Associate(associateId + "", associateId
					+ "-fn", associateId + "-ln");
			associateController.register(ass2);
			foodPreferenceController.setFoodPreference("simulation",
					associateId + "", "Snack");
			System.out.println("Should regenerate="
					+ rotationController.shouldRegenerate());
		}
	}

}
