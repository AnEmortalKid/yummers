package com.anemortalkid.yummers.experiment;

import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateController;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.foodevent.FoodEvent;
import com.anemortalkid.yummers.foodevent.FoodEventController;
import com.anemortalkid.yummers.foodevent.FoodEventRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.slots.BannedDate;
import com.anemortalkid.yummers.slots.BannedDateController;
import com.anemortalkid.yummers.slots.BannedDateRepository;
import com.anemortalkid.yummers.slots.Slot;
import com.anemortalkid.yummers.slots.SlotRepository;

@Component
public class Simulation {

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	@Autowired
	private SlotRepository slotRepository;

	@Autowired
	private RotationController rotationController;

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

	private DateTime simulationDate = new DateTime();

	private boolean setupData;

	private long associateId = 0L;

	private void tick() {
		simulationDate = simulationDate.plusDays(1);
	}

	private void setupData() {
		DateTimeFormatter pattern = DateTimeFormat.forPattern("dd/MM/yyyy");
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

		// clear repos
		associateRepository.deleteAll();
		slotRepository.deleteAll();
		foodEventRepository.deleteAll();
		foodPreferenceRepository.deleteAll();

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

	@Scheduled(fixedRate = 2000)
	public void checkStateAndDoAction() {
		System.out.println("Time:"
				+ simulationDate.toString(DateTimeFormat
						.forPattern("dd/MM/yyyy")));

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
			// rug rog
			System.out.println("Scheduled new rotation");
			rotationController.scheduleNewRotation();
			upcomingEvent = foodEventController.getUpcomingEvent();
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
			} else if (simDay == sDay + 1) {
				// make event obsolete
				System.out.println("Deactivating->" + upcomingEvent);
				foodEventController.deactivateEvent(upcomingEvent);
			}
		}
	}

	public static void main(String[] args) {
		Timer timer = new Timer();
		Simulation simulation = new Simulation();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				simulation.shouldUpdateSchedule();
			}
		}, 2000, 90000);
	}

}
