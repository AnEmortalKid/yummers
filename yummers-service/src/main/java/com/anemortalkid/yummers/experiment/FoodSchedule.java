package com.anemortalkid.yummers.experiment;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Represents a scheduled food event with the participants for it for testing
 * 
 * @author JMonterrubio
 *
 */
public class FoodSchedule {

	String id;
	private DateTime dateTime;
	private List<String> breakfastParticipantIds;
	private List<String> snackParticipantIds;

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public List<String> getBreakfastParticipantIds() {
		return breakfastParticipantIds;
	}

	public void setBreakfastParticipantIds(List<String> breakfastParticipantIds) {
		this.breakfastParticipantIds = breakfastParticipantIds;
	}

	public List<String> getSnackParticipantIds() {
		return snackParticipantIds;
	}

	public void setSnackParticipantIds(List<String> snackParticipantIds) {
		this.snackParticipantIds = snackParticipantIds;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FoodSchedule [id=");
		builder.append(id);
		builder.append(", dateTime=");
		builder.append(dateTime);
		builder.append(", breakfastParticipantIds=");
		builder.append(breakfastParticipantIds);
		builder.append(", snackParticipantIds=");
		builder.append(snackParticipantIds);
		builder.append("]");
		return builder.toString();
	}

}
