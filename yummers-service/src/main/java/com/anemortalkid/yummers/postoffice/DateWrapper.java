package com.anemortalkid.yummers.postoffice;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.annotation.DateTimeFormat;

public class DateWrapper {

	private DateTime dateOne;

	public DateWrapper() {
		// leave jason alone
	}
	
	public DateTime getDateOne() {
		return dateOne;
	}
	
	public void setDateOne(String dateOne)
	{
		DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		DateTime dateTime = DateTime.parse(dateOne, formatter);
		this.dateOne = dateTime;
	}
	
	public static void main(String[] args) {
		DateTime dt = new DateTime();
		System.out.println(dt);
		String dateString = dt.toString();
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
		System.out.println(dateString);
		System.out.println(dt.toString(pattern));
		DateWrapper dtw = new DateWrapper();
	}

}
