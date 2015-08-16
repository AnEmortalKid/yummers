package com.anemortalkid.yummers.postoffice;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.elasticsearch.common.joda.time.MutableDateTime;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyList;
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
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.UidGenerator;

/**
 * TODO: finish the calendar generation
 * 
 * TODO: convert all the dates to be parsed using that weird calendar and then
 * the string format, see wth
 * 
 * @author JMonterrubio
 *
 */
public class OutlookCalendarInvite {

	private String mailto = "janmonterrubio@gmail.com";
	private DateTime dateStart = new DateTime();
	private DateTime dateEnd = new DateTime();
	private String location = "Yoloville";
	private String description = "Description";
	private String summary = " Summary";

	public OutlookCalendarInvite(CalendarInviteData inviteData) {
		this(inviteData.getMailto(), inviteData.getDateStart(), inviteData.getDateEnd(), inviteData.getLocation(),
				inviteData.getDescription(), inviteData.getSummary());
	}

	/**
	 * @param mailto
	 * @param dateStart
	 * @param dateEnd
	 * @param location
	 * @param description
	 * @param summary
	 */
	public OutlookCalendarInvite(String mailto, DateTime dateStart, DateTime dateEnd, String location,
			String description, String summary) {
		this.mailto = mailto;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.location = location;
		this.description = description;
		this.summary = summary;
	}

