package com.anemortalkid.yummers.slots;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;

public class Slot {
	@Id
	private String id;

	private LocalDate slotDate;

	private boolean isSchedulable;

	public Slot() {
		// free json
	}

	/**
	 * @param slotDate
	 */
	public Slot(LocalDate slotDate) {
		this.slotDate = slotDate;
		isSchedulable = true;
	}

	public LocalDate getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(LocalDate slotDate) {
		this.slotDate = slotDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSchedulable() {
		return isSchedulable;
	}

	public void setSchedulable(boolean isSchedulable) {
		this.isSchedulable = isSchedulable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Slot [id=");
		builder.append(id);
		builder.append(", slotDate=");
		builder.append(slotDate);
		builder.append(", isSchedulable=");
		builder.append(isSchedulable);
		builder.append("]");
		return builder.toString();
	}

}
