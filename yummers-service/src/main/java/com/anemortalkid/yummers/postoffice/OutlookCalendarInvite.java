package com.anemortalkid.yummers.postoffice;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Version;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
	private String location;
	private String uid;
	private String description;
	private String summary;
	private DateTime dateTrigger;

	private Calendar calendar;

	public OutlookCalendarInvite() throws URISyntaxException, ParseException {
		DateTimeZone.setDefault(DateTimeZone.UTC);
		calendar = new Calendar();
		calendar.getProperties()
				.add(new ProdId(
						"-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		// add event
		VEvent event = generateVEvent();

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
		VEvent vEvent = new VEvent();

		vEvent.getProperties().add(generateAttendee());
		vEvent.getProperties().add(new Organizer("MAILTO:" + mailto));
		// TODO convert these from joda to regular date
		// vEvent.getStartDate().setDate(toICALDate(localDate));
		toICALDate();
		calendar.getComponents().add(vEvent);

		calendar.getComponents().add(generateVAlarm());
		// add alarm
		return vEvent;
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
			ParseException {
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
