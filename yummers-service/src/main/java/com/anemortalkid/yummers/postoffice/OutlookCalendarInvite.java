package com.anemortalkid.yummers.postoffice;

import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 * TODO: finish the calendar generation
 * 
 * TODO: investigate the time issues portion thing
 * 
 * @author JMonterrubio
 *
 */
public class OutlookCalendarInvite {

	private static final String ICAL_DATE_FORMAT = "yyyyMMdd'T'HHmmss";
	private static final java.time.format.DateTimeFormatter DD_MM_YYYY_FORMATTER = java.time.format.DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	private String mailto;
	private LocalDate localDate = LocalDate.parse("20150816235500",
			DD_MM_YYYY_FORMATTER);
	private java.time.Instant localDateTime = null;
	private DateTime dateEnd;
	private String location = "Yoloville";
	private String description = "Description";
	private String summary = " Summary";
	private DateTime dateTrigger;

	private Calendar calendar;

	public OutlookCalendarInvite() throws URISyntaxException, ParseException,
			SocketException {
		DateTimeZone.setDefault(DateTimeZone.UTC);
		calendar = new Calendar();
		calendar.getProperties()
				.add(new ProdId(
						"-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		// add event
		// VEvent event = generateVEvent();
		fortunaSample();

	}

	private VEvent generateVEvent() throws URISyntaxException, ParseException {
		/**
		 * <pre>
		 *  * 							+ "BEGIN:VEVENT\n"
		 * 							+ "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:xx@xx.com\n"
		 * 							+ "ORGANIZER:MAILTO:xx@xx.com\n"
		 * 							+ "DTSTART:20051208T053000Z\n"
		 * 							+ "DTEND:20051208T060000Z\n"
		 * 							+ "LOCATION:Conference room\n"
		 * 							+ "TRANSP:OPAQUE\n"
		 * 							+ "SEQUENCE:0\n"
		 * 							+ "UID:040000008200E00074C5B7101A82E00800000000002FF466CE3AC5010000000000000000100\n"
		 * 							+ " 000004377FE5C37984842BF9440448399EB02\n"
		 * 							+ "DTSTAMP:20051206T120102Z\n"
		 * 							+ "CATEGORIES:Meeting\n"
		 * 							+ "DESCRIPTION:This the description of the meeting.\n\n"
		 * 							+ "SUMMARY:Test meeting request\n" + "PRIORITY:5\n"
		 * 							+ "CLASS:PUBLIC\n"
		 * </pre>
		 */

		dateEnd = DateTimeFormat.forPattern("yyyyMMddHHmmss").parseDateTime(
				"20150813101500");
		System.out.println(dateEnd);
		java.util.Calendar startCal = java.util.Calendar.getInstance();
		// startCal.set(2015, 8, 13, 20, 20, 00);
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
		String strDate = sdFormat.format(startCal.getTime());
		System.out.println("strDate=" + strDate);
		startCal.toInstant();

		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
				.createRegistry();
		net.fortuna.ical4j.model.TimeZone timezone = registry
				.getTimeZone("America/Mexico_City");
		VTimeZone tz = timezone.getVTimeZone();

		// Start Date is on: April 1, 2008, 9:00 am
		java.util.Calendar startDate = new GregorianCalendar();
		startDate.setTimeZone(timezone);
		startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
		startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		startDate.set(java.util.Calendar.YEAR, 2008);
		startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
		startDate.set(java.util.Calendar.MINUTE, 0);
		startDate.set(java.util.Calendar.SECOND, 0);

		// End Date is on: April 1, 2008, 13:00
		java.util.Calendar endDate = new GregorianCalendar();
		endDate.setTimeZone(timezone);
		endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
		endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		endDate.set(java.util.Calendar.YEAR, 2008);
		endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
		endDate.set(java.util.Calendar.MINUTE, 0);
		endDate.set(java.util.Calendar.SECOND, 0);

		// Create the event
		String eventName = "Progress Meeting";
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent meeting = new VEvent();

		// add timezone info..
		meeting.getProperties().add(tz.getTimeZoneId());

		Date fortunaDate = new Date("20150813T101500Z", "yyyyMMdd'T'HHmmss'Z'");
		fortunaDate.setTime(dateEnd.getMillis());
		VEvent vEvent = new VEvent(new Date(startCal.getTime()), new Date(),
				summary);
		// vEvent.getStartDate().setValue("20051208T060000Z");
		// vEvent.getProperties().add(new DtStart("20051208T060000Z"));
		// vEvent.getProperties().add(
		// new Date(Property.DTSTART, "20051208T060000Z"));
		// Property

		vEvent.getProperties().add(generateAttendee());
		vEvent.getProperties().add(new Organizer("MAILTO:" + mailto));
		// TODO convert these from joda to regular date
		// vEvent.getStartDate().setDate(toICALDate(localDate));
		// Set date to value via string.ezpz
		// vEvent.getStartDate().setDate(null);

		vEvent.getProperties().add(new Location(location));
		vEvent.getProperties().add(new Transp("OPAQUE"));
		vEvent.getProperties().add(new Sequence(0));
		vEvent.getProperties().add(
				new Uid(Long.toString(new Random().nextLong()))); // TODO proper
																	// uid
		// generate it to fortuna
		vEvent.getDateStamp().setDate(new Date());
		vEvent.getProperties().add(new Categories("Meeting"));
		vEvent.getProperties().add(new Description(description));
		vEvent.getProperties().add(new Clazz("PUBLIC"));

		calendar.getComponents().add(vEvent);

		calendar.getComponents().add(generateVAlarm());
		// add alarm
		return vEvent;
	}

	private void fortunaSample() throws SocketException, ParseException {
		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
				.createRegistry();
		net.fortuna.ical4j.model.TimeZone timezone = registry
				.getTimeZone("America/Mexico_City");
		VTimeZone tz = timezone.getVTimeZone();

		// Start Date is on: April 1, 2008, 9:00 am
		java.util.Calendar startDate = new GregorianCalendar();
		startDate.setTimeZone(timezone);
		startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
		startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		startDate.set(java.util.Calendar.YEAR, 2008);
		startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
		startDate.set(java.util.Calendar.MINUTE, 0);
		startDate.set(java.util.Calendar.SECOND, 0);

		// End Date is on: April 1, 2008, 13:00
		java.util.Calendar endDate = new GregorianCalendar();
		endDate.setTimeZone(timezone);
		endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
		endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		endDate.set(java.util.Calendar.YEAR, 2008);
		endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
		endDate.set(java.util.Calendar.MINUTE, 0);
		endDate.set(java.util.Calendar.SECOND, 0);

		// Create the event
		String eventName = "Progress Meeting";
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		java.util.Date shittyDate = new java.util.Date();
		shittyDate.setSeconds(30);
		Date fortunaDate = new Date(shittyDate);
		VEvent meeting = new VEvent(fortunaDate, new Date(), eventName);
		// meeting.getStartDate().setValue(start.toString());

		// add timezone info..
		meeting.getProperties().add(tz.getTimeZoneId());

		// generate unique identifier..
		UidGenerator ug = new UidGenerator("uidGen");
		Uid uid = ug.generateUid();
		meeting.getProperties().add(uid);

		// add attendees..
		Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
		dev1.getParameters().add(Role.REQ_PARTICIPANT);
		dev1.getParameters().add(new Cn("Developer 1"));
		meeting.getProperties().add(dev1);

		Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
		dev2.getParameters().add(Role.OPT_PARTICIPANT);
		dev2.getParameters().add(new Cn("Developer 2"));
		meeting.getProperties().add(dev2);

		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(
				new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);

		// Add the event and print
		icsCalendar.getComponents().add(meeting);
		System.out.println(icsCalendar);
	}

	private Attendee generateAttendee() throws URISyntaxException {
		ParameterList paramList = new ParameterList();
		paramList.add(ParameterFactoryImpl.getInstance().createParameter(
				Parameter.ROLE, "REQ-PARTICIPANT"));
		paramList.add(ParameterFactoryImpl.getInstance().createParameter(
				Parameter.RSVP, "TRUE"));
		return new Attendee(paramList.toString());
	}

	private VAlarm generateVAlarm() throws URISyntaxException {
		/**
		 * <pre>
		 * &quot;BEGIN:VALARM\n&quot; + &quot;TRIGGER:PT1440M\n&quot; + &quot;ACTION:DISPLAY\n&quot;
		 * 		+ &quot;DESCRIPTION:Reminder\n&quot; + &quot;END:VALARM\n&quot;
		 * </pre>
		 */
		VAlarm vAlarm = new VAlarm();
		ParameterList paramList = new ParameterList();
		// the
		// TODO: change trigger value
		vAlarm.getProperties().add(
				new Trigger(new net.fortuna.ical4j.model.DateTime()));
		vAlarm.getProperties().add(new Action("DISPLAY"));
		vAlarm.getProperties().add(new Description("Reminder"));

		return vAlarm;
	}

	private static Parameter createParameter(String key, String value)
			throws URISyntaxException {
		return ParameterFactoryImpl.getInstance().createParameter(key, value);
	}

	private static Date toICALDate() throws ParseException {
		java.util.Calendar startCal = java.util.Calendar.getInstance();
		// startCal.set(2015, 8, 13, 20, 20, 00);
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
		String strDate = sdFormat.format(startCal.getTime());
		System.out.println("strDate=" + strDate);

		net.fortuna.ical4j.model.DateTime dateTime = new net.fortuna.ical4j.model.DateTime();
		net.fortuna.ical4j.model.DateTime.from(java.time.Instant.now());
		System.out.println("Fortuna\n" + dateTime);

		DateTime jodaDt = new DateTime();
		System.out.println(jodaDt);
		System.out.println(jodaDt.toString(DateTimeFormat
				.forPattern("yyyyMMdd'T'hhmmss'Z'")));
		return null;
	}

	@Override
	public String toString() {
		return calendar.toString();
	}

	public static void main(String[] args) throws URISyntaxException,
			ParseException, SocketException {
		System.out.println(new OutlookCalendarInvite().toString());
	}

	/**
	 * <pre>
	 * "BEGIN:VCALENDAR\n"
	 * 							+ "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n"
	 * 							+ "VERSION:2.0\n"
	 * 							+ "METHOD:REQUEST\n"
	 * 							+ "BEGIN:VEVENT\n"
	 * 							+ "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:xx@xx.com\n"
	 * 							+ "ORGANIZER:MAILTO:xx@xx.com\n"
	 * 							+ "DTSTART:20051208T053000Z\n"
	 * 							+ "DTEND:20051208T060000Z\n"
	 * 							+ "LOCATION:Conference room\n"
	 * 							+ "TRANSP:OPAQUE\n"
	 * 							+ "SEQUENCE:0\n"
	 * 							+ "UID:040000008200E00074C5B7101A82E00800000000002FF466CE3AC5010000000000000000100\n"
	 * 							+ " 000004377FE5C37984842BF9440448399EB02\n"
	 * 							+ "DTSTAMP:20051206T120102Z\n"
	 * 							+ "CATEGORIES:Meeting\n"
	 * 							+ "DESCRIPTION:This the description of the meeting.\n\n"
	 * 							+ "SUMMARY:Test meeting request\n" + "PRIORITY:5\n"
	 * 							+ "CLASS:PUBLIC\n" + "BEGIN:VALARM\n"
	 * 							+ "TRIGGER:PT1440M\n" + "ACTION:DISPLAY\n"
	 * 							+ "DESCRIPTION:Reminder\n" + "END:VALARM\n"
	 * 							+ "END:VEVENT\n" + "END:VCALENDAR");
	 * </pre>
	 */
}
