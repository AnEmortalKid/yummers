package com.anemortalkid.yummers.postoffice;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Data object for the required fields we need when generating an
 * {@link OutlookCalendarInvite}
 * 
 * @author jmonterrubio
 *
 */
public class EventData {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

	private String mailTo;
	private DateTime dateStart;
	private DateTime dateEnd;
	private String location;
	private String description;
	private String summary;

	public EventData() {
		// free json
	}

	/**
	 * @return the email address that is responsible for this event
	 */
	public String getMailTo() {
		return mailTo;
	}

	/**
	 * @param mailto
	 *            the email address that is responsible for this event
	 */
	public void setMailTo(String mailto) {
		this.mailTo = mailto;
	}

	/**
	 * @return the date and time that the invite starts at
	 */
	public DateTime getDateStart() {
		return dateStart;
	}

	/**
	 * @param dateStart
	 *            the date and time that the invite starts at in the form
	 *            "yyyy-MM-ddTHH:mm:ss.SSSZZ"
	 */
	public void setDateStart(String dateStart) {
		this.dateStart = DateTime.parse(dateStart, DATE_FORMAT);
	}

	/**
	 * @param dateStart
	 *            the date and time that the invite starts at
	 */
	public void setDateTimeStart(DateTime dateStart) {
		this.dateStart = dateStart;
	}

	/**
	 * @return the date and time that the invite ends on
	 */
	public DateTime getDateEnd() {
		return dateEnd;
	}

	/**
	 * @param dateEnd
	 *            the date and time that the invite ends at in the form
	 *            "yyyy-MM-ddTHH:mm:ss.SSSZZ"
	 */
	public void setDateEnd(String dateEnd) {
		this.dateEnd = DateTime.parse(dateEnd, DATE_FORMAT);
	}

	/**
	 * @param dateEnd
	 *            the date and time that the invite ends on
	 */
	public void setDateTimeEnd(DateTime dateEnd) {
		this.dateEnd = dateEnd;
	}

	/**
	 * @return the location where the event takes place
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location where the event takes place
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return a description of the event
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            a description of the event
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the summary for the event
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary for the event
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalendarInviteData [mailto=");
		builder.append(mailTo);
		builder.append(", dateStart=");
		builder.append(dateStart);
		builder.append(", dateEnd=");
		builder.append(dateEnd);
		builder.append(", location=");
		builder.append(location);
		builder.append(", description=");
		builder.append(description);
		builder.append(", summary=");
		builder.append(summary);
		builder.append("]");
		return builder.toString();
	}

}
