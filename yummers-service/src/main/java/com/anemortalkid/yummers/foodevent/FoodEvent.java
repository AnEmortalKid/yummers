package com.anemortalkid.yummers.foodevent;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.anemortalkid.yummers.slots.Slot;

public class FoodEvent {

	@Id
	private String id;

	private List<String> breakfastParticipants;
	private List<String> snackParticipants;
	private Slot slot;
	private boolean isActive;
	private boolean calendarInviteSent = false;
	private boolean reminderSent = false;

	public FoodEvent() {
		// free jason
	}

	/**
	 * @param id
	 * @param breakfastParticipants
	 * @param snackParticipants
	 * @param date
	 */
	public FoodEvent(List<String> breakfastParticipants, List<String> snackParticipants, Slot date) {
		this.breakfastParticipants = breakfastParticipants;
		this.snackParticipants = snackParticipants;
		this.slot = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getBreakfastParticipants() {
		return breakfastParticipants;
	}

	public void setBreakfastParticipants(List<String> breakfastParticipants) {
		this.breakfastParticipants = breakfastParticipants;
	}

	public List<String> getSnackParticipants() {
		return snackParticipants;
	}

	public void setSnackParticipants(List<String> snackParticipants) {
		this.snackParticipants = snackParticipants;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot date) {
		this.slot = date;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isCalendarInviteSent() {
		return calendarInviteSent;
	}

	public void setCalendarInviteSent(boolean calendarInviteSent) {
		this.calendarInviteSent = calendarInviteSent;
	}

	public boolean isReminderSent() {
		return reminderSent;
	}

	public void setReminderSent(boolean reminderSent) {
		this.reminderSent = reminderSent;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FoodEvent [id=");
		builder.append(id);
		builder.append(", breakfastParticipants=");
		builder.append(breakfastParticipants);
		builder.append(", snackParticipants=");
		builder.append(snackParticipants);
		builder.append(", date=");
		builder.append(slot);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append(", calendarInviteSent=");
		builder.append(calendarInviteSent);
		builder.append(", reminderSent=");
		builder.append(reminderSent);
		builder.append("]");
		return builder.toString();
	}

}
