package com.anemortalkid.yummers.foodevent;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.anemortalkid.yummers.slots.Slot;

public class FoodEvent {

	@Id
	private String id;

	private List<String> breakfastParticipants;
	private List<String> snackParticipants;
	private Slot date;
	private boolean isActive;

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
		this.date = date;
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

	public Slot getDate() {
		return date;
	}

	public void setDate(Slot date) {
		this.date = date;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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
		builder.append(date);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append("]");
		return builder.toString();
	}

}
