package com.anemortalkid.yummers.experiment;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class FridayFinder {

	/**
	 * dd/MM/yyyy
	 */
	public static final DateTimeFormatter DATE_PATTERN = DateTimeFormat.forPattern("dd/MM/yyyy");

	public static List<DateTime> getFridays(String startDate, String endDate) {
		DateTime startDateTime = DATE_PATTERN.parseDateTime(startDate);
		DateTime endDateTime = DATE_PATTERN.parseDateTime(endDate);
		return getFridays(startDateTime, endDateTime);
	}

	public static List<DateTime> getNextFridaysFromDate(DateTime dateTime, int fridaysNeeded) {
		List<DateTime> fridays = new ArrayList<>();

		DateTime startDateTime = dateTime.plusDays(1);
		int fridaysFound = 0;
		boolean reachedAFriday = false;
		// add the
		while (fridaysFound < fridaysNeeded) {
			if (startDateTime.getDayOfWeek() == DateTimeConstants.FRIDAY) {
				fridays.add(startDateTime);
				fridaysFound++;
				reachedAFriday = true;
			}
			if (reachedAFriday) {
				startDateTime = startDateTime.plusWeeks(1);
			} else {
				startDateTime = startDateTime.plusDays(1);
			}
		}
		return fridays;
	}

	public static List<DateTime> getFridays(DateTime startDateTime, DateTime endDateTime) {
		List<DateTime> fridays = new ArrayList<>();
		boolean reachedAFriday = false;
		while (startDateTime.isBefore(endDateTime)) {
			if (startDateTime.getDayOfWeek() == DateTimeConstants.FRIDAY) {
				fridays.add(startDateTime);
				reachedAFriday = true;
			}
			if (reachedAFriday) {
				startDateTime = startDateTime.plusWeeks(1);
			} else {
				startDateTime = startDateTime.plusDays(1);
			}
		}
		return fridays;
	}

	public static List<DateTime> getNextFridaysFromDate(String dateString, int fridaysNeeded) {
		// TODO Auto-generated method stub
		DateTime dateTime = DateTime.parse(dateString, DATE_PATTERN);
		return getNextFridaysFromDate(dateTime, fridaysNeeded);
	}

}
