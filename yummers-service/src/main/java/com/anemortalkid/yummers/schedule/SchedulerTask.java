package com.anemortalkid.yummers.schedule;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.anemortalkid.yummers.foodevent.FoodEvent;
import com.anemortalkid.yummers.foodevent.FoodEventController;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.slots.BannedDateController;

@Component
public class SchedulerTask {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private DateTimeFormatter dayMonthYear = DateTimeFormat.forPattern("dd/MM/yyyy");
	private DateTimeFormatter ddMMyyyHHmmss = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

	@Autowired
	private FoodEventController foodEventController;

	@Autowired
	private RotationController rotationController;

	@Autowired
	private FoodEventScheduler foodEventScheduler;

	@Autowired
	private BannedDateController bannedDateController;

	/**
	 * Every day at 6:30 AM check what the application state is, create a new
	 * rotation if we have to. Send email reminders if we have to. Check that
	 * stuff is schedulable, otherwise do nothing.
	 */
	@Scheduled(cron = "0 30 6 ? * *")
	public void checkEventStateDaily() {

		// check what day we are in
		DateTime currentDateTime = new DateTime();
		logger.info("Current date " + currentDateTime.toString(ddMMyyyHHmmss));

		// get upcoming event or create it
		FoodEvent upcomingEvent = foodEventController.getUpcomingEvent();
		if (upcomingEvent == null) {
			// we need to create a rotation
			Rotation newRotation = rotationController.scheduleNewRotation();
			if (newRotation == null) {
				// there were data issues
				String unschedulableReason = foodEventScheduler.getUnschedulableReason();
				logger.error(unschedulableReason);
				return;
			} else {
				logger.info("Sending invite to participants");
				// send food schedule calendar invite to participants
				List<FoodEvent> activeEvents = foodEventController.getActiveEvents();
				foodEventScheduler.sendCalendarinvites(activeEvents);
			}
			upcomingEvent = foodEventController.getUpcomingEvent();
		}

		// check the date for the event
		DateTime eventDate = upcomingEvent.getDate().getSlotDate();
		int eventDay = eventDate.getDayOfMonth();
		int eventMonth = eventDate.getMonthOfYear();
		int eventYear = eventDate.getYear();
		logger.info("Next upcoming event date " + eventDate.toString(dayMonthYear));

		// check date and if we should send reminder
		int currentDay = currentDateTime.getDayOfMonth();
		int currentMonth = currentDateTime.getMonthOfYear();
		int currentYear = currentDateTime.getYear();

		if (eventYear == currentYear) {
			if (eventMonth == currentMonth) {
				// check if we are 2 days away from event and send a reminder
				if (currentDay + 2 == eventDay) {
					logger.info("Sending reminder to event participants");
					foodEventScheduler.sendEmailReminder(upcomingEvent);
				}
				if (currentDay == eventDay) {
					logger.info("Event day today");
				}
				if (currentDateTime.getDayOfWeek() == DateTimeConstants.FRIDAY) {
					boolean isBanned = bannedDateController.isBannedDate(currentDateTime);
					if (isBanned) {
						logger.info("Current friday is banned, date = " + currentDateTime.toString(dayMonthYear));
					}
				}
			}
		}

	}

	/**
	 * Check every Friday at 10:30 PM for the next upcoming event and deactivate
	 */
	@Scheduled(cron = "0 30 22 ? * FRI")
	private void purgePastEvent() {
		FoodEvent upcomingEvent = foodEventController.getUpcomingEvent();
		if (upcomingEvent != null) {
			logger.info("Deactivating event=" + upcomingEvent);
			foodEventController.deactivateEvent(upcomingEvent);
		}
	}

}
