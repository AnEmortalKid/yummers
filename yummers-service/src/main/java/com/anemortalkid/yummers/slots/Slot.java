package com.anemortalkid.yummers.slots;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

public class Slot {

	@Id
	private String id;
	
	private DateTime slotDate;

	public Slot() {
		// free json
	}

	/**
	 * @param slotDate
	 */
	public Slot(DateTime slotDate) {
		this.slotDate = slotDate;
	}

	public DateTime getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(DateTime slotDate) {
		this.slotDate = slotDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Slot [id=");
		builder.append(id);
		builder.append(", slotDate=");
		builder.append(slotDate);
		builder.append("]");
		return builder.toString();
	}

}
