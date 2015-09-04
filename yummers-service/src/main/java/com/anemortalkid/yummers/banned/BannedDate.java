package com.anemortalkid.yummers.banned;

import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

public class BannedDate {

	@Id
	private String id;

	@DateTimeFormat(pattern = "dd/MM/YYYY")
	private LocalDate bannedDate;

	private int year;
	private int month;
	private int day;

	public BannedDate() {
		// free json
	}

	/**
	 * @param bannedDate
	 */
	public BannedDate(LocalDate bannedDate) {
		this.bannedDate = bannedDate;
		this.day = bannedDate.getDayOfMonth();
		this.month = bannedDate.getMonthOfYear();
		this.year = bannedDate.getYear();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getBannedDate() {
		return bannedDate;
	}

	@DateTimeFormat(pattern = "dd/MM/YYYY")
	public void setBannedDate(LocalDate bannedDate) {
		this.bannedDate = bannedDate;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BannedDate [id=");
		builder.append(id);
		builder.append(", bannedDate=");
		builder.append(bannedDate);
		builder.append("]");
		return builder.toString();
	}

}
