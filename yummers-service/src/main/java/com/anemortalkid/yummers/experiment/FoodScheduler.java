package com.anemortalkid.yummers.experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.sun.media.sound.FFT;

public class FoodScheduler {

	private static List<String> breakfastParticipants = Arrays.asList("b1", "b2", "b3", "b4", "b5", "b6");
	private static List<String> snackParticipants = Arrays.asList("s1", "s2", "s3", "s4", "s5", "s6");

	private static List<DateTime> fridays = FridayFinder.getFridays("08/09/2015", "31/12/2015");

	private static void transposeList(List<FoodSchedule> schedule, int transpose) {
		// Get date from first event
		FoodSchedule foodSchedule = schedule.get(0);
		DateTime dateTime = foodSchedule.getDateTime();

		// we need at least as many fridays as are scheduled
		int minFridays = schedule.size();

		int year = dateTime.getYear();
		String lastDate = "31/12/" + year;

		// Get fridays and remove count
		String startDate = dateTime.toString(FridayFinder.DATE_PATTERN);
		List<DateTime> fridaysOfYear = FridayFinder.getFridays(startDate, lastDate);

		// check that fridays is at least minFridays+transpose
		if (fridaysOfYear.size() < minFridays + transpose) {
			// add a new year
			DateTime lastDayOfCurrYear = FridayFinder.DATE_PATTERN.parseDateTime(lastDate);
			DateTime firstDayNextYear = lastDayOfCurrYear.plusDays(1);
			DateTime nextYearLastDay = lastDayOfCurrYear.plusYears(1);

			// get new list and add all
			fridaysOfYear.addAll(FridayFinder.getFridays(firstDayNextYear, nextYearLastDay));
		}

		for (int i = 0; i < transpose; i++) {
			fridaysOfYear.remove(0);
		}

		// rebuild participant list
		for (int i = 0; i < schedule.size(); i++) {
			FoodSchedule fs = schedule.get(i);
			fs.setDateTime(fridaysOfYear.get(i));
		}

	}

	private static List<FoodSchedule> schedule(List<String> breakfastParticipants, List<String> snackParticipants, List<DateTime> fridaysAvailable) {
		// shuffle both lists
		Collections.shuffle(breakfastParticipants);
		List<String> modifiableBreakfast = new ArrayList<String>(breakfastParticipants);
		Collections.shuffle(snackParticipants);
		List<String> modifiableSnack = new ArrayList<String>(snackParticipants);

		// while not empty, schedule
		int participantCount = breakfastParticipants.size();

		List<FoodSchedule> schedule = new ArrayList<FoodSchedule>();
		for (int i = 0; i < participantCount; i += 2) {
			String bp1 = modifiableBreakfast.remove(0);
			String bp2 = modifiableBreakfast.remove(0);
			String sp1 = modifiableSnack.remove(0);
			String sp2 = modifiableSnack.remove(0);
			DateTime friday = fridaysAvailable.remove(0);
			FoodSchedule fs = new FoodSchedule();
			fs.setDateTime(friday);
			fs.setBreakfastParticipantIds(Arrays.asList(bp1, bp2));
			fs.setSnackParticipantIds(Arrays.asList(sp1, sp2));
			schedule.add(fs);
		}
		return schedule;
	}
}
