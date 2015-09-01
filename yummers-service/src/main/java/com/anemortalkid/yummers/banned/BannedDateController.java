package com.anemortalkid.yummers.banned;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;

@RestController
@RequestMapping("/banned")
public class BannedDateController {

	private static final String BANNED = "/banned";

	@Autowired
	private BannedDateRepository bannedDateRepository;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/banDate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<BannedDate> addBannedDate(@DateTimeFormat(pattern = "dd/MM/YYYY") DateTime dateTime) {
		String callingPath = BANNED + "/banDate";
		// check if it exists
		BannedDate bannedDate = findBannedDateFromTime(dateTime);
		if (bannedDate == null) {
			BannedDate savedDate = bannedDateRepository.save(new BannedDate(dateTime));
			return ResponseFactory.respondCreated(callingPath, savedDate);
		} else {
			return ResponseFactory.respondFound(callingPath, bannedDate);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public YummersResponseEntity<List<BannedDate>> bannedDates() {
		String callingPath = BANNED + "/list";
		return ResponseFactory.respondOK(callingPath, bannedDateRepository.findAll());
	}

	public List<BannedDate> getBannedDates() {
		return bannedDateRepository.findAll();
	}

	@PreAuthorize("hasRole('ROLE_BASIC')")
	@RequestMapping(value = "/checkDate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> checkBannedDate(@DateTimeFormat(pattern = "dd/MM/yyyy") DateTime dateTime) {
		String callingPath = BANNED + "/checkDate";
		return ResponseFactory.respondOK(callingPath, findBannedDateFromTime(dateTime) != null);
	}

	public boolean isBannedDate(@DateTimeFormat(pattern = "dd/MM/yyyy") DateTime dateTime) {
		return findBannedDateFromTime(dateTime) != null;
	}

	private BannedDate findBannedDateFromTime(DateTime dateTime) {
		int day = dateTime.getDayOfMonth();
		int month = dateTime.getMonthOfYear();
		int year = dateTime.getYear();
		return bannedDateRepository.findByYearAndMonthAndDay(year, month, day);
	}

}
