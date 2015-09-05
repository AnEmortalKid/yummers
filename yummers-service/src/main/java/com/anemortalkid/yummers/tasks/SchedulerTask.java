package com.anemortalkid.yummers.tasks;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.anemortalkid.yummers.auth.AbstractAuthenticatedAction;
import com.anemortalkid.yummers.banned.BannedDateController;
import com.anemortalkid.yummers.foodevent.FoodEvent;
import com.anemortalkid.yummers.foodevent.FoodEventController;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.schedule.FoodEventScheduler;

/**
 * A class that contains scheduled methods that perform the continuous running
 * of the application.
 * 
 * @author JMonterrubio
 *
 */
@Component
public class SchedulerTask {

	private DateTimeFormatter dayMonthYear = DateTimeFormat.forPattern("dd/MM/yyyy");
	private DateTimeFormatter ddMMyyyHHmmss = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Value("${yummers.prod.super.user}")
	private String superUser;

	@Value("${yummers.prod.super.password}")
	private String superPassword;

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
	public void checkEventStateAction() {
		new AbstractAuthenticatedAction<Void>(superUser, superPassword) {

			@Override
			protected Void performAction() {
				LOGGER.info("Checking event state daily");
				checkEventStateDaily();
				return null;
			}
		}.perform();
	}

	private void checkEventStateDaily() {
		// check what day we are in
		DateTime currentDateTime = new DateTime();
		LOGGER.info("Current date " + currentDateTime.toString(ddMMyyyHHmmss));
		LocalDate currentDate = currentDateTime.toLocalDate();

		// get upcoming event or create it
		FoodEvent upcomingEvent = foodEventController.getUpcomingEvent();
		if (upcomingEvent == null) {
			// we need to create a rotation
			Rotation newRotation = rotationController.scheduleNewRotation();
			if (newRotation == null) {
				// there were data issues
				String unschedulableReason = foodEventScheduler.getUnschedulableReason();
				LOGGER.error(unschedulableReason);
				return;
			} else {
				LOGGER.info("Sending invite to participants");
				// send food schedule calendar invite to participants
				List<FoodEvent> activeEvents = foodEventController.getActiveEvents();
				foodEventScheduler.sendCalendarinvites(activeEvents);
			}
			upcomingEvent = foodEventController.getUpcomingEvent();
		}

		// check the date for the event
		LocalDate eventDate = upcomingEvent.getSlot().getSlotDate();
		int eventDay = eventDate.getDayOfMonth();
		int eventMonth = eventDate.getMonthOfYear();
		int eventYear = eventDate.getYear();
		LOGGER.info("Next upcoming event date " + eventDate.toString(dayMonthYear));

		// check date and if we should send reminder
		int currentDay = currentDateTime.getDayOfMonth();
		int currentMonth = currentDateTime.getMonthOfYear();
		int currentYear = currentDateTime.getYear();

		if (eventYear == currentYear) {
			if (eventMonth == currentMonth) {
				// check if we are 2 days away from event and send a reminder
				if (currentDay + 2 == eventDay) {
					LOGGER.info("Sending reminder to event participants");
					foodEventScheduler.sendEmailReminder(upcomingEvent);
				}
				if (currentDay == eventDay) {
					LOGGER.info("Event day today");
				}
				if (currentDateTime.getDayOfWeek() == DateTimeConstants.FRIDAY) {
					boolean isBanned = bannedDateController.isBannedDate(currentDate);
					if (isBanned) {
						LOGGER.info("Current friday is banned, date = " + currentDateTime.toString(dayMonthYear));
					}
				}
			}
		}

	}

	/**
	 * Check every Friday at 10:30 PM for the next upcoming event and deactivate
	 * that friday's event so the next one is fresh and new
	 */
	@Scheduled(cron = "0 30 22 ? * FRI")
	public void purgePastEventsAction() {
		new AbstractAuthenticatedAction<Void>(superUser, superPassword) {

			@Override
			protected Void performAction() {
				LOGGER.info("Purging past events");
				purgePastEvent();
				return null;
			}
		}.perform();
	}

	private void purgePastEvent() {
		FoodEvent upcomingEvent = foodEventController.getUpcomingEvent();
		if (upcomingEvent != null) {
			LOGGER.info("Deactivating event=" + upcomingEvent);
			foodEventController.deactivateEvent(upcomingEvent);
		}
	}

}
