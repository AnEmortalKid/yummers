package com.anemortalkid.yummers.schedule;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateController;
import com.anemortalkid.yummers.foodevent.FoodEvent;
import com.anemortalkid.yummers.foodevent.FoodEventController;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceController;
import com.anemortalkid.yummers.postoffice.EventData;
import com.anemortalkid.yummers.postoffice.EmailData;
import com.anemortalkid.yummers.postoffice.PostmanController;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.slots.Slot;
import com.anemortalkid.yummers.slots.SlotController;

@Component
public class FoodEventScheduler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String LASTNAME_FIRSTNAME_PATTERN = "{0},{1}";

	@Value("${yummers.mail.summaryText}")
	private String summaryText;

	@Autowired
	private SlotController slotController;

	@Autowired
	private RotationController rotationController;

	@Autowired
	private AssociateController associateController;

	@Autowired
	private FoodPreferenceController foodPreferenceController;

	@Autowired
	private FoodEventController foodEventController;

	@Autowired
	private PostmanController postmanController;

	public boolean canScheduleRotation() {
		// get the preferences
		List<Associate> breakfastAssociates = foodPreferenceController.getAssociatesWithBreakfast();
		List<Associate> snackAssociates = foodPreferenceController.getAssociatesWithSnack();

		// check conditions
		if (breakfastAssociates.size() != snackAssociates.size()) {
			int difference = Math.abs(breakfastAssociates.size() - snackAssociates.size());
			if (difference > 1) {
				return false;
			}
		}

		/*
		 * check that at least there's 2 of each otherwise we cant's schedule a
		 * rotation
		 */
		if (breakfastAssociates.size() < 2 || snackAssociates.size() < 2) {
			return false;
		}

		return true;
	}

	public Rotation scheduleNewRotation() {
		if (!canScheduleRotation()) {
			return null;
		}

		// get the preferences
		List<Associate> breakfastAssociates = foodPreferenceController.getAssociatesWithBreakfast();
		List<Associate> snackAssociates = foodPreferenceController.getAssociatesWithSnack();

		// conditions guaranteed - check previous rotation
		YummersResponseEntity<Rotation> response = rotationController.currentRotation();
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

			Associate breakfastStarter = associateController.findById(breakfastId);
			System.out.println("Finding breakfast associate with id=" + breakfastId + " returned=" + breakfastStarter);
			Associate snackStarter = associateController.findById(snackId);

			System.out.println("Finding snack associate with id=" + snackId + " returned=" + snackStarter);
			breakfastIndex = breakfastAssociates.indexOf(breakfastStarter);
			snackIndex = snackAssociates.indexOf(snackStarter);
			System.out.println("IndexOfBreakfast=" + breakfastIndex + "indexOfSnack=" + snackIndex);
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
				nextBreakfastStarter.getAssociateId(), nextSnackStarter.getAssociateId(), true);
		rotationController.insertNewRotation(rotation);

		// Schedule the events
		List<FoodEvent> foodEventSchedule = createSchedule(breakfastSchedulable, snackSchedulable);

		// remove all
		foodEventController.saveNewEvents(foodEventSchedule);

		return rotation;
	}

	private static List<String> extractIds(List<Associate> associates) {
		return associates.parallelStream().map(x -> x.getAssociateId()).collect(Collectors.toList());
	}

	private List<FoodEvent> createSchedule(List<Associate> balancedBreakfast, List<Associate> balancedSnack) {

		// check how many fridays we need
		int fridaysNeeded = balancedBreakfast.size() / 2;
		List<Slot> slots = slotController.getNextXSlots(fridaysNeeded);

		// schedule that
		List<FoodEvent> foodEvents = new ArrayList<>();
		for (int i = 0; i < balancedBreakfast.size(); i += 2) {
			// get the pairs
			Associate b1 = balancedBreakfast.get(i);
			Associate b2 = balancedBreakfast.get(i + 1);
			Associate s1 = balancedSnack.get(i);
			Associate s2 = balancedSnack.get(i + 1);
			Slot slot = slots.remove(0);
			FoodEvent newEvent = new FoodEvent(getAssociateIds(b1, b2), getAssociateIds(s1, s2), slot);
			foodEvents.add(newEvent);
			slotController.removeSlot(slot);
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

	public String getUnschedulableReason() {
		List<Associate> breakfastAssociates = foodPreferenceController.getAssociatesWithBreakfast();
		List<Associate> snackAssociates = foodPreferenceController.getAssociatesWithSnack();

		// check conditions
		if (breakfastAssociates.size() != snackAssociates.size()) {
			int difference = Math.abs(breakfastAssociates.size() - snackAssociates.size());
			if (difference > 1) {
				return "Difference between associates with breakfast preference and snack preference is > 1 .";
			}
		}

		/*
		 * check that at least there's 2 of each otherwise we cant's schedule a
		 * rotation
		 */
		if (breakfastAssociates.size() < 2 || snackAssociates.size() < 2) {
			return "There aren't enough breakfast or snack preferences. Required a minimum of 2 for each.";
		}

		return "No issues";
	}

	/**
	 * 
	 * @param activeEvents
	 */
	public void sendCalendarinvites(List<FoodEvent> activeEvents) {
		// Generate the invites and mails
		for (FoodEvent foodEvent : activeEvents) {
			sendBreakfast(foodEvent);
			sendSnack(foodEvent);
			foodEventController.setCalendarInviteSent(foodEvent, true);
		}
	}

	private void sendBreakfast(FoodEvent foodEvent) {
		EventData breakfastInvite = createBreakfastInvite(foodEvent);
		String breakfastSubject = createSubject("Breakfast", foodEvent);
		List<String> emails = foodEvent.getBreakfastParticipants().stream().map(id -> createEmailfromId(id))
				.collect(Collectors.toList());

		postmanController.sendCalendarInviteData(emails, breakfastSubject, breakfastInvite);
	}

	private void sendSnack(FoodEvent foodEvent) {
		EventData snackInvite = createSnackfastInvite(foodEvent);
		String snackSubject = createSubject("Snack", foodEvent);
		List<String> emails = foodEvent.getSnackParticipants().stream().map(id -> createEmailfromId(id))
				.collect(Collectors.toList());

		postmanController.sendCalendarInviteData(emails, snackSubject, snackInvite);
	}

	private String createSubject(String topic, FoodEvent foodEvent) {
		return topic + " reminder for Food Friday on " + toDateString(foodEvent.getSlot().getSlotDate());
	}

	private EventData createCalendarInvite(DateTime startDate, DateTime endDate, String descriptionStr,
			String locationStr) {
		EventData calendarInviteData = new EventData();
		calendarInviteData.setDateTimeStart(startDate);
		calendarInviteData.setDateTimeEnd(endDate);
		calendarInviteData.setDescription(descriptionStr);
		calendarInviteData.setLocation(locationStr);
		calendarInviteData.setMailTo("postman@yummers-rest.com");
		calendarInviteData.setSummary(summaryText);
		return calendarInviteData;
	}

	private EventData createBreakfastInvite(FoodEvent foodEvent) {
		DateTime eventDate = foodEvent.getSlot().getSlotDate();
		DateTime startDate = new DateTime(eventDate.getYear(), eventDate.getMonthOfYear(), eventDate.getDayOfMonth(), 8,
				30, 0, 0);
		DateTime endDate = startDate.plusHours(2);
		List<String> breakfastIds = foodEvent.getBreakfastParticipants();
		String description = getDescription(breakfastIds.get(0), breakfastIds.get(1), eventDate, "Breakfast");
		return createCalendarInvite(startDate, endDate, description, "Aisle");
	}

	private EventData createSnackfastInvite(FoodEvent foodEvent) {
		DateTime eventDate = foodEvent.getSlot().getSlotDate();
		DateTime startDate = new DateTime(eventDate.getYear(), eventDate.getMonthOfYear(), eventDate.getDayOfMonth(),
				12, 0, 0, 0);
		DateTime endDate = startDate.plusHours(2);
		List<String> snackIds = foodEvent.getSnackParticipants();
		String description = getDescription(snackIds.get(0), snackIds.get(1), eventDate, "Snacks");
		return createCalendarInvite(startDate, endDate, description, "Aisle");
	}

	private String getDescription(String id1, String id2, DateTime eventDate, String type) {
		return MessageFormat.format("{0} remdiner for {1} and {2} on {3} .", type, createNameFromId(id1),
				createNameFromId(id2), toDateString(eventDate));
	}

	private String createNameFromId(String associateId) {
		Associate associate = associateController.findById(associateId);
		if (associate == null) {
			logger.error("Received an associate with id = " + associateId
					+ " to send a calendar invite. Associate does not exist in the system anymore.");
			return "";
		}
		String associateFullName = MessageFormat.format(LASTNAME_FIRSTNAME_PATTERN, associate.getLastName(),
				associate.getFirstName());
		return associateFullName;
	}

	private String createEmailfromId(String associateId) {
		Associate associate = associateController.findById(associateId);
		if (associate == null) {
			logger.error("Received an associate with id = " + associateId
					+ " to create an email. Associate does not exist in the system anymore.");
			return "";
		}
		return associate.getEmail();
	}

	private String toDateString(DateTime dateTime) {
		return dateTime.toString("dd/MMM/yyyy");
	}

	public void sendEmailReminder(FoodEvent upcomingEvent) {
		EmailData breakfastEmail = createBreakfastEmail(upcomingEvent);
		EmailData snackEmail = createSnackEmail(upcomingEvent);
		boolean breakfastSent = postmanController.sendEmailData(breakfastEmail);
		if (!breakfastSent) {
			logger.error("Failed to send breakfast reminders for event " + upcomingEvent);
		}
		boolean snackSent = postmanController.sendEmailData(snackEmail);
		if (!snackSent) {
			logger.error("Failed to send snack reminders for event " + upcomingEvent);
		}
		foodEventController.setReminderEmailSent(upcomingEvent, true);
	}

	private EmailData createBreakfastEmail(FoodEvent foodEvent) {
		EmailData email = new EmailData();
		String subject = createSubject("Breakfast", foodEvent);
		email.setSubject(subject);
		email.setContent(summaryText);
		email.setRecipients(createEmailList(foodEvent.getBreakfastParticipants()));
		return email;
	}

	private EmailData createSnackEmail(FoodEvent foodEvent) {
		EmailData email = new EmailData();
		String subject = createSubject("Snack", foodEvent);
		email.setSubject(subject);
		email.setContent(summaryText);
		email.setRecipients(createEmailList(foodEvent.getSnackParticipants()));
		return email;
	}

	private List<String> createEmailList(List<String> associateIds) {
		return associateIds.stream().map((id) -> createEmailfromId(id)).collect(Collectors.toList());
	}

}