	/**
	 * Given any date, hopefully in UTC, this method returns a fortuna string
	 * like 20120812T133045Z
	 * 
	 * @param dateTime
	 * @return
	 */
	private String dateToFortunaStringFormat(DateTime dateTime) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(dateTime.getYear());
		stringBuilder.append(String.format("%02d", dateTime.getMonthOfYear()));
		stringBuilder.append(String.format("%02d", dateTime.getDayOfMonth()));
		stringBuilder.append("T");
		stringBuilder.append(String.format("%02d", dateTime.getHourOfDay()));
		stringBuilder.append(String.format("%02d", dateTime.getMinuteOfHour()));
		stringBuilder.append(String.format("%02d", dateTime.getSecondOfMinute()));
		stringBuilder.append("Z");
		return stringBuilder.toString();
	}

	/**
	 * Returns a String in the form:
	 * 
	 * <pre>
	 * "BEGIN:VCALENDAR\n"
	 * "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n"
	 * "VERSION:2.0\n"
	 * "METHOD:REQUEST\n"
	 * "BEGIN:VEVENT\n"
	 * "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:{0}\n"
	 * "ORGANIZER:MAILTO:{0}\n"
	 * "DTSTART:{1}\n"
	 * "DTEND:{2}\n"
	 * "LOCATION:{3}\n"
	 * "TRANSP:OPAQUE\n"
	 * "SEQUENCE:0\n"
	 * "UID:{4}\n"
	 * "DTSTAMP:{5}\n"
	 * "CATEGORIES:Meeting\n"
	 * "DESCRIPTION:{6}\n"
	 * "SUMMARY:{7}\n"
	 * "PRIORITY:5\n"
	 * "CLASS:PUBLIC\n"
	 * "END:VEVENT\n"
	 * "END:VCALENDAR";
	 * </pre>
	 * 
	 * @return
	 */
	private String generateCalendarString() {
		/**
		 * <pre>
		 * "BEGIN:VCALENDAR\n"
		 * "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n"
		 * "VERSION:2.0\n"
		 * "METHOD:REQUEST\n"
		 * "BEGIN:VEVENT\n"
		 * "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:xx@xx.com\n"
		 * "ORGANIZER:MAILTO:xx@xx.com\n"
		 * "DTSTART:20051208T053000Z\n"
		 * "DTEND:20051208T060000Z\n"
		 * "LOCATION:Conference room\n"
		 * "TRANSP:OPAQUE\n"
		 * "SEQUENCE:0\n"
		 * "UID:040000008200E00074C5B7101A82E00800000000002FF466CE3AC5010000000000000000100\n"
		 * "DTSTAMP:20051206T120102Z\n"
		 * "CATEGORIES:Meeting\n"
		 * "DESCRIPTION:This the description of the meeting.\n\n"
		 * "SUMMARY:Test meeting request\n"
		 * "PRIORITY:5\n"
		 * "CLASS:PUBLIC\n"
		 * "END:VEVENT\n" + "END:VCALENDAR");
		 * </pre>
		 */

		StringBuilder calendarBuilder = new StringBuilder();
		calendarBuilder.append("BEGIN:VCALENDAR\n");
		calendarBuilder.append("PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n");
		calendarBuilder.append("VERSION:2.0\n");
		calendarBuilder.append("METHOD:REQUEST\n");
		// append event stuff
		calendarBuilder.append("{0}");
		calendarBuilder.append("END:VCALENDAR");

		String vEventString = generateVEventString(mailto, dateStart, dateEnd, location, description, summary);
		String template = calendarBuilder.toString();
		return MessageFormat.format(template, vEventString);
	}

	private String generateVEventString(String mailTo, DateTime dtStart, DateTime dtEnd, String location,
			String description, String summary) {
		/**
		 * <pre>
		 * "BEGIN:VEVENT\n"
		 * "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:xx@xx.com\n"
		 * "ORGANIZER:MAILTO:xx@xx.com\n"
		 * "DTSTART:20051208T053000Z\n"
		 * "DTEND:20051208T060000Z\n"
		 * "LOCATION:Conference room\n"
		 * "TRANSP:OPAQUE\n"
		 * "SEQUENCE:0\n"
		 * "UID:040000008200E00074C5B7101A82E00800000000002FF466CE3AC5010000000000000000100\n"
		 * "DTSTAMP:20051206T120102Z\n"
		 * "CATEGORIES:Meeting\n"
		 * "DESCRIPTION:This the description of the meeting.\n\n"
		 * "SUMMARY:Test meeting request\n"
		 * "PRIORITY:5\n"
		 * "CLASS:PUBLIC\n"
		 * </pre>
		 */
		StringBuilder vEventTemplateBldr = new StringBuilder();
		vEventTemplateBldr.append("BEGIN:VEVENT\n");
		vEventTemplateBldr.append("ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:{0}\n");
		vEventTemplateBldr.append("ORGANIZER:MAILTO:{0}\n");
		vEventTemplateBldr.append("DTSTART:{1}\n");
		vEventTemplateBldr.append("DTEND:{2}\n");
		vEventTemplateBldr.append("LOCATION:{3}\n");
		vEventTemplateBldr.append("TRANSP:OPAQUE\n");
		vEventTemplateBldr.append("SEQUENCE:0\n");
		vEventTemplateBldr.append("UID:{4}\n");
		vEventTemplateBldr.append("DTSTAMP:{5}\n");
		vEventTemplateBldr.append("CATEGORIES:Meeting\n");
		vEventTemplateBldr.append("DESCRIPTION:{6}\n");
		vEventTemplateBldr.append("SUMMARY:{7}\n");
		vEventTemplateBldr.append("PRIORITY:5\n");
		vEventTemplateBldr.append("CLASS:PUBLIC\n");
		vEventTemplateBldr.append("END:VEVENT\n");
		String template = vEventTemplateBldr.toString();

		// Convert the dates to UTC
		DateTimeZone dtStartZone = dtStart.getZone();
		DateTime convertedStart = new DateTime(dtStartZone.convertLocalToUTC(dtStart.getMillis(), true));
		DateTimeZone dtEndZone = dtEnd.getZone();
		DateTime convertedEnd = new DateTime(dtEndZone.convertLocalToUTC(dtEnd.getMillis(), true));
		String startDateStr = dateToFortunaStringFormat(convertedStart);
		String endDateStr = dateToFortunaStringFormat(convertedEnd);
		String timeStamp = dateToFortunaStringFormat(new DateTime());

		// TODO: this will be database eventually
		UUID randomUUID = UUID.randomUUID();
		return MessageFormat.format(template, mailTo, startDateStr, endDateStr, location, randomUUID.toString(),
				timeStamp, description, summary);
	}

	/**
	 * Returns the crazy email format string
	 * 
	 * @return
	 */
	public String getDataString() {
		return generateCalendarString();
	}

	private void testingData() {
		// Let's do some fuzzy date shiz
		DateTimeZone tz = DateTimeZone.getDefault();
		MutableDateTime mutableDateTime = new MutableDateTime();
		mutableDateTime.setHourOfDay(15);
		mutableDateTime.setMinuteOfHour(30);
		mutableDateTime.setSecondOfMinute(0);
		DateTime convertedStartDate = new DateTime(tz.convertLocalToUTC(mutableDateTime.getMillis(), true));

		MutableDateTime mutableEndTime = new MutableDateTime();
		mutableEndTime.setHourOfDay(16);
		mutableEndTime.setMinuteOfHour(30);
		mutableEndTime.setSecondOfMinute(0);
		DateTime convertedEndDate = new DateTime(tz.convertLocalToUTC(mutableEndTime.getMillis(), true));

	}
}
