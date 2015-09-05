package com.anemortalkid.yummers.slots;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility class that can find Fridays for us
 * 
 * @author JMonterrubio
 *
 */
public class FridayFinder {

	/**
	 * dd/MM/yyyy
	 */
	public static final DateTimeFormatter DATE_PATTERN = DateTimeFormat.forPattern("dd/MM/yyyy");

	/**
	 * @return a list of all the fridays between the given start and end date
	 */
	public static List<DateTime> getFridays(String startDate, String endDate) {
		DateTime startDateTime = DATE_PATTERN.parseDateTime(startDate);
		DateTime endDateTime = DATE_PATTERN.parseDateTime(endDate);
		return getFridays(startDateTime, endDateTime);
	}

	/**
	 * @return returns a List of fridaysNeeded from the given start date
	 */
	public static List<LocalDate> getNextFridaysFromDate(LocalDate startDate, int fridaysNeeded) {
		List<LocalDate> fridays = new ArrayList<>();

		LocalDate startDateTime = startDate.plusDays(1);
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

	/**
	 * @return a list of all the fridays between the given start and end date
	 */
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

	/**
	 * @return returns a List of fridaysNeeded from the given start date
	 */
	public static List<LocalDate> getNextFridaysFromDate(String dateString, int fridaysNeeded) {
		DateTime dateTime = DateTime.parse(dateString, DATE_PATTERN);
		return getNextFridaysFromDate(dateTime.toLocalDate(), fridaysNeeded);
	}

}
